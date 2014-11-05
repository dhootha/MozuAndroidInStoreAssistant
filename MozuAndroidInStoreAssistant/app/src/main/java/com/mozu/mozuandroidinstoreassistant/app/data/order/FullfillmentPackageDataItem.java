package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.fulfillment.PackageItem;
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
        if (mFulfillmentItem.getOrderPackage() == null || mFulfillmentItem.getOrderPackage().getItems() == null || mFulfillmentItem.getOrderPackage().getItems().size() <= 0) {
            return 0;
        }

        int totalItemsCount = 0;
        for(PackageItem item:mFulfillmentItem.getOrderPackage().getItems()){
            totalItemsCount += item.getQuantity();
        }

        return totalItemsCount;
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
