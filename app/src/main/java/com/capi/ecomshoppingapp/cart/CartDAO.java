package com.capi.ecomshoppingapp.cart;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.capi.ecomshoppingapp.Model.Cart;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDAO {
    @Query("SELECT * FROM Cart")
    Flowable<List<Cart>> getAllCart();

    @Query("SELECT Count(*) FROM Cart")
    Single<Integer> countItemsInCart();

    @Query("SELECT SUM(product_price*quantity) FROM Cart")
    Single<Double> sumPriceInCart();

    @Query("SELECT * FROM Cart WHERE product_id=:product_id")
    Single<Cart> getItemInCart(String product_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(Cart... cart);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItem(Cart cart);

    @Delete
    Single<Integer> deleteCartItem(Cart cart);

    @Query("DELETE FROM Cart")
    Single<Integer> cleanCart();
}
