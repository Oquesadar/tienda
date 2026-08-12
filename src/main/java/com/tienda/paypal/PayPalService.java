package com.tienda.paypal;

import java.util.Collections;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PayPalService {

    private final WebClient paypalWebClient;
    private final PayPalAuthService authService;
    private final MessageSource messageSource;

    public PayPalService(WebClient paypalWebClient, PayPalAuthService authService, MessageSource messageSource) {
        this.paypalWebClient = paypalWebClient;
        this.authService = authService;
        this.messageSource = messageSource;
    }


    /**
     * 1. Crea una orden de pago en PayPal.
     * Esta llamada inicializa la transacción y devuelve el enlace de aprobación.
     * @param amount
     * @param currency
     * @param returnUrl
     * @param cancelUrl
     * @return 
     */
    public OrderResponse createOrder(double amount, String currency, String returnUrl, String cancelUrl) {
        // Obtener el token de acceso (se maneja la expiración dentro de PayPalAuthService)
        String accessToken = authService.getAccessToken();
        
        // Construcción del objeto de solicitud JSON
        OrderRequest orderRequest = buildOrderRequest(amount, currency, returnUrl, cancelUrl);
        
        return paypalWebClient.post()
                .uri("/v2/checkout/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(orderRequest)
                .retrieve()
                // Si la respuesta es exitosa (2xx), mapea el cuerpo a OrderResponse.
                // Si la respuesta es de error (4xx/5xx), lanza una excepción.
                .bodyToMono(OrderResponse.class) 
                .block(); // Bloquear la ejecución (para aplicaciones síncronas/tradicionales)
    }

    /**
     * 2. Captura la orden de pago.
     * Se llama después de que el usuario aprueba el pago en la página de PayPal.
     * Este es el paso final que mueve el dinero.
     * @param orderId
     * @return 
     */
    public CaptureResponse captureOrder(String orderId) {
        String accessToken = authService.getAccessToken();
        
        // PayPal usa POST, pero el cuerpo está típicamente vacío para la captura.
        // Se utiliza el orderId en el path (URI).
        return paypalWebClient.post()
                .uri("/v2/checkout/orders/{orderId}/capture", orderId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(CaptureResponse.class)
                .block();
    }
    
    /**
     * Construye el cuerpo de la solicitud (DTO) para crear la orden de PayPal.
     * Nota: Se utiliza una estructura simplificada aquí, idealmente incluirías el desglose
     * de ítems, impuestos y envío para mayor detalle.
     */
    private OrderRequest buildOrderRequest(double amount, String currency, String returnUrl, String cancelUrl) {
        // Formatear el monto con dos decimales, ya que PayPal requiere el valor como String
        String amountValue = String.format(java.util.Locale.US, "%.2f", amount);

        // 1. Crear el objeto Amount
        OrderRequest.Amount amountObj = new OrderRequest.Amount();
        amountObj.setCurrencyCode(currency);
        amountObj.setValue(amountValue);

        // 2. Crear el objeto PurchaseUnit
        OrderRequest.PurchaseUnit purchaseUnit = new OrderRequest.PurchaseUnit();
        purchaseUnit.setAmount(amountObj);        
        String customIdMessage = messageSource.getMessage("app.paypal.custom.description", null, Locale.getDefault());
        purchaseUnit.setCustomId(customIdMessage);
        
        // 3. Crear el objeto ApplicationContext
        OrderRequest.ApplicationContext appContext = new OrderRequest.ApplicationContext();
        appContext.setReturnUrl(returnUrl);
        appContext.setCancelUrl(cancelUrl);
        appContext.setUserAction("PAY_NOW"); // Muestra el botón "Pagar ahora" en PayPal

        // 4. Crear el objeto OrderRequest final
        OrderRequest request = new OrderRequest();
        request.setIntent("CAPTURE"); // Indica que queremos capturar los fondos inmediatamente
        request.setPurchaseUnits(Collections.singletonList(purchaseUnit)); // Solo una unidad de compra
        request.setApplicationContext(appContext);

        return request;
    }
}