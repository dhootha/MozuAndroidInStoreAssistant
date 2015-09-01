package com.mozu.mozuandroidinstoreassistant.app.utils;

import com.mozu.mozuandroidinstoreassistant.app.order.OrderDetailFullfillmentFragment;

public class FulfillmentUtils {


    public static String getDefaultFullfilment(String fulfillmentType) {
        if (fulfillmentType.equals("DirectShip")) {
            return OrderDetailFullfillmentFragment.SHIP;
        } else if (fulfillmentType.equals("InStorePickup")) {
            return OrderDetailFullfillmentFragment.PICKUP;
        } else {
            return OrderDetailFullfillmentFragment.DIGITAL;
        }
    }


}
