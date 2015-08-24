package com.mozu.mozuandroidinstoreassistant.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mozu.mozuandroidinstoreassistant.app.R;

public class ErrorMessageAlertDialog {

    public static AlertDialog getStandardErrorMessageAlertDialog(Context context, String message) {
        return new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
