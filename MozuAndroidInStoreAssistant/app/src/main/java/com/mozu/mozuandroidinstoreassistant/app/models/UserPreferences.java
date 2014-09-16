package com.mozu.mozuandroidinstoreassistant.app.models;

import java.util.ArrayList;
import java.util.List;

public class UserPreferences {

    private String mEmail;
    private String mDefaultTenantId;
    private String mDefaultSiteId;
    private boolean mShowAsGrids = true;

    private List<RecentSearch> mRecentProductSearches;

    private List<RecentSearch> mRecentOrderSearches;

    private List<RecentSearch> mRecentCustomerSearches;

    private List<RecentSearch> mRecentGlobalSearchs;

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

    public void setRecentProductSearchs(List<RecentSearch> recentProductSearchs) {

        mRecentProductSearches = recentProductSearchs;
    }

    public List<RecentSearch> getRecentProductSearches() {
        if (mRecentProductSearches == null) {
            mRecentProductSearches = new ArrayList<RecentSearch>();
        }

        return mRecentProductSearches;
    }

    public void setRecentGlobalSearchs(List<RecentSearch> recentProductSearchs) {

        mRecentGlobalSearchs = recentProductSearchs;
    }

    public List<RecentSearch> getRecentGlobalSearchs() {
        if (mRecentGlobalSearchs == null) {
            mRecentGlobalSearchs = new ArrayList<RecentSearch>();
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

    public void setRecentCustomerSearchs(List<RecentSearch> recentCustomerSearches) {

        mRecentCustomerSearches = recentCustomerSearches;
    }

    public List<RecentSearch> getRecentCustomerSearches() {
        if (mRecentCustomerSearches == null) {
            mRecentCustomerSearches = new ArrayList<RecentSearch>();
        }

        return mRecentCustomerSearches;
    }
}
