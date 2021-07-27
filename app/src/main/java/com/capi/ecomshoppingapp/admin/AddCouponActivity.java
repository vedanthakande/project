package com.capi.ecomshoppingapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capi.ecomshoppingapp.Model.Coupon;
import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddCouponActivity extends AppCompatActivity
{
    EditText code, min, price;
    TextView codeTxt;
    Button save;

    String mode;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coupon);

        code = findViewById(R.id.coupon_code);
        min = findViewById(R.id.coupon_min);
        price = findViewById(R.id.coupon_price);
        save = findViewById(R.id.save_btn);
        codeTxt = findViewById(R.id.coupon_code_not);

        mode = getIntent().getStringExtra("mode");

        if (mode.equals("edit"))
        {
            id = getIntent().getStringExtra("id");
            codeTxt.setText(id);
            code.setVisibility(View.INVISIBLE);
            codeTxt.setVisibility(View.VISIBLE);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Coupon")
                    .child(id);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Coupon coupon = snapshot.getValue(Coupon.class);
                    code.setText(coupon.getCouponCode());
                    min.setText(coupon.getCouponMin());
                    price.setText(coupon.getCouponPrice());

                    save.setText("Save Coupon");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            code.setVisibility(View.VISIBLE);
            codeTxt.setVisibility(View.GONE);
            save.setText("Add Coupon");
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(code.getText().toString()))
                {
                    code.setError("Field is Required!");
                }
                else if (TextUtils.isEmpty(price.getText().toString()))
                {
                    price.setError("Field is Required!");
                }
                else if (TextUtils.isEmpty(min.getText().toString()))
                {
                    min.setError("Field is Required!");
                }
                else {
                    addCoupon();
                }
             }
        });
    }

    private void addCoupon()
    {
        DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference("Coupon");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("couponCode", code.getText().toString().toUpperCase());
        hashMap.put("couponMin", min.getText().toString());
        hashMap.put("couponPrice", price.getText().toString());

        if (mode.equals("add"))
        {
            hashMap.put("couponId", code.getText().toString().toUpperCase());
            couponRef.child(code.getText().toString().toUpperCase()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(AddCouponActivity.this, "Coupon Added Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
        else if (mode.equals("edit"))
        {
            hashMap.put("couponId", id);
            couponRef.child(id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(AddCouponActivity.this, "Coupon Updated Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }
}