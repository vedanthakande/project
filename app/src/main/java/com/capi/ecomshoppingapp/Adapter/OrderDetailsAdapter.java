package com.capi.ecomshoppingapp.Adapter;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        View view = LayoutInflater.from(context).inflate(R.layout.order_products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.orderName.setText(orderProductList.get(position).getProductName());
        List<String> selectedVars = orderProductList.get(position).getProductVariations();
        if (selectedVars != null && selectedVars.size() != 1) {
            holder.orderName.append("(");
            for (String s : selectedVars) {
                holder.orderName.append(s);
                if (!s.equals(selectedVars.get(selectedVars.size() - 1))) {
                    holder.orderName.append(",");
                } else {
                    holder.orderName.append(")");
                }
            }
        }
        holder.orderPrice.setText(orderProductList.get(position).getProductPrice());
        holder.quantity.setText("x" + orderProductList.get(position).getProductQuantity());
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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.wishlist.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(user.getUid())
                            .child(orderProductList.get(position).getProductId())
                            .child("id")
                            .setValue(orderProductList.get(position).getProductId());
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(user.getUid())
                            .child(orderProductList.get(position).getProductId()).removeValue();
                }
            }
        });

        isLiked(orderProductList.get(position).getProductId(), holder.wishlist);
    }

    @Override
    public int getItemCount() {
        return orderProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView orderImage;
        ImageView wishlist;
        TextView orderName;
        TextView orderPrice;
        TextView quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderImage = itemView.findViewById(R.id.order_product_image);
            orderName = itemView.findViewById(R.id.order_product_name);
            orderPrice = itemView.findViewById(R.id.order_product_price);
            wishlist = itemView.findViewById(R.id.wishlist);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }

    private void isLiked(final String productId, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(productId).exists()) {
                    imageView.setImageResource(R.drawable.ic_love_fill);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_fav);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
