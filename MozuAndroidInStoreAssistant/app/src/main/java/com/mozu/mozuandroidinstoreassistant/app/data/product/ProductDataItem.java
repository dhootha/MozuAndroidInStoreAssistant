package com.mozu.mozuandroidinstoreassistant.app.data.product;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class ProductDataItem implements IData {

    private Product mProduct;

    public ProductDataItem(Product product) {
        mProduct = product;
    }

   public Product getProduct() {
        return mProduct;
    }
}
