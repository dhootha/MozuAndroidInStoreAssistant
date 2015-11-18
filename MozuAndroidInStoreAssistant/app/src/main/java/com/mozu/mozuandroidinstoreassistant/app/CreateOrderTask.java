package com.mozu.mozuandroidinstoreassistant.app;

import android.os.AsyncTask;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.resources.commerce.OrderResource;

public class CreateOrderTask extends AsyncTask<Void, Integer, Order> {


    private final CreateOrderListener mListener;
    private final Order mOrder;
    private Integer tenantId;
    private Integer siteId;

    public CreateOrderTask(Integer tenantId, Integer siteId, Order order, CreateOrderListener listener) {
        this.tenantId = tenantId;
        this.siteId = siteId;
        mOrder = order;
        mListener = listener;
    }

    @Override
    protected Order doInBackground(Void... params) {
        final OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));
        Order createdOrder = null;
        try {
            createdOrder = orderResource.createOrder(mOrder);
        } catch (Exception e) {
            mListener.errorCreatingOrder(e);
        }
        return createdOrder;
    }

    @Override
    protected void onPostExecute(Order order) {
        super.onPostExecute(order);
        mListener.orderCreated(order);
    }

    public interface CreateOrderListener {
        void orderCreated(Order order);

        void errorCreatingOrder(Exception e);
    }
}
