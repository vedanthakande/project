package com.capi.ecomshoppingapp.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.capi.ecomshoppingapp.Adapter.OrderDetailsAdapter;
import com.capi.ecomshoppingapp.Model.Address;
import com.capi.ecomshoppingapp.Model.Order;
import com.capi.ecomshoppingapp.Model.OrderProduct;
import com.capi.ecomshoppingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity
{
    RecyclerView productRecycler;
    List<OrderProduct> orderProductList;
    OrderDetailsAdapter orderDetailsAdapter;
    ImageView back;

    FirebaseUser user;

    TextView shipping_date, shipping_phone, shipping_address, order_payment_items, order_payment_subtotal, order_payment_shipping;
    TextView order_payment_total, order_payment_total_currency, order_payment_subtotal_currency, order_payment_shipping_currency, order_payment_method;

    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

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

        orderId = getIntent().getStringExtra("orderId");

        user = FirebaseAuth.getInstance().getCurrentUser();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderDetailsActivity.this);
        productRecycler.setLayoutManager(layoutManager);

        orderProductList = new ArrayList<>();

        orderDetailsAdapter = new OrderDetailsAdapter(OrderDetailsActivity.this, orderProductList);
        productRecycler.setAdapter(orderDetailsAdapter);

        getOrderDetails();
    }

    private void getOrderDetails()
    {
        final DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference("Orders")
                .child(user.getUid())
                .child(orderId);

        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                shipping_date.setText(order.getOrderDate());
                order_payment_subtotal.setText(order.getSubtotalPrice());
                order_payment_shipping.setText(order.getShippingPrice());
                order_payment_total.setText(order.getTotalPrice());
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
                        .child(user.getUid())
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
