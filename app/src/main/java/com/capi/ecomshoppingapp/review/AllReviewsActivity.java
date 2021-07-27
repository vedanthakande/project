package com.capi.ecomshoppingapp.review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.capi.ecomshoppingapp.Adapter.ReviewAdapter;
import com.capi.ecomshoppingapp.Model.Review;
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

public class AllReviewsActivity extends AppCompatActivity {

    Button addBtn;
    TextView no_review, title;

    RecyclerView rating_recycler;
    List<Review> reviewList;
    ReviewAdapter reviewAdapter;

    ImageView back;

    String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);


        addBtn = findViewById(R.id.add_review_btn);
        rating_recycler = findViewById(R.id.ratings_recycler);
        back = findViewById(R.id.back_btn);
        no_review = findViewById(R.id.no_review);
        title = findViewById(R.id.title);

        productId = getIntent().getStringExtra("productId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ratings")
                    .child(productId)
                    .child(user.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        addBtn.setVisibility(View.GONE);
                    } else {
                        addBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            addBtn.setVisibility(View.GONE);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllReviewsActivity.this, AddReviewActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AllReviewsActivity.this);
        rating_recycler.setLayoutManager(layoutManager);

        reviewList = new ArrayList<>();

        reviewAdapter = new ReviewAdapter(AllReviewsActivity.this, reviewList);
        rating_recycler.setAdapter(reviewAdapter);

        getRating();
    }

    private void getRating() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Ratings")
                .child(productId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    reviewList.add(review);
                }
                reviewAdapter.notifyDataSetChanged();
                if (reviewAdapter.getItemCount() == 0) {
                    no_review.setVisibility(View.VISIBLE);
                } else {
                    no_review.setVisibility(View.GONE);
                }
                title.setText(reviewAdapter.getItemCount() + " Reviews");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
