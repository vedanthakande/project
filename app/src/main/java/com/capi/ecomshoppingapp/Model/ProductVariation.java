package com.capi.ecomshoppingapp.Model;

import java.io.Serializable;

public class ProductVariation implements Serializable {
    private String variationId;
    private String variationName;
    private String variationType;
    private String variationOptions;

    public ProductVariation(String variationId, String variationName, String variationType, String variationOptions) {
        this.variationId = variationId;
        this.variationName = variationName;
        this.variationType = variationType;
        this.variationOptions = variationOptions;
    }

    public ProductVariation() {
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

    public String getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(String variationOptions) {
        this.variationOptions = variationOptions;
    }
}
