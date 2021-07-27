package com.capi.ecomshoppingapp.admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Constants;
import com.capi.ecomshoppingapp.Model.Coupon;
import com.capi.ecomshoppingapp.R;

import com.capi.ecomshoppingapp.admin.AddCouponActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder>
{
    Context context;
    List<Coupon> couponList;

    public CouponAdapter(Context context, List<Coupon> couponList) {
        this.context = context;
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_coupon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String couponId = couponList.get(position).getCouponId();
        final String couponCode = couponList.get(position).getCouponCode();
        final String couponMin = couponList.get(position).getCouponMin();
        final String couponPrice = couponList.get(position).getCouponPrice();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Settings")
                .child("currency");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currency = snapshot.getValue(String.class);
                holder.couponDesc.setText("Use Coupon Code "
                        +couponCode
                        +" to get "+ currency
                        +couponPrice
                        + " Off on orders above "
                        +currency
                        +couponMin
                        +".");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddCouponActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("id", couponId);
                context.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Coupon")
                        .child(couponId);
                new Constants().showAlertDialog(databaseReference, context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView couponDesc;
        ImageView edit;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            couponDesc = itemView.findViewById(R.id.coupon_desc);
            edit = itemView.findViewById(R.id.edit_coupon);
            delete = itemView.findViewById(R.id.delete_coupon);
        }
    }
}
