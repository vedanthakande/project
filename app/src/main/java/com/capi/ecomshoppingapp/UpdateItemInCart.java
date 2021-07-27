package com.capi.ecomshoppingapp;


import com.capi.ecomshoppingapp.Model.Cart;

public class UpdateItemInCart {
    private Cart cart;

    public UpdateItemInCart(Cart cart) {
        this.cart = cart;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
