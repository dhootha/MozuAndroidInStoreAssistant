package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mozu.mozuandroidinstoreassistant.app.OrderCreationActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;

/**
 * Created by chris_pound on 8/12/15.
 */
public class CustomerAddAddressFragment extends Fragment {


    private int mTenantId;
    private int mSiteId;

    public static CustomerAddAddressFragment getInstance(Integer tenantId, Integer siteId) {
        Bundle bundle = new Bundle();
        CustomerAddAddressFragment fragment = new CustomerAddAddressFragment();
        bundle.putInt(OrderCreationActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(OrderCreationActivity.CURRENT_SITE_ID, siteId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_add_address, container, false);
        ButterKnife.inject(this, view);
        mTenantId = getArguments().getInt(OrderCreationActivity.CURRENT_TENANT_ID);
        mSiteId = getArguments().getInt(OrderCreationActivity.CURRENT_SITE_ID);
        return view;
    }
}
