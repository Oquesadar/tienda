package com.tienda.paypal;

import com.tienda.domain.*;
import com.tienda.service.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/paypal")
public class PayPalController {

    private final PayPalService payPalService;
    private final CarritoService carritoService;
    private final UsuarioService usuarioService;
    private final ConstanteService constanteService;

    public PayPalController(PayPalService payPalService, CarritoService carritoService, UsuarioService usuarioService, ConstanteService constanteService) {
        this.payPalService = payPalService;
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
        this.constanteService = constanteService;
    }

    private static final String CURRENCY = "USD";

    /**
     * Paso 1: Crea la orden de PayPal. Es llamado por el frontend/cliente para
     * iniciar el pago.
     *
     * @param usdAmount
     * @return ResponseEntity con la OrderResponse y el link de aprobación.
     */
    @PostMapping("/facturar")
    public ResponseEntity<OrderResponse> createOrder(@RequestParam Double usdAmount) {
        Optional<Constante> constanteReturnUrl = constanteService.findByAtributo("app.paypal.return-url");
        Optional<Constante> constanteCancelUrl = constanteService.findByAtributo("app.paypal.cancel-url");
        Optional<Constante> constanteServidorHttp = constanteService.findByAtributo("servidor.http");
        if (constanteReturnUrl.isPresent() && constanteCancelUrl.isPresent() && constanteServidorHttp.isPresent()) {
            String returnUrl = constanteServidorHttp.get().getValor()+constanteReturnUrl.get().getValor();
            String cancelUrl = constanteServidorHttp.get().getValor()+constanteCancelUrl.get().getValor();
            try {
                // Lógica para obtener dinámicamente el returnUrl y cancelUrl si fuera necesario
                OrderResponse orderResponse = payPalService.createOrder(
                        usdAmount,
                        CURRENCY,
                        returnUrl,
                        cancelUrl
                );

                // Si todo es correcto, devuelve la respuesta al cliente, que ahora buscará
                // el 'approval_link' para redirigir al usuario a PayPal.
                return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);

            } catch (Exception e) {
                // Manejo de errores (ej: fallo de conexión a PayPal, credenciales incorrectas)
                System.err.println("Error al crear la orden de PayPal: " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
                System.err.println("No estan definidas las constantes servidor, returnUrl o cancelUrl: ");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Paso 2 & 3: Captura la orden después de la aprobación del usuario. Es
     * llamado por PayPal (redirige al cliente a esta URL) después de que el
     * usuario aprueba el pago. Se usa un RedirectView para que el navegador del
     * cliente sea redirigido a una página de éxito/fallo.
     *
     * @param token El token de la orden proporcionado por PayPal en la
     * redirección.
     * @param session
     * @param redirectAttributes
     * @return Una redirección a la página de éxito o cancelación de tu app.
     */
    @GetMapping("/order/capture")
    public String captureOrder(@RequestParam("token") String token, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            // Usa el token/ID de la orden para capturar los fondos
            CaptureResponse captureResponse = payPalService.captureOrder(token);

            // Verifica el estado de la captura
            if ("COMPLETED".equalsIgnoreCase(captureResponse.getStatus())) {
                // Lógica de Negocio: Marca el pedido como pagado en tu base de datos
                // Puedes usar el ID de la orden (token) o el ID de la captura.

                try {
                    List<Item> carrito = carritoService.obtenerCarrito(session);

                    // Obtención del usuario autenticado*
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    String username = auth.getName();
                    System.out.println("El username es:" + username);
                    Usuario usuario = usuarioService.getUsuarioPorUsername(username).get();

                    // 1. La lógica transaccional ocurre en el servicio
                    Factura factura = carritoService.procesarCompra(carrito, usuario);

                    // 2. Limpiar el carrito de la sesión después de una compra exitosa
                    carritoService.limpiarCarrito(session);

                    // 3. Pasar el ID de la factura como Flash Attribute
                    redirectAttributes.addFlashAttribute("idFactura", factura.getIdFactura());
                    redirectAttributes.addFlashAttribute("orderId", token);

                    // 4. Redirigir a una ruta nueva para ver la factura            
                    return "redirect:/carrito/verFactura";

                } catch (RuntimeException e) {
                    // Captura errores de stock, carrito vacío o de la transacción
                    System.out.println("Hubo un error en la infor del carriro");
                    redirectAttributes.addFlashAttribute("error", "Error al procesar la compra: " + e.getMessage());
                    return "redirect:/carrito/listado";
                }
            } else {
                // El pago fue aprobado, pero la captura falló (ej: fondos insuficientes)
                System.out.println("Hubo un error raro");
                return "redirect:/pago/fallo?orderId=" + token + "&status=" + captureResponse.getStatus();
            }

        } catch (Exception e) {
            System.err.println("Error al capturar la orden de PayPal: " + e.getMessage());
            // Redirige al cliente a una URL de error genérico
            return "redirect:/pago/error_interno";
        }
    }
}