package com.capi.ecomshoppingapp.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capi.ecomshoppingapp.Model.OrderProduct;
import com.capi.ecomshoppingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {
    private Context context;
    private List<OrderProduct> orderProductList;

    public OrderDetailsAdapter(Context context, List<OrderProduct> orderProductList) {
        this.context = context;
        this.orderProductList = orderProductList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_order_products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.orderName.setText(orderProductList.get(position).getProductName());
        holder.orderPrice.setText(orderProductList.get(position).getProductPrice());
        holder.quantity.setText("x"+orderProductList.get(position).getProductQuantity());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products")
                .child(orderProductList.get(position).getProductId())
                .child("productImage");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.getValue(String.class);
                Glide.with(context).load(image).into(holder.orderImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView orderImage;
        TextView orderName;
        TextView orderPrice;
        TextView quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderImage = itemView.findViewById(R.id.order_product_image);
            orderName = itemView.findViewById(R.id.order_product_name);
            orderPrice = itemView.findViewById(R.id.order_product_price);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }
}
