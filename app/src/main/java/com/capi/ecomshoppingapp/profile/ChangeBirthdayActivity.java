package com.capi.ecomshoppingapp.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeBirthdayActivity extends AppCompatActivity {

    ImageView back;
    Button save;
    EditText birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_birthday);

        back = findViewById(R.id.back_btn);
        save = findViewById(R.id.save_btn);
        birthday = findViewById(R.id.birthday_form);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        birthday.setText(getIntent().getStringExtra("birthday"));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (TextUtils.isEmpty(birthday.getText().toString()))
                {
                    birthday.setError("Field is Required!");
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(user.getUid())
                            .child("birthday")
                            .setValue(birthday.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ChangeBirthdayActivity.this, "Profile Updated Succesfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
