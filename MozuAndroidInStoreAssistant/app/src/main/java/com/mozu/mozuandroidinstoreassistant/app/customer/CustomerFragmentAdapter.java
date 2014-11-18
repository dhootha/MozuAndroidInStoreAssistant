package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.mozu.api.contracts.customer.CustomerAccount;

public class CustomerFragmentAdapter extends FragmentStatePagerAdapter {

    private enum TabTypes {
        ACCOUNT_INFO("ACCOUNT INFO"),
        ADDRESSES("ADDRESSES"),
        ORDER_HISTORY("ORDER HISTORY"),
        WISHLIST("WISHLIST"),
        STORE_CREDIT("STORE CREDIT");
        String mDisplayTitle;

        TabTypes(String displayTitle) {
            mDisplayTitle = displayTitle;
        }
    }

    private CustomerAccount mCustomer;

    public CustomerFragmentAdapter(FragmentManager manager, CustomerAccount customer) {
        super(manager);
        mCustomer = customer;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setData(CustomerAccount customer) {
        mCustomer = customer;
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
            case ACCOUNT_INFO:
                CustomerAccountInfoFragment customerAccountInfoFragment = CustomerAccountInfoFragment.getInstance(mCustomer);
                return customerAccountInfoFragment;
            case ADDRESSES:
                CustomerAddressFragment customerAddressFragment = CustomerAddressFragment.getInstance(mCustomer);
                return customerAddressFragment;

            case ORDER_HISTORY:
                CustomerOrderHistoryFragment customerOrderHistoryFragment = CustomerOrderHistoryFragment.getInstance(mCustomer);
                return customerOrderHistoryFragment;

            case STORE_CREDIT:
                CustomerStoreCreditFragment customerStoreCreditFragment = CustomerStoreCreditFragment.getInstance(mCustomer);
                return customerStoreCreditFragment;

            case WISHLIST:
                CustomerWishListFragment customerWishListFragment = CustomerWishListFragment.getInstance(mCustomer);
                return customerWishListFragment;

        }

        return null;
    }

}
