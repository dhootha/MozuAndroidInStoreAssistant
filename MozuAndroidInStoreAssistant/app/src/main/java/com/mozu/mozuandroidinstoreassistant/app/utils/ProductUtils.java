package com.mozu.mozuandroidinstoreassistant.app.utils;

/**
 * Created by santhosh_mankala on 10/2/14.
 */
public class ProductUtils {

    public static String getPackageorPickupProductCode(String productCode){
        if(productCode == null || productCode.length()<1)
            return null;

       int lastIndex = productCode.lastIndexOf("-");
       if(lastIndex == -1)
           return productCode;
       return productCode.substring(0,lastIndex);

    }
}
