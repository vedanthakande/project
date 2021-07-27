package com.capi.ecomshoppingapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capi.ecomshoppingapp.Model.SliderItem;
import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.admin.Adapter.BannerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminBannerActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    List<SliderItem> sliderItemList;
    BannerAdapter adapter;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_banner);

        recyclerView = findViewById(R.id.banners_recycler);
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminBannerActivity.this, AdminMainActivity.class));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sliderItemList = new ArrayList<>();
        adapter = new BannerAdapter(AdminBannerActivity.this,sliderItemList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminBannerActivity.this, AddBannerActivity.class));
            }
        });

        getBanners();
    }

    private void getBanners()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Banners");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SliderItem sliderItem = dataSnapshot.getValue(SliderItem.class);
                    sliderItemList.add(sliderItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}