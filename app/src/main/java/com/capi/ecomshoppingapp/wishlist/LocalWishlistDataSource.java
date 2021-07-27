package com.capi.ecomshoppingapp.wishlist;


import com.capi.ecomshoppingapp.Model.Wishlist;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalWishlistDataSource implements WishlistDataSource {

    private WishlistDAO wishlistDAO;

    public LocalWishlistDataSource(WishlistDAO wishlistDAO) {
        this.wishlistDAO = wishlistDAO;
    }


    @Override
    public Flowable<List<Wishlist>> getAllWishlist() {
        return wishlistDAO.getAllWishlist();
    }

    @Override
    public Single<Integer> countItemsInWishlist() {
        return wishlistDAO.countItemsInWishlist();
    }

    @Override
    public Single<Wishlist> getItemInWishlist(String product_id) {
        return wishlistDAO.getItemInWishlist(product_id);
    }

    @Override
    public Completable insertOrReplaceAll(Wishlist... wishlist) {
        return wishlistDAO.insertOrReplaceAll(wishlist);
    }

    @Override
    public Single<Integer> deleteWishlistItem(Wishlist wishlist) {
        return wishlistDAO.deleteWishlistItem(wishlist);
    }

    @Override
    public Single<Integer> cleanWishlist() {
        return wishlistDAO.cleanWishlist();
    }
}
