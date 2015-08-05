package com.mozu.mozuandroidinstoreassistant.app.order;

import com.mozu.api.contracts.commerceruntime.orders.Order;

/**
 * Created by chris_pound on 8/5/15.
 */
public interface CreateOrderListener {
    void createNewOrder(Order order);
}
