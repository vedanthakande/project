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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddCategoryActivity extends AppCompatActivity {
    ImageView imageView;
    ImageView back;
    EditText editText;
    Button save;

    String randomKey;
    Uri imageUri;
    String myUrl = "";

    String id;
    String name;
    String image;
    String mode;

    StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        imageView = findViewById(R.id.category_image);
        editText = findViewById(R.id.category_name);
        save = findViewById(R.id.save_btn);
        back = findViewById(R.id.back_btn);

        mode = getIntent().getStringExtra("mode");
        storageReference = FirebaseStorage.getInstance().getReference("categories");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddCategoryActivity.this, AdminCategoriesActivity.class));
            }
        });

        if (mode.equals("edit")) {
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            id = intent.getStringExtra("id");
            image = intent.getStringExtra("image");

            editText.setText(name);
            Glide.with(AddCategoryActivity.this).load(image).into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(AddCategoryActivity.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(editText.getText().toString())) {
                    editText.setError("Field is Required!");
                } else {
                    save.setVisibility(View.INVISIBLE);
                    if (mode.equals("add")) {
                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                        String saveCurrentDate = currentDate.format(calendar.getTime());

                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                        String saveCurrentTime = currentTime.format(calendar.getTime());

                        randomKey = saveCurrentDate + saveCurrentTime;

                    }
                    uploadImage();
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

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Categories")
                                .child(randomKey);

                        HashMap<String, Object> hashMap = new HashMap<>();

                        String mode1 = "";
                        if (mode.equals("add"))
                        {
                            mode1 = randomKey;
                        }
                        else if (mode.equals("edit"))
                        {
                            mode1 = id;
                        }
                        hashMap.put("categoryId", mode1);
                        hashMap.put("categoryImage", myUrl);
                        hashMap.put("categoryName", editText.getText().toString());

                        databaseReference.setValue(hashMap);

                        save.setVisibility(View.VISIBLE);
                        startActivity(new Intent(AddCategoryActivity.this, AdminCategoriesActivity.class));
                    } else {
                        save.setVisibility(View.VISIBLE);
                        Toast.makeText(AddCategoryActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    save.setVisibility(View.VISIBLE);
                    Toast.makeText(AddCategoryActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            save.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();


            imageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "An Error Ocurred!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void saveCategory() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories")
                .child(id);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("categoryId", id);
        hashMap.put("categoryName", editText.getText().toString());
        hashMap.put("categoryImage", image);

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(AddCategoryActivity.this, AdminCategoriesActivity.class));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddCategoryActivity.this, AdminCategoriesActivity.class));
    }
}
