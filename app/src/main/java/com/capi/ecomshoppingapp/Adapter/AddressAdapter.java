package com.capi.ecomshoppingapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Model.Address;
import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.address.AddAddressActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    Context context;
    List<Address> addressList;
    String mode;
    int checkedPosition = 0;
    private StripeCardsAdapter.OnItemClick mCallback;

    public AddressAdapter(Context context, List<Address> addressList, String mode, StripeCardsAdapter.OnItemClick mCallback) {
        this.context = context;
        this.addressList = addressList;
        this.mode = mode;
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.select.setChecked(false);
        if (checkedPosition == position) {
            holder.select.setChecked(true);
        }
        if (mode.equals("ship")) {
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.INVISIBLE);
        }

        holder.addressArea.setText
                (addressList.get(position).getAddressStreet()
                        + ",\n"
                        + addressList.get(position).getAddressCity());

        holder.select.setClickable(false);

        holder.editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddAddressActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("addressId", addressList.get(position).getAddressId());
                context.startActivity(intent);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_dialog);
                dialog.setCancelable(true);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                Button delete = dialog.findViewById(R.id.delete_btn);
                Button cancel = dialog.findViewById(R.id.cancel_btn);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("Address")
                                .child(user.getUid())
                                .child(addressList.get(position).getAddressId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                Toast.makeText(context, "Address Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equals("ship")) {
                    holder.select.setChecked(true);
                    checkedPosition = position;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView addressArea;
        ImageView editAddress;
        ImageView deleteBtn;
        CheckBox select;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            addressArea = itemView.findViewById(R.id.address_area);
            editAddress = itemView.findViewById(R.id.edit_address_btn);
            deleteBtn = itemView.findViewById(R.id.delete_address_btn);
            select = itemView.findViewById(R.id.select_address_cb);
        }
    }

    public interface OnItemClick {
        void onClick(int value);
    }

    public String getSelectedAddressId() {
        return addressList.get(checkedPosition).getAddressId();
    }
}
