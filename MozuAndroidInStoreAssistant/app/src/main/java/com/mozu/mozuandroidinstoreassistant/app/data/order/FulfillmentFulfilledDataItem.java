package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class FulfillmentFulfilledDataItem implements IData {

    private Pickup mPickup;
    private Integer mPickupCount;

    public FulfillmentFulfilledDataItem(Pickup mPickup, Integer mPickupCount) {
        this.mPickup = mPickup;
        this.mPickupCount = mPickupCount;
    }

    public Pickup getPickup() {
        return mPickup;
    }

    public Integer getPickupCount() {
        return mPickupCount;
    }
}
