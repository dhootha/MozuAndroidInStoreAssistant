package com.mozu.mozuandroidinstoreassistant.app;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.order.CreateOrderListener;
import com.mozu.mozuandroidinstoreassistant.app.order.OrderListener;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderInStoreLookupAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.OrdersLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrdersInStoreFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<List<Order>>, View.OnClickListener {

    public static final int LOADER_CUSTOMER = 452;
    public static final int CREATE_CUSTOMER = 1;
    @InjectView(R.id.lookup)
    AutoCompleteTextView orderLookup;
    @InjectView(R.id.create)
    Button mCreateOrder;
    @InjectView(R.id.lookup_spinner)
    ProgressBar mOrderProgressBar;
    private String mQuery = "";
    private Integer mTenantId;
    private Integer mSiteId;
    private String mDefaultSearchQuery;
    private OrdersLoader mOrdersLoader;
    private OrderInStoreLookupAdapter mAdapter;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;
    private OrderListener mListener;
    private int mResourceOfCurrentSelectedColumn = -1;
    private boolean mLaunchFromGlobalSearch = false;
    private boolean mCurrentSortIsAsc;
    private String mCurrentSearch;
    private CreateOrderListener mOrderCreateListener;
    private String[] filterOptions = new String[4];


    public void setTenantId(Integer tenantId) {
        mTenantId = tenantId;
    }

    public void setSiteId(Integer siteId) {
        mSiteId = siteId;
    }

    public void setDefaultSearchQuery(String searchQuery) {
        mDefaultSearchQuery = searchQuery;
    }


    public void setListener(OrderListener listener) {
        mListener = listener;
    }

    public void setOrderCreateListener(CreateOrderListener listener) {
        mOrderCreateListener = listener;
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
        View view = inflater.inflate(R.layout.orders_instore_mode, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderLookup.setThreshold(0);
        orderLookup.setOnItemClickListener(this);
        orderLookup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getLoaderManager().restartLoader(LOADER_CUSTOMER, null, OrdersInStoreFragment.this);
                    Loader<List<Order>> loader = getLoaderManager().getLoader(LOADER_CUSTOMER);
                    mOrdersLoader = (OrdersLoader) loader;
                    mOrdersLoader.forceLoad();
                }
            }
        });
        mCreateOrder.setOnClickListener(this);
        orderLookup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //DO NOTHING
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mQuery = orderLookup.getText().toString();
                getLoaderManager().restartLoader(LOADER_CUSTOMER, null, OrdersInStoreFragment.this);
                Loader<List<Order>> loader = getLoaderManager().getLoader(LOADER_CUSTOMER);
                mOrderProgressBar.setVisibility(View.VISIBLE);
                mOrdersLoader = (OrdersLoader) loader;
                mOrdersLoader.forceLoad();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //DO NOTHING
            }
        });
    }

    @Override
    public Loader<List<Order>> onCreateLoader(int id, Bundle args) {
        OrdersLoader loader = new OrdersLoader(getActivity(), mTenantId, mSiteId);
        if (!TextUtils.isEmpty(mQuery)) {
            loader.setQueryFilter(mQuery);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Order>> loader, List<Order> data) {
        mOrderProgressBar.setVisibility(View.INVISIBLE);
        if (mAdapter == null) {
            mAdapter = new OrderInStoreLookupAdapter(getActivity());
            orderLookup.setAdapter(mAdapter);
        }

        mAdapter.clear();
        mAdapter.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Order>> loader) {

    }

    @Override
    public void onClick(View v) {
        mOrderCreateListener.createNewOrder();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.getItemAtPosition(position) instanceof Order) {
            Order order = (Order) adapterView.getItemAtPosition(position);
            mListener.orderSelected(order, new ArrayList<Order>(), 0);
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
        getActivity().startActivityForResult(intent, CREATE_CUSTOMER);
    }

}
