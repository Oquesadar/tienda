package com.tienda.paypal;

import com.tienda.domain.Constante;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tienda.service.ConstanteService;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PayPalAuthService {

    private final WebClient paypalWebClient;
    private final ConstanteService constanteService;

    // Campos para almacenar y gestionar el token
    private String currentAccessToken;
    private LocalDateTime tokenExpirationTime;

    public PayPalAuthService(WebClient paypalWebClient, ConstanteService constanteService) {
        this.paypalWebClient = paypalWebClient;
        this.constanteService = constanteService;
        this.tokenExpirationTime = LocalDateTime.now().minusSeconds(1); // Forzar la obtención inicial
    }

    /**
     * Devuelve un Access Token válido. Si el token actual ha expirado, solicita uno nuevo.
     * @return El Access Token de PayPal.
     */
    public String getAccessToken() {
        // Verifica si el token actual está a punto de expirar (ej. 30 segundos antes)
        if (currentAccessToken == null || LocalDateTime.now().isAfter(tokenExpirationTime.minusSeconds(30))) {
            // Si expiró o no existe, llama al método para obtener uno nuevo.
            requestNewAccessToken();
        }
        return currentAccessToken;
    }

    /**
     * Realiza la llamada a la API de autenticación de PayPal para obtener un nuevo token.
     */
    private synchronized void requestNewAccessToken() {
        // 1. Obtener Credenciales
        Optional<Constante> clienteIdOpt = constanteService.findByAtributo("paypal.client-id");
        Optional<Constante> clienteSecretOpt = constanteService.findByAtributo("paypal.client-secret");

        if (clienteIdOpt.isEmpty() || clienteSecretOpt.isEmpty()) {
            throw new RuntimeException("Error de Autenticación de PayPal: Client ID o Client Secret no encontrados.");
        }

        String clientId = clienteIdOpt.get().getValor();
        String clientSecret = clienteSecretOpt.get().getValor();

        // 2. Codificar Credenciales para el encabezado 'Authorization: Basic'
        // Formato: ClientID:ClientSecret
        String base64Credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        try {
            // 3. Llamar al endpoint de token de PayPal
            AccessTokenResponse response = paypalWebClient.post()
                    .uri("/v1/oauth2/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials)
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .bodyValue("grant_type=client_credentials") // Cuerpo requerido para obtener token
                    .retrieve()
                    .bodyToMono(AccessTokenResponse.class)
                    .block(); // Bloquea hasta que la respuesta esté disponible

            // 4. Almacenar el nuevo token y su tiempo de expiración
            if (response != null && response.getAccessToken() != null) {
                this.currentAccessToken = response.getAccessToken();
                // Calcular el tiempo de expiración: tiempo actual + segundos_que_vive_el_token
                this.tokenExpirationTime = LocalDateTime.now().plusSeconds(response.getExpiresIn());
            } else {
                 throw new RuntimeException("Fallo al obtener el Access Token de PayPal: Respuesta nula o incompleta.");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("Fallo en la conexión o respuesta de la API de Autenticación de PayPal.", e);
        }
    }

    // --- Mapear la respuesta del endpoint de token ---
    private static class AccessTokenResponse {
        
        @JsonProperty("access_token")
        private String accessToken;
        
        @JsonProperty("expires_in")
        private int expiresIn; // Tiempo en segundos que el token es válido

        public String getAccessToken() { return accessToken; }
        public int getExpiresIn() { return expiresIn; }
        // Se omiten los Setters y otros campos para simplicidad
    }
}