package com.capi.ecomshoppingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DialogUtils {

    @Nullable
    public static Dialog custom_alertDialog = null;
    static boolean mStartRecording = true;
    static int mRecordPromptCount = 0;
    static long timeWhenPaused = 0;
    static boolean mPauseRecording = true;

    @Nullable
    private static ProgressDialog sProgressDialog;
    @Nullable
    private static Dialog alertDialog;

    public static void displayProgressDialog(@NonNull Context context) {
        try {
            displayProgressDialog(context, null);
        } catch (Exception ex) {

        }

    }

    /**
     * Dismisses progress dialog.
     */
    public static void hideProgressDialog() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            try {
                sProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            sProgressDialog = null;
        }
    }


    public static void showNetworkAlertDialog(Context mContext) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage("Please check you");
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                alertDialog.dismiss();
            }
        });

    }

    /**
     * Creates and shows progress dialog and sets your OnCancelListener
     *
     * @param context
     * @param listener
     */
    public static void displayProgressDialog(@NonNull Context context,
                                             DialogInterface.OnCancelListener listener) {

        try {
            displayProgressDialog(context, listener,
                    "Loading...");
        } catch (Exception ex) {

        }
    }


    public static void displayProgressDialog(@Nullable Context context,
                                             DialogInterface.OnCancelListener listener, String msg) {
        // Log.e("test","g");
        if (sProgressDialog != null && sProgressDialog.isShowing())
            return;

        if (context != null) {
            sProgressDialog = new ProgressDialog(context);
            sProgressDialog = ProgressDialog.show(context, null, null, true, false);
            sProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sProgressDialog.setContentView(R.layout.progress_bar);


            sProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            sProgressDialog.setOnCancelListener(listener);

        }
    }
}
