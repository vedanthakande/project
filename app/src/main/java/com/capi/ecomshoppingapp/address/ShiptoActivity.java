package com.capi.ecomshoppingapp.address;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.capi.ecomshoppingapp.Adapter.AddressAdapter;
import com.capi.ecomshoppingapp.Constants;
import com.capi.ecomshoppingapp.MainActivity;
import com.capi.ecomshoppingapp.Model.Address;
import com.capi.ecomshoppingapp.Model.Cart;
import com.capi.ecomshoppingapp.Model.OrderProduct;
import com.capi.ecomshoppingapp.PaymentOptionActivity;
import com.capi.ecomshoppingapp.R;
import com.capi.ecomshoppingapp.SuccessActivity;
import com.capi.ecomshoppingapp.cart.CartDataSource;
import com.capi.ecomshoppingapp.cart.CartViewModal;
import com.capi.ecomshoppingapp.notification.APIService;
import com.capi.ecomshoppingapp.notification.Client;
import com.capi.ecomshoppingapp.notification.Data;
import com.capi.ecomshoppingapp.notification.MyResponse;
import com.capi.ecomshoppingapp.notification.Sender;
import com.capi.ecomshoppingapp.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShiptoActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = ShiptoActivity.class.getSimpleName();
    ImageView addBtn;
    ImageView back;
    Button next;

    private String razorpay_api;

    ProgressBar codProgress;
    ProgressBar razorpayProgress;

    ImageButton cod;
    ImageButton razorpay;

    private Dialog paymentDialog;

    String payment_method;
    String saveCurrentDate;
    String saveCurrentTime;

    RecyclerView addressRecycler;
    List<Address> addressList;
    List<OrderProduct> cartList;
    private CartDataSource cartDataSource;
    private CartViewModal cartViewModal;
    private AddressAdapter addressAdapter;
    FirebaseUser user;

    private TextView subtotal;
    private TextView items;

    private TextView shipping;
    private TextView totalPrice;

    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static Random random = new Random();

    String key;
    String code;
    String currency;
    String itemsS, totalS, subtotalS;

    String addressId;

    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartViewModal = ViewModelProviders.of(this).get(CartViewModal.class);
        setContentView(R.layout.activity_shipto);

        addressRecycler = findViewById(R.id.addressRecycler);
        addBtn = findViewById(R.id.add_address_btn);
        back = findViewById(R.id.back_btn);
        next = findViewById(R.id.next_btn);
        subtotal = findViewById(R.id.subtotal_price);
        items = findViewById(R.id.items);
        shipping = findViewById(R.id.shipping_price);
        totalPrice = findViewById(R.id.total_price);

        cartList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        cartViewModal.initCardDataSource(ShiptoActivity.this);

        Checkout.preload(getApplicationContext());

        final Intent intent = getIntent();
        itemsS = intent.getStringExtra("items");
        subtotalS = intent.getStringExtra("subtotal");
        totalS = intent.getStringExtra("total");
        items.setText(intent.getStringExtra("items"));
        subtotal.setText(intent.getStringExtra("subtotal"));
        totalPrice.setText(intent.getStringExtra("total"));
        shipping.setText(intent.getStringExtra("shipping"));

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        paymentDialog = new Dialog(this);
        paymentDialog.setContentView(R.layout.payment_dialog);
        paymentDialog.setCancelable(true);
        paymentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cod = paymentDialog.findViewById(R.id.cod_btn);
        razorpay = paymentDialog.findViewById(R.id.razorpay);
        codProgress = paymentDialog.findViewById(R.id.cod_progress);
        razorpayProgress = paymentDialog.findViewById(R.id.razorpay_progress);

        cartViewModal.getMutableLiveDataCart().observe(this, new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                for (Cart cart : carts) {
                    cartList.add(new OrderProduct(
                            cart.getProduct_id(),
                            cart.getProduct_name(),
                            cart.getProduct_price(),
                            Arrays.asList(cart.getProduct_variations().split(",")),
                            cart.getQuantity(),
                            "",
                            ""
                    ));
                }
            }
        });


        currency = Constants.CURRENCY_CODE;

        key = FirebaseDatabase.getInstance().getReference("Orders")
                .child(user.getUid())
                .push()
                .getKey();
        code = randomString(10);

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentDialog.setCancelable(false);
                payment_method = "COD";
                codProgress.setVisibility(View.VISIBLE);
                cod.setVisibility(View.VISIBLE);
                placeOrder();
            }
        });

        razorpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentDialog.setCancelable(false);
                payment_method = "RAZORPAY";
                razorpayProgress.setVisibility(View.VISIBLE);
                razorpay.setVisibility(View.INVISIBLE);
                placeOrder();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShiptoActivity.this, AddAddressActivity.class);
                intent.putExtra("mode", "add");
                intent.putExtra("type", "ship");
                intent.putExtra("items", items.getText().toString());
                intent.putExtra("subtotal", subtotal.getText().toString());
                intent.putExtra("shipping", shipping.getText().toString());
                intent.putExtra("total", totalPrice.getText().toString());
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(ShiptoActivity.this, LinearLayoutManager.HORIZONTAL, false);
        addressRecycler.setLayoutManager(layoutManager);

        addressList = new ArrayList<>();

        addressAdapter = new AddressAdapter(ShiptoActivity.this, addressList, "ship", null);
        addressRecycler.setAdapter(addressAdapter);
        ((SimpleItemAnimator) addressRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        addressAdapter.notifyDataSetChanged();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ShiptoActivity.this, PaymentOptionActivity.class);
                intent1.putExtra("amount", Double.parseDouble(totalS));
                intent1.putExtra("subtotal", Double.parseDouble(subtotalS));
                intent1.putExtra("items", itemsS);
                intent1.putExtra("address", addressAdapter.getSelectedAddressId());
                startActivity(intent1);
                //paymentDialog.show();
            }
        });

        getAddressCount();
    }

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(random.nextInt(DATA.length())));
        }

        return sb.toString();
    }

    private void placeOrder() {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MMM/yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        addressId = addressAdapter.getSelectedAddressId();


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders")
                .child(user.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, Object> orderMap = new HashMap<>();
                orderMap.put("orderId", key);
                orderMap.put("orderCode", code);
                orderMap.put("userId", user.getUid());
                orderMap.put("orderDate", saveCurrentDate);
                orderMap.put("orderTime", saveCurrentTime);
                orderMap.put("subtotalPrice", subtotal.getText().toString());
                orderMap.put("totalPrice", totalPrice.getText().toString());
                orderMap.put("shippingPrice", shipping.getText().toString());
                orderMap.put("address", addressId);
                orderMap.put("paymentMethod", payment_method);
                orderMap.put("status", "pending");
                orderMap.put("paymentStatus", "unpaid");

                reference.child(key).setValue(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .orderByChild("mode")
                                    .equalTo("admin")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                String adminId = dataSnapshot.child("id").getValue(String.class);
                                                sendNotification(adminId);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference("Orders")
                                    .child(user.getUid())
                                    .child(key)
                                    .child("Products");

                            for (OrderProduct orderProduct : cartList) {
                                orderReference.child(orderProduct.getProductId()).setValue(orderProduct);
                            }

                            if (payment_method.equals("COD")) {
                                Intent intent = new Intent(ShiptoActivity.this, SuccessActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("orderId", code);
                                intent.putExtra("orderDate", saveCurrentDate);
                                intent.putExtra("orderItems", items.getText().toString());
                                intent.putExtra("subtotal", subtotal.getText().toString());
                                intent.putExtra("totalPrice", totalPrice.getText().toString());
                                startActivity(intent);
                            } else if (payment_method.equals("RAZORPAY")) {
                                paywithRazor();
                            }

                        } else {
                            Toast.makeText(ShiptoActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            codProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void paywithRazor() {
        razorpay_api = getResources().getString(R.string.razorpay_key_id);
        String appname = getResources().getString(R.string.app_name);
        int price = Integer.parseInt(totalPrice.getText().toString()) * 100;

        String totalPriceValue = String.valueOf(price);

        Checkout checkout = new Checkout();

        checkout.setKeyID(razorpay_api);
        checkout.setImage(R.drawable.shop);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", appname);
            options.put("description", "OrderID# " + code);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", currency);
            options.put("amount", totalPriceValue);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong!", e);
        }
    }

    private void getAddressCount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Address")
                .child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Intent intent = new Intent(ShiptoActivity.this, AddAddressActivity.class);
                    intent.putExtra("mode", "add");
                    intent.putExtra("type", "ship");
                    intent.putExtra("items", items.getText().toString());
                    intent.putExtra("subtotal", subtotal.getText().toString());
                    intent.putExtra("shipping", shipping.getText().toString());
                    intent.putExtra("total", totalPrice.getText().toString());
                    startActivity(intent);
                } else {
                    getAddress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAddress() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Address")
                .child(user.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addressList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Address address = dataSnapshot.getValue(Address.class);
                    addressList.add(address);
                }
                addressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
        FirebaseDatabase.getInstance().getReference("Orders")
                .child(user.getUid())
                .child(key)
                .child("paymentStatus").setValue("paid")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference("Users")
                                .orderByChild("mode")
                                .equalTo("admin")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            String adminId = dataSnapshot.child("id").getValue(String.class);
                                            sendNotification(adminId);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        Intent intent = new Intent(ShiptoActivity.this, SuccessActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("orderId", code);
                        intent.putExtra("orderDate", saveCurrentDate);
                        intent.putExtra("orderItems", items.getText().toString());
                        intent.putExtra("subtotal", subtotal.getText().toString());
                        intent.putExtra("totalPrice", totalPrice.getText().toString());
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onPaymentError(int i, String s) {
        FirebaseDatabase.getInstance().getReference("Orders")
                .child(user.getUid())
                .child(key)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ShiptoActivity.this, "Payment unsuccessful, Try again!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ShiptoActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


    private void sendNotification(final String adminId) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.orderByKey().equalTo(adminId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            Data data = new Data(user.getUid(), R.drawable.shop, "You got a new Order# " + code, "New Order", adminId);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(ShiptoActivity.this, "Failed Sending Notification!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
