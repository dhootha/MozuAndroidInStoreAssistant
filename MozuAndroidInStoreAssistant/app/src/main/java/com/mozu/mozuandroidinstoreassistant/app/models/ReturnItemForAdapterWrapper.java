package com.mozu.mozuandroidinstoreassistant.app.models;

import com.mozu.api.contracts.commerceruntime.returns.ReturnItem;

import org.joda.time.DateTime;


public class ReturnItemForAdapterWrapper {

    private ReturnItem mReturnItem;
    private DateTime mDate;
    private String mReturnType;

    public ReturnItemForAdapterWrapper() {

    }

    public ReturnItemForAdapterWrapper(ReturnItem returnItem, DateTime date, String returnType) {
        mReturnItem = returnItem;
        mDate = date;
        mReturnType = returnType;
    }

    public ReturnItem getReturnItem() {
        return mReturnItem;
    }

    public void setReturnItem(ReturnItem returnItem) {
        mReturnItem = returnItem;
    }

    public DateTime getDate() {
        return mDate;
    }

    public void setDate(DateTime date) {
        mDate = date;
    }

    public String getReturnType() {
        return mReturnType;
    }

    public void setReturnType(String returnType) {
        mReturnType = returnType;
    }
}
