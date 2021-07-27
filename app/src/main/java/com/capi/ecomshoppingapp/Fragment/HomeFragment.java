package com.capi.ecomshoppingapp.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Adapter.CategoryAdapter;
import com.capi.ecomshoppingapp.Adapter.ProductsAdapter;
import com.capi.ecomshoppingapp.Adapter.SliderAdapter;
import com.capi.ecomshoppingapp.DialogUtils;
import com.capi.ecomshoppingapp.Model.Category;
import com.capi.ecomshoppingapp.Model.Products;
import com.capi.ecomshoppingapp.Model.SliderItem;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ///////category
    private RecyclerView category_recyler;
    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;

    ///////recent sale
    private RecyclerView recent_recyclerview;

    ///////discount sale
    private RecyclerView discount_recyclerView;
    private List<Products> discountList;
    private ProductsAdapter discountAdapter;

    ///////recommended recyclerView
    private RecyclerView recommendedRecycler;
    private List<Products> productsList;
    private ProductsAdapter productsAdapter;
    private String lastKey = "";
    private Boolean isLeft = true;
    /////////////////

    private FirebaseAuth auth;
    private FirebaseUser user;

    private Dialog signInDialog;

    SliderView imageSlider;
    SliderAdapter sliderAdapter;
    ImageView wishlist;
    ImageView notification;
    EditText editText;
    TextView flashsale_recyclerview_txt;
    TextView megasale_recyclerview_txt;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        category_recyler = view.findViewById(R.id.category_recyclerview);
        recent_recyclerview = view.findViewById(R.id.flashsale_recyclerview);
        discount_recyclerView = view.findViewById(R.id.megasale_recyclerview);
        recommendedRecycler = view.findViewById(R.id.recommended_recyclerView);
        imageSlider = view.findViewById(R.id.imageSlider);
        wishlist = view.findViewById(R.id.wishlist);
        notification = view.findViewById(R.id.notification);
        editText = view.findViewById(R.id.search_edittext);
        megasale_recyclerview_txt = view.findViewById(R.id.megasale_recyclerview_txt);
        flashsale_recyclerview_txt = view.findViewById(R.id.flashsale_recyclerview_txt);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    startActivity(new Intent(getContext(), SearchActivity.class));
                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });

        signInDialog = new Dialog(getContext());
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button signInDialogBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button signUpDialogBtn = signInDialog.findViewById(R.id.sign_up_btn);

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
                if (user != null) {
                    startActivity(new Intent(getContext(), WishlistActivity.class));
                } else {
                    signInDialog.show();
                }
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    startActivity(new Intent(getContext(), NotificationActivity.class));
                } else {
                    signInDialog.show();
                }
            }
        });

        ////////////Category Recycler View
        LinearLayoutManager categorylayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        category_recyler.setLayoutManager(categorylayoutManager);
        categoryList = new ArrayList<>();
        ////////////Category RecyclerView


        ////////////Recent RecyclerView
        LinearLayoutManager recentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recent_recyclerview.setLayoutManager(recentLayoutManager);
        recentLayoutManager.setReverseLayout(true);
        recentLayoutManager.setStackFromEnd(true);
        ////////////Recent RecyclerView

        ////////////Sale RecyclerView
        LinearLayoutManager discountLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        discount_recyclerView.setLayoutManager(discountLayoutManager);
        discountList = new ArrayList<>();
        discountAdapter = new ProductsAdapter(getContext(), discountList);
        discount_recyclerView.setAdapter(discountAdapter);
        ////////////Sale RecyclerView

        ///////////Recommended RecyclerView
        LinearLayoutManager recommendedlayoutManager = new GridLayoutManager(getContext(), 2);
        recommendedRecycler.setLayoutManager(recommendedlayoutManager);
        productsList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(getContext(), productsList);
        recommendedRecycler.setAdapter(productsAdapter);
        recent_recyclerview.setAdapter(productsAdapter);
        ///////////Recommended RecyclerView

        sliderAdapter = new SliderAdapter(getContext());
        imageSlider.setSliderAdapter(sliderAdapter);

        DialogUtils.displayProgressDialog(getContext());
        getCategories();
        getProducts(true);
        getBanners();

        recommendedRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isLeft) {
                        getProducts(false);
                    }
                }
            }
        });

        recent_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isLeft) {
                        getProducts(false);
                    }
                }
            }
        });

        discount_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isLeft) {
                        getProducts(false);
                    }
                }
            }
        });

        return view;
    }

    private void getBanners() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Banners");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SliderItem sliderItem = dataSnapshot.getValue(SliderItem.class);
                    sliderAdapter.addItem(sliderItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCategories() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }
                categoryAdapter = new CategoryAdapter(getContext(), categoryList);
                category_recyler.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProducts(Boolean isFirst) {
        DialogUtils.displayProgressDialog(getContext());
        Query query;
        if (!isFirst) {
            query = FirebaseDatabase.getInstance().getReference("Products")
                    .orderByKey()
                    .startAt(lastKey)
                    .limitToFirst(10);
        } else {
            query = FirebaseDatabase.getInstance().getReference("Products")
                    .orderByKey()
                    .limitToFirst(10);
        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 10) {
                    isLeft = false;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Products products = snapshot.getValue(Products.class);
                    productsList.add(products);

                    if (products.getDiscounted().equals("true")) {
                        discountList.add(products);
                    }
                    lastKey = snapshot.getKey();
                }
                DialogUtils.hideProgressDialog();
                productsAdapter.notifyDataSetChanged();
                discountAdapter.notifyDataSetChanged();
                if (productsAdapter.getItemCount() == 0) {
                    flashsale_recyclerview_txt.setVisibility(View.GONE);
                } else {
                    flashsale_recyclerview_txt.setVisibility(View.VISIBLE);
                }
                if (discountAdapter.getItemCount() == 0) {
                    megasale_recyclerview_txt.setVisibility(View.GONE);
                } else {
                    megasale_recyclerview_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
