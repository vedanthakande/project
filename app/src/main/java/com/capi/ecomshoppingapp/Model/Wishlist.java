package com.capi.ecomshoppingapp.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Wishlist")
public class Wishlist {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "product_id")
    private String product_id;

    @ColumnInfo(name = "product_name")
    private String product_name;

    @ColumnInfo(name = "product_image")
    private String product_image;

    @ColumnInfo(name = "product_price")
    private String product_price;

    @ColumnInfo(name = "sale_price")
    private String sale_price;

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

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }
}
