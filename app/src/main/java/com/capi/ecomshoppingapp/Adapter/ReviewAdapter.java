package com.capi.ecomshoppingapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capi.ecomshoppingapp.Model.Review;
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

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    Context context;
    List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.date.setText(reviewList.get(position).getDate());
        holder.review.setText(reviewList.get(position).getReview());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(reviewList.get(position).getUserId());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageurl = snapshot.child("imageurl").getValue(String.class);
                String fullname = snapshot.child("fullname").getValue(String.class);

                Glide.with(context).load(imageurl).into(holder.image);
                holder.user.setText(fullname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (reviewList.get(position).getUserId().equals(user.getUid())) {
                holder.delete.setVisibility(View.VISIBLE);
            } else {
                holder.delete.setVisibility(View.INVISIBLE);
            }
        }

        if (reviewList.get(position).getUserId().equals(user.getUid()))
        {
            holder.delete.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.delete.setVisibility(View.INVISIBLE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_dialog);
                dialog.setCancelable(true);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                Button delete = dialog.findViewById(R.id.delete_btn);
                Button cancel = dialog.findViewById(R.id.cancel_btn);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("Ratings")
                                .child(reviewList.get(position).getProductId())
                                .child(user.getUid())
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                Toast.makeText(context, "Review Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        holder.ratingBar.setRating(reviewList.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView delete;
        TextView user;
        TextView review;
        TextView date;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.rating_image);
            user = itemView.findViewById(R.id.rating_name);
            review = itemView.findViewById(R.id.rating_description);
            date = itemView.findViewById(R.id.rating_date);
            delete = itemView.findViewById(R.id.delete_rating);
            ratingBar = itemView.findViewById(R.id.star_container);
        }
    }
}
