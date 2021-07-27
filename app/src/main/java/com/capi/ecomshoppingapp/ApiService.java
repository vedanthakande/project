package com.capi.ecomshoppingapp;

import com.capi.ecomshoppingapp.Model.StripeCardList;
import com.capi.ecomshoppingapp.Model.StripeNewUserDetails;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("stripe/stripe-api.php")
    Call<StripeNewUserDetails> createStripeCustomer(
            @Query("create_customer") String create_customer,
            @Query("apiKey") String apiKey,
            @Query("email") String email,
            @Query("description") String description,
            @Query("name") String name,
            @Query("phone") String phone
    );

    @GET("stripe/stripe-api.php")
    Call<JsonObject> getStripe(
            @Query("get_stripe") String get_stripe,
            @Query("apiKey") String apiKey,
            @Query("amount") String amount,
            @Query("currency") String currency,
            @Query("customer_id") String customer_id,
            @Query("payment_method_id") String payment_method_id,
            @Query("off_session") Boolean off_session,
            @Query("confirm") Boolean confirm
    );

    @GET("stripe/stripe-api.php")
    Call<StripeCardList> getStripeCards(
            @Query("get_cards") String get_cards,
            @Query("apiKey") String apiKey,
            @Query("customer_id") String customer_id
    );
}
