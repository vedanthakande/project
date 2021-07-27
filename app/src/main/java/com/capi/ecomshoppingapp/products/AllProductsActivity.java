package com.capi.ecomshoppingapp.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.capi.ecomshoppingapp.Adapter.ProductsAdapter;
import com.capi.ecomshoppingapp.Model.Products;
import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.SearchActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllProductsActivity extends AppCompatActivity
{
    TextView categoryTitle;
    TextView no_products;
    RecyclerView allRecyclerView;
    List<Products> productsList;
    ProductsAdapter productsAdapter;
    ImageView back;
    ImageView search_btn;

    String title;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        Intent intent = getIntent();
        id = intent.getStringExtra("categoryId");
        title = intent.getStringExtra("categoryName");

        categoryTitle = findViewById(R.id.categoryName);
        allRecyclerView = findViewById(R.id.allRecyclerView);
        back = findViewById(R.id.back_btn);
        search_btn = findViewById(R.id.search_btn);
        no_products = findViewById(R.id.no_products);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllProductsActivity.this, SearchActivity.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        categoryTitle.setText(title);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(AllProductsActivity.this,2);
        allRecyclerView.setLayoutManager(layoutManager);
        productsList = new ArrayList<>();

        productsAdapter = new ProductsAdapter(AllProductsActivity.this,productsList);
        allRecyclerView.setAdapter(productsAdapter);

        readProducts();
    }

    private void readProducts()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Products products = dataSnapshot.getValue(Products.class);
                    if (products.getCategoryId().equals(id))
                    {
                        productsList.add(products);
                    }
                }
                productsAdapter.notifyDataSetChanged();
                if (productsAdapter.getItemCount()==0){
                    no_products.setVisibility(View.VISIBLE);
                }
                else {
                    no_products.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
