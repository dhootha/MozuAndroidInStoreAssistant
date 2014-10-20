package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.product.ProductDetailInventoryFragment;
import com.mozu.mozuandroidinstoreassistant.app.product.ProductDetailOverviewFragment;
import com.mozu.mozuandroidinstoreassistant.app.product.ProductDetailPropertiesFragment;
import com.mozu.mozuandroidinstoreassistant.app.product.ProductDetailShippingFragment;

import java.util.List;

public class ProductDetailSectionPagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_OF_PRODUCT_DETAIL_TABS = 4;
    public static final int OVERVIEW_TAB_POSITION = 0;
    public static final int PROPERTIES_TAB_POSITION = 1;
    public static final int SHIPPING_TABS_POSITION =2;
    public static final int INVENTORY_TAB_POSITION = 3;

    private Product mProduct;
    private List<String> mPageTitles;

    private int mTenantId;
    private int mSiteId;

    public ProductDetailSectionPagerAdapter(FragmentManager manager, Product product, List<String> pageTitles, int tenantId, int siteId) {
        super(manager);

        mProduct = product;
        mPageTitles = pageTitles;

        mTenantId = tenantId;
        mSiteId = siteId;
    }

    @Override
    public int getCount() {

        return NUM_OF_PRODUCT_DETAIL_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mPageTitles.size() - 1 < position) {
            return "";
        }

        return mPageTitles.get(position);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case OVERVIEW_TAB_POSITION:
                ProductDetailOverviewFragment overviewFragment = new ProductDetailOverviewFragment();
                overviewFragment.setProduct(mProduct);

                return overviewFragment;

            case PROPERTIES_TAB_POSITION:
                ProductDetailPropertiesFragment propertiesFragment = new ProductDetailPropertiesFragment();
                propertiesFragment.setProduct(mProduct);

                return propertiesFragment;

            case SHIPPING_TABS_POSITION:
                ProductDetailShippingFragment shippingFragment = new ProductDetailShippingFragment();
                shippingFragment.setProduct(mProduct);

                return shippingFragment;

            case INVENTORY_TAB_POSITION:
                ProductDetailInventoryFragment inventoryFragment = new ProductDetailInventoryFragment();
                inventoryFragment.setProduct(mProduct);
                inventoryFragment.setTenantId(mTenantId);
                inventoryFragment.setSiteId(mSiteId);

                return inventoryFragment;

        }

        return null;
    }

}
