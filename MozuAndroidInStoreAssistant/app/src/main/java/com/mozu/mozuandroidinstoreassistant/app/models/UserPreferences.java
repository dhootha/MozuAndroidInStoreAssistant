package com.mozu.mozuandroidinstoreassistant.app.models;

import java.util.ArrayList;
import java.util.List;

public class UserPreferences {

    private String mEmail;
    private String mDefaultTenantId;
    private String mDefaultTenantName;
    private String mDefaultSiteId;
    private String mDefaultSiteName;
    private String mDefaultSiteDomain;
    private String mDefaultLocationId;
    private String mDefaultLocationName;

    private boolean mShowAsGrids = true;

    private List<RecentSearch> mRecentProductSearches;

    private List<RecentSearch> mRecentOrderSearches;

    private List<RecentSearch> mRecentCustomerSearches;
    private List<RecentSearch> mRecentGlobalSearches;

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

    public String getDefaultLocationId() {
        return mDefaultLocationId;
    }

    public void setDefaultLocationId(String locationId) {
        mDefaultLocationId = locationId;
    }

    public String getDefaultLocationName() {
        return mDefaultLocationName;
    }

    public void setDefaultLocationName(String defaultLocationName) {
        mDefaultLocationName = defaultLocationName;
    }

    public String getDefaultTenantName() {

        return mDefaultTenantName;
    }

    public void setDefaultTenantName(String tenantName) {
        mDefaultTenantName = tenantName;
    }

    public String getDefaultSiteId() {

        return mDefaultSiteId;
    }

    public void setDefaultSiteId(String siteId) {
        mDefaultSiteId = siteId;
    }

    public String getDefaultSiteName() {
        return mDefaultSiteName;
    }

    public void setDefaultSiteName(String siteName) {
        mDefaultSiteName = siteName;
    }

    public String getDefaultSiteDomain() {
        return mDefaultSiteDomain;
    }

    public void setDefaultSiteDomain(String siteDomain) {
        mDefaultSiteDomain = siteDomain;
    }

    public boolean getShowAsGrids() {

        return mShowAsGrids;
    }

    public void setShowAsGrids(boolean showAsGrids) {

        mShowAsGrids = showAsGrids;
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

    public List<RecentSearch> getRecentGlobalSearchs() {
        if (mRecentGlobalSearches == null) {
            mRecentGlobalSearches = new ArrayList<RecentSearch>();
        }

        return mRecentGlobalSearches;
    }

    public void setRecentGlobalSearchs(List<RecentSearch> recentGlobalSearchs) {

        mRecentGlobalSearches = recentGlobalSearchs;
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
