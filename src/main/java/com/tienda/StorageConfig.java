package com.tienda;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class StorageConfig {

    @Value("${firebase.json.path}")
    private String jsonPath;

    @Value("${firebase.json.file}")
    private String jsonFile;

    @Bean
    public Storage storage() throws IOException {
        try (InputStream inputStream = obtenerCredenciales()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);
            return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        }
    }

    private InputStream obtenerCredenciales() throws IOException {
        String credsJson = System.getenv("FIREBASE_CREDENTIALS");
        if (credsJson != null && !credsJson.isBlank()) {
            // Producción (Render): el JSON completo viene en una variable de entorno
            return new ByteArrayInputStream(credsJson.getBytes(StandardCharsets.UTF_8));
        }
        // Local (NetBeans): se lee el archivo de src/main/resources/firebase/
        return new ClassPathResource(jsonPath + File.separator + jsonFile).getInputStream();
    }
}