package com.capi.ecomshoppingapp;


import com.capi.ecomshoppingapp.Model.Wishlist;

public class UpdateItemInWishlist {
    private Wishlist wishlist;

    public UpdateItemInWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }
}
