package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class FullfillmentPickupItem implements IData {
    private Pickup mPickup;
    private Integer mPickupCount;

    public FullfillmentPickupItem(Pickup pickup, Integer pickUpCount) {
        mPickup = pickup;
        mPickupCount = pickUpCount;

    }

    public Pickup getPickup() {
        return mPickup;
    }

    public Integer getmPickupCount() {
        return mPickupCount;
    }
}
