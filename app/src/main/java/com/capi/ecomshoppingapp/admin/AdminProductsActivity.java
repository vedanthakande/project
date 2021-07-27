package com.capi.ecomshoppingapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.capi.ecomshoppingapp.admin.Adapter.ProductsAdapter;
import com.capi.ecomshoppingapp.Model.Products;
import com.capi.ecomshoppingapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminProductsActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    List<Products> productsList;
    ProductsAdapter productsAdapter;
    ImageView back;
    EditText search;
    ConstraintLayout notFound;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_products);

        recyclerView = findViewById(R.id.products_recycler);
        FloatingActionButton fab = findViewById(R.id.add_fab);
        back = findViewById(R.id.back_btn);
        search = findViewById(R.id.search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminProductsActivity.this, ChooseCategoryActivity.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminProductsActivity.this, AdminMainActivity.class));
            }
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(false);

        dialog.show();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(AdminProductsActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);

        productsList = new ArrayList<>();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchProducts(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        readProducts();
    }

    private void searchProducts(String s)
    {
        Query query = FirebaseDatabase.getInstance().getReference("Products").orderByChild("productName")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Products product = snapshot.getValue(Products.class);
                    productsList.add(product);
                }
                productsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readProducts()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Products products = dataSnapshot.getValue(Products.class);
                    productsList.add(products);
                }

                productsAdapter = new ProductsAdapter(AdminProductsActivity.this, productsList);
                recyclerView.setAdapter(productsAdapter);
                productsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminProductsActivity.this, AdminMainActivity.class));
    }
}
