package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class FullfillmentHeaderDataItem implements IData {

    private String mHeaderName;
    public FullfillmentHeaderDataItem(String headerName) {
        mHeaderName = headerName;
    }

    public String getHeaderName() {
        return mHeaderName;
    }

}
