package com.mozu.mozuandroidinstoreassistant.app.models;

public class UserPreferences {

    private String mEmail;
    private String mDefaultTenantId;
    private String mDefaultSiteId;

    private boolean mDontAskToSetTenant;

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getDefaultTenantId() {
        return mDefaultTenantId;
    }

    public void setDefaultTenantId(String tenantId) {
        mDefaultTenantId = tenantId;
    }

    public String getDefaultSiteId() {
            return mDefaultSiteId;
    }

    public void setDefaultSiteId(String siteId) {
        mDefaultSiteId = siteId;
    }

    public void setDontAskToSetTenant(boolean dontAsk) {

        mDontAskToSetTenant = dontAsk;
    }

    public boolean getDontAskToSetTenant() {

        return mDontAskToSetTenant;
    }
}