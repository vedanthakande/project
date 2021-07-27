package com.capi.ecomshoppingapp.wishlist;

import com.capi.ecomshoppingapp.Model.Wishlist;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface WishlistDataSource {
    Flowable<List<Wishlist>> getAllWishlist();
    Single<Integer> countItemsInWishlist();
    Single<Wishlist> getItemInWishlist(String product_id);
    Completable insertOrReplaceAll(Wishlist... wishlist);
    Single<Integer> deleteWishlistItem(Wishlist wishlist);
    Single<Integer> cleanWishlist();
}
