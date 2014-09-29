package com.mozu.mozuandroidinstoreassistant.app.data.customer;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class PrimaryAccountInfo implements IData{

    private String mCustomerSince;
    private Double mLiveTimeValue;
    private Integer mTotalVisits;
    private Integer mTotalOrders;
    private String mStoreCredits;



    public Integer getTotalOrders() {
        return mTotalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        mTotalOrders = totalOrders;
    }

    public String getCustomerSince() {
        return mCustomerSince;
    }

    public void setCustomerSince(String mCustomerSince) {
        this.mCustomerSince = mCustomerSince;
    }

    public Double getLiveTimeValue() {
        return mLiveTimeValue;
    }

    public void setLiveTimeValue(Double liveTimeValue) {
        mLiveTimeValue = liveTimeValue;
    }

    public Integer getTotalVisits() {
        return mTotalVisits;
    }

    public void setTotalVisits(Integer totalVisits) {
        mTotalVisits = totalVisits;
    }

    public String getStoreCredits() {
        return mStoreCredits;
    }

    public void setStoreCredits(String mStoreCredits) {
        this.mStoreCredits = mStoreCredits;
    }




}
