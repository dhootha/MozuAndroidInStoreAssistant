package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.fragments.OrderDetailFullfillmentFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.OrderDetailNotesFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.OrderDetailOverviewFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.OrderDetailPaymentFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.OrderDetailReturnsFragment;

import java.util.List;

public class OrderDetailSectionPagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_OF_ORDER_DETAIL_TABS = 5;
    public static final int OVERVIEW_TAB_POSITION = 0;
    public static final int FULFILLMENT_TAB_POSITION = 1;
    public static final int PAYMENT_TAB_POSITION = 2;
    public static final int RETURNS_TABS_POSITION = 3;
    public static final int NOTES_TAB_POSITION = 4;

    private Order mOrder;
    private List<String> mPageTitles;

    private int mTenantId;
    private int mSiteId;

    public OrderDetailSectionPagerAdapter(FragmentManager manager, Order product, List<String> pageTitles, int tenantId, int siteId) {
        super(manager);

        mOrder = product;
        mPageTitles = pageTitles;

        mTenantId = tenantId;
        mSiteId = siteId;
    }

    @Override
    public int getCount() {

        return NUM_OF_ORDER_DETAIL_TABS;
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
                OrderDetailOverviewFragment overviewFragment = new OrderDetailOverviewFragment();
                overviewFragment.setOrder(mOrder);

                return overviewFragment;

            case FULFILLMENT_TAB_POSITION:
                OrderDetailFullfillmentFragment fullfillmentFragment = new OrderDetailFullfillmentFragment();
                fullfillmentFragment.setOrder(mOrder);

                return fullfillmentFragment;

            case PAYMENT_TAB_POSITION:
                OrderDetailPaymentFragment paymentFragment = new OrderDetailPaymentFragment();
                paymentFragment.setOrder(mOrder);

                return paymentFragment;

            case RETURNS_TABS_POSITION:
                OrderDetailReturnsFragment returnsFragment = new OrderDetailReturnsFragment();
                returnsFragment.setOrder(mOrder);

                return returnsFragment;

            case NOTES_TAB_POSITION:
                OrderDetailNotesFragment notesFragment = new OrderDetailNotesFragment();
                notesFragment.setOrder(mOrder);

                return notesFragment;

        }

        return null;
    }

}
