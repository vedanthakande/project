package com.capi.ecomshoppingapp.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.capi.ecomshoppingapp.Adapter.CategoryAdapter;
import com.capi.ecomshoppingapp.Model.Category;
import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.SearchActivity;
import com.capi.ecomshoppingapp.WishlistActivity;
import com.capi.ecomshoppingapp.notification.NotificationActivity;
import com.capi.ecomshoppingapp.signup.LoginActivity;
import com.capi.ecomshoppingapp.signup.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment
{
    RecyclerView category;
    List<Category> mancategoryList;
    CategoryAdapter categoryAdapter;
    ImageView wishlist;
    ImageView notification;
    EditText editText;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private Dialog signInDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        category = view.findViewById(R.id.explore_category_recyclerview);
        wishlist = view.findViewById(R.id.wishlist);
        notification = view.findViewById(R.id.notification);
        editText = view.findViewById(R.id.search_edittext);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        signInDialog=new Dialog(getContext());
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button signInDialogBtn=signInDialog.findViewById(R.id.sign_in_btn);
        Button signUpDialogBtn=signInDialog.findViewById(R.id.sign_up_btn);

        signInDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInDialog.dismiss();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
        signUpDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInDialog.dismiss();
                startActivity(new Intent(getContext(), RegisterActivity.class));
            }
        });

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user!=null)
                {
                    startActivity(new Intent(getContext(), WishlistActivity.class));
                }
                else
                {
                    signInDialog.show();
                }
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user!=null)
                {
                    startActivity(new Intent(getContext(), NotificationActivity.class));
                }
                else {
                    signInDialog.show();
                }
            }
        });

        RecyclerView.LayoutManager manLayoutManager = new GridLayoutManager(getContext(),3);
        category.setLayoutManager(manLayoutManager);

        mancategoryList = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(getContext(), mancategoryList);
        category.setAdapter(categoryAdapter);

        readCategories();
        return view;
    }

    private void readCategories()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Category category = dataSnapshot.getValue(Category.class);
                    mancategoryList.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
