package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.clients.commerce.catalog.admin.CategoryClient;
import com.mozu.api.contracts.productruntime.Category;
import com.mozu.api.contracts.productruntime.CategoryCollection;
import com.mozu.api.resources.commerce.catalog.storefront.CategoryResource;

import java.util.ArrayList;
import java.util.List;

public class CategoryLoader extends InternetConnectedAsyncTaskLoader<List<Category>> {

    private List<Category> mCategoryList;
    private Integer mTenantId;
    private Integer mSiteId;

    public CategoryLoader(Context context, Integer tenantId, Integer siteId) {
        super(context);

        mTenantId = tenantId;
        mSiteId = siteId;
    }

    @Override
    protected void onForceLoad() {

        super.onForceLoad();
    }

    @Override
    public List<Category> loadInBackground() {
        super.loadInBackground();

        mCategoryList = loadCategoriesFromWeb();

        return mCategoryList;
    }

    @Override
    public void deliverResult(List<Category> data) {
        if (isReset())
            return;

        if (isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mCategoryList == null) {
            mCategoryList = new ArrayList<Category>();
        }

        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {

        cancelLoad();
    }

    @Override
    public void onCanceled(List<Category> data) {

        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();

        mCategoryList = null;

        super.onReset();
    }

    private List<Category> loadCategoriesFromWeb() {
        List<Category> allCategories = new ArrayList<Category>();

        CategoryCollection categoryPagedCollection;

        CategoryResource categoryResource = new CategoryResource(new MozuApiContext(mTenantId, mSiteId));

        try {
            categoryPagedCollection = categoryResource.getCategoryTree();

            allCategories = categoryPagedCollection.getItems();

            allCategories.get(0).getContent().getCategoryImages()
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return allCategories;
    }

}