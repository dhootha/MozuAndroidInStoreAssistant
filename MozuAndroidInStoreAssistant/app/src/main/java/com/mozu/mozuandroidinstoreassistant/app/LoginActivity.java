package com.mozu.mozuandroidinstoreassistant.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.contracts.appdev.AppAuthInfo;
import com.mozu.api.contracts.core.UserAuthInfo;
import com.mozu.mozuandroidinstoreassistant.app.loaders.ProfileQuery;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.AppAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.AppAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import net.hockeyapp.android.UpdateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>, Observer, OnClickListener {

    private static final int CONTACT_LOADER = 0;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mAppAuthErrorView;

    private AppAuthenticationStateMachine mAppAuthStateMachine;
    private UserAuthenticationStateMachine mUserAuthStateMachine;

    private boolean mHaveNotAskedToUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_login);

        mAppAuthErrorView = findViewById(R.id.app_auth_error_layout);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
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
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        setupAppAuth();
    }

    private void setupAppAuth() {
        AppAuthInfo authInfo = new AppAuthInfo();
        authInfo.setApplicationId(getString(R.string.app_auth_appid));
        authInfo.setSharedSecret(getString(R.string.app_auth_shared_secret));

        mAppAuthStateMachine = AppAuthenticationStateMachineProducer.getInstance(authInfo, getString(R.string.service_url));
        mAppAuthStateMachine.addObserver(this);

        if (!mAppAuthStateMachine.getCurrentAppAuthState().isAuthenticatedState() && !mAppAuthStateMachine.getCurrentAppAuthState().isErrorState()) {
            showProgress(true);
            mAppAuthStateMachine.authenticateApp();

            return;
        }

        if (mAppAuthStateMachine.getCurrentAppAuthState().isErrorState()) {
            showErrorAuthenticatingApp();

            return;
        } else {
            showProgress(true);
        }

        //this means app is already authenticated, so we should auth user
        if (mUserAuthStateMachine == null) {
            setupUserAuth();
        }
    }

    private void setupUserAuth() {
        mUserAuthStateMachine = UserAuthenticationStateMachineProducer.getInstance(getApplicationContext());
        mUserAuthStateMachine.addObserver(this);

        if (!mAppAuthStateMachine.getCurrentAppAuthState().isAuthenticatedState()) {
            mAppAuthStateMachine.authenticateApp();
            return;
        }

        //user is already authenticated
        if (mUserAuthStateMachine.getCurrentUserAuthState().isAuthenticatedState()) {

            loginSuccess();
        }

        if (mUserAuthStateMachine.getCurrentUserAuthState().isErrorState()) {

            loginFailure();
        }

        if (mUserAuthStateMachine.getCurrentUserAuthState().isLoadingState()) {
            showProgress(true);
        } else {
            showProgress(false);
        }
    }

    @Override
    protected void onDestroy() {
        mAppAuthStateMachine.deleteObserver(this);

        if (mUserAuthStateMachine != null) {
            mUserAuthStateMachine.deleteObserver(this);
        }

        super.onDestroy();
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

            //Only register for updates if not a debug build
            if ( !(0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE) ) && mHaveNotAskedToUpdate) {
                mHaveNotAskedToUpdate = false;
                UpdateManager.register(this, getString(R.string.hockey_app_id));
            }
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            UserAuthInfo authInfo = new UserAuthInfo();
            authInfo.setEmailAddress(email);
            authInfo.setPassword(password);
            mUserAuthStateMachine.setUserAuthInfo(authInfo);
            mUserAuthStateMachine.authenticateUser();
        }
    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    public void showProgress(final boolean show) {
        if (!show) {
            populateAutoComplete();
        }

        mAppAuthErrorView.setVisibility(View.GONE);

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

    private void showErrorAuthenticatingApp() {
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
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        for (UserPreferences pref: mUserAuthStateMachine.getAllUserPrefs()) {
            emails.add(pref.getEmail());
        }

        addEmailsToAutoComplete(emails);
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

        if (mUserAuthStateMachine.getCurrentUserAuthState().isTenantSelectedState() && mUserAuthStateMachine.getCurrentUsersPreferences().getDontAskToSetTenantSiteIfSet()) {

            startActivity(new Intent(this, MainActivity.class));
        } else {

            startActivity(new Intent(this, ChooseTenantAndSiteActivity.class));
        }

        finish();
    }

    public void loginFailure() {
        showProgress(false);

        mPasswordView.setError(getString(R.string.login_error));
        mPasswordView.requestFocus();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof AppAuthenticationStateMachine) {

            AppAuthenticationStateMachine machine = (AppAuthenticationStateMachine)observable;

            if (machine.getCurrentAppAuthState().isErrorState()) {
                showErrorAuthenticatingApp();
                return;
            }

            if (machine.getCurrentAppAuthState().isAuthenticatedState()) {
                appAuthenticated();

                if (mUserAuthStateMachine == null) {
                    setupUserAuth();
                }

                return;
            }
        }

        if (observable instanceof UserAuthenticationStateMachine) {

            UserAuthenticationStateMachine machine = (UserAuthenticationStateMachine)observable;

            if (machine.getCurrentUserAuthState().isErrorState()) {
                loginFailure();

                return;
            }

            if (machine.getCurrentUserAuthState().isAuthenticatedState()) {
                loginSuccess();

                return;
            }

            if (machine.getCurrentUserAuthState().isLoadingState()) {
                showProgress(true);
            } else {
                showProgress(false);
                mEmailView.requestFocus();
            }
        }
    }

    private void appAuthenticated() {
        //user is already authenticated
        if (mUserAuthStateMachine == null) {
            setupUserAuth();
            return;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.try_app_auth_again_button) {
            showProgress(true);
            findViewById(R.id.try_app_auth_again_button).setOnClickListener(null);
            mAppAuthStateMachine.authenticateApp();
        }
    }
}



