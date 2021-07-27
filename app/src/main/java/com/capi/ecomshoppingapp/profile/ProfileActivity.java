package com.capi.ecomshoppingapp.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capi.ecomshoppingapp.Model.User;
import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {

    ImageView back;
    ImageView imageProfile, edit_profile_image;
    ConstraintLayout fullname, gender, birthday, phone, password;
    TextView fullname_txt, birthday_txt, phone_txt, gender_txt, email_txt, password_txt, profile_name;
    ProgressBar profile_progress;

    FirebaseUser firebaseUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;

    int genderId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back = findViewById(R.id.back_btn);
        fullname = findViewById(R.id.fullname_layout);
        gender = findViewById(R.id.gender_layout);
        birthday = findViewById(R.id.birthday_layout);
        password = findViewById(R.id.password_layout);
        phone = findViewById(R.id.phone_layout);
        imageProfile = findViewById(R.id.image_profile);
        fullname_txt = findViewById(R.id.fullname);
        birthday_txt = findViewById(R.id.birthday);
        phone_txt = findViewById(R.id.phone);
        gender_txt = findViewById(R.id.gender);
        email_txt = findViewById(R.id.email);
        password_txt = findViewById(R.id.password);
        profile_name = findViewById(R.id.profile_name);
        edit_profile_image = findViewById(R.id.edit_profile_image);
        profile_progress = findViewById(R.id.profile_progress);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userInfo();

        fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChageNameActivity.class);
                intent.putExtra("fullname", fullname_txt.getText().toString());
                startActivity(intent);
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangeGenderActivity.class);
                intent.putExtra("genderId", genderId);
                startActivity(intent);
            }
        });
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangeBirthdayActivity.class);
                intent.putExtra("birthday", birthday_txt.getText().toString());
                startActivity(intent);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangePhoneActivity.class);
                intent.putExtra("phone", phone_txt.getText().toString());
                startActivity(intent);
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        edit_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ProfileActivity.this);
            }
        });
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(ProfileActivity.this).load(user.getImageurl()).into(imageProfile);
                fullname_txt.setText(user.getFullname());
                profile_name.setText(user.getFullname());
                birthday_txt.setText(user.getBirthday());
                phone_txt.setText(user.getPhone());
                email_txt.setText(user.getEmail());

                if (snapshot.child("gender").exists())
                {
                    gender_txt.setText(snapshot.child("gender").child("genderName").getValue(String.class));
                    genderId = snapshot.child("gender").child("genderId").getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadImage() {
        profile_progress.setVisibility(View.VISIBLE);
        if (mImageUri != null) {
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mime.getExtensionFromMimeType(contentResolver.getType(mImageUri));
            String extension = mime.getMimeTypeFromExtension(contentResolver.getType(mImageUri));

            final StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + extension);
            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", "" + myUrl);

                        reference.updateChildren(hashMap);
                        profile_progress.setVisibility(View.GONE);
                        finish();
                    } else {
                        profile_progress.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            profile_progress.setVisibility(View.GONE);
            Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();
        } else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfo();
    }
}
