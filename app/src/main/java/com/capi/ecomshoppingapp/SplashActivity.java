package com.capi.ecomshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.capi.ecomshoppingapp.Model.User;
import com.capi.ecomshoppingapp.admin.AdminMainActivity;
import com.capi.ecomshoppingapp.signup.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity
{
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (user != null) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(user.getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if (user.getMode().equals("admin")) {
                                    Intent intent = new Intent(SplashActivity.this, AdminMainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else if (user.getMode().equals("user")) {
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }


                }
            }, 1000);
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
            alertDialog.setCancelable(false);
            alertDialog.setTitle("No Internet");
            alertDialog.setMessage("Please check your Internet Connection before trying again");
            alertDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    onBackPressed();
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.show();
        }
    }
}
