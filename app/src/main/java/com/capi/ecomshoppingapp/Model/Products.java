package com.capi.ecomshoppingapp.Model;

import java.io.Serializable;

public class Products implements Serializable {
    private String productId;
    private String productImage;
    private String productName;
    private String productSearch;
    private String productDescription;
    private String productPrice;
    private String productDiscount;
    private String productPercent;
    private String categoryId;
    private String categoryName;
    private String discounted;

    public Products(String productId, String productImage, String productName, String productSearch, String productDescription, String productPrice, String productDiscount, String productPercent, String categoryId, String categoryName, String discounted) {
        this.productId = productId;
        this.productImage = productImage;
        this.productName = productName;
        this.productSearch = productSearch;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productDiscount = productDiscount;
        this.productPercent = productPercent;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.discounted = discounted;
    }

    public Products() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getProductPercent() {
        return productPercent;
    }

    public void setProductPercent(String productPercent) {
        this.productPercent = productPercent;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDiscounted() {
        return discounted;
    }

    public void setDiscounted(String discounted) {
        this.discounted = discounted;
    }

    public String getProductSearch() {
        return productSearch;
    }

    public void setProductSearch(String productSearch) {
        this.productSearch = productSearch;
    }
}
