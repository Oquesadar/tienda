package com.tienda.paypal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {
    private String intent = "CAPTURE";

    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;

    @JsonProperty("application_context")
    private ApplicationContext applicationContext;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<PurchaseUnit> getPurchaseUnits() {
        return purchaseUnits;
    }

    public void setPurchaseUnits(List<PurchaseUnit> purchaseUnits) {
        this.purchaseUnits = purchaseUnits;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    
    // Clase Interna: Unidad de Compra (PurchaseUnit)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PurchaseUnit {
        private Amount amount;
        private String customId;

        public Amount getAmount() {
            return amount;
        }

        public void setAmount(Amount amount) {
            this.amount = amount;
        }

        public String getCustomId() {
            return customId;
        }

        public void setCustomId(String customId) {
            this.customId = customId;
        }
    }

    // Clase Interna: Monto (Amount)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Amount {
        @JsonProperty("currency_code")
        private String currencyCode;
        private String value;
        private Breakdown breakdown;

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

        public Breakdown getBreakdown() {
            return breakdown;
        }

        public void setBreakdown(Breakdown breakdown) {
            this.breakdown = breakdown;
        }

       
    }

    // Clase Interna: Desglose del Monto (Breakdown)
    public static class Breakdown {
        @JsonProperty("item_total")
        private AmountValue itemTotal;
        private AmountValue shipping;
        private AmountValue taxTotal;

        public AmountValue getItemTotal() {
            return itemTotal;
        }

        public void setItemTotal(AmountValue itemTotal) {
            this.itemTotal = itemTotal;
        }

        public AmountValue getShipping() {
            return shipping;
        }

        public void setShipping(AmountValue shipping) {
            this.shipping = shipping;
        }

        public AmountValue getTaxTotal() {
            return taxTotal;
        }

        public void setTaxTotal(AmountValue taxTotal) {
            this.taxTotal = taxTotal;
        }

        
    }

    // Clase Interna: Valor del Monto (AmountValue)
    public static class AmountValue {
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

    // Clase Interna: Contexto de la Aplicación (ApplicationContext)
    public static class ApplicationContext {
        @JsonProperty("return_url")
        private String returnUrl;
        @JsonProperty("cancel_url")
        private String cancelUrl;
        @JsonProperty("user_action")
        private String userAction = "PAY_NOW"; // Opcional: "CONTINUE" o "PAY_NOW"

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getCancelUrl() {
            return cancelUrl;
        }

        public void setCancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
        }

        public String getUserAction() {
            return userAction;
        }

        public void setUserAction(String userAction) {
            this.userAction = userAction;
        }
    }
}