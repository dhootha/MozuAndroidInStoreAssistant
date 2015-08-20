package com.mozu.mozuandroidinstoreassistant.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerAddAddressFragment;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerCreationFragment;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerCreationListener;
import com.mozu.mozuandroidinstoreassistant.app.customer.adapters.CustomerAddressesAdapter;

public class CustomerCreationActivity extends BaseActivity implements CustomerCreationListener, CustomerAddressesAdapter.AddressEditListener {

    public static final String CUSTOMER = "customer";
    private Integer mTenantId;
    private Integer mSiteId;
    private CustomerAccount mCustomerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_customer);

        if (savedInstanceState != null) {
            mTenantId = savedInstanceState.getInt(OrderCreationAddCustomerActivity.CURRENT_TENANT_ID, -1);
            mSiteId = savedInstanceState.getInt(OrderCreationAddCustomerActivity.CURRENT_SITE_ID, -1);
            Object temp = savedInstanceState.getSerializable(CUSTOMER);
            if (temp != null && temp instanceof CustomerAccount) {
                mCustomerAccount = (CustomerAccount) savedInstanceState.getSerializable(CUSTOMER);
            }
        } else {
            mTenantId = getIntent().getExtras().getInt(OrderCreationAddCustomerActivity.CURRENT_TENANT_ID, -1);
            mSiteId = getIntent().getExtras().getInt(OrderCreationAddCustomerActivity.CURRENT_SITE_ID, -1);
            CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId);
            getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment, "create_customer").commit();
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void onNextClicked(CustomerAccount account) {
        mCustomerAccount = account;
        CustomerAddAddressFragment customerCreationFragment = CustomerAddAddressFragment.getInstance(mTenantId, mSiteId, mCustomerAccount);
        getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment, "add_address").commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(OrderCreationAddCustomerActivity.CURRENT_TENANT_ID, mTenantId);
        outState.putInt(OrderCreationAddCustomerActivity.CURRENT_SITE_ID, mSiteId);
        outState.putSerializable(CUSTOMER, mCustomerAccount);
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
