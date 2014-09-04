package com.mozu.mozuandroidinstoreassistant.app.models;

import java.util.ArrayList;
import java.util.List;

public class UserPreferences {

    private String mEmail;
    private String mDefaultTenantId;
    private String mDefaultSiteId;
    private String mDefaultSiteDomain;
    private boolean mShowAsGrids = true;

    private List<RecentSearch> mRecentProductSearches;

    private List<RecentSearch> mRecentOrderSearches;

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

    public String getDefaultSiteDomain() {

        return mDefaultSiteId;
    }

    public void setDefaultSiteDomain(String siteDomain) {

        mDefaultSiteDomain = siteDomain;
    }


    public void setShowAsGrids(boolean showAsGrids) {

        mShowAsGrids = showAsGrids;
    }

    public boolean getShowAsGrids() {

        return mShowAsGrids;
    }

    public void setRecentProductSearchs(List<RecentSearch> recentProductSearchs) {

        mRecentProductSearches = recentProductSearchs;
    }

    public List<RecentSearch> getRecentProductSearches() {
        if (mRecentProductSearches == null) {
            mRecentProductSearches = new ArrayList<RecentSearch>();
        }

        return mRecentProductSearches;
    }

    public void setRecentOrderSearchs(List<RecentSearch> recentOrderSearchs) {

        mRecentOrderSearches = recentOrderSearchs;
    }

    public List<RecentSearch> getRecentOrderSearches() {
        if (mRecentOrderSearches == null) {
            mRecentOrderSearches = new ArrayList<RecentSearch>();
        }

        return mRecentOrderSearches;
    }
}
