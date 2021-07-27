package com.capi.ecomshoppingapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Model.StripeCardList;
import com.capi.ecomshoppingapp.R;

import java.util.List;

public class StripeCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<StripeCardList.CardData> dataModel;
    private int lastSelectedPosition = -1;
    private OnItemClick mCallback;

    public StripeCardsAdapter(Context context, List<StripeCardList.CardData> dataModel, OnItemClick mCallback) {
        this.context = context;
        this.dataModel = dataModel;
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_item, parent, false);
        vh = new MyViewHolder(view);


        return vh;

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        ((MyViewHolder) holder).rb_card.setChecked(dataModel.get(position).getDefaultCard());

        String cardlast = dataModel.get(position).getCard().getLast4();
        ((MyViewHolder) holder).text_card.setText("Card ending with " + cardlast);
        if (dataModel.get(position).getCard().getBrand().equalsIgnoreCase("mastercard")) {
            ((MyViewHolder) holder).card.setImageDrawable(context.getResources().getDrawable(R.drawable.mastercard));
        } else if (dataModel.get(position).getCard().getBrand().equalsIgnoreCase("visa")) {
            ((MyViewHolder) holder).card.setImageDrawable(context.getResources().getDrawable(R.drawable.visa));
        } else if (dataModel.get(position).getCard().getBrand().equalsIgnoreCase("amex")) {
            ((MyViewHolder) holder).card.setImageDrawable(context.getResources().getDrawable(R.drawable.americanexpress));
        } else if (dataModel.get(position).getCard().getBrand().equalsIgnoreCase("discover")) {
            ((MyViewHolder) holder).card.setImageDrawable(context.getResources().getDrawable(R.drawable.discover));
        } else if (dataModel.get(position).getCard().getBrand().equalsIgnoreCase("jcb")) {
            ((MyViewHolder) holder).card.setImageDrawable(context.getResources().getDrawable(R.drawable.jcb));
        } else if (dataModel.get(position).getCard().getBrand().equalsIgnoreCase("unionpay")) {
            ((MyViewHolder) holder).card.setImageDrawable(context.getResources().getDrawable(R.drawable.unionpay));
        } else {
            ((MyViewHolder) holder).card.setImageDrawable(context.getResources().getDrawable(R.drawable.creditcard));
        }
        ((MyViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = position;
                mCallback.onClick(lastSelectedPosition);
                dataModel.get(lastSelectedPosition).setDefaultCard(true);
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text_card;
        ImageView card;
        RadioButton rb_card;

        public MyViewHolder(@NonNull View view) {
            super(view);


            this.text_card = view.findViewById(R.id.tv_card_number);
            this.rb_card = view.findViewById(R.id.rb_card);
            this.card = view.findViewById(R.id.card);
        }
    }

    public interface OnItemClick {
        void onClick(int value);
    }
}

