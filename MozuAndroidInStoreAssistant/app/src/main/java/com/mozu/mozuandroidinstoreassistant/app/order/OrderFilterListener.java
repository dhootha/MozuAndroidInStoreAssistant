package com.mozu.mozuandroidinstoreassistant.app.order;

public interface OrderFilterListener {
    void filter(String start, String end, String status, String paymentStatus);
}
