package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.order.NewOrderCreateFragment;
import com.mozu.mozuandroidinstoreassistant.app.order.OrderDetailNotesFragment;

/**
 * Created by santhosh_mankala on 8/14/15.
 */
public class NewOrderFragmentAdapter extends FragmentStatePagerAdapter {

    private CustomerAccount mCustomer;
    private Order mOrder;

    public NewOrderFragmentAdapter(FragmentManager manager, Order order) {
        super(manager);
        mOrder = order;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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
                OrderDetailNotesFragment fragment = OrderDetailNotesFragment.getInstance(true);
                fragment.setOrder(mOrder);
                return fragment;
            default:
                return NewOrderCreateFragment.getInstance(mOrder, true);
        }
    }

    private enum TabTypes {
        DETAILS("DETAILS"),
        NOTES("NOTES");
        String mDisplayTitle;

        TabTypes(String displayTitle) {
            mDisplayTitle = displayTitle;
        }
    }
}
