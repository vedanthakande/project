package com.capi.ecomshoppingapp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.capi.ecomshoppingapp.Model.PaymentMethod;
import com.capi.ecomshoppingapp.R;

import java.util.List;

public class PaymentAdapter extends BaseAdapter {
    Activity activity;
    List<PaymentMethod> list;
    LayoutInflater inflater;

    public PaymentAdapter(Activity activity, List<PaymentMethod> list) {
        this.activity = activity;
        this.list = list;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.payment_list_item, parent, false);

            holder = new ViewHolder();
            holder.checkBox = (RadioButton) convertView.findViewById(R.id.rb_card);
            holder.text = (TextView) convertView.findViewById(R.id.tv_card_number);
            holder.imageView = (ImageView) convertView.findViewById(R.id.card);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PaymentMethod model = list.get(position);

        holder.text.setText(model.getTitle());

        if (model.isSelected())
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        if (model.getImage() == 0)
            holder.imageView.setImageDrawable(activity.getResources().getDrawable(model.getImage()));
        else
            holder.imageView.setImageDrawable(activity.getResources().getDrawable(model.getImage()));

        return convertView;
    }

    public void updateRecords(List<PaymentMethod> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        RadioButton checkBox;
        ImageView imageView;
        TextView text;
    }
}
