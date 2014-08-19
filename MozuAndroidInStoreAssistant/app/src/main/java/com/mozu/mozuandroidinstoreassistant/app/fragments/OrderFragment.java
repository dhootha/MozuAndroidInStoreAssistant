package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrdersAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.OrdersLoader;

import java.util.List;

public class OrderFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Order>>, AbsListView.OnScrollListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, SearchManager.OnCancelListener, SearchManager.OnDismissListener, MenuItem.OnActionExpandListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int LOADER_ORDERS = 523;

    private int mTenantId;
    private int mSiteId;

    private ListView mOrdersList;
    private LinearLayout mProgress;

    private OrdersLoader mOrdersLoader;

    private OrdersAdapter mAdapter;

    private SearchView mSearchView;

    private MenuItem mSearchMenuItem;

    private OrderListener mListener;

    private TextView mOrderNumberHeader;
    private TextView mOrderDateHeader;
    private TextView mOrderEmailHeader;
    private TextView mOrderStatusHeader;
    private TextView mOrderTotalHeader;

    public OrderFragment() {

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);

        mOrderNumberHeader = (TextView) view.findViewById(R.id.order_number_header);
        mOrderDateHeader = (TextView) view.findViewById(R.id.order_date_header);
        mOrderEmailHeader = (TextView) view.findViewById(R.id.order_email_header);
        mOrderStatusHeader = (TextView) view.findViewById(R.id.order_status_header);
        mOrderTotalHeader = (TextView) view.findViewById(R.id.order_total_header);

        mOrderNumberHeader.setOnClickListener(this);
        mOrderDateHeader.setOnClickListener(this);
        mOrderEmailHeader.setOnClickListener(this);
        mOrderStatusHeader.setOnClickListener(this);
        mOrderTotalHeader.setOnClickListener(this);

        mOrdersList = (ListView) view.findViewById(R.id.order_list);
        mProgress = (LinearLayout) view.findViewById(R.id.order_list_progress);

        mOrdersList.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        getLoaderManager().initLoader(LOADER_ORDERS, null, this).forceLoad();

        mOrdersList.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.orders, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnCloseListener(this);
        mSearchMenuItem.setOnActionExpandListener(this);
        searchManager.setOnCancelListener(this);
        searchManager.setOnDismissListener(this);
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
        if (firstVisibleItem + visibleItemCount > totalItemCount / 2 && getOrdersLoader() != null && mOrdersLoader.hasMoreResults() && !mOrdersLoader.isLoading()) {
            getOrdersLoader().forceLoad();
        }

    }

    private OrdersLoader getOrdersLoader() {
        if (mOrdersLoader == null) {

            Loader<List<Order>> loader = getLoaderManager().getLoader(LOADER_ORDERS);

            mOrdersLoader = (OrdersLoader) loader;
        }

        return mOrdersLoader;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        getOrdersLoader().reset();
        getOrdersLoader().init();
        getOrdersLoader().setFilter(query);
        getOrdersLoader().startLoading();
        getOrdersLoader().forceLoad();

        mProgress.setVisibility(View.VISIBLE);
        mOrdersList.setVisibility(View.GONE);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        clearSearchReload();

        return true;
    }

    @Override
    public void onCancel() {
        clearSearchReload();
    }

    @Override
    public void onDismiss() {
        clearSearchReload();
    }

    private void clearSearchReload() {
        getOrdersLoader().removeFilter();

        getOrdersLoader().reset();
        getOrdersLoader().init();
        getOrdersLoader().startLoading();
        getOrdersLoader().forceLoad();

        mProgress.setVisibility(View.VISIBLE);
        mOrdersList.setVisibility(View.GONE);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {

        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        clearSearchReload();

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.orderSelected((Order)mOrdersList.getAdapter().getItem(position));
    }

    public void setListener(OrderListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == mOrderNumberHeader.getId()) {
            getOrdersLoader().orderByNumber();
            clearSearchReload();
        } else if (v.getId() == mOrderDateHeader.getId()) {
            getOrdersLoader().orderByDate();
            clearSearchReload();
        } else if (v.getId() == mOrderEmailHeader.getId()) {
//TODO: NOT CURRENTLY A WAY TO SORT BY EMAIL
//            getOrdersLoader().orderByEmail();
//            clearSearchReload();
        } else if (v.getId() == mOrderStatusHeader.getId()) {
            getOrdersLoader().orderByStatus();
            clearSearchReload();
        } else if (v.getId() == mOrderTotalHeader.getId()) {
            getOrdersLoader().orderByTotal();
            clearSearchReload();
        }

    }
}
