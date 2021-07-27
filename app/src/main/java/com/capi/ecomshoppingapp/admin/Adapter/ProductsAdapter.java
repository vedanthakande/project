package com.capi.ecomshoppingapp.admin.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capi.ecomshoppingapp.admin.AddProductsActivity;
import com.capi.ecomshoppingapp.Model.Products;
import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder>
{
    Context context;
    List<Products> productsList;

    public ProductsAdapter(Context context, List<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.name.setText(productsList.get(position).getProductName());
        holder.price.setText(productsList.get(position).getProductPrice());

        holder.discount.setText(productsList.get(position).getProductDiscount());
        holder.percentage.setText(productsList.get(position).getProductPercent());

        Glide.with(context).load(productsList.get(position).getProductImage()).into(holder.image);

        if (productsList.get(position).getDiscounted().equals("true")){
            holder.discountcurrency.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            holder.discount.setVisibility(View.VISIBLE);
            holder.percentage.setVisibility(View.VISIBLE);
        }
        else {
            holder.discountcurrency.setVisibility(View.INVISIBLE);
            holder.divider.setVisibility(View.INVISIBLE);
            holder.discount.setVisibility(View.INVISIBLE);
            holder.percentage.setVisibility(View.INVISIBLE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                Button deleteBtn = dialog.findViewById(R.id.delete_btn);
                Button cancelBtn = dialog.findViewById(R.id.cancel_btn);

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        final Dialog loading = new Dialog(context);
                        loading.setContentView(R.layout.loading_dialog);
                        loading.show();
                        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Cart");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                {
                                    dataSnapshot.child("Products")
                                            .child(productsList.get(position).getProductId()).getRef().removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Likes");
                                                        databaseReference.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for (DataSnapshot ds : snapshot.getChildren())
                                                                {
                                                                    ds.child(productsList.get(position).getProductId()).getRef().removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    FirebaseDatabase.getInstance().getReference("Products")
                                                                                            .child(productsList.get(position).getProductId()).removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful())
                                                                                                    {
                                                                                                        loading.dismiss();
                                                                                                        Toast.makeText(context, "Product Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });


                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddProductsActivity.class);
                intent.putExtra("id", productsList.get(position).getProductId());
                intent.putExtra("mode", "edit");
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView name;
        TextView price;
        TextView discount;
        TextView percentage;
        TextView discountcurrency;
        TextView product_currency;
        ImageView delete;
        ImageView edit;
        RelativeLayout divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.product_image);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            discount = itemView.findViewById(R.id.product_before_discount_price);
            percentage = itemView.findViewById(R.id.product_discount_ratio);
            delete = itemView.findViewById(R.id.delete_product);
            edit = itemView.findViewById(R.id.edit_product);
            divider = itemView.findViewById(R.id.product_discount_divider);
            discountcurrency = itemView.findViewById(R.id.product_currency_discounted);
            product_currency = itemView.findViewById(R.id.product_currency);
        }
    }
}
