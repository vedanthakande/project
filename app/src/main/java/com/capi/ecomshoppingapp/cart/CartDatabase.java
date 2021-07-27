package com.capi.ecomshoppingapp.cart;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.capi.ecomshoppingapp.Model.Cart;
import com.capi.ecomshoppingapp.Model.Wishlist;
import com.capi.ecomshoppingapp.wishlist.WishlistDAO;

@Database(version = 1, entities = {Cart.class, Wishlist.class}, exportSchema = false)
public abstract class CartDatabase extends RoomDatabase {
    public abstract CartDAO cartDAO();

    public abstract WishlistDAO wishlistDAO();

    private static CartDatabase instance;

    public static CartDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, CartDatabase.class, "UnnatiShoppingCartDB1").build();
        return instance;
    }
}
