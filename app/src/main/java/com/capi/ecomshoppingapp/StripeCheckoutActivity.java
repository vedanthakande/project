package com.capi.ecomshoppingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.capi.ecomshoppingapp.Model.Cart;
import com.capi.ecomshoppingapp.Model.OrderProduct;
import com.capi.ecomshoppingapp.cart.CartViewModal;
import com.capi.ecomshoppingapp.notification.APIService;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.view.CardValidCallback;
import com.vinaygaba.creditcardview.CardType;
import com.vinaygaba.creditcardview.CreditCardView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StripeCheckoutActivity extends AppCompatActivity {
    String paymentIntentClientSecret;
    CardInputWidget cardInputWidget;
    private Stripe stripe;
    CheckBox cb_save_card;
    private String stripeId;
    Button payButton;
    CreditCardView card1;
    Double amountPayable = 0.0;
    Double subtotal = 0.0;
    String date, slot, addressId;
    String items;
    String code;
    APIService apiService;
    String key;
    FirebaseUser user;
    List<OrderProduct> cartList;
    ArrayList<String> cardsList;
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static Random random = new Random();
    private CartViewModal cartViewModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_checkout);
        cartViewModal = ViewModelProviders.of(this).get(CartViewModal.class);
        cartViewModal.initCardDataSource(StripeCheckoutActivity.this);

        apiService = com.capi.ecomshoppingapp.notification.Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Intent intent = getIntent();
        amountPayable = intent.getDoubleExtra("amount", 0.0);
        subtotal = intent.getDoubleExtra("subtotal", 0.0);
        cardsList = intent.getStringArrayListExtra("cards");
        addressId = intent.getStringExtra("address");
        items = intent.getStringExtra("items");
        user = FirebaseAuth.getInstance().getCurrentUser();
        cartList = new ArrayList<>();

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

        cb_save_card = findViewById(R.id.cb_save_card);
        payButton = findViewById(R.id.payButton);
        card1 = findViewById(R.id.card1);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        cardInputWidget.setPostalCodeEnabled(false);
        ImageView iv_backpress = findViewById(R.id.iv_backpress);
        iv_backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        key = FirebaseDatabase.getInstance().getReference("Orders")
                .child(user.getUid())
                .push()
                .getKey();
        code = randomString(10);
        stripeId = Preferences.getInstance(this).getSTRIPE_ID();
        PaymentConfiguration.init(
                getApplicationContext(),
                Constants.STRIPE_PUBLISHABLE_KEY
        );
        payButton.setText("Pay Rs" + new DecimalFormat("##.##").format(amountPayable));
        cardInputWidget.setCardValidCallback(new CardValidCallback() {
            @Override
            public void onInputChanged(boolean b, @NotNull Set<? extends Fields> set) {
                if (b) {
                    payButton.setEnabled(true);
                    hideKeyboard(StripeCheckoutActivity.this);
                } else {
                    payButton.setEnabled(false);
                }
            }
        });
        cardInputWidget.setCardNumberTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                card1.setCardNumber(s.toString());
                card1.setType(CardType.AUTO);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cardInputWidget.setExpiryDateTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                card1.setExpiryDate(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        startCheckout();
    }

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(random.nextInt(DATA.length())));
        }

        return sb.toString();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void startCheckout() {
        int amount = amountPayable.intValue() * 100;
        retrofit2.Call<JsonObject> call = Client.getClient(Constants.BASE_URL)
                .create(ApiService.class).getStripe(
                        "",
                        Constants.STRIPE_SECRET_KEY,
                        String.valueOf(amount),
                        Constants.CURRENCY_CODE,
                        stripeId,
                        null,
                        null,
                        null
                );

        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    paymentIntentClientSecret = response.body().get("client_secret").getAsString();
                } else {
                    Log.e("text", response.body().get("message").getAsString());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.e("tedt", t.getMessage());
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                if (params != null) {
                    boolean saveCard = cb_save_card.isChecked();
                    if (cardsList != null) {
                        for (int i = 0; i < cardsList.size(); i++) {
                            String fingerprint = cardsList.get(i);
                            if (fingerprint.equals(cardInputWidget.getCard().getFingerprint())) {
                                saveCard = false;
                            }
                        }
                    }
                    Map<String, String> extraParams = new HashMap<>();
                    extraParams.put("setup_future_usage", "off_session");
                    DialogUtils.displayProgressDialog(StripeCheckoutActivity.this);
                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                            .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret, null, true, extraParams);
                    if (!saveCard) {
                        confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                    }
                    final Context context = StripeCheckoutActivity.this.getApplicationContext();
                    stripe = new Stripe(context, PaymentConfiguration.getInstance(context).getPublishableKey());
                    stripe.confirmPayment(StripeCheckoutActivity.this, confirmParams);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    // ...

    private void displayAlert(String payment_completed, String toJson, boolean b) {
        Log.e("test", payment_completed + " " + toJson);
    }

    private void onPaymentSuccess(Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );


        paymentIntentClientSecret = responseMap.get("client_secret");
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<StripeCheckoutActivity> activityRef;

        PaymentResultCallback(@NonNull StripeCheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final StripeCheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(paymentIntent);
                activity.placeOrder();
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed
                DialogUtils.hideProgressDialog();
                Toast.makeText(activity, "Payment Failed!", Toast.LENGTH_SHORT).show();
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
                        false
                );
            } else {
                DialogUtils.hideProgressDialog();
                Toast.makeText(activity, "Payment Failed", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final StripeCheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString(), false);
        }
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<StripeCheckoutActivity> activityRef;

        PayCallback(@NonNull StripeCheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull final IOException e) {
            final StripeCheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(
                                                   activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                                           ).show();
                                       }
                                   }
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final StripeCheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                activity.runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                           }
                                       }
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private void placeOrder() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MMM/yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());

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
                orderMap.put("subtotalPrice", String.valueOf(subtotal));
                orderMap.put("totalPrice", String.valueOf(amountPayable));
                orderMap.put("shippingPrice", Constants.DELIVERY_PRICE);
                orderMap.put("address", addressId);
                orderMap.put("paymentMethod", "Stripe");
                orderMap.put("status", "pending");
                orderMap.put("paymentStatus", "paid");

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
                            Intent intent = new Intent(StripeCheckoutActivity.this, SuccessActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("orderId", code);
                            intent.putExtra("orderDate", saveCurrentDate);
                            intent.putExtra("orderItems", items);
                            intent.putExtra("subtotal", String.valueOf(subtotal));
                            intent.putExtra("totalPrice", String.valueOf(amountPayable));
                            startActivity(intent);

                        } else {
                            Toast.makeText(StripeCheckoutActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            DialogUtils.hideProgressDialog();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                                    .enqueue(new retrofit2.Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(retrofit2.Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(StripeCheckoutActivity.this, "Failed Sending Notification!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(retrofit2.Call<MyResponse> call, Throwable t) {

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