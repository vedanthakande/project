package com.capi.ecomshoppingapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public class Preferences {
    private static Preferences sInstance;
    private final Context context;
    private final SharedPreferences sharedPreferences;

    private final String PREFS = "Prefs";
    private final String STRIPE_ID = "STRIPE_ID";

    private Preferences(Context c) {
        context = c;
        sharedPreferences = context.getSharedPreferences(PREFS,
                Context.MODE_PRIVATE);

    }

    @Nullable
    public static Preferences getInstance(@Nullable Context c) {
        if (sInstance == null) {
            if (c == null) {
                return null;
            }
            sInstance = new Preferences(c);

        }

        return sInstance;
    }

    public String getSTRIPE_ID() {
        return sharedPreferences.getString(STRIPE_ID, "");
    }

    public void setSTRIPE_ID(String stripe_id) {
        sharedPreferences.edit().putString(STRIPE_ID, stripe_id).commit();
    }
}
