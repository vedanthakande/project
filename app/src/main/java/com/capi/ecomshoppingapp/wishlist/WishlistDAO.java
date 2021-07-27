package com.capi.ecomshoppingapp.wishlist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.capi.ecomshoppingapp.Model.Wishlist;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface WishlistDAO {
    @Query("SELECT * FROM Wishlist")
    Flowable<List<Wishlist>> getAllWishlist();

    @Query("SELECT Count(*) FROM Wishlist")
    Single<Integer> countItemsInWishlist();

    @Query("SELECT * FROM Wishlist WHERE product_id=:product_id")
    Single<Wishlist> getItemInWishlist(String product_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(Wishlist... wishlist);

    @Delete
    Single<Integer> deleteWishlistItem(Wishlist wishlist);

    @Query("DELETE FROM Wishlist")
    Single<Integer> cleanWishlist();
}
