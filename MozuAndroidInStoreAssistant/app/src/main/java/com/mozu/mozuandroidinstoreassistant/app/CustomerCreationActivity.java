package com.mozu.mozuandroidinstoreassistant.app;

import android.os.Bundle;

import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerCreationFragment;

public class CustomerCreationActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        setTitle("Create Customer");
        int tenantId = getIntent().getExtras().getInt(OrderCreationActivity.CURRENT_TENANT_ID, -1);
        int siteId = getIntent().getExtras().getInt(OrderCreationActivity.CURRENT_SITE_ID, -1);
        CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(tenantId, siteId);
        getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment).commit();
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
