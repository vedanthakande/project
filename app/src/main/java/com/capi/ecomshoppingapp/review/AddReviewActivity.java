package com.capi.ecomshoppingapp.review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddReviewActivity extends AppCompatActivity
{
    LinearLayout rateNowContainer;
    Button submit;
    EditText editText;
    FirebaseUser user;

    String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        rateNowContainer = findViewById(R.id.star_container);
        submit = findViewById(R.id.submit_review_btn);
        editText = findViewById(R.id.write_review_edittext);

        productId = getIntent().getStringExtra("productId");

        user = FirebaseAuth.getInstance().getCurrentUser();

        for (int x = 0;x < rateNowContainer.getChildCount(); x++)
        {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setRating(starPosition);

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (starPosition>=1)
                            {
                                submit.setVisibility(View.INVISIBLE);

                                Calendar calendar = Calendar.getInstance();

                                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                                final String saveCurrentDate = currentDate.format(calendar.getTime());

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ratings")
                                        .child(productId)
                                        .child(user.getUid());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("userId", user.getUid());
                                hashMap.put("productId", productId);
                                hashMap.put("rating", starPosition+1);
                                hashMap.put("review", editText.getText().toString());
                                hashMap.put("date", saveCurrentDate);

                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            submit.setVisibility(View.VISIBLE);
                                            Toast.makeText(AddReviewActivity.this, "Rating Added Successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(AddReviewActivity.this, "PLease select your rating!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }

    private void setRating(int starPosition)
    {
        if (starPosition > -1)
        {
            for (int x = 0; x < rateNowContainer.getChildCount();x++)
            {
                ImageView starBtn = (ImageView)rateNowContainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#E1DEDE")));
                if (x <= starPosition)
                {
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFC833")));
                }
            }
        }
    }
}
