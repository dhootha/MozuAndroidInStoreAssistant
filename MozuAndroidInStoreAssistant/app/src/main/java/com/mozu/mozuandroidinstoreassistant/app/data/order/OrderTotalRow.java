package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class OrderTotalRow implements IData {

    public Order mOrder;

    public OrderTotalRow(Order order) {
        mOrder = order;
    }
}
