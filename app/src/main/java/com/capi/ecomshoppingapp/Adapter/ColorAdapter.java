package com.capi.ecomshoppingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Model.ProductVariation;
import com.capi.ecomshoppingapp.R;

import java.util.Arrays;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    Context context;
    List<String> colorList;
    ProductVariation variation;

    int checkedPosition = 0;

    public ColorAdapter(Context context, ProductVariation variation) {
        this.context = context;
        this.variation = variation;
        this.colorList = Arrays.asList(variation.getVariationOptions().split(","));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.color_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String code = colorList.get(position).toLowerCase();
        String color = getStringResourceByName(code);
        holder.color_image.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
        holder.dot.setVisibility(View.GONE);
        if (checkedPosition == position) {
            holder.dot.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.dot.setVisibility(View.VISIBLE);
                checkedPosition = position;
                notifyDataSetChanged();
                Intent intent = new Intent("variable-change");
                intent.putExtra("value", colorList.get(position));
                intent.putExtra("position", position);
                intent.putExtra("id", variation.getVariationId());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView color_image;
        ImageView dot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            color_image = itemView.findViewById(R.id.color_image);
            dot = itemView.findViewById(R.id.dot);
        }
    }

    private String getStringResourceByName(String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString.replace(" ", ""), "string", packageName);
        return context.getString(resId);
    }
}
