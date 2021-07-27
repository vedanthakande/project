package com.capi.ecomshoppingapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capi.ecomshoppingapp.Adapter.OrderDetailsAdapter;
import com.capi.ecomshoppingapp.Model.Address;
import com.capi.ecomshoppingapp.Model.Order;
import com.capi.ecomshoppingapp.Model.OrderProduct;
import com.capi.ecomshoppingapp.Model.User;
import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderDetailsActivity extends AppCompatActivity
{
    RecyclerView productRecycler;
    List<OrderProduct> orderProductList;
    OrderDetailsAdapter orderDetailsAdapter;
    ImageView back;
    ImageView user_image;
    Button change_status_btn;

    TextView shipping_date, shipping_phone, shipping_address, order_payment_items, order_payment_subtotal, order_payment_shipping, user_name, user_email;
    TextView order_payment_total, order_payment_total_currency, order_payment_subtotal_currency, order_payment_shipping_currency, order_payment_method, order_payment_status;

    String orderId;
    String userId;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_details);

        productRecycler = findViewById(R.id.orderProductsRecycler);
        back = findViewById(R.id.back_btn);
        shipping_date = findViewById(R.id.shipping_date);
        shipping_phone = findViewById(R.id.shipping_phone);
        shipping_address = findViewById(R.id.shipping_address);
        order_payment_items = findViewById(R.id.order_payment_items);
        order_payment_subtotal = findViewById(R.id.order_payment_subtotal);
        order_payment_shipping = findViewById(R.id.order_payment_shipping);
        order_payment_total = findViewById(R.id.order_payment_total);
        order_payment_total_currency = findViewById(R.id.order_payment_total_currency);
        order_payment_subtotal_currency = findViewById(R.id.order_payment_currency);
        order_payment_shipping_currency = findViewById(R.id.order_payment_shipping_currency);
        order_payment_method = findViewById(R.id.order_payment_method);
        user_image = findViewById(R.id.user_image);
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        order_payment_status = findViewById(R.id.order_payment_status);
        change_status_btn = findViewById(R.id.change_status_btn);

        orderId = getIntent().getStringExtra("orderId");
        userId = getIntent().getStringExtra("userId");

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(false);

        dialog.show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AdminOrderDetailsActivity.this);
        productRecycler.setLayoutManager(layoutManager);

        orderProductList = new ArrayList<>();

        orderDetailsAdapter = new OrderDetailsAdapter(AdminOrderDetailsActivity.this, orderProductList);
        productRecycler.setAdapter(orderDetailsAdapter);

        change_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog statusDialog = new Dialog(AdminOrderDetailsActivity.this);
                statusDialog.setContentView(R.layout.status_dialog);
                statusDialog.setCancelable(true);
                statusDialog.getWindow().setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                statusDialog.show();

                RelativeLayout pendingLayout = statusDialog.findViewById(R.id.pending_layout);
                RelativeLayout processingLayout = statusDialog.findViewById(R.id.processing_layout);
                RelativeLayout deliveredLayout = statusDialog.findViewById(R.id.delivered_layout);
                RelativeLayout cancelledLayout = statusDialog.findViewById(R.id.cancelled_layout);

                pendingLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Orders")
                                .child(userId)
                                .child(orderId)
                                .child("status")
                                .setValue("pending")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            statusDialog.dismiss();
                                            Toast.makeText(AdminOrderDetailsActivity.this, "Order Status Changed Successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                });

                processingLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Orders")
                                .child(userId)
                                .child(orderId)
                                .child("status")
                                .setValue("processing")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            statusDialog.dismiss();
                                            Toast.makeText(AdminOrderDetailsActivity.this, "Order Status Changed Successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                });

                deliveredLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Orders")
                                .child(userId)
                                .child(orderId)
                                .child("status")
                                .setValue("delivered")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            statusDialog.dismiss();
                                            Toast.makeText(AdminOrderDetailsActivity.this, "Order Status Changed Successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                });

                cancelledLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Orders")
                                .child(userId)
                                .child(orderId)
                                .child("status")
                                .setValue("cancelled")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            statusDialog.dismiss();
                                            Toast.makeText(AdminOrderDetailsActivity.this, "Order Status Changed Successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                });
            }
        });

        getOrderDetails();
    }

    private void getOrderDetails()
    {
        final DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference("Orders")
                .child(userId)
                .child(orderId);

        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                shipping_date.setText(order.getOrderDate());
                order_payment_subtotal.setText(order.getSubtotalPrice());
                order_payment_shipping.setText(order.getShippingPrice());
                order_payment_total.setText(order.getTotalPrice());
                order_payment_status.setText(order.getPaymentStatus());
                if (order.getPaymentMethod().equals("COD"))
                {
                    order_payment_method.setText("Cash On Delivery");
                }
                else if (order.getPaymentMethod().equals("RAZORPAY"))
                {
                    order_payment_method.setText("RazorPay");
                }
                String addressId = order.getAddress();

                DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("Address")
                        .child(userId)
                        .child(addressId);

                addressRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Address address = snapshot.getValue(Address.class);
                        shipping_phone.setText(address.getAddressPhone());
                        shipping_address.setText(address.getAddressStreet()
                                + " "
                                + address.getAddressState()
                                + ",\n"
                                + address.getAddressCity()
                                + ",\n"
                                + address.getAddressZipCode()
                                + " "
                                + address.getAddressCountry());

                        orderReference.child("Products")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        order_payment_items.setText("Items("+snapshot.getChildrenCount()+")");
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {
                                            OrderProduct orderProduct = dataSnapshot.getValue(OrderProduct.class);
                                            orderProductList.add(orderProduct);
                                        }
                                        orderDetailsAdapter.notifyDataSetChanged();

                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                                                .child(userId);

                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                User user = snapshot.getValue(User.class);
                                                user_name.setText(user.getFullname());
                                                user_email.setText(user.getEmail());
                                                Glide.with(AdminOrderDetailsActivity.this).load(user.getImageurl()).into(user_image);
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}