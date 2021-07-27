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
import com.capi.ecomshoppingapp.admin.Adapter.CategoryAdapter;
import com.capi.ecomshoppingapp.Model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoriesActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    List<Category> categoryList;
    CategoryAdapter categoryAdapter;

    Dialog dialog;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        FloatingActionButton fab = findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoriesActivity.this, AddCategoryActivity.class);
                intent.putExtra("mode", "add");
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.category_recycler);
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminCategoriesActivity.this, AdminMainActivity.class));
            }
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(false);

        dialog.show();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdminCategoriesActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(AdminCategoriesActivity.this, categoryList);
        recyclerView.setAdapter(categoryAdapter);

        readCategories();
    }

    private void readCategories()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Category category = dataSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }

                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminCategoriesActivity.this, AdminMainActivity.class));
    }
}
