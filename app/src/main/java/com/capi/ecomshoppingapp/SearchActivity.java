package com.capi.ecomshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.capi.ecomshoppingapp.Adapter.ProductsAdapter;
import com.capi.ecomshoppingapp.Model.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
{
    EditText search_et;
    TextView resultCount;
    ImageView back;

    List<Products> productsList;
    ProductsAdapter productsAdapter;
    RecyclerView searchRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_et = findViewById(R.id.search_edittext);
        resultCount = findViewById(R.id.result_count);
        searchRecycler = findViewById(R.id.search_recycler_view);
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchRecycler.setHasFixedSize(true);
        searchRecycler.setLayoutManager(new GridLayoutManager(SearchActivity.this,2));

        productsList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(SearchActivity.this, productsList);
        searchRecycler.setAdapter(productsAdapter);

        readUsers();
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchProducts(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchProducts(String s)
    {
        Query query = FirebaseDatabase.getInstance().getReference("Products").orderByChild("productSearch")
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
                resultCount.setText(productsAdapter.getItemCount()+" Results");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Products products = snapshot.getValue(Products.class);
                    productsList.add(products);
                }

                productsAdapter.notifyDataSetChanged();
                resultCount.setText(productsAdapter.getItemCount()+" Results");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}