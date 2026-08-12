package com.tienda.paypal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PayPalConfig {

    // URL de Sandbox (pruebas). Cambiar a "https://api-m.paypal.com" para Producción real.
    // Aunque esta URL debería ser una propiedad configurable (ej., en application.properties).
    private static final String PAYPAL_BASE_URL = "https://api-m.sandbox.paypal.com"; 

    /**
     * Define y configura el bean de WebClient necesario para todas las
     * llamadas a la API REST de PayPal (Autenticación, Creación y Captura).
     * @return 
     */
    @Bean
    public WebClient paypalWebClient() {
        return WebClient.builder()
                .baseUrl(PAYPAL_BASE_URL)
                .build();
    }
}