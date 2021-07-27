package com.capi.ecomshoppingapp.Adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Model.ProductVariation;
import com.capi.ecomshoppingapp.R;

import java.util.List;

public class VariationAdapter extends RecyclerView.Adapter<VariationAdapter.ViewHolder> {
    List<ProductVariation> variationList;
    Context context;

    public VariationAdapter(List<ProductVariation> variationList, Context context) {
        this.variationList = variationList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("variable-change"));

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.variation_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = variationList.get(position).getVariationName();
        holder.textView.setText("Select " + name);
        if (name.toLowerCase().equals("color")) {
            holder.recyclerView.setHasFixedSize(true);
            ColorAdapter colorAdapter = new ColorAdapter(context, variationList.get(position));
            colorAdapter.setHasStableIds(true);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(colorAdapter);
            colorAdapter.notifyDataSetChanged();
        } else {
            holder.recyclerView.setHasFixedSize(true);
            SizeAdapter sizeAdapter = new SizeAdapter(context, variationList.get(position));
            sizeAdapter.setHasStableIds(true);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(sizeAdapter);
            sizeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return variationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.select_variation_txt);
            recyclerView = itemView.findViewById(R.id.variation_recyclerView);
        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String value = intent.getStringExtra("value");
            Integer position = intent.getIntExtra("position", 0);
            String id = intent.getStringExtra("id");
            Intent intent1 = new Intent("variation-change");
            intent1.putExtra("value", value);
            intent1.putExtra("id", id);
            intent1.putExtra("position", position);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
        }
    };

}
