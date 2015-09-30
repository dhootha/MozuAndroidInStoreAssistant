package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.orders.OrderAttribute;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class OrderAttributeRowItem implements IData{

    public OrderAttributeRowItem(OrderAttribute orderAttribute) {
        mOrderAttribute = orderAttribute;
    }

    public OrderAttribute mOrderAttribute;
}
