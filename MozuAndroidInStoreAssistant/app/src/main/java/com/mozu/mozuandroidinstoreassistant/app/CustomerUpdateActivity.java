package com.mozu.mozuandroidinstoreassistant.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerAddAddressFragment;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerCreationFragment;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerCreationListener;
import com.mozu.mozuandroidinstoreassistant.app.customer.adapters.CustomerAddressesAdapter;

import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;

public class CustomerUpdateActivity extends BaseActivity implements CustomerCreationListener, CustomerAddressesAdapter.AddressEditListener {

    public static final String CUSTOMER = "customer";
    public static final String CUSTOMER_CREATED = "customer_created";
    public static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    public static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";
    private Integer mTenantId;
    private Integer mSiteId;
    private CustomerAccount mCustomerAccount;
    private boolean mIsCustomerCreated;
    private Set<Integer> alreadyCreatedAddresses = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        ButterKnife.inject(this);
        if (savedInstanceState != null) {
            mTenantId = savedInstanceState.getInt(CURRENT_TENANT_ID, -1);
            mSiteId = savedInstanceState.getInt(CURRENT_SITE_ID, -1);
            mIsCustomerCreated = savedInstanceState.getBoolean(CUSTOMER_CREATED, false);
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
                    //customer is created with addresses
                    if (alreadyCreatedAddresses == null || !alreadyCreatedAddresses.isEmpty()) {
                        alreadyCreatedAddresses = new HashSet<>();
                    }
                    if (mCustomerAccount != null && mCustomerAccount.getContacts() != null) {
                        for (CustomerContact contact : mCustomerAccount.getContacts()) {
                            alreadyCreatedAddresses.add(contact.getId());
                        }
                    }
                    onNextClicked(mCustomerAccount);
                } else {
                    //customer is created but has no addresses
                    CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId, mCustomerAccount);
                    getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment, "create_customer").commit();
                }
            } else {
                //customer is not created
                CustomerCreationFragment customerCreationFragment = CustomerCreationFragment.getInstance(mTenantId, mSiteId);
                getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment, "create_customer").commit();
            }
        }
    }

    public void onNextClicked(CustomerAccount account) {
        mCustomerAccount = account;
        CustomerAddAddressFragment customerCreationFragment = CustomerAddAddressFragment.getInstance(mTenantId, mSiteId, mCustomerAccount, mIsCustomerCreated);
        getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, customerCreationFragment, "add_address").commit();
    }

    public Set<Integer> getAlreadyCreatedAddresses() {
        return alreadyCreatedAddresses;
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
