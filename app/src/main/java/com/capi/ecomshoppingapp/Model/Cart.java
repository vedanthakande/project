package com.capi.ecomshoppingapp.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cart")
public class Cart {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    private String product_id;

    @ColumnInfo(name = "product_name")
    private String product_name;

    @ColumnInfo(name = "quantity")
    private Integer quantity;

    @ColumnInfo(name = "product_image")
    private String product_image;

    @ColumnInfo(name = "product_price")
    private String product_price;

    @ColumnInfo(name = "product_variations")
    private String product_variations;

    @NonNull
    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(@NonNull String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_variations() {
        return product_variations;
    }

    public void setProduct_variations(String product_variations) {
        this.product_variations = product_variations;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof com.capi.ecomshoppingapp.Model.Cart))
            return false;
        com.capi.ecomshoppingapp.Model.Cart cart = (com.capi.ecomshoppingapp.Model.Cart) obj;
        return cart.getProduct_id().equals(this.product_id);
    }
}
