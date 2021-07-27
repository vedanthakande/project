package com.capi.ecomshoppingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity
{
    Button home;
    TextView orderDate, orderItems, orderId, subTotal, totalPrice, deliveryMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        home = findViewById(R.id.back_to_home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SuccessActivity.this, MainActivity.class));
            }
        });

        orderDate = findViewById(R.id.order_date);
        orderItems = findViewById(R.id.order_items_txt);
        orderId = findViewById(R.id.order_id);
        subTotal = findViewById(R.id.order_subtotal);
        totalPrice = findViewById(R.id.order_price);
        deliveryMsg = findViewById(R.id.order_delivery_msg);

        Intent intent = getIntent();
        orderDate.setText(intent.getStringExtra("orderDate"));
        orderItems.setText(intent.getStringExtra("orderItems"));
        orderId.setText(intent.getStringExtra("orderId"));
        subTotal.setText(intent.getStringExtra("subtotal"));
        totalPrice.setText(intent.getStringExtra("totalPrice"));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SuccessActivity.this, MainActivity.class));
    }
}
