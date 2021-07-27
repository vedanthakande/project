package com.capi.ecomshoppingapp.Model;

import java.util.List;

public class OrderProduct {
    String productId;
    String productName;
    String productPrice;
    List<String> productVariations;
    int productQuantity;
    String date;
    String time;

    public OrderProduct(String productId, String productName, String productPrice, List<String> productVariations, int productQuantity, String date, String time) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productVariations = productVariations;
        this.productQuantity = productQuantity;
        this.date = date;
        this.time = time;
    }

    public OrderProduct() {
    }

    public List<String> getProductVariations() {
        return productVariations;
    }

    public void setProductVariations(List<String> productVariations) {
        this.productVariations = productVariations;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
