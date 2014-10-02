package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class FullfillmentDataItem implements IData {
    private OrderItem mOrderItem;

    public FullfillmentDataItem(OrderItem orderItem){
        mOrderItem = orderItem;
    }
    public OrderItem getOrderItem() {
        return mOrderItem;
    }

}
