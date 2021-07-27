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
import com.capi.ecomshoppingapp.Model.Notification;
import com.capi.ecomshoppingapp.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>
{
    Context context;
    List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(notificationList.get(position).getTitle());
        holder.description.setText(notificationList.get(position).getText());
        holder.date.setText(notificationList.get(position).getDate());
        if (notificationList.get(position).getType()==1)
        {
            holder.image.setVisibility(View.VISIBLE);
            holder.imageOffer.setVisibility(View.INVISIBLE);
            holder.imageTransaction.setVisibility(View.INVISIBLE);
            Glide.with(context).load(notificationList.get(position).getImage()).into(holder.image);
        }
        else if (notificationList.get(position).getType()==2)
        {
            holder.image.setVisibility(View.INVISIBLE);
            holder.imageOffer.setVisibility(View.VISIBLE);
            holder.imageTransaction.setVisibility(View.INVISIBLE);
        }
        else if (notificationList.get(position).getType()==3)
        {
            holder.image.setVisibility(View.INVISIBLE);
            holder.imageOffer.setVisibility(View.INVISIBLE);
            holder.imageTransaction.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        ImageView imageOffer;
        ImageView imageTransaction;
        TextView title;
        TextView description;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            imageOffer = itemView.findViewById(R.id.image_offer);
            imageTransaction = itemView.findViewById(R.id.image_transaction);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.notification_txt);
            date = itemView.findViewById(R.id.date);
        }
    }
}
