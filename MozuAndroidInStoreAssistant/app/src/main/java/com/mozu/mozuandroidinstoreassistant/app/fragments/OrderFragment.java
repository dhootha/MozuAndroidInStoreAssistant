package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrdersAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.OrdersLoader;

import java.util.List;

public class OrderFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Order>>, AbsListView.OnScrollListener {

    private static final int LOADER_ORDERS = 523;

    private int mTenantId;
    private int mSiteId;

    private ListView mOrdersList;
    private ProgressBar mProgress;

    private OrdersLoader mOrdersLoader;

    private OrdersAdapter mAdapter;

    public OrderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);

        mOrdersList = (ListView) view.findViewById(R.id.order_list);
        mProgress = (ProgressBar) view.findViewById(R.id.order_list_progress);

        mOrdersList.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        getLoaderManager().initLoader(LOADER_ORDERS, null, this).forceLoad();

        return view;
    }

    public void setTenantId(int tenantId) {
        mTenantId = tenantId;
    }

    public void setSiteId(int siteId) {
        mSiteId = siteId;
    }

    @Override
    public Loader<List<Order>> onCreateLoader(int id, Bundle args) {
        return new OrdersLoader(getActivity(), mTenantId, mSiteId);
    }

    @Override
    public void onLoadFinished(Loader<List<Order>> loader, List<Order> data) {
        if (loader.getId() == LOADER_ORDERS) {
            if (mAdapter == null) {

                mAdapter = new OrdersAdapter(getActivity());
            }

            mAdapter.clear();
            mAdapter.addAll(data);

            if (mOrdersList.getAdapter() == null) {
                mOrdersList.setAdapter(mAdapter);
            }

            mOrdersList.setOnScrollListener(this);

            mProgress.setVisibility(View.GONE);
            mOrdersList.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Order>> loader) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        //if the user has scrolled half way through the list and we can load more, then load more
        if (firstVisibleItem + visibleItemCount > totalItemCount / 2 && getProductLoader() != null && mOrdersLoader.hasMoreResults() && !mOrdersLoader.isLoading()) {
            getProductLoader().forceLoad();
        }

    }

    private OrdersLoader getProductLoader() {
        if (mOrdersLoader == null) {

            Loader<List<Order>> loader = getLoaderManager().getLoader(LOADER_ORDERS);

            mOrdersLoader = (OrdersLoader) loader;
        }

        return mOrdersLoader;
    }
}
