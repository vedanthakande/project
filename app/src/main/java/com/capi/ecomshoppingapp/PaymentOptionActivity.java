package com.capi.ecomshoppingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capi.ecomshoppingapp.Adapter.PaymentAdapter;
import com.capi.ecomshoppingapp.Adapter.StripeCardsAdapter;
import com.capi.ecomshoppingapp.Model.Cart;
import com.capi.ecomshoppingapp.Model.OrderProduct;
import com.capi.ecomshoppingapp.Model.PaymentMethod;
import com.capi.ecomshoppingapp.Model.StripeCardList;
import com.capi.ecomshoppingapp.Model.StripeNewUserDetails;
import com.capi.ecomshoppingapp.address.ShiptoActivity;
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
import com.google.gson.reflect.TypeToken;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;

import static com.capi.ecomshoppingapp.StripeCheckoutActivity.randomString;

public class PaymentOptionActivity extends AppCompatActivity implements StripeCardsAdapter.OnItemClick, PaymentResultListener {

    LinearLayout rb_slots, llsavecards;
    ListView lv_payment;
    List<PaymentMethod> paymentMethodList;
    PaymentAdapter paymentAdapter;
    StripeCardsAdapter cardsAdapter;
    RecyclerView mRecyclerview;
    private OkHttpClient httpClient;
    String paymentIntentClientSecret;
    int preSelectedIndex = -1;
    int selectedCard = -1;
    ApiService apiService;
    String stripId;
    FirebaseUser user;
    StripeCardList cardList;
    Button next_btn;
    Double totalS, subtotalS;
    String itemsS, addressId;
    TextView items, subtotal_price, shipping_price, total_price;
    List<OrderProduct> cartList;
    private static final String TAG = ShiptoActivity.class.getSimpleName();
    private CartViewModal cartViewModal;
    String codeS;
    String keyS;
    String saveCurrentDate;
    String saveCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);
        cartViewModal = ViewModelProviders.of(this).get(CartViewModal.class);
        cartViewModal.initCardDataSource(PaymentOptionActivity.this);
        Intent intent = getIntent();
        itemsS = intent.getStringExtra("items");
        subtotalS = intent.getDoubleExtra("subtotal", 0.00);
        totalS = intent.getDoubleExtra("amount", 0.00);
        addressId = intent.getStringExtra("address");

        apiService = Client.getClient(Constants.BASE_URL).create(ApiService.class);
        cardList = new StripeCardList();
        cartList = new ArrayList<>();
        httpClient = new OkHttpClient();
        cardsAdapter = new StripeCardsAdapter(PaymentOptionActivity.this, cardList.getData(), PaymentOptionActivity.this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        stripId = Preferences.getInstance(this).getSTRIPE_ID();
        mRecyclerview = findViewById(R.id.rv_saved_cards);
        llsavecards = findViewById(R.id.ll_saved_cards);
        lv_payment = findViewById(R.id.lv_payment);
        next_btn = findViewById(R.id.next_btn);
        items = findViewById(R.id.items);
        subtotal_price = findViewById(R.id.subtotal_price);
        shipping_price = findViewById(R.id.shipping_price);
        total_price = findViewById(R.id.total_price);

        subtotal_price.setText(getResources().getString(R.string.currency) + new DecimalFormat("##.##").format(subtotalS));
        total_price.setText(getResources().getString(R.string.currency) + new DecimalFormat("##.##").format(totalS));
        shipping_price.setText(getResources().getString(R.string.currency) + new DecimalFormat("##.##").format(Double.parseDouble(Constants.DELIVERY_PRICE)));
        items.setText(itemsS);

        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        paymentMethodList = new ArrayList<>();
        paymentAdapter = new PaymentAdapter(this, paymentMethodList);
        lv_payment.setAdapter(paymentAdapter);
        loadPaymentMethods();
        lv_payment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                if (selectedCard != -1) {
                    cardList.getData().get(selectedCard).setDefaultCard(false);
                    cardsAdapter = new StripeCardsAdapter(PaymentOptionActivity.this, cardList.getData(), PaymentOptionActivity.this);
                    mRecyclerview.setAdapter(cardsAdapter);
                    cardsAdapter.notifyDataSetChanged();
                    selectedCard = -1;
                }

                PaymentMethod model = paymentMethodList.get(i); //changed it to model because viewers will confused about it

                model.setSelected(true);

                paymentMethodList.set(i, model);

                if (preSelectedIndex > -1 && preSelectedIndex != i) {

                    PaymentMethod preRecord = paymentMethodList.get(preSelectedIndex);
                    preRecord.setSelected(false);

                    paymentMethodList.set(preSelectedIndex, preRecord);

                }

                preSelectedIndex = i;

                paymentAdapter.updateRecords(paymentMethodList);
            }
        });
        DialogUtils.displayProgressDialog(this);
        if (!stripId.equals("")) {
            getStripeCards(stripId);
        } else {
            createStripeCustomer();
        }

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = paymentMethodList.get(preSelectedIndex).getId();
                if (id.equals("stripe")) {
                    if (selectedCard != -1) {
                        DialogUtils.displayProgressDialog(PaymentOptionActivity.this);
                        payUsingExistingCard(totalS * 100, cardList.getData().get(0).getId());
                    } else {
                        Intent intent = new Intent(PaymentOptionActivity.this, StripeCheckoutActivity.class);
                        intent.putExtra("amount", totalS);
                        intent.putExtra("subtotal", subtotalS);
                        intent.putExtra("items", itemsS);
                        intent.putExtra("address", addressId);
                        ArrayList<String> dataArrayList = new ArrayList<>();

                        if (cardList.getData() != null) {
                            for (int x = 0; x < cardList.getData().size(); x++) {
                                dataArrayList.add(cardList.getData().get(x).getCard().getFingerprint());
                            }
                        }


                        intent.putStringArrayListExtra("cards", dataArrayList);
                        startActivity(intent);
                    }
                } else if (id.equals("cod")) {
                    DialogUtils.displayProgressDialog(PaymentOptionActivity.this);
                    placeOrder();
                } else if (id.equals("razorpay")) {
                    DialogUtils.displayProgressDialog(PaymentOptionActivity.this);
                    placeOrder();
                }
            }
        });

        setListViewHeightBasedOnChildren(lv_payment);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void createStripeCustomer() {
        retrofit2.Call<StripeNewUserDetails> call = apiService.createStripeCustomer(
                "yes",
                Constants.STRIPE_SECRET_KEY,
                user.getEmail(),
                null,
                null,
                null
        );

        call.enqueue(new retrofit2.Callback<StripeNewUserDetails>() {
            @Override
            public void onResponse(retrofit2.Call<StripeNewUserDetails> call, Response<StripeNewUserDetails> response) {
                llsavecards.setVisibility(View.GONE);
                DialogUtils.hideProgressDialog();
                if (response.isSuccessful()) {
                    Preferences.getInstance(PaymentOptionActivity.this).setSTRIPE_ID(response.body().getId());
                    Toast.makeText(PaymentOptionActivity.this, "CREATED!", Toast.LENGTH_SHORT).show();
                } else {
                    llsavecards.setVisibility(View.GONE);
                    DialogUtils.hideProgressDialog();
                    Toast.makeText(PaymentOptionActivity.this, "Code:" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<StripeNewUserDetails> call, Throwable t) {
                llsavecards.setVisibility(View.GONE);
                DialogUtils.hideProgressDialog();
            }
        });
    }

    private void getStripeCards(String customerId) {
        retrofit2.Call<StripeCardList> call = apiService.getStripeCards(
                "yes",
                Constants.STRIPE_SECRET_KEY,
                customerId
        );
        call.enqueue(new retrofit2.Callback<StripeCardList>() {
            @Override
            public void onResponse(retrofit2.Call<StripeCardList> call, Response<StripeCardList> response) {
                DialogUtils.hideProgressDialog();
                if (response.isSuccessful()) {
                    StripeCardList cards = response.body();
                    next_btn.setVisibility(View.VISIBLE);
                    llsavecards.setVisibility(View.VISIBLE);
                    cardList = cards;
                    if (cards.getData().isEmpty()) {
                        llsavecards.setVisibility(View.GONE);
                    } else {
                        int x = 0;
                        for (StripeCardList.CardData cardData : cardList.getData()) {
                            if (cards.getDefault_payment_method() != null && cards.getDefault_payment_method().equals(cardData.getId())) {
                                cardList.getData().get(x).setDefaultCard(true);
                                selectedCard = x;
                            } else {
                                cardList.getData().get(x).setDefaultCard(false);
                            }
                            x++;
                        }
                        cardsAdapter = new StripeCardsAdapter(PaymentOptionActivity.this, cardList.getData(), PaymentOptionActivity.this);
                        mRecyclerview.setAdapter(cardsAdapter);
                        mRecyclerview.setNestedScrollingEnabled(false);
                        mRecyclerview.setHasFixedSize(false);
                    }
                } else {
                    llsavecards.setVisibility(View.GONE);
                    Toast.makeText(PaymentOptionActivity.this, "Error:" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<StripeCardList> call, Throwable t) {
                DialogUtils.hideProgressDialog();
                llsavecards.setVisibility(View.GONE);
                Log.d("test", t.getMessage());
            }
        });
    }

    private void loadPaymentMethods() {
        paymentMethodList.addAll(Constants.paymentMethodList());
        paymentAdapter.notifyDataSetChanged();
    }

    private void payUsingExistingCard(Double amount, String payment_method_id) {
        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "/stripe/stripe-api.php?currency=" + Constants.CURRENCY_CODE +
                        "&get_stripe=" +
                        "&amount=" + amount +
                        "&customer_id=" + stripId +
                        "&apiKey=" + Constants.STRIPE_SECRET_KEY +
                        "&payment_method_id=" + payment_method_id +
                        "&off_session=true&confirm=true"
                ).build();
        httpClient.newCall(request)
                .enqueue(new PaymentOptionActivity.PayCallback(this));
    }

    @Override
    public void onClick(int value) {
        if (preSelectedIndex != -1) {
            List<PaymentMethod> paymentMethodList1 = new ArrayList<>();
            for (PaymentMethod paymentMethod : paymentMethodList) {
                paymentMethod.setSelected(false);
                paymentMethodList1.add(paymentMethod);
            }
            paymentMethodList = paymentMethodList1;
            paymentAdapter = new PaymentAdapter(this, paymentMethodList);
            lv_payment.setAdapter(paymentAdapter);
        }
        if (selectedCard != -1) {
            cardList.getData().get(selectedCard).setDefaultCard(false);
            cardsAdapter.notifyDataSetChanged();
        }
        selectedCard = value;
        preSelectedIndex = 2;
    }

    @Override
    public void onPaymentSuccess(String s) {
        DialogUtils.displayProgressDialog(PaymentOptionActivity.this);
        FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("mode")
                .equalTo("admin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String adminId = dataSnapshot.child("id").getValue(String.class);
                            sendNotification(adminId, codeS);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        DialogUtils.hideProgressDialog();
        Intent intent = new Intent(PaymentOptionActivity.this, SuccessActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("orderId", codeS);
        intent.putExtra("orderDate", saveCurrentDate);
        intent.putExtra("orderItems", itemsS);
        intent.putExtra("subtotal", String.valueOf(subtotalS));
        intent.putExtra("totalPrice", String.valueOf(totalS));
        startActivity(intent);
    }

    @Override
    public void onPaymentError(int i, String s) {
        FirebaseDatabase.getInstance().getReference("Orders")
                .child(user.getUid())
                .child(keyS)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(PaymentOptionActivity.this, "Payment unsuccessful, Try again!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PaymentOptionActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<PaymentOptionActivity> activityRef;

        PayCallback(@NonNull PaymentOptionActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull final IOException e) {
            final PaymentOptionActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           DialogUtils.hideProgressDialog();
                                           Toast.makeText(
                                                   activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                                           ).show();
                                       }
                                   }
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final okhttp3.Response response)
                throws IOException {
            final PaymentOptionActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                activity.runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               DialogUtils.hideProgressDialog();
                                               Toast.makeText(
                                                       activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                                               ).show();
                                           }
                                       }
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }

    }

    private void onPaymentSuccess(okhttp3.Response response) throws IOException {
        DialogUtils.hideProgressDialog();
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("client_secret");

        placeOrder();
    }

    private void placeOrder() {
        String key = FirebaseDatabase.getInstance().getReference("Orders")
                .child(user.getUid())
                .push()
                .getKey();

        String code = randomString(10);
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MMM/yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

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
                orderMap.put("subtotalPrice", String.valueOf(subtotalS));
                orderMap.put("totalPrice", String.valueOf(totalS));
                orderMap.put("shippingPrice", Constants.DELIVERY_PRICE);
                orderMap.put("address", addressId);
                String paymentMethod = paymentMethodList.get(preSelectedIndex).getTitle();
                String paymentMethodId = paymentMethodList.get(preSelectedIndex).getId();
                orderMap.put("paymentMethod", paymentMethod);
                orderMap.put("status", "pending");
                orderMap.put("paymentStatus", "paid");

                if (paymentMethodId.equals("cod")) {
                    orderMap.remove("paymentStatus");
                    orderMap.put("paymentStatus", "unpaid");
                }

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
                                                sendNotification(adminId, code);
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
                            if (!paymentMethodId.equals("razorpay")) {
                                DialogUtils.hideProgressDialog();
                                Intent intent = new Intent(PaymentOptionActivity.this, SuccessActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("orderId", code);
                                intent.putExtra("orderDate", saveCurrentDate);
                                intent.putExtra("orderItems", itemsS);
                                intent.putExtra("subtotal", String.valueOf(subtotalS));
                                intent.putExtra("totalPrice", String.valueOf(totalS));
                                startActivity(intent);
                            } else {
                                DialogUtils.hideProgressDialog();
                                paywithRazor(code);
                            }

                        } else {
                            Toast.makeText(PaymentOptionActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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

    private void paywithRazor(String code) {
        String razorpay_api = getResources().getString(R.string.razorpay_key_id);
        String appname = getResources().getString(R.string.app_name);
        int price = totalS.intValue() * 100;

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
            options.put("currency", Constants.CURRENCY_CODE);
            options.put("amount", totalPriceValue);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong!", e);
        }
    }

    private void sendNotification(final String adminId, String code) {
        APIService apiService = com.capi.ecomshoppingapp.notification.Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
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