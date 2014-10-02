package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;

public class FullfillmentPackageDataItem implements IData {
    private FulfillmentItem mFulfillmentItem;

    public FullfillmentPackageDataItem(FulfillmentItem fulfillmentItem){
        mFulfillmentItem = fulfillmentItem;
    }

    public FulfillmentItem getFulfillmentItem(){
        return  mFulfillmentItem;
    }

    public Integer getPackageCount() {
        return mFulfillmentItem.getOrderPackage().getItems().size();
    }


    public String getPackageName() {
        return mFulfillmentItem.getPackageNumber();
    }


    public String getShipmentTrackingNumber() {
        if (mFulfillmentItem.getShipment() != null) {
            return mFulfillmentItem.getShipment().getTrackingNumber();
        } else {
            return null;
        }
    }


    public String getPackageTrackingNumber() {
        return mFulfillmentItem.getOrderPackage().getTrackingNumber();
    }


}
