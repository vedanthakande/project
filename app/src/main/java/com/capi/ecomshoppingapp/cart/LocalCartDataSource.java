package com.capi.ecomshoppingapp.cart;

import com.capi.ecomshoppingapp.Model.Cart;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDAO cartDAO;

    public LocalCartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Flowable<List<Cart>> getAllCart() {
        return cartDAO.getAllCart();
    }

    @Override
    public Single<Integer> countItemsInCart() {
        return cartDAO.countItemsInCart();
    }

    @Override
    public Single<Double> sumPriceInCart() {
        return cartDAO.sumPriceInCart();
    }

    @Override
    public Single<Cart> getItemInCart(String product_id) {
        return cartDAO.getItemInCart(product_id);
    }


    @Override
    public Completable insertOrReplaceAll(Cart... cart) {
        return cartDAO.insertOrReplaceAll(cart);
    }

    @Override
    public Single<Integer> updateCartItem(Cart cart) {
        return cartDAO.updateCartItem(cart);
    }

    @Override
    public Single<Integer> deleteCartItem(Cart cart) {
        return cartDAO.deleteCartItem(cart);
    }

    @Override
    public Single<Integer> cleanCart() {
        return cartDAO.cleanCart();
    }
}
