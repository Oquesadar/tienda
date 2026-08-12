package com.tienda.paypal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @GetMapping("/fallo")
    public String pagoFallo(@PathVariable("orderId") String orderId, 
            @PathVariable("status") String estado, Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("estado", estado);
        return "/paypal/pago_cancel";
    }
    
    @GetMapping("/error_interno")
    public String pagoError() {
        return "/paypal/pago_error";
    }
}