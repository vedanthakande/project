package com.capi.ecomshoppingapp.address;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capi.ecomshoppingapp.Model.Address;
import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddAddressActivity extends AppCompatActivity {

    Button saveBtn;
    TextView title;
    ImageView back;

    //Edit Text Fields
    EditText country;
    EditText firstName;
    EditText lastName;
    EditText city;
    EditText state;
    EditText zipCode;
    EditText street;
    EditText streetOpt;
    EditText phone;

    String mode;
    String type;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        type = intent.getStringExtra("type");

        title = findViewById(R.id.title_pg);
        saveBtn = findViewById(R.id.save_address_btn);
        back = findViewById(R.id.back_btn);

        country = findViewById(R.id.country_form);
        firstName = findViewById(R.id.firstname_form);
        lastName = findViewById(R.id.lastname_form);
        city = findViewById(R.id.city_form);
        state = findViewById(R.id.state_form);
        street = findViewById(R.id.street_form);
        streetOpt = findViewById(R.id.street2_form);
        phone = findViewById(R.id.phone_form);
        zipCode = findViewById(R.id.zipcode_form);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (mode.equals("edit")) {
            title.setText("Edit Address");
             final String addressId = intent.getStringExtra("addressId");

            DatabaseReference addressReference = FirebaseDatabase.getInstance().getReference("Address")
                    .child(user.getUid())
                    .child(addressId);

            addressReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Address address = snapshot.getValue(Address.class);

                    country.setText(address.getAddressCountry());
                    firstName.setText(address.getAddressFirstName());
                    lastName.setText(address.getAddressLastName());
                    city.setText(address.getAddressCity());
                    street.setText(address.getAddressStreet());
                    streetOpt.setText(address.getAddressStreetOpt());
                    state.setText(address.getAddressState());
                    zipCode.setText(address.getAddressZipCode());
                    phone.setText(address.getAddressPhone());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(country.getText().toString())) {
                        country.setError("Field is required!");
                    } else if (TextUtils.isEmpty(firstName.getText().toString())) {
                        firstName.setError("Field is required!");
                    } else if (TextUtils.isEmpty(lastName.getText().toString())) {
                        lastName.setError("Field is required!");
                    } else if (TextUtils.isEmpty(city.getText().toString())) {
                        city.setError("Field is required!");
                    } else if (TextUtils.isEmpty(street.getText().toString())) {
                        street.setError("Field is required!");
                    } else if (TextUtils.isEmpty(zipCode.getText().toString())) {
                        zipCode.setError("Field is required!");
                    } else if (TextUtils.isEmpty(phone.getText().toString())) {
                        phone.setError("Field is required!");
                    } else {
                        saveData(addressId);
                    }
                }
            });
        } else if (mode.equals("add")) {
            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            final String saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            final String saveCurrentTime = currentTime.format(calendar.getTime());

            final String addressId = saveCurrentDate + saveCurrentTime;

            title.setText("Add Address");
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (TextUtils.isEmpty(country.getText().toString())) {
                        country.setError("Field is required!");
                    } else if (TextUtils.isEmpty(firstName.getText().toString())) {
                        firstName.setError("Field is required!");
                    } else if (TextUtils.isEmpty(lastName.getText().toString())) {
                        lastName.setError("Field is required!");
                    } else if (TextUtils.isEmpty(city.getText().toString())) {
                        city.setError("Field is required!");
                    } else if (TextUtils.isEmpty(street.getText().toString())) {
                        street.setError("Field is required!");
                    } else if (TextUtils.isEmpty(zipCode.getText().toString())) {
                        zipCode.setError("Field is required!");
                    } else if (TextUtils.isEmpty(phone.getText().toString())) {
                        phone.setError("Field is required!");
                    } else {
                        addData(addressId);
                    }
                }
            });
        }


    }

    private void saveData(String id) {
        DatabaseReference addressReference = FirebaseDatabase.getInstance().getReference()
                .child("Address")
                .child(user.getUid())
                .child(id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("addressId", id);
        hashMap.put("addressCountry", country.getText().toString());
        hashMap.put("addressFirstName", firstName.getText().toString());
        hashMap.put("addressLastName", lastName.getText().toString());
        hashMap.put("addressStreet", street.getText().toString());
        hashMap.put("addressStreetOpt", streetOpt.getText().toString());
        hashMap.put("addressCity", city.getText().toString());
        hashMap.put("addressState", state.getText().toString());
        hashMap.put("addressZipCode", zipCode.getText().toString());
        hashMap.put("addressPhone", phone.getText().toString());

        addressReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddAddressActivity.this, "Address Updated Successfully!", Toast.LENGTH_SHORT).show();
                    if (type.equals("ship")){
                        Intent getIntent = getIntent();
                        Intent intent = new Intent(AddAddressActivity.this, ShiptoActivity.class);
                        intent.putExtra("mode", "ship");
                        intent.putExtra("items", getIntent.getStringExtra("items"));
                        intent.putExtra("subtotal", getIntent.getStringExtra("subtotal"));
                        intent.putExtra("shipping", getIntent.getStringExtra("shipping"));
                        intent.putExtra("total", getIntent.getStringExtra("total"));
                        startActivity(intent);
                    }
                    else {
                        finish();
                    }
                }
            }
        });
    }

    private void addData(final String id)
    {
        final DatabaseReference addressReference = FirebaseDatabase.getInstance().getReference()
                .child("Address")
                .child(user.getUid());
        addressReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("addressId", id);
                hashMap.put("addressCountry", country.getText().toString());
                hashMap.put("addressFirstName", firstName.getText().toString());
                hashMap.put("addressLastName", lastName.getText().toString());
                hashMap.put("addressStreet", street.getText().toString());
                hashMap.put("addressStreetOpt", streetOpt.getText().toString());
                hashMap.put("addressCity", city.getText().toString());
                hashMap.put("addressState", state.getText().toString());
                hashMap.put("addressZipCode", zipCode.getText().toString());
                hashMap.put("addressPhone", phone.getText().toString());
                if (snapshot.getChildrenCount() == 0) {
                    hashMap.put("selected", true);
                } else {
                    hashMap.put("selected", false);
                }
                addressReference.child(id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddAddressActivity.this, "Address Added Successfully!", Toast.LENGTH_SHORT).show();
                            if (type.equals("ship")){
                                Intent getIntent = getIntent();
                                Intent intent = new Intent(AddAddressActivity.this, ShiptoActivity.class);
                                intent.putExtra("mode", "ship");
                                intent.putExtra("items", getIntent.getStringExtra("items"));
                                intent.putExtra("subtotal", getIntent.getStringExtra("subtotal"));
                                intent.putExtra("shipping", getIntent.getStringExtra("shipping"));
                                intent.putExtra("total", getIntent.getStringExtra("total"));
                                startActivity(intent);
                            }
                            else {
                                finish();
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
