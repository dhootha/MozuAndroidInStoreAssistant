package com.mozu.mozuandroidinstoreassistant.app.models;

import java.util.ArrayList;
import java.util.List;

public class UserPreferences {

    private String mEmail;
    private String mDefaultTenantId;
    private String mDefaultSiteId;
    private boolean mShowAsGrids = true;

    private List<RecentProductSearch> mRecentProductSearches;

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


    public void setShowAsGrids(boolean showAsGrids) {

        mShowAsGrids = showAsGrids;
    }

    public boolean getShowAsGrids() {

        return mShowAsGrids;
    }

    public void setRecentProductSearchs(List<RecentProductSearch> recentProductSearchs) {

        mRecentProductSearches = recentProductSearchs;
    }

    public List<RecentProductSearch> getRecentProductSearches() {
        if (mRecentProductSearches == null) {
            mRecentProductSearches = new ArrayList<RecentProductSearch>();
        }

        return mRecentProductSearches;
    }
}
