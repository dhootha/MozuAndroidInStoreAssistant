package com.mozu.mozuandroidinstoreassistant.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mozu.api.contracts.appdev.AppAuthInfo;
import com.mozu.api.contracts.core.UserAuthInfo;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.AppAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.AppAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import java.util.Observable;
import java.util.Observer;


public abstract class AuthActivity extends Activity implements Observer {

    private AppAuthenticationStateMachine mAppAuthStateMachine;
    private UserAuthenticationStateMachine mUserAuthStateMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupAppAuth();
    }

    public void setupAppAuth() {
        AppAuthInfo authInfo = new AppAuthInfo();
        authInfo.setApplicationId(getString(R.string.app_auth_appid));
        authInfo.setSharedSecret(getString(R.string.app_auth_shared_secret));

        mAppAuthStateMachine = AppAuthenticationStateMachineProducer.getInstance(this, authInfo, getString(R.string.service_url));
        mAppAuthStateMachine.addObserver(this);

        if (!mAppAuthStateMachine.getCurrentAppAuthState().isAuthenticatedState() && !mAppAuthStateMachine.getCurrentAppAuthState().isErrorState()) {
            loadingState();
            mAppAuthStateMachine.authenticateApp();

            return;
        }

        if (mAppAuthStateMachine.getCurrentAppAuthState().isErrorState()) {
            authError();

            return;
        } else {
            stoppedLoading();
        }

        //this means app is already authenticated, so we should auth user
        if (mUserAuthStateMachine == null) {
            setupUserAuth();
        }
    }

    public void setupUserAuth() {
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
            loadingState();
        } else {
            stoppedLoading();
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

    public void setUserAuthInfo(UserAuthInfo authInfo) {

        mUserAuthStateMachine.setUserAuthInfo(authInfo);
    }

    public void authenticateUser() {

        mUserAuthStateMachine.authenticateUser();
    }

    public AppAuthenticationStateMachine getAppAuthStateMachine() {

        return mAppAuthStateMachine;
    }

    public UserAuthenticationStateMachine getUserAuthStateMachine() {

        return mUserAuthStateMachine;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof AppAuthenticationStateMachine) {

            AppAuthenticationStateMachine machine = (AppAuthenticationStateMachine)observable;

            if (machine.getCurrentAppAuthState().isErrorState()) {

                return;
            }

            if (machine.getCurrentAppAuthState().isAuthenticatedState()) {
                appAuthenticated();

                if (getUserAuthStateMachine() == null) {
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
                loadingState();
                loadingState();
            } else {
                stoppedLoading();
            }
        }
    }

    private void appAuthenticated() {
        //user is already authenticated
        if (getUserAuthStateMachine() == null) {
            setupUserAuth();
            return;
        }
    }

    public abstract void loginSuccess();
    public abstract void loginFailure();
    public abstract void authError();
    public abstract void loadingState();
    public abstract void stoppedLoading();
}
