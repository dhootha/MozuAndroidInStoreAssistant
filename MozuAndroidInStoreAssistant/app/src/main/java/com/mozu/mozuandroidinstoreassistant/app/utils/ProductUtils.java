package com.mozu.mozuandroidinstoreassistant.app.utils;


public class ProductUtils {


    public static String getPackageorPickupProductCode(String productCode){
        if(productCode == null || productCode.length()<1)
            return productCode;

        int count = productCode.length() - productCode.replace("-", "").length();
        if (count <= 3) {
            return productCode;
        }

       int lastIndex = productCode.lastIndexOf("-");
       if(lastIndex == -1)
           return productCode;
       return productCode.substring(0,lastIndex);

    }
}
