package com.capi.ecomshoppingapp.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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


public class AddBannerActivity extends AppCompatActivity {
    ImageView imageView;
    ImageView back;
    Button add_btn;

    Uri imageUri;
    String myUrl = "";

    StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_banner);

        imageView = findViewById(R.id.banner_1);
        back = findViewById(R.id.back_btn);
        add_btn = findViewById(R.id.save_banner);
		
		storageReference = FirebaseStorage.getInstance().getReference("banners");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddBannerActivity.this, AdminBannerActivity.class));
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(10, 6)
                        .start(AddBannerActivity.this)
                ;
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_btn.setVisibility(View.INVISIBLE);
                uploadImage();
            }
        });
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

                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                        String saveCurrentDate = currentDate.format(calendar.getTime());

                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                        String saveCurrentTime = currentTime.format(calendar.getTime());

                        String randomKey = saveCurrentDate + saveCurrentTime;

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                .getReference("Banners")
                                .child(randomKey);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id",randomKey);
                        hashMap.put("image", myUrl);

                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                add_btn.setVisibility(View.VISIBLE);
                                Toast.makeText(AddBannerActivity.this, "Banner Added Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddBannerActivity.this, AdminBannerActivity.class));
                            }
                        });

                    } else {
                        add_btn.setVisibility(View.VISIBLE);
                        Toast.makeText(AddBannerActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    add_btn.setVisibility(View.VISIBLE);
                    Toast.makeText(AddBannerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            add_btn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
        }

    }
}