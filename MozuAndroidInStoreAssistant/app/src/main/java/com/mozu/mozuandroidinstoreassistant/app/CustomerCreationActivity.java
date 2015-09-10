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
    public static final String CUSTOMER_CREATED = "customer_created";
    public static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    public static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";
    public static final String ORDER_CUSTOMER_EXTRA_KEY = "order_customer";
    public static final int CREATE_CUSTOMER = 1;
    private static String BILLING = "billing";
    private static String SHIPPING = "shipping";
    private Integer mTenantId;
    private Integer mSiteId;
    private CustomerAccount mCustomerAccount;
    private boolean mIsCustomerCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_customer);

        if (savedInstanceState != null) {
            mTenantId = savedInstanceState.getInt(CURRENT_TENANT_ID, -1);
            mSiteId = savedInstanceState.getInt(CURRENT_SITE_ID, -1);
            mIsCustomerCreated = getIntent().getExtras().getBoolean(CUSTOMER_CREATED, false);
            Object temp = savedInstanceState.getSerializable(CUSTOMER);
            if (temp != null && temp instanceof CustomerAccount) {
                mCustomerAccount = (CustomerAccount) savedInstanceState.getSerializable(CUSTOMER);
            }
        } else {
            mTenantId = getIntent().getExtras().getInt(CURRENT_TENANT_ID, -1);
            mSiteId = getIntent().getExtras().getInt(CURRENT_SITE_ID, -1);
            mIsCustomerCreated = getIntent().getExtras().getBoolean(CUSTOMER_CREATED, false);
            if (getIntent().getExtras().getSerializable("customer") != null) {
                mCustomerAccount = (CustomerAccount) getIntent().getExtras().getSerializable("customer");
            }
            if (mCustomerAccount != null) {
                if (mCustomerAccount.getContacts() != null && mCustomerAccount.getContacts().size() > 0) {
                    onNextClicked(mCustomerAccount);
                } else {
                    CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId, mCustomerAccount);
                    getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment, "create_customer").commit();
                }
            } else {
                CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId);
                getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment, "create_customer").commit();
            }
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void onNextClicked(CustomerAccount account) {
        mCustomerAccount = account;
        CustomerAddAddressFragment customerCreationFragment = CustomerAddAddressFragment.getInstance(mTenantId, mSiteId, mCustomerAccount, mIsCustomerCreated);
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
        outState.putInt(CURRENT_TENANT_ID, mTenantId);
        outState.putInt(CURRENT_SITE_ID, mSiteId);
        outState.putBoolean(CUSTOMER_CREATED, mIsCustomerCreated);
        outState.putSerializable(CUSTOMER, mCustomerAccount);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void addNewAddress(CustomerAccount customerAccount) {
        mCustomerAccount = customerAccount;
        CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId, mCustomerAccount);
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
