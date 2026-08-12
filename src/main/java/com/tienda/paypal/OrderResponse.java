package com.tienda.paypal;

import java.util.List;

public class OrderResponse {
    
    // El ID único de la orden de PayPal. CRÍTICO para el paso de Captura.
    private String id; 
    private String status;
    
    // Lista de enlaces HATEOAS para interactuar con la orden.
    private List<Link> links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    
    
    /**
     * Helper para obtener la URL de aprobación.
     * @return URL de redirección para que el usuario apruebe el pago.
     */
    public String getApprovalLink() {
        if (links != null) {
            return links.stream()
                .filter(link -> "approve".equalsIgnoreCase(link.rel))
                .findFirst()
                .map(link -> link.href)
                .orElse(null);
        }
        return null;
    }

    // Clase Interna: Enlace (Link)
    public static class Link {
        private String href; // La URL
        private String rel;  // El propósito del enlace (ej: "approve", "capture", "self")
        private String method; // El método HTTP a usar
        
        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getRel() {
            return rel;
        }

        public void setRel(String rel) {
            this.rel = rel;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }    
}