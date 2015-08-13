package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.CustomerCreationActivity;
import com.mozu.mozuandroidinstoreassistant.app.OrderCreationActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.adapters.CustomersAdapter;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomersLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chris_pound on 8/5/15.
 */
public class CustomerLookupFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<CustomerAccount>>, AdapterView.OnItemSelectedListener, View.OnClickListener, CustomerCreationInterface {

    public static final int LOADER_CUSTOMER = 452;
    @InjectView(R.id.customer_lookup)
    AutoCompleteTextView customerLookup;
    @InjectView(R.id.create)
    Button mCreateCustomer;
    private CustomersLoader mCustomersLoader;
    private int mTenantId;
    private int mSiteId;
    private CustomersAdapter mAdapter;
    private String mQuery = "";

    public static CustomerLookupFragment getInstance(int tenantId, int siteId) {
        CustomerLookupFragment fragment = new CustomerLookupFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(OrderCreationActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(OrderCreationActivity.CURRENT_SITE_ID, siteId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTenantId = getArguments().getInt(OrderCreationActivity.CURRENT_TENANT_ID);
            mSiteId = getArguments().getInt(OrderCreationActivity.CURRENT_SITE_ID);

        }
        getLoaderManager().initLoader(LOADER_CUSTOMER, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_lookup, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customerLookup.setThreshold(1);
        customerLookup.setOnItemSelectedListener(this);
        mCreateCustomer.setOnClickListener(this);
        customerLookup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //DO NOTHING
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mQuery = customerLookup.getText().toString();
                getLoaderManager().restartLoader(LOADER_CUSTOMER, null, CustomerLookupFragment.this);
                Loader<List<CustomerAccount>> loader = getLoaderManager().getLoader(LOADER_CUSTOMER);
                mCustomersLoader = (CustomersLoader) loader;
                mCustomersLoader.forceLoad();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //DO NOTHING
            }
        });
    }

    @Override
    public Loader<List<CustomerAccount>> onCreateLoader(int id, Bundle args) {
        CustomersLoader loader = new CustomersLoader(getActivity(), mTenantId, mSiteId, mQuery);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<CustomerAccount>> loader, List<CustomerAccount> data) {
        if (mAdapter == null) {
            mAdapter = new CustomersAdapter(getActivity());
            customerLookup.setAdapter(mAdapter);
        }

        mAdapter.clear();
        mAdapter.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<CustomerAccount>> loader) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        launchCreateCustomerDialog();
    }

    private void launchCreateCustomerDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(OrderCreationActivity.CURRENT_TENANT_ID, mTenantId);
        bundle.putInt(OrderCreationActivity.CURRENT_SITE_ID, mSiteId);
        Intent intent = new Intent(getActivity(), CustomerCreationActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void createCustomer() {
    }
}
