package com.mozu.mozuandroidinstoreassistant.app.order;

import com.mozu.api.contracts.commerceruntime.orders.Order;

import java.util.ArrayList;

public interface OrderListener {

    void orderSelected(Order order,ArrayList<Order> orderList,int position);

}
