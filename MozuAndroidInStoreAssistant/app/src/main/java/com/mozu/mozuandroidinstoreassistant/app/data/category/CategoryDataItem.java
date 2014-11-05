package com.mozu.mozuandroidinstoreassistant.app.data.category;

import com.mozu.api.contracts.productruntime.Category;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class CategoryDataItem implements IData {
    private Category mCategory;

    public CategoryDataItem(Category category) {
        mCategory = category;
    }

    public Category getCategory() {
        return mCategory;
    }

}
