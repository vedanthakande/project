package com.capi.ecomshoppingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserSession {

    SharedPreferences pref;
    Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    public static final String PREFER_NAME = "Reg";
    public static final String STRIPE_ID = "STRIPE_ID";

    public UserSession(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getStripeId() {
        return pref.getString(STRIPE_ID, "");
    }

    public void setStripeId(String stripeId) {
        editor.putString(STRIPE_ID, stripeId);
    }
}