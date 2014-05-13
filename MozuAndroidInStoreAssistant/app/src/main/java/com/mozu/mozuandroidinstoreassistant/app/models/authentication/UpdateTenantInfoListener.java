package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.api.security.AuthenticationProfile;

public interface UpdateTenantInfoListener {

    void updateTenantFinished(AuthenticationProfile profile);
    void updateTenantFailed();

}
