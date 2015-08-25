package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Activity;
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
import com.mozu.mozuandroidinstoreassistant.app.OrderCreationAddCustomerActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.adapters.CustomerLookupAdapter;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomersLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomerLookupFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<CustomerAccount>>, AdapterView.OnItemClickListener, View.OnClickListener, CustomerCreationInterface {

    public static final int LOADER_CUSTOMER = 452;
    public static final int CREATE_CUSTOMER = 1;
    @InjectView(R.id.customer_lookup)
    AutoCompleteTextView customerLookup;
    @InjectView(R.id.create)
    Button mCreateCustomer;
    private CustomersLoader mCustomersLoader;
    private int mTenantId;
    private int mSiteId;
    private CustomerLookupAdapter mAdapter;
    private String mQuery = "";
    private CustomerSelectionListener mCustomerSelectionListener;

    public static CustomerLookupFragment getInstance(int tenantId, int siteId) {
        CustomerLookupFragment fragment = new CustomerLookupFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(OrderCreationAddCustomerActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(OrderCreationAddCustomerActivity.CURRENT_SITE_ID, siteId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CustomerSelectionListener) {
            mCustomerSelectionListener = (CustomerSelectionListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTenantId = getArguments().getInt(OrderCreationAddCustomerActivity.CURRENT_TENANT_ID);
            mSiteId = getArguments().getInt(OrderCreationAddCustomerActivity.CURRENT_SITE_ID);

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
        customerLookup.setOnItemClickListener(this);
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
        launchCreateCustomerDialog();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.getItemAtPosition(position) instanceof CustomerAccount && mCustomerSelectionListener != null) {
            mCustomerSelectionListener.onCustomerSelected((CustomerAccount) adapterView.getItemAtPosition(position));
        }
    }

    private void launchCreateCustomerDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(OrderCreationAddCustomerActivity.CURRENT_TENANT_ID, mTenantId);
        bundle.putInt(OrderCreationAddCustomerActivity.CURRENT_SITE_ID, mSiteId);
        Intent intent = new Intent(getActivity(), CustomerCreationActivity.class);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, CREATE_CUSTOMER);
    }

    @Override
    public void createCustomer() {
    }

    public interface CustomerSelectionListener {
        void onCustomerSelected(CustomerAccount customerAccount);
    }
}
