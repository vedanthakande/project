package com.capi.ecomshoppingapp.address;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Adapter.AddressAdapter;
import com.capi.ecomshoppingapp.Model.Address;
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

public class AddressActivity extends AppCompatActivity {
    RecyclerView addressRecycler;
    List<Address> addressList;
    AddressAdapter addressAdapter;
    Button addBtn;
    ImageView back;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        addressRecycler = findViewById(R.id.addressRecycler);
        addBtn = findViewById(R.id.add_address_btn);
        back = findViewById(R.id.back_btn);

        user = FirebaseAuth.getInstance().getCurrentUser();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                intent.putExtra("mode", "add");
                intent.putExtra("type", "profile");
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddressActivity.this);
        addressRecycler.setLayoutManager(layoutManager);

        addressList = new ArrayList<>();

        addressAdapter = new AddressAdapter(AddressActivity.this, addressList, "address", null);
        addressRecycler.setAdapter(addressAdapter);

        getAddressCount();
    }

    private void getAddressCount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Address")
                .child(user.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                    intent.putExtra("mode", "add");
                    startActivity(intent);
                } else {
                    getAddress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAddress() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Address")
                .child(user.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addressList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Address address = dataSnapshot.getValue(Address.class);
                    addressList.add(address);
                }
                addressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
