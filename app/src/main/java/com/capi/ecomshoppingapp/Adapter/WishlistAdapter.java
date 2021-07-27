package com.capi.ecomshoppingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capi.ecomshoppingapp.Model.Wishlist;
import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.cart.CartDatabase;
import com.capi.ecomshoppingapp.wishlist.LocalWishlistDataSource;
import com.capi.ecomshoppingapp.wishlist.WishlistDataSource;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
    Context context;
    List<Wishlist> wishlistList;
    WishlistDataSource wishlistDataSource;

    public WishlistAdapter(Context context, List<Wishlist> wishlistList) {
        this.context = context;
        this.wishlistList = wishlistList;
        this.wishlistDataSource = new LocalWishlistDataSource(CartDatabase.getInstance(context).wishlistDAO());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wishlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.productName.setText(wishlistList.get(position).getProduct_name());
        holder.productPrice.setText(wishlistList.get(position).getProduct_price());
        Glide.with(context).load(wishlistList.get(position).getProduct_image()).into(holder.productImage);
        if (wishlistList.get(position).getSale_price() != null) {
            holder.currency_discounted.setVisibility(View.VISIBLE);
            holder.discountDivider.setVisibility(View.VISIBLE);
            holder.productBeforeDicountPrice.setVisibility(View.VISIBLE);
            holder.productDiscountRatio.setVisibility(View.VISIBLE);
            holder.productBeforeDicountPrice.setText(wishlistList.get(position).getProduct_price());
            holder.productPrice.setText(wishlistList.get(position).getSale_price());
            holder.productDiscountRatio.setText(100 - ((Integer.parseInt(wishlistList.get(position).getSale_price()) * 100) / Integer.parseInt(wishlistList.get(position).getProduct_price())) + "% Off");
            //holder.productDiscountRatio.setText(wishlistList.get(position).getProductDiscount());
        } else {
            holder.currency_discounted.setVisibility(View.INVISIBLE);
            holder.discountDivider.setVisibility(View.GONE);
            holder.productBeforeDicountPrice.setVisibility(View.GONE);
            holder.productDiscountRatio.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wishlistDataSource.deleteWishlistItem(wishlistList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                notifyItemRemoved(position);
                                //EventBus.getDefault().postSticky(new UpdateItemInWishlist(wishlistList.get(position)));
                                Toast.makeText(context, "Item Deleted!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlistList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        ImageView delete;
        TextView productName;
        RelativeLayout discountDivider;
        TextView productPrice;
        TextView productBeforeDicountPrice;
        TextView productDiscountRatio;
        TextView currency_discounted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.wishlist_image);
            productName = itemView.findViewById(R.id.wishlist_name);
            productPrice = itemView.findViewById(R.id.wishlist_price);
            productBeforeDicountPrice = itemView.findViewById(R.id.wishlist_before_discount_price);
            discountDivider = itemView.findViewById(R.id.wishlist_discount_divider);
            productDiscountRatio = itemView.findViewById(R.id.wishlist_discount_ratio);
            currency_discounted = itemView.findViewById(R.id.wishlist_currency_discounted);
            delete = itemView.findViewById(R.id.delete_wishlist);
        }
    }
}
