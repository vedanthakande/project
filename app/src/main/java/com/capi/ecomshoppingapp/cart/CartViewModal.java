package com.capi.ecomshoppingapp.cart;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.capi.ecomshoppingapp.Model.Cart;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CartViewModal extends ViewModel
{
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;
    private MutableLiveData<List<Cart>> mutableLiveDataCart;

    public CartViewModal(){
        compositeDisposable = new CompositeDisposable();
    }

    public void initCardDataSource(Context context)
    {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    private void onStop(){
        compositeDisposable.clear();
    }

    public MutableLiveData<List<Cart>> getMutableLiveDataCart()
    {
        if(mutableLiveDataCart == null)
            mutableLiveDataCart = new MutableLiveData<>();
        getAllCartItems();
        return mutableLiveDataCart;
    }

    private void getAllCartItems()
    {
        compositeDisposable.add(cartDataSource.getAllCart()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(carts ->{
            mutableLiveDataCart.setValue(carts);
        }, throwable -> {
            mutableLiveDataCart.setValue(null);
        }));
    }
}
