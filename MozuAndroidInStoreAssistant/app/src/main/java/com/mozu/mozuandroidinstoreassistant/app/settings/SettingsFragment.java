package com.mozu.mozuandroidinstoreassistant.app.settings;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.api.security.AppAuthenticator;
import com.mozu.mozuandroidinstoreassistant.app.ChooseTenantAndSiteActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.webview.WebViewActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingsFragment extends DialogFragment {

    @InjectView(R.id.imageView_close) ImageView mImageClose;
    @InjectView(R.id.default_store_value) TextView mDefaultStore;
    @InjectView(R.id.website_value) TextView mWebsiteValue;
    @InjectView(R.id.clear_search) Button mClearSearch;
    @InjectView(R.id.update_store) Button mUpdateStore;

    private UserAuthenticationStateMachine mUserState;
    public static SettingsFragment getInstance(){
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment_layout, null);
        mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        ButterKnife.inject(this, view);
        mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        setUpViews();
        return view;
    }

    private void setUpViews(){
        StringBuilder store = new StringBuilder();
        if (mUserState.getTenantName() != null) {
            store.append(mUserState.getTenantName());
        } else {
            store.append("N/A");
        }
        store.append(" (");
        if (mUserState.getSiteName() != null) {
            store.append(mUserState.getSiteName());
        } else {
            store.append("N/A");
        }
        store.append(") ");
        mDefaultStore.setText(store.toString());
        mUpdateStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tenantChooser = new Intent(getActivity(), ChooseTenantAndSiteActivity.class);
                tenantChooser.putExtra(ChooseTenantAndSiteActivity.LAUNCH_FROM_SETTINGS, true);
                startActivity(tenantChooser);
            }
        });
        mWebsiteValue.setText(mUserState.getSiteDomain());
        mWebsiteValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String httpString = (AppAuthenticator.isUseSSL()) ? "https:" : "http:";
                StringBuilder siteDomainBuilder = new StringBuilder(httpString).append("//").append(mUserState.getSiteDomain());

                Intent browse = new Intent(getActivity(), WebViewActivity.class);
                browse.putExtra(WebViewActivity.URL, siteDomainBuilder.toString());
                startActivity(browse);
            }
        });
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        if (enableClearSearch()) {
            mClearSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearSearch();
                }
            });
        } else {
            mClearSearch.setEnabled(false);
        }
    }


    private boolean enableClearSearch(){
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();
        if (prefs.getRecentCustomerSearches().size() > 0) {
            return true;
        }
        if (prefs.getRecentGlobalSearchs().size() > 0) {
            return true;
        }
        if (prefs.getRecentProductSearches().size() > 0) {
            return true;
        }
        if (prefs.getRecentOrderSearches().size() > 0) {
            return true;
        }

        return false;
    }

    private void clearSearch(){
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();
        prefs.getRecentCustomerSearches().clear();
        prefs.getRecentOrderSearches().clear();
        prefs.getRecentProductSearches().clear();
        prefs.getRecentGlobalSearchs().clear();
        mUserState.updateUserPreferences();
        mClearSearch.setEnabled(false);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
