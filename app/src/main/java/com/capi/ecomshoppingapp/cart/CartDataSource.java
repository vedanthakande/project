package com.capi.ecomshoppingapp.cart;

import com.capi.ecomshoppingapp.Model.Cart;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {
    Flowable<List<Cart>> getAllCart();

    Single<Integer> countItemsInCart();

    Single<Double> sumPriceInCart();

    Single<Cart> getItemInCart(String product_id);

    Completable insertOrReplaceAll(Cart... cart);

    Single<Integer> updateCartItem(Cart cart);

    Single<Integer> deleteCartItem(Cart cart);

    Single<Integer> cleanCart();
}
