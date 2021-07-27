package com.capi.ecomshoppingapp.Model;

public class VariationOption {
    private String variationId;
    private String variationName;
    private String variationType;

    public VariationOption(String variationId, String variationName, String variationType) {
        this.variationId = variationId;
        this.variationName = variationName;
        this.variationType = variationType;
    }

    public VariationOption() {
    }

    public String getVariationId() {
        return variationId;
    }

    public void setVariationId(String variationId) {
        this.variationId = variationId;
    }

    public String getVariationName() {
        return variationName;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

    public String getVariationType() {
        return variationType;
    }

    public void setVariationType(String variationType) {
        this.variationType = variationType;
    }
}
