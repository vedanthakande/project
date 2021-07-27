package com.capi.ecomshoppingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Model.ProductVariation;
import com.capi.ecomshoppingapp.R;

import java.util.Arrays;
import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {
    Context context;
    List<String> sizeList;
    ProductVariation variation;

    private int checkedPosition = 0;

    public SizeAdapter(Context context, ProductVariation variation) {
        this.context = context;
        this.variation = variation;
        this.sizeList = Arrays.asList(variation.getVariationOptions().split(","));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.size_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.sizeName.setText(sizeList.get(position));
        holder.sizeLayout.setEnabled(true);
        if (checkedPosition == position) {
            holder.sizeLayout.setEnabled(false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.sizeLayout.setEnabled(false);
                checkedPosition = position;
                Intent intent = new Intent("variable-change");
                intent.putExtra("value", sizeList.get(position));
                intent.putExtra("position", position);
                intent.putExtra("id", variation.getVariationId());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return sizeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sizeName;
        ConstraintLayout sizeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sizeName = itemView.findViewById(R.id.size);
            sizeLayout = itemView.findViewById(R.id.sizeLayout);
        }
    }

    public String getSelected() {
        if (checkedPosition != -1) {
            return sizeList.get(checkedPosition);
        }
        return null;
    }
}
