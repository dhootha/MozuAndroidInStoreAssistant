package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

public interface AppAuthenticatorAsyncListener {

    void appInitialized();
    void errorInitializingApp(String errorMessage);

}
