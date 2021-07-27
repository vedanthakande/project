package com.capi.ecomshoppingapp.signup;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capi.ecomshoppingapp.DialogUtils;
import com.capi.ecomshoppingapp.MainActivity;
import com.capi.ecomshoppingapp.Model.User;
import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.admin.AdminMainActivity;
import com.capi.ecomshoppingapp.profile.ChangePasswordActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    CardView googleBtn;
    CardView facebookBtn;
    ImageView close;
    TextView register;
    TextView forgot_password;
    EditText email;
    EditText password;
    String type;

    private GoogleSignInClient googleSignInClient;
    private final static int RC_SIGN_IN = 123;

    FirebaseAuth auth;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.login_btn);
        close = findViewById(R.id.close_activity);
        forgot_password = findViewById(R.id.forgot_password);
        register = findViewById(R.id.dont_have_txt);
        googleBtn = findViewById(R.id.google_signin_btn);
        facebookBtn = findViewById(R.id.facebook_signin_btn);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                if (TextUtils.isEmpty(str_email)) {
                    email.setError("Email is Required!");
                } else if (TextUtils.isEmpty(str_password)) {
                    password.setError("Password is Empty!");
                } else {
                    loginBtn.setVisibility(View.INVISIBLE);
                    auth.signInWithEmailAndPassword(str_email, str_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        signIn();
                                    } else {
                                        loginBtn.setVisibility(View.VISIBLE);
                                        Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "facebook";
                DialogUtils.displayProgressDialog(LoginActivity.this);
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        facebookAuth(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        DialogUtils.hideProgressDialog();
                        Log.d("fff", "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        DialogUtils.hideProgressDialog();
                        Log.d("fff", "facebook:onError", error);
                    }
                });
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_out_from_left);
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));
            }
        });
        createRequest();

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.displayProgressDialog(LoginActivity.this);
                type = "google";
                signInOption();
            }
        });
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInOption() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);
                if (user1.getMode().equals("user")) {
                    loginBtn.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (user1.getMode().equals("admin")) {
                    loginBtn.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (type.equals("facebook")) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void facebookAuth(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        DialogUtils.hideProgressDialog();
                        if (task.isSuccessful()) {
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(auth.getCurrentUser().getUid());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        FirebaseUser user = auth.getCurrentUser();

                                        if (user.getEmail() == null) {
                                            final Dialog dialog = new Dialog(LoginActivity.this);
                                            dialog.setContentView(R.layout.email_layout);
                                            dialog.setCancelable(false);
                                            dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                                            dialog.show();

                                            final EditText email = dialog.findViewById(R.id.email_edittext);
                                            Button saveEmail = dialog.findViewById(R.id.save_email);
                                            Button cancelEmail = dialog.findViewById(R.id.cancel_email);

                                            saveEmail.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (TextUtils.isEmpty(email.getText().toString())) {
                                                        email.setError("Field is Required");
                                                    } else {
                                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("id", firebaseUser.getUid());
                                                        hashMap.put("fullname", firebaseUser.getDisplayName());
                                                        hashMap.put("imageurl", firebaseUser.getPhotoUrl().toString());
                                                        hashMap.put("phone", firebaseUser.getPhoneNumber());
                                                        hashMap.put("birthday", "");
                                                        hashMap.put("mode", "user");
                                                        hashMap.put("email", email.getText().toString());

                                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });

                                            cancelEmail.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog.dismiss();
                                                    auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(LoginActivity.this, "Registration has been cancelled!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });

                                        } else {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("id", user.getUid());
                                            hashMap.put("fullname", user.getDisplayName());
                                            hashMap.put("imageurl", user.getPhotoUrl().toString());
                                            hashMap.put("phone", "");
                                            hashMap.put("birthday", "");
                                            hashMap.put("mode", "user");
                                            hashMap.put("email", user.getEmail());

                                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        signIn();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                DialogUtils.hideProgressDialog();
                if (task.isSuccessful()) {
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(auth.getCurrentUser().getUid());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                FirebaseUser user = auth.getCurrentUser();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("id", user.getUid());
                                hashMap.put("fullname", user.getDisplayName());
                                hashMap.put("imageurl", user.getPhotoUrl().toString());
                                hashMap.put("phone", "");
                                hashMap.put("birthday", "");
                                hashMap.put("mode", "user");
                                hashMap.put("email", user.getEmail());

                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            } else {
                                signIn();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}
