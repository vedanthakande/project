package com.capi.ecomshoppingapp.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capi.ecomshoppingapp.Model.Products;
import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddProductsActivity extends AppCompatActivity {
    ImageView productImage;
    EditText productName;
    EditText productDescription;
    EditText productPrice;
    EditText productDiscount;
    EditText productPercentage;
    TextView product_discount_txt;
    TextView product_discount_ratio_txt;
    CheckBox discounted;
    Button add;
    ImageView back;

    String randomKey;
    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    String categoryId;
    String categoryName;
    String mode;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        Intent intent = getIntent();
        categoryName = intent.getStringExtra("categoryName");
        categoryId = intent.getStringExtra("categoryId");
        mode = intent.getStringExtra("mode");

        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        productDiscount = findViewById(R.id.product_discount);
        productPercentage = findViewById(R.id.product_discount_ratio);
        discounted = findViewById(R.id.isDiscounted);
        add = findViewById(R.id.save_product);
        back = findViewById(R.id.back_btn);
        product_discount_ratio_txt = findViewById(R.id.product_discount_ratio_txt);
        product_discount_txt = findViewById(R.id.product_discount_txt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddProductsActivity.this, AdminProductsActivity.class));
            }
        });

        if (mode.equals("edit"))
        {
            id = getIntent().getStringExtra("id");
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Products")
                    .child(id);

            productRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Products products = snapshot.getValue(Products.class);
                    productName.setText(products.getProductName());
                    Glide.with(AddProductsActivity.this).load(products.getProductImage()).into(productImage);
                    productDescription.setText(products.getProductDescription());
                    productPercentage.setText(products.getProductPercent());
                    productPrice.setText(products.getProductPrice());
                    productDiscount.setText(products.getProductDiscount());
                    if (products.getDiscounted().equals("true"))
                    {
                        discounted.setChecked(true);
                    }
                    else
                    {
                        discounted.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        storageReference = FirebaseStorage.getInstance().getReference("products");

        discounted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    product_discount_ratio_txt.setVisibility(View.VISIBLE);
                    product_discount_txt.setVisibility(View.VISIBLE);
                    productDiscount.setVisibility(View.VISIBLE);
                    productPercentage.setVisibility(View.VISIBLE);
                } else {
                    product_discount_ratio_txt.setVisibility(View.GONE);
                    product_discount_txt.setVisibility(View.GONE);
                    productDiscount.setVisibility(View.GONE);
                    productPercentage.setVisibility(View.GONE);
                }
            }
        });


        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equals("add")) {
                    CropImage.activity()
                            .setAspectRatio(1, 1)
                            .start(AddProductsActivity.this);
                }
                else if (mode.equals("edit"))
                {
                    Toast.makeText(AddProductsActivity.this, "Product Image cannot be changed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_name = productName.getText().toString();
                String str_desc = productDescription.getText().toString();
                String str_price = productPrice.getText().toString();
                String str_discount = productDiscount.getText().toString();
                String str_percent = productPercentage.getText().toString();

                if (discounted.isChecked()) {
                    if (TextUtils.isEmpty(str_discount)) {
                        productDiscount.setError("Product description is required!");
                    }
                    if (str_percent.length() > 2) {
                        productPercentage.setError("must be between 1-99");
                    }
                    if (TextUtils.isEmpty(str_percent)) {
                        productDiscount.setError("Product description is required!");
                    }
                }

                if (TextUtils.isEmpty(str_name)) {
                    productName.setError("Product name is required!");
                } else if (TextUtils.isEmpty(str_desc)) {
                    productDescription.setError("Product description is required!");
                } else if (TextUtils.isEmpty(str_price)) {
                    productPrice.setError("Product price is required!");
                } else {
                    add.setVisibility(View.INVISIBLE);
                    if (mode.equals("add")) {
                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                        String saveCurrentDate = currentDate.format(calendar.getTime());

                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                        String saveCurrentTime = currentTime.format(calendar.getTime());

                        randomKey = saveCurrentDate + saveCurrentTime;

                        uploadImage();
                    }
                    else if (mode.equals("edit"))
                    {
                        EditProduct();
                    }
                }
            }
        });
    }

    private void EditProduct()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products")
                .child(id);
        HashMap <String, Object> hashMap = new HashMap<>();
        hashMap.put("productId", id);
        hashMap.put("productName", productName.getText().toString());
        hashMap.put("productDescription", productDescription.getText().toString());
        hashMap.put("productPrice", productPrice.getText().toString());
        hashMap.put("productDiscount", productDiscount.getText().toString());
        hashMap.put("productPercent", productPercentage.getText().toString());
        hashMap.put("discounted", String.valueOf(discounted.isChecked()));

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(AddProductsActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddProductsActivity.this, AdminProductsActivity.class));
                }
            }
        });
    }

    private String getExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void uploadImage() {
        if (imageUri != null) {
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExtension(imageUri));
            uploadTask = reference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products")
                                .child(randomKey);

                        final HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("productId", randomKey);
                        hashMap.put("productImage", myUrl);
                        hashMap.put("productName", productName.getText().toString());
                        hashMap.put("productSearch", productName.getText().toString().toLowerCase());
                        hashMap.put("productDescription", productDescription.getText().toString());
                        hashMap.put("productPrice", productPrice.getText().toString());
                        hashMap.put("productDiscount", productDiscount.getText().toString());
                        hashMap.put("productPercent", productPercentage.getText().toString());
                        hashMap.put("categoryId", categoryId);
                        hashMap.put("categoryName", categoryName);
                        hashMap.put("discounted", String.valueOf(discounted.isChecked()));

                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                                    String saveCurrentDate = currentDate.format(Calendar.getInstance().getTime());


                                    DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
                                    HashMap<String, Object> notificationHashmap = new HashMap<>();
                                    notificationHashmap.put("title",productName.getText().toString());
                                    notificationHashmap.put("image", myUrl);
                                    notificationHashmap.put("date", saveCurrentDate);
                                    notificationHashmap.put("text",productName.getText().toString()+" - New Product Added");
                                    notificationHashmap.put("type", 1);

                                    notificationRef.child(saveCurrentDate).setValue(notificationHashmap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                add.setVisibility(View.VISIBLE);
                                                startActivity(new Intent(AddProductsActivity.this, AdminProductsActivity.class));
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        add.setVisibility(View.VISIBLE);
                        Toast.makeText(AddProductsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    add.setVisibility(View.VISIBLE);
                    Toast.makeText(AddProductsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            add.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();


            productImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "An Error Ocurred!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddProductsActivity.this, AdminProductsActivity.class));
    }
}
