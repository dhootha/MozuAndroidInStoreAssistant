package com.mozu.mozuandroidinstoreassistant.app;

import android.os.Bundle;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerAddAddressFragment;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerCreationFragment;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerCreationListener;
import com.mozu.mozuandroidinstoreassistant.app.customer.adapters.CustomerAddressesAdapter;

public class CustomerCreationActivity extends BaseActivity implements CustomerCreationListener, CustomerAddressesAdapter.AddressEditListener {

    private Integer mTenantId;
    private Integer mSiteId;
    private CustomerAccount mCustomerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        setTitle("Create Customer");
        if (savedInstanceState != null) {
            mTenantId = savedInstanceState.getInt(OrderCreationActivity.CURRENT_TENANT_ID, -1);
            mSiteId = savedInstanceState.getInt(OrderCreationActivity.CURRENT_SITE_ID, -1);
            Object temp = savedInstanceState.getSerializable("customer");
            if (temp != null && temp instanceof CustomerAccount) {
                mCustomerAccount = (CustomerAccount) savedInstanceState.getSerializable("customer");
            }
        } else {
            mTenantId = getIntent().getExtras().getInt(OrderCreationActivity.CURRENT_TENANT_ID, -1);
            mSiteId = getIntent().getExtras().getInt(OrderCreationActivity.CURRENT_SITE_ID, -1);
            CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId);
            getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment, "create_customer").commit();
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //replace fragment
    public void onNextClicked(CustomerAccount account) {
        mCustomerAccount = account;
        CustomerAddAddressFragment customerCreationFragment = CustomerAddAddressFragment.getInstance(mTenantId, mSiteId, mCustomerAccount);
        getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment, "add_address").commit();
    }

    public void onCustomerSaved() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(OrderCreationActivity.CURRENT_TENANT_ID, mTenantId);
        outState.putInt(OrderCreationActivity.CURRENT_SITE_ID, mSiteId);
        outState.putSerializable("customer", mCustomerAccount);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void addNewAddress(CustomerAccount customerAccount) {
        mCustomerAccount = customerAccount;
        CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId, mCustomerAccount, -1);
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_fragment_holder, customerCreationFragment)
                .commit();
    }

    @Override
    public void onEditAddressClicked(int position) {
        CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId, mCustomerAccount, position);
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_fragment_holder, customerCreationFragment)
                .commit();
    }
}
