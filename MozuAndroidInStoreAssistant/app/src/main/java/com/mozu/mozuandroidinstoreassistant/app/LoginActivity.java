package com.mozu.mozuandroidinstoreassistant.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mozu.api.contracts.core.UserAuthInfo;
import com.mozu.mozuandroidinstoreassistant.app.loaders.ProfileQuery;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationFailedSessionExpired;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AuthActivity implements LoaderCallbacks<Cursor>, OnClickListener {

    private static final int CONTACT_LOADER = 0;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mAppAuthErrorView;
    private  List<String> mEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getResources().getBoolean(R.bool.allow_portrait)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_login);
        setUpViews();
        showProgress(true);
    }

    private void setUpViews(){


        mAppAuthErrorView = findViewById(R.id.app_auth_error_layout);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    hideKeyboard();
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        hideKeyboard();
        if (mEmails != null) {
            addEmailsToAutoComplete(mEmails);
        }

    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(CONTACT_LOADER, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            UserAuthInfo authInfo = new UserAuthInfo();
            authInfo.setEmailAddress(email);
            authInfo.setPassword(password);
            super.setUserAuthInfo(authInfo);
            super.authenticateUser();
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void showProgress(final boolean show) {
        if (!show) {
            populateAutoComplete();
        }

        mAppAuthErrorView.setVisibility(View.GONE);

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        if (show) {
            hideKeyboard();
        }

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showErrorAuthenticatingApp() {
        hideKeyboard();
        mLoginFormView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        mAppAuthErrorView.setVisibility(View.VISIBLE);
        findViewById(R.id.try_app_auth_again_button).setOnClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

            // Select only email addresses.
            ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

            // Show primary email addresses first. Note that there won't be
            // a primary email address if the user hasn't specified one.
            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_login);
        setUpViews();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mEmails = new ArrayList<String>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            mEmails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        for (UserPreferences pref : getUserAuthStateMachine().getAllUserPrefs()) {
            mEmails.add(pref.getEmail());
        }

        addEmailsToAutoComplete(mEmails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    public void loginSuccess() {
        showProgress(true);
        if (getUserAuthStateMachine().getCurrentUserAuthState().isTenantSelectedState() && getUserAuthStateMachine().getTenantId() != null && getUserAuthStateMachine().getSiteId() != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {

            startActivity(new Intent(this, ChooseTenantAndSiteActivity.class));
        }

        finish();
    }

    public void loginFailure() {
        showProgress(false);
        //ewww, don't like this check here, not polymorphic and not cohesive to this class. This is the only activity
        //currently that contains showing this error, and I don't have time to come up with something else right now
        //I will keep it this way for now and readdress in a bit
        if (getUserAuthStateMachine().getCurrentUserAuthState() instanceof UserAuthenticationFailedSessionExpired) {
            mPasswordView.setError(getString(R.string.action_sign_in_session_expired));
        } else {
            mPasswordView.setError(getString(R.string.login_error));
        }

        mPasswordView.requestFocus();
    }

    @Override
    public void loadingState() {
        showProgress(true);
    }

    @Override
    public void stoppedLoading() {
        showProgress(false);
    }

    @Override
    public void authError() {
        mProgressView.setVisibility(View.GONE);
        showErrorAuthenticatingApp();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.try_app_auth_again_button) {
            showProgress(true);
            findViewById(R.id.try_app_auth_again_button).setOnClickListener(null);
            getAppAuthStateMachine().authenticateApp();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
    }

}



