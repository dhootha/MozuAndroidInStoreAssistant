package com.mozu.mozuandroidinstoreassistant.app.fragments;

import com.mozu.api.contracts.productruntime.Category;

public interface CategoryFragmentListener {

    void onLeafCategoryChosen(Category leaf);

}