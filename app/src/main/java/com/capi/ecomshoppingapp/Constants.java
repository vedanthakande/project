package com.capi.ecomshoppingapp;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.capi.ecomshoppingapp.Model.Order;
import com.capi.ecomshoppingapp.Model.PaymentMethod;
import com.capi.ecomshoppingapp.admin.Adapter.OrderAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final String DELIVERY_PRICE = "10";
    //public static final String BASE_URL = "http://localhost/admin/product.php";
    public static final String BASE_URL = "http://192.168.18.152/dashboard/";
    public static final String CURRENCY_CODE = "INR";
    public static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51HY9z4HwEB0YSQZumyHWuNhTyIEhy3qMWAK1YnUD6u6h4iAhcEBuiGyg6ryIa0SkICYg96tng8ahC6ZHdzV9sMYF00k71LCa6c";
    public static final String STRIPE_SECRET_KEY = "sk_test_51HY9z4HwEB0YSQZuMwGHavWcsELyvjKcPoIH2tX7hrsnonVN4SLxI43cXiFkiCkb0tfY7BcgjWO4xvUmOSty7WmI00bTQtu6uo";

    public void getOrders(final OrderAdapter orderAdapter, final List<Order> orderList, final String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Orders");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Order order = ds.getValue(Order.class);
                        if (order.getStatus().equals(status)) {
                            orderList.add(order);
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static List<PaymentMethod> paymentMethodList() {
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod("cod", "Cash On Delivery", R.drawable.cod, false));
        paymentMethodList.add(new PaymentMethod("razorpay", "RazorPay", R.drawable.creditcard, false));
        paymentMethodList.add(new PaymentMethod("stripe", "Stripe", R.drawable.creditcard, false));

        return paymentMethodList;
    }

    public void showAlertDialog(final DatabaseReference databaseReference, final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button delete = dialog.findViewById(R.id.delete_btn);
        Button cancel = dialog.findViewById(R.id.cancel_btn);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "deleted!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
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
}
