package com.mozu.mozuandroidinstoreassistant.app.data.customer;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class CustomerOverviewDataItem implements IData {

    private String mHeader1;
    private String mValue1;
    private String mHeader2;
    private String mValue2;

    public CustomerOverviewDataItem(String header1, String value1, String header2, String value2) {
        mHeader1 = header1;
        mHeader2 = header2;
        mValue1 = value1;
        mValue2 = value2;
    }

    public String getHeader1() {
        return mHeader1;
    }

    public String getHeader2() {
        return mHeader2;
    }

    public String getValue1() {
        return mValue1;
    }

    public String getValue2() {
        return mValue2;
    }

}
