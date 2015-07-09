package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.os.AsyncTask;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productadmin.Category;
import com.mozu.api.contracts.productadmin.CategoryLocalizedImage;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.contracts.productruntime.ProductCollection;
import com.mozu.api.resources.commerce.catalog.admin.CategoryResource;
import com.mozu.api.resources.commerce.catalog.storefront.ProductResource;

import java.util.ArrayList;
import java.util.List;

public class CategoryImageUpdateTask extends AsyncTask<Void,Void,Category> {

    public static final String FILTER_BY = "categoryId eq ";
    public static final String SORT_BY = "productname asc";

    private Integer mTenantId;
    private  Integer mSiteId;
    private CategoryImageUpdateListener mCategoryUpdateListener;
    private Integer mCategoryId;
    public  CategoryImageUpdateTask(CategoryImageUpdateListener categoryImageUpdateListener,Integer tenantId, Integer siteId,Integer categoryId){
        mTenantId = tenantId;
        mSiteId = siteId;
        mCategoryUpdateListener = categoryImageUpdateListener;
        mCategoryId = categoryId;
    }

    @Override
    protected Category doInBackground(Void... voids) {
        CategoryResource categoryResource = new CategoryResource(new MozuApiContext(mTenantId,mSiteId));
        Category category;
        try {
            category = categoryResource.getCategory(mCategoryId);
            Category tempCat = category;
            while (tempCat.getChildCount() > 0) {
                tempCat = categoryResource.getChildCategories(tempCat.getId()).getItems().get(0);
            }

            ProductResource productResource = new ProductResource(new MozuApiContext(mTenantId, mSiteId));
            ProductCollection productCollection = productResource.getProducts(FILTER_BY + String.valueOf(tempCat.getId()), 1, 5, SORT_BY, null);
            if (productCollection != null && !productCollection.getItems().isEmpty()) {
                String imageUrl = null;
                for (Product product : productCollection.getItems()) {
                    if (!product.getContent().getProductImages().isEmpty() && product.getContent().getProductImages().get(0).getImageUrl() != null) {
                        imageUrl = product.getContent().getProductImages().get(0).getImageUrl();
                        break;
                    }
                }

                List<CategoryLocalizedImage> categoryImageList = category.getContent().getCategoryImages();
                if (categoryImageList.size() > 0) {
                    categoryImageList.get(0).setImageUrl(imageUrl);
                } else {
                    categoryImageList = new ArrayList<CategoryLocalizedImage>();
                    CategoryLocalizedImage image = new CategoryLocalizedImage();
                    image.setImageUrl(imageUrl);
                    categoryImageList.add(image);
                }
                category.getContent().setCategoryImages(categoryImageList);
                return categoryResource.updateCategory(category,mCategoryId);
            }

        }catch (Exception e) {
            return null;
        }

        return null;
    }


    @Override
    protected void onPostExecute(Category category) {
        if (isCancelled()) {
            mCategoryUpdateListener.onImageUpdateFailure("Category update task cancelled");
        }

        if (mCategoryUpdateListener != null) {
            if (category != null) {
                mCategoryUpdateListener.onImageUpdateSucces(category.getContent().getName(), category.getId().toString());
            } else {
                mCategoryUpdateListener.onImageUpdateFailure("Failed to update image for category");
            }
        }
    }

}
