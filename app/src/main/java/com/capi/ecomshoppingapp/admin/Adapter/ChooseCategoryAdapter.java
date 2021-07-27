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

import com.bumptech.glide.Glide;
import com.capi.ecomshoppingapp.admin.AddProductsActivity;
import com.capi.ecomshoppingapp.Model.Category;
import com.capi.ecomshoppingapp.R;

import java.util.List;

public class ChooseCategoryAdapter extends RecyclerView.Adapter<ChooseCategoryAdapter.ViewHolder>
{
    Context context;
    List<Category> categoryList;

    public ChooseCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context).load(categoryList.get(position).getCategoryImage()).into(holder.image);
        holder.name.setText(categoryList.get(position).getCategoryName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddProductsActivity.class);
                intent.putExtra("mode", "add");
                intent.putExtra("categoryName", categoryList.get(position).getCategoryName());
                intent.putExtra("categoryId", categoryList.get(position).getCategoryId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.choose_category_image);
            name = itemView.findViewById(R.id.choose_category_name);
        }
    }
}
