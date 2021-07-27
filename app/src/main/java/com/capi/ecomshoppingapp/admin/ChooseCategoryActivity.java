package com.capi.ecomshoppingapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.admin.Adapter.ChooseCategoryAdapter;
import com.capi.ecomshoppingapp.Model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChooseCategoryActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    List<Category> categoryList;
    ChooseCategoryAdapter chooseCategoryAdapter;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        back = findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseCategoryActivity.this, AdminProductsActivity.class));
            }
        });
        recyclerView = findViewById(R.id.choose_category_recycler);

        LinearLayoutManager linearLayoutManager = new GridLayoutManager(ChooseCategoryActivity.this,3);
        recyclerView.setLayoutManager(linearLayoutManager);

        categoryList = new ArrayList<>();
        chooseCategoryAdapter = new ChooseCategoryAdapter(ChooseCategoryActivity.this, categoryList);
        recyclerView.setAdapter(chooseCategoryAdapter);

        readCategories();
    }

    private void readCategories()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Category category = dataSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }

                chooseCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
