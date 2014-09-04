package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.os.AsyncTask;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productadmin.Category;
import com.mozu.api.contracts.productadmin.CategoryLocalizedImage;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.resources.commerce.catalog.admin.CategoryResource;
import com.mozu.api.resources.commerce.catalog.storefront.ProductResource;
import com.mozu.api.contracts.productruntime.ProductCollection;
import java.util.List;

public class CategoryImageUpdateTask extends AsyncTask<Integer,Void,Void> {

    public static final String FILTER_BY = "categoryId eq ";
    public static final String SORT_BY = "productname asc";

    private Integer mTenantId;
    private  Integer mSiteId;
    public CategoryImageUpdateTask(Integer tenantId, Integer siteId){
        mTenantId = tenantId;
        mSiteId = siteId;
    }
    @Override
    protected Void doInBackground(Integer... categoryIds) {
        Integer categoryId = categoryIds[0];
        CategoryResource categoryResource = new CategoryResource(new MozuApiContext(mTenantId,mSiteId));
        Category category = null;
        try {
            category = categoryResource.getCategory(categoryId);
            Category tempCat = category;
            while(tempCat.getChildCount() >0 ){
                tempCat = categoryResource.getChildCategories(categoryId).getItems().get(0);
            }

            ProductResource productResource = new ProductResource(new MozuApiContext(mTenantId, mSiteId));
            ProductCollection productCollection = productResource.getProducts(FILTER_BY + String.valueOf(tempCat.getId()), 1, 1, SORT_BY, null);
            if (productCollection != null && !productCollection.getItems().isEmpty()) {
                Product product = productCollection.getItems().get(0);
                String imageUrl = product.getContent().getProductImages().get(0).getImageUrl();

                List<CategoryLocalizedImage> categoryImageList = category.getContent().getCategoryImages();
                categoryImageList.get(0).setImageUrl(imageUrl);
                category.getContent().setCategoryImages(categoryImageList);
                categoryResource.updateCategory(category, categoryId);
            }

        }catch (Exception e) {
            return null;
        }

        return null;
    }



}
