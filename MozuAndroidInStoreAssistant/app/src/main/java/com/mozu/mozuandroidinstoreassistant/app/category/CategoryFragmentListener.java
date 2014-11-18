package com.mozu.mozuandroidinstoreassistant.app.category;

import com.mozu.api.contracts.productruntime.Category;

public interface CategoryFragmentListener {

    void onCategoryChosen(Category leaf);

    void onSearchPerformedFromCategory(int currentCategoryId, String query);
}
