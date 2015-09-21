package com.mozu.mozuandroidinstoreassistant.app.utils;

import com.mozu.mozuandroidinstoreassistant.app.order.OrderStrings;

public class FulfillmentUtils {
    public static String getDefaultFullfilment(String fulfillmentType) {
        if (fulfillmentType.equals("DirectShip")) {
            return OrderStrings.SHIP;
        } else if (fulfillmentType.equals("InStorePickup")) {
            return OrderStrings.PICKUP;
        } else {
            return OrderStrings.DIGITAL;
        }
    }


}
