package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class FullfillmentCategoryHeaderDataItem implements IData {

    private String mHeaderName;

    public FullfillmentCategoryHeaderDataItem(String headerName) {
        mHeaderName = headerName;
    }

    public String getHeaderName() {
        return mHeaderName;
    }

}
