package com.capi.ecomshoppingapp.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.R;

public class CartViewHolder extends RecyclerView.ViewHolder {
    public ImageView cartImage;
    public TextView cartName;
    public TextView cartPrice;
    public ImageView wishlistBtn;
    public ImageView deleteBtn;
    public ImageView cartPlus;
    public ImageView cartMinus;
    public TextView cart_qty;
    public TextView cart_currency;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        cartImage = itemView.findViewById(R.id.cart_image);
        cartName = itemView.findViewById(R.id.cart_name);
        cartPrice = itemView.findViewById(R.id.cart_price);
        wishlistBtn = itemView.findViewById(R.id.add_to_wishlist);
        deleteBtn = itemView.findViewById(R.id.delete_cart);
        cartPlus = itemView.findViewById(R.id.cart_add);
        cartMinus = itemView.findViewById(R.id.cart_minus);
        cart_qty = itemView.findViewById(R.id.cart_quantity);
        cart_currency = itemView.findViewById(R.id.cart_currency);

    }
}