package com.capi.ecomshoppingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Adapter.WishlistAdapter;
import com.capi.ecomshoppingapp.Model.Wishlist;
import com.capi.ecomshoppingapp.wishlist.WishlistViewModal;

import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WishlistAdapter wishlistAdapter;
    ImageView back;
    private WishlistViewModal wishlistViewModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wishlistViewModal = ViewModelProviders.of(this).get(WishlistViewModal.class);
        setContentView(R.layout.activity_wishlist);

        recyclerView = findViewById(R.id.wishlistReycler);
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(WishlistActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);

        wishlistViewModal.initWishlistDataSource(WishlistActivity.this);
        wishlistViewModal.getMutableLiveDataWishlist().observe(this, new Observer<List<Wishlist>>() {
            @Override
            public void onChanged(List<Wishlist> wishlists) {
                if (wishlists == null || wishlists.isEmpty()) {
                    //onEmptyCart();
                } else {
                    wishlistAdapter = new WishlistAdapter(WishlistActivity.this, wishlists);
                    recyclerView.setAdapter(wishlistAdapter);
                }
            }
        });
    }
}
