package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class OrderReturnTitleDataItem implements IData {

    private String mTitle;
    public OrderReturnTitleDataItem(String title){
        mTitle = title;
    }

    public String getTitle(){
        return mTitle;
    }
}
