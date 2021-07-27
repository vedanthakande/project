package com.capi.ecomshoppingapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.admin.Adapter.CouponAdapter;
import com.capi.ecomshoppingapp.Model.Coupon;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminCouponActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    List<Coupon> couponList;
    CouponAdapter couponAdapter;

    ImageView back;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        FloatingActionButton fab = findViewById(R.id.add_fab);
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminCouponActivity.this, AdminMainActivity.class));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCouponActivity.this, AddCouponActivity.class);
                intent.putExtra("mode", "add");
                startActivity(intent);
            }
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(false);

        dialog.show();

        recyclerView = findViewById(R.id.coupon_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        couponList = new ArrayList<>();
        couponAdapter = new CouponAdapter(AdminCouponActivity.this, couponList);
        recyclerView.setAdapter(couponAdapter);

        getCoupons();
    }

    private void getCoupons()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Coupon");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                couponList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Coupon coupon = dataSnapshot.getValue(Coupon.class);
                    couponList.add(coupon);
                }
                couponAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.dismiss();
    }
}