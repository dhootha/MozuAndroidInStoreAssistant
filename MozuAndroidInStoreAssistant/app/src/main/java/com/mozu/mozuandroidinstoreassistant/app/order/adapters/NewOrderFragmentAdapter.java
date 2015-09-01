package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.order.NewOrderCreateFragment;
import com.mozu.mozuandroidinstoreassistant.app.order.OrderDetailNotesFragment;

public class NewOrderFragmentAdapter extends FragmentStatePagerAdapter {

    private Order mOrder;

    public NewOrderFragmentAdapter(FragmentManager manager, Order order) {
        super(manager);
        mOrder = order;
    }

    @Override
    public int getCount() {
        return TabTypes.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TabTypes.values()[position].mDisplayTitle;
    }

    @Override
    public Fragment getItem(int position) {
        switch (TabTypes.values()[position]) {
            case NOTES:
                OrderDetailNotesFragment fragment = OrderDetailNotesFragment.getInstance();
                fragment.setOrder(mOrder);
                return fragment;
            case DETAILS:
                NewOrderCreateFragment newOrderCreateFragment = NewOrderCreateFragment.getInstance(true);
                newOrderCreateFragment.setOrder(mOrder);
                return newOrderCreateFragment;
        }
        return null;
    }

    private enum TabTypes {
        NOTES("NOTES"),
        DETAILS("DETAILS");

        String mDisplayTitle;

        TabTypes(String displayTitle) {
            mDisplayTitle = displayTitle;
        }
    }
}
