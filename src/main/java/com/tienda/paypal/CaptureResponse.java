package com.tienda.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CaptureResponse {
    
    private String id;
    
    // El estado de la orden después de la captura (DEBE ser "COMPLETED").
    private String status;
    
    // Lista de las unidades de compra con los resultados de la transacción.
    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;

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

    public List<PurchaseUnit> getPurchaseUnits() {
        return purchaseUnits;
    }

    public void setPurchaseUnits(List<PurchaseUnit> purchaseUnits) {
        this.purchaseUnits = purchaseUnits;
    }

    
    
    // Clase Interna: Unidad de Compra (PurchaseUnit)
    public static class PurchaseUnit {
        private Payments payments;

        public Payments getPayments() {
            return payments;
        }

        public void setPayments(Payments payments) {
            this.payments = payments;
        }
    }

    // Clase Interna: Pagos (Payments)
    public static class Payments {
        private List<Capture> captures;

        public List<Capture> getCaptures() {
            return captures;
        }

        public void setCaptures(List<Capture> captures) {
            this.captures = captures;
        }
    }

    // Clase Interna: Captura (Capture)
    public static class Capture {
        // ID único de la captura (transacción final).
        private String id;
        
        // El estado de la captura (ej: "COMPLETED", "PENDING").
        private String status;
        
        // Información del monto capturado.
        private Amount amount;
        

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

        public Amount getAmount() {
            return amount;
        }

        public void setAmount(Amount amount) {
            this.amount = amount;
        }
    }
    
    // Clase Interna: Monto (Amount)
    public static class Amount {
        @JsonProperty("currency_code")
        private String currencyCode;
        private String value;

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }    
}