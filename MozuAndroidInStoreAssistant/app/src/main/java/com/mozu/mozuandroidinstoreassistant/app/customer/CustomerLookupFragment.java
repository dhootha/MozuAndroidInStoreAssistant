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
import android.widget.ProgressBar;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.CustomerUpdateActivity;
import com.mozu.mozuandroidinstoreassistant.app.OrderCreationAddCustomerActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.adapters.CustomerLookupAdapter;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomersLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomerLookupFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<CustomerAccount>>, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final int LOADER_CUSTOMER = 452;
    private static String SEARCH = "search";
    @InjectView(R.id.customer_lookup)
    AutoCompleteTextView customerLookup;
    @InjectView(R.id.create)
    Button mCreateCustomer;
    @InjectView(R.id.lookup_spinner)
    ProgressBar mCustomerProgressBar;
    private CustomersLoader mCustomersLoader;
    private int mTenantId;
    private int mSiteId;
    private CustomerLookupAdapter mAdapter;
    private String mQuery = "";

    public static CustomerLookupFragment getInstance(int tenantId, int siteId) {
        CustomerLookupFragment fragment = new CustomerLookupFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CustomerUpdateActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(CustomerUpdateActivity.CURRENT_SITE_ID, siteId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTenantId = getArguments().getInt(CustomerUpdateActivity.CURRENT_TENANT_ID);
            mSiteId = getArguments().getInt(CustomerUpdateActivity.CURRENT_SITE_ID);

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
        customerLookup.setThreshold(0);
        customerLookup.setOnItemClickListener(this);
        customerLookup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getLoaderManager().restartLoader(LOADER_CUSTOMER, null, CustomerLookupFragment.this);
                    Loader<List<CustomerAccount>> loader = getLoaderManager().getLoader(LOADER_CUSTOMER);
                    mCustomersLoader = (CustomersLoader) loader;
                    mCustomersLoader.forceLoad();
                }
            }
        });
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
                mCustomerProgressBar.setVisibility(View.VISIBLE);
                mCustomersLoader = (CustomersLoader) loader;
                mCustomersLoader.forceLoad();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //DO NOTHING
            }
        });
        if (savedInstanceState != null) {
            String search = savedInstanceState.getString(SEARCH);
            customerLookup.setText(search);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH, customerLookup.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<List<CustomerAccount>> onCreateLoader(int id, Bundle args) {
        CustomersLoader loader = new CustomersLoader(getActivity(), mTenantId, mSiteId, mQuery);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<CustomerAccount>> loader, List<CustomerAccount> data) {
        mCustomerProgressBar.setVisibility(View.INVISIBLE);
        if (mAdapter == null) {
            mAdapter = new CustomerLookupAdapter(getActivity());
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
    public void onClick(View v) {
        launchCreateCustomerDialog(null, false);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.getItemAtPosition(position) instanceof CustomerAccount) {
            CustomerAccount customerAccount = (CustomerAccount) adapterView.getItemAtPosition(position);
            launchCreateCustomerDialog(customerAccount, true);
        }
    }

    private void launchCreateCustomerDialog(CustomerAccount customerAccount, boolean isCustomerCreated) {
        Bundle bundle = new Bundle();
        bundle.putInt(CustomerUpdateActivity.CURRENT_TENANT_ID, mTenantId);
        bundle.putInt(CustomerUpdateActivity.CURRENT_SITE_ID, mSiteId);
        bundle.putBoolean(CustomerUpdateActivity.CUSTOMER_CREATED, isCustomerCreated);
        bundle.putSerializable(CustomerUpdateActivity.CUSTOMER, customerAccount);
        Intent intent = new Intent(getActivity(), CustomerUpdateActivity.class);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, OrderCreationAddCustomerActivity.CREATE_CUSTOMER);
    }
}
