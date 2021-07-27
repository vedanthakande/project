package com.capi.ecomshoppingapp.wishlist;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.capi.ecomshoppingapp.Model.Wishlist;
import com.capi.ecomshoppingapp.cart.CartDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WishlistViewModal extends ViewModel {
    private CompositeDisposable compositeDisposable;
    private WishlistDataSource wishlistDataSource;
    private MutableLiveData<List<Wishlist>> mutableLiveDataWishlist;

    public WishlistViewModal() {
        compositeDisposable = new CompositeDisposable();
    }

    public void initWishlistDataSource(Context context) {
        wishlistDataSource = new LocalWishlistDataSource(CartDatabase.getInstance(context).wishlistDAO());
    }

    private void onStop() {
        compositeDisposable.clear();
    }

    public MutableLiveData<List<Wishlist>> getMutableLiveDataWishlist() {
        if (mutableLiveDataWishlist == null)
            mutableLiveDataWishlist = new MutableLiveData<>();
        getAllWishlistItems();
        return mutableLiveDataWishlist;
    }

    private void getAllWishlistItems() {
        compositeDisposable.add(wishlistDataSource.getAllWishlist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(wishlists -> {
                    mutableLiveDataWishlist.setValue(wishlists);
                }, throwable -> {
                    mutableLiveDataWishlist.setValue(null);
                }));
    }
}
