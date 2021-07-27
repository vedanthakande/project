package com.capi.ecomshoppingapp.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {

    Button save;
    ImageView back;
    EditText editText;
    TextView passwordResetTxt;
    ProgressBar password_reset_progress;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        back = findViewById(R.id.back_btn);
        save = findViewById(R.id.save);
        passwordResetTxt = findViewById(R.id.password_change_txt);
        editText = findViewById(R.id.email_password_form);
        password_reset_progress = findViewById(R.id.password_reset_progress);

        auth = FirebaseAuth.getInstance();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    editText.setError("Email is Required!");
                } else {
                    password_reset_progress.setVisibility(View.VISIBLE);

                    auth.sendPasswordResetEmail(editText.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        passwordResetTxt.setText("Recovery Email sent successfully! check your inbox.");
                                    } else {
                                        String error = task.getException().getMessage();
                                        passwordResetTxt.setText(error);
                                        passwordResetTxt.setTextColor(getResources().getColor(R.color.red));
                                        passwordResetTxt.setVisibility(View.VISIBLE);
                                        password_reset_progress.setVisibility(View.GONE);
                                    }
                                    password_reset_progress.setVisibility(View.GONE);
                                }
                            });

                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
