package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class OrderItemRow implements IData {
    public OrderItem orderItem;

    public OrderItemRow(OrderItem orderItem) {
        this.orderItem = orderItem;
    }


}
