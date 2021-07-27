package com.capi.ecomshoppingapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.capi.ecomshoppingapp.Fragment.CurrencyFragment;
import com.capi.ecomshoppingapp.Fragment.DeliveryFragment;
import com.capi.ecomshoppingapp.Fragment.PaymentFragment;
import com.capi.ecomshoppingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity
{
    ConstraintLayout currency, delivery, payment;
    ConstraintLayout fragment;
    ImageView back;

    TextView currencyTxt, deliveryTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currency = findViewById(R.id.currency_layout);
        delivery = findViewById(R.id.delivery_layout);
        payment = findViewById(R.id.payment_layout);
        fragment = findViewById(R.id.fragment_container);
        currencyTxt = findViewById(R.id.currency);
        deliveryTxt = findViewById(R.id.delivery);
        back = findViewById(R.id.back_btn);

        currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CurrencyFragment()).commit();
            }
        });
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DeliveryFragment()).commit();
            }
        });
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PaymentFragment()).commit();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment.getVisibility()==View.VISIBLE) {
                    fragment.setVisibility(View.GONE);
                }
                else {
                    startActivity(new Intent(SettingsActivity.this, AdminMainActivity.class));
                }
            }
        });

        checkValues();
    }

    private void checkValues()
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Settings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("currency").exists()) {
                        String currencytv = snapshot.child("currency").getValue(String.class);
                        currencyTxt.setText(currencytv);
                    }
                    if (snapshot.child("delivery").exists()) {
                        String deliveryTv = snapshot.child("delivery").getValue(String.class);
                        deliveryTxt.setText(deliveryTv);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fragment.getVisibility()==View.VISIBLE) {
            fragment.setVisibility(View.GONE);
        }
        else {
            startActivity(new Intent(SettingsActivity.this, AdminMainActivity.class));
        }
    }
}
