package com.mozu.mozuandroidinstoreassistant.app.order;

/**
 * Created by chris_pound on 7/22/15.
 */
public interface OrderFilterListener {
    void filter(String start, String end, String status, String paymentStatus);
}
