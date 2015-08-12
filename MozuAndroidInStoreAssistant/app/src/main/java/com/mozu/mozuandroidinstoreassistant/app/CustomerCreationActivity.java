package com.mozu.mozuandroidinstoreassistant.app;

import android.os.Bundle;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerAddAddressFragment;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerCreationFragment;

public class CustomerCreationActivity extends BaseActivity {

    private Integer mTenantId;
    private Integer mSiteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        setTitle("Create Customer");
        mTenantId = getIntent().getExtras().getInt(OrderCreationActivity.CURRENT_TENANT_ID, -1);
        mSiteId = getIntent().getExtras().getInt(OrderCreationActivity.CURRENT_SITE_ID, -1);
        CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId);
        getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment).commit();
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //replace fragment
    public void onNextClicked(CustomerAccount account) {
        CustomerAddAddressFragment customerCreationFragment = CustomerAddAddressFragment.getInstance(mTenantId, mSiteId);
        getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment).commit();
    }
}
