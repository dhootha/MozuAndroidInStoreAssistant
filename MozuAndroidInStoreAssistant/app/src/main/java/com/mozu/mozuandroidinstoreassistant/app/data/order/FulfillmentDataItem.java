package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class FulfillmentDataItem implements IData {
    private OrderItem mOrderItem;

    public FulfillmentDataItem(OrderItem orderItem) {
        mOrderItem = orderItem;
    }
    public OrderItem getOrderItem() {
        return mOrderItem;
    }

}
