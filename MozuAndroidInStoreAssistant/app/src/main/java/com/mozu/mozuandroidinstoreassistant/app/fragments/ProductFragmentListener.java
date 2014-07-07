package com.mozu.mozuandroidinstoreassistant.app.fragments;

import com.mozu.api.contracts.productruntime.Category;

public interface ProductFragmentListener {

    void onSearchPerformedFromProduct(int currentCategoryId, String query);
}
