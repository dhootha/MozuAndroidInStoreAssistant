package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.fulfillment.FulfillmentInfo;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class ShippingItemRow implements IData {

    public Order mOrder;
    public FulfillmentInfo mCurrentFulfillmentInfo;

    public ShippingItemRow(Order order, FulfillmentInfo currentFulFillmentInfo) {
        mOrder = order;
        mCurrentFulfillmentInfo = currentFulFillmentInfo;
    }
}
