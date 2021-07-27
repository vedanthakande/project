package com.capi.ecomshoppingapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.admin.SettingsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PaymentFragment extends Fragment {

    Switch cod, razorpay;
    Button save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        cod = view.findViewById(R.id.cod_switch);
        razorpay = view.findViewById(R.id.razorpay_switch);
        save = view.findViewById(R.id.save_btn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });

        cod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    FirebaseDatabase.getInstance().getReference("Settings")
                            .child("payment")
                            .child("cod")
                            .setValue("enabled");
                }
                if (!b)
                {
                    FirebaseDatabase.getInstance().getReference("Settings")
                            .child("payment")
                            .child("cod")
                            .setValue("disabled");
                }
            }
        });

        razorpay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    FirebaseDatabase.getInstance().getReference("Settings")
                            .child("payment")
                            .child("razorpay")
                            .setValue("enabled");
                }
                if (!b)
                {
                    FirebaseDatabase.getInstance().getReference("Settings")
                            .child("payment")
                            .child("razorpay")
                            .setValue("disabled");
                }
            }
        });

        getPaymentOptions();

        return view;
    }

    private void getPaymentOptions()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Settings")
                .child("payment");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("cod").getValue(String.class).equals("enabled")) {
                        cod.setChecked(true);
                    } else if (snapshot.child("cod").getValue(String.class).equals("disabled")) {
                        cod.setChecked(false);
                    }

                    if (snapshot.child("razorpay").getValue(String.class).equals("enabled")) {
                        razorpay.setChecked(true);
                    } else if (snapshot.child("razorpay").getValue(String.class).equals("disabled")) {
                        razorpay.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}