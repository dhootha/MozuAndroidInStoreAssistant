package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

import java.util.List;

public class FulfillmentMoveToDataItem implements IData {

    List<OrderItem> items;

    public FulfillmentMoveToDataItem(List<OrderItem> items) {
        this.items = items;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
