package com.capi.ecomshoppingapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.navigation.ui.AppBarConfiguration;

import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.notification.Token;
import com.capi.ecomshoppingapp.signup.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class AdminMainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth auth;
    FirebaseUser user;
    LinearLayout product, category, offer, order;
    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        product = findViewById(R.id.product_layout);
        category = findViewById(R.id.category_layout);
        offer = findViewById(R.id.offers_layout);
        order = findViewById(R.id.orders_layout);

        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AdminProductsActivity.class));
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AdminCategoriesActivity.class));
            }
        });
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AdminCouponActivity.class));
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AdminOrdersActivity.class));
            }
        });

        /*TextView email = findViewById(R.id.email);
        email.setText(user.getDisplayName());*/

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_menu, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        break;
                    case R.id.nav_orders:
                        startActivity(new Intent(AdminMainActivity.this, AdminOrdersActivity.class));
                        break;
                    case R.id.nav_products:
                        startActivity(new Intent(AdminMainActivity.this, AdminProductsActivity.class));
                        break;
                    case R.id.nav_categories:
                        startActivity(new Intent(AdminMainActivity.this, AdminCategoriesActivity.class));
                        break;
                    case R.id.nav_offers:
                        startActivity(new Intent(AdminMainActivity.this, AdminCouponActivity.class));
                        break;
                   /* case R.id.nav_settings:
                        startActivity(new Intent(AdminMainActivity.this, SettingsActivity.class));
                        break;*/
                    case R.id.nav_banners:
                        startActivity(new Intent(AdminMainActivity.this, AdminBannerActivity.class));
                        break;
                    case R.id.nav_logout:
                        auth.signOut();

                        Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
        mAppBarConfiguration = new AppBarConfiguration.Builder()
                .setDrawerLayout(drawer)
                .build();
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(user.getUid()).setValue(token1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(AdminMainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    updateToken(newToken);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminMainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
