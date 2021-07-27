package com.capi.ecomshoppingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Model.Order;
import com.capi.ecomshoppingapp.order.OrderDetailsActivity;
import com.capi.ecomshoppingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>
{
    Context context;
    List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderAdapter.ViewHolder holder, final int position) {
        holder.orderId.setText(orderList.get(position).getOrderCode());
        holder.orderDate.setText(orderList.get(position).getOrderDate());
        holder.orderPrice.setText(orderList.get(position).getTotalPrice());
        holder.orderStatus.setText(orderList.get(position).getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                intent.putExtra("userId", orderList.get(position).getUserId());
                context.startActivity(intent);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders")
                .child(user.getUid())
                .child(orderList.get(position).getOrderId())
                .child("Products");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long items = snapshot.getChildrenCount();
                holder.orderItems.setText(String.valueOf(items));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderId;
        TextView orderDate;
        TextView orderItems;
        TextView orderPrice;
        TextView orderStatus;
        TextView currency;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderItems = itemView.findViewById(R.id.order_items);
            orderPrice = itemView.findViewById(R.id.order_price);
            orderStatus = itemView.findViewById(R.id.order_status);
            currency = itemView.findViewById(R.id.order_price_currency);
        }
    }
}
