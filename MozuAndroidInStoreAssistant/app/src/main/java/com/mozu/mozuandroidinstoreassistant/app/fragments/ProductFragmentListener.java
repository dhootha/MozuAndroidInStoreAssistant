package com.mozu.mozuandroidinstoreassistant.app.fragments;

public interface ProductFragmentListener {

    void onSearchPerformedFromProduct(int currentCategoryId, String query);
    void onProductChoosentFromProuct(String productCode);
}
