package com.mozu.mozuandroidinstoreassistant.app.order;

import com.mozu.api.contracts.commerceruntime.orders.Order;

public interface OrderListener {

    void orderSelected(Order order);

}
