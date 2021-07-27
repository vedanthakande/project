package com.capi.ecomshoppingapp.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capi.ecomshoppingapp.MainActivity;
import com.capi.ecomshoppingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Button registerBtn;
    ImageView close;
    TextView login;
    ProgressBar progressBar;
    EditText fullname;
    EditText email;
    EditText passsword;
    EditText passwordAgain;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = findViewById(R.id.register_btn);
        close = findViewById(R.id.close_activity);
        login = findViewById(R.id.already_have_msg);
        fullname = findViewById(R.id.fullname_register);
        email = findViewById(R.id.email_register);
        passsword = findViewById(R.id.password_register);
        passwordAgain = findViewById(R.id.password_again_register);
        progressBar = findViewById(R.id.signup_progress);

        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_email = email.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_password = passsword.getText().toString();
                String str_passagain = passwordAgain.getText().toString();

                if (TextUtils.isEmpty(str_email))
                {
                    email.setError("Email is Required!");
                }
                else if (TextUtils.isEmpty(str_fullname))
                {
                    fullname.setError("Name is Required!");
                }
                else if (TextUtils.isEmpty(str_password))
                {
                    passsword.setError("Password is Empty!");
                }
                else if (str_password.length()<8)
                {
                    passsword.setError("Password is too short");
                }
                else if (!TextUtils.equals(str_password,str_passagain))
                {
                    passwordAgain.setError("Password does not match!");
                }
                else
                {
                    registerBtn.setVisibility(View.INVISIBLE);
                    register(str_fullname, str_email, str_password);
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.no_animation,R.anim.slide_down);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_out_from_right);
            }
        });
    }

    private void register(final String fullname, final String email, final String password)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(userid);


                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("fullname", fullname);
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/cc-ecommerce-app.appspot.com/o/user%20(1).png?alt=media&token=739a819d-f1ef-40f8-a6a4-ae6e35373441");
                            hashMap.put("phone", "");
                            hashMap.put("birthday", "");
                            hashMap.put("mode", "user");
                            hashMap.put("email", firebaseUser.getEmail());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        registerBtn.setVisibility(View.VISIBLE);
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                        else
                        {
                            registerBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(RegisterActivity.this, "You cant register with this email or password.", Toast.LENGTH_SHORT).show();
                            Log.e("test", task.getException().getMessage());
                        }
                    }
                });
    }
}
