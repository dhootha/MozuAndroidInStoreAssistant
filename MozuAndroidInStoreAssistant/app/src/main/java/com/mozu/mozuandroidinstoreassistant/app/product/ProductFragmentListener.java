package com.mozu.mozuandroidinstoreassistant.app.product;

public interface ProductFragmentListener {

    void onSearchPerformedFromProduct(int currentCategoryId, String query);
    void onProductChoosentFromProuct(String productCode);
}
