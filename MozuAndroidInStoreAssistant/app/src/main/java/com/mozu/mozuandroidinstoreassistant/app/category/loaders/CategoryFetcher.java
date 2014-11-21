package com.mozu.mozuandroidinstoreassistant.app.category.loaders;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productruntime.Category;
import com.mozu.api.contracts.productruntime.CategoryCollection;
import com.mozu.api.contracts.productruntime.ProductCollection;
import com.mozu.api.resources.commerce.catalog.storefront.CategoryResource;
import com.mozu.api.resources.commerce.catalog.storefront.ProductResource;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class CategoryFetcher {

    private static final int ITEMS_PER_PAGE = 50;
    public static final String SORT_BY = "productname asc";
    public static final String FILTER_BY = "categoryId eq ";
    public static final String CATEGORY_RESPONSE_FIELDS = "items(id,Content,ChildrenCategories)";
    public static final String PRODUCT_RESPONSE_FIELDS = "items(id,Content,ProductCode,Price)";



    private Integer mCategoryId;
    private Integer mCurrentPage;
    public Observable<List<Category>> getCategoryInformation(Integer tenantId, Integer siteId) {
        final CategoryResource categoryResource = new CategoryResource(new MozuApiContext(tenantId, siteId));

        return Observable
                .create(new Observable.OnSubscribe<List<Category>>() {
                    @Override
                    public void call(Subscriber<? super List<Category>> subscriber) {
                        List<Category> allCategories;
                        CategoryCollection categoryCollection;
                        try {
                            categoryCollection = categoryResource.getCategoryTree(CATEGORY_RESPONSE_FIELDS);
                            allCategories = categoryCollection.getItems();
                            subscriber.onNext(allCategories);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }
    public Observable<ProductCollection> getProductInformation(Integer tenantId, Integer siteId) {
        final ProductResource productResource = new ProductResource(new MozuApiContext(tenantId, siteId));
        return Observable
                .create(new Observable.OnSubscribe<ProductCollection>() {
                    @Override
                    public void call(Subscriber<? super ProductCollection> subscriber) {
                        ProductCollection productCollection;
                        try {
                            productCollection = productResource.getProducts(FILTER_BY + String.valueOf(mCategoryId), mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, SORT_BY, PRODUCT_RESPONSE_FIELDS);
                            subscriber.onNext(productCollection);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }


    public Integer getCategoryId(){
        return mCategoryId;
    }

    public Integer getCurrentPage(){
        return mCurrentPage;
    }

    public void setCategoryId(Integer categoryId){
        mCategoryId = categoryId;

    }

    public void setCurrentPage(Integer currentPage){
        mCurrentPage = currentPage;

    }

}
