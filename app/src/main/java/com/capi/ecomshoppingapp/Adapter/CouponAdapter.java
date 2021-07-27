package com.capi.ecomshoppingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Model.Coupon;
import com.capi.ecomshoppingapp.R;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {
    private Context context;
    private List<Coupon> couponList;

    public CouponAdapter(Context context, List<Coupon> couponList) {
        this.context = context;
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.coupon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String couponCode = couponList.get(position).getCouponCode();
        final String couponPrice = couponList.get(position).getCouponPrice();

        String currency = context.getResources().getString(R.string.currency);

        holder.coupon_txt.setText("Use Coupon Code "
                + couponCode
                + " to get a Discount of "
                + currency
                + couponPrice);
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView coupon_txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            coupon_txt = itemView.findViewById(R.id.coupon_txt);
        }
    }
}
