package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

public class SetDefaultFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private SetDefaultFragmentListener mListener;

    public SetDefaultFragment() {
        //required empty constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle(getString(R.string.set_default_tenant_site_title));
        alertDialogBuilder.setMessage(getString(R.string.set_default_tenant_site_message));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes), this);
        alertDialogBuilder.setNegativeButton(getString(R.string.no), this);

        return alertDialogBuilder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        //this is monster hacky, but not sure how else to change the style of this one bar
        int titleId = getDialog().getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
        View title = getDialog().findViewById(titleId);
        if (title != null) {
            ((TextView) title).setTextColor(getActivity().getResources().getColor(R.color.mozu_color));
        }

        int divierId = getDialog().getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = getDialog().findViewById(divierId);
        if (divider != null) {
            divider.setBackgroundColor(getActivity().getResources().getColor(R.color.mozu_color));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (SetDefaultFragmentListener) activity;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {

            mListener.setChosenTenantSiteAsDefault();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {

            mListener.doNotSetDefault();
        }

        dismiss();
    }
}
