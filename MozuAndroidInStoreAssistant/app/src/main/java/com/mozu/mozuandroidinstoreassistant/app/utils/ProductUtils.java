package com.mozu.mozuandroidinstoreassistant.app.utils;


import com.mozu.api.contracts.commerceruntime.products.Product;

public class ProductUtils {

    private static final String PRODUCT_CONFIGURABLE = "Configurable";

    public static String getPackageorPickupProductCode(String productCode) {
        if (productCode == null || productCode.length() < 1)
            return productCode;

        int count = productCode.length() - productCode.replace("-", "").length();
        if (count <= 3) {
            return productCode;
        }

        int lastIndex = productCode.lastIndexOf("-");
        if (lastIndex == -1)
            return productCode;
        return productCode.substring(0, lastIndex);

    }

    public static boolean isProductConfigurable(Product product) {
        return PRODUCT_CONFIGURABLE.equalsIgnoreCase(product.getProductUsage());
    }
}
