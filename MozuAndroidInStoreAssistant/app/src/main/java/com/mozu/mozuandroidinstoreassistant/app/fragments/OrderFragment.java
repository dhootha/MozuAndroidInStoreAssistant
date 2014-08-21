package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Loader;
import android.database.MatrixCursor;
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
import android.widget.SearchView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrdersAdapter;
import com.mozu.mozuandroidinstoreassistant.app.adapters.SearchSuggestionsCursorAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.OrdersLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.RecentSearch;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Order>>, AbsListView.OnScrollListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, SearchManager.OnCancelListener, SearchManager.OnDismissListener, SearchView.OnSuggestionListener, MenuItem.OnActionExpandListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final int MAX_NUMBER_OF_ORDER_SEARCHES = 5;

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
        mSearchView.setQueryHint(getString(R.string.order_search_hint_text));
        
        mSearchMenuItem.setOnActionExpandListener(this);
        searchManager.setOnCancelListener(this);
        searchManager.setOnDismissListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            showSuggestions();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSuggestions() {
        UserPreferences prefs = UserAuthenticationStateMachineProducer.getInstance(getActivity()).getCurrentUsersPreferences();

        List<RecentSearch> recentOrderSearches = prefs.getRecentOrderSearches();

        // Load data from list to cursor
        String[] columns = new String[] { "_id", "text" };
        Object[] temp = new Object[] { 0, "default" };

        MatrixCursor cursor = new MatrixCursor(columns);

        if (recentOrderSearches == null || recentOrderSearches.size() < 1) {
            return;
        }

        for(int i = 0; i < recentOrderSearches.size(); i++) {

            temp[0] = i;
            temp[1] = recentOrderSearches.get(i);

            cursor.addRow(temp);

        }

        mSearchView.setSuggestionsAdapter(new SearchSuggestionsCursorAdapter(getActivity(), cursor, recentOrderSearches));

        mSearchView.setOnSuggestionListener(this);
    }

    private void saveSearchToList(String query) {
        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

        //save search to list
        UserPreferences prefs = userState.getCurrentUsersPreferences();

        List<RecentSearch> recentOrderSearches = prefs.getRecentOrderSearches();

        if (recentOrderSearches == null) {
            recentOrderSearches = new ArrayList<RecentSearch>();
        }

        //if search already exists then dont add it again
        for (int i = 0; i < recentOrderSearches.size(); i++) {
            if (recentOrderSearches.get(i).getSearchTerm().equalsIgnoreCase(query)) {
                recentOrderSearches.remove(i);
                break;
            }
        }

        RecentSearch search = new RecentSearch();
        search.setSearchTerm(query);

        recentOrderSearches.add(0, search);

        if (recentOrderSearches.size() > MAX_NUMBER_OF_ORDER_SEARCHES) {
            recentOrderSearches.remove(recentOrderSearches.size() - 1);
        }

        prefs.setRecentProductSearchs(recentOrderSearches);

        userState.updateUserPreferences();
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

        saveSearchToList(query);

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

        determineSortActionForView(v);

    }

    private void determineSortActionForView(View v) {

        if (v.getId() == mOrderNumberHeader.getId()) {
            getOrdersLoader().orderByNumber();
        } else if (v.getId() == mOrderDateHeader.getId()) {
            getOrdersLoader().orderByDate();
        } else if (v.getId() == mOrderEmailHeader.getId()) {
            //TODO: NOT CURRENTLY A WAY TO SORT BY EMAIL
            //            getOrdersLoader().orderByEmail();
            //            clearSearchReload();
        } else if (v.getId() == mOrderStatusHeader.getId()) {
            getOrdersLoader().orderByStatus();
        } else if (v.getId() == mOrderTotalHeader.getId()) {
            getOrdersLoader().orderByTotal();
        } else {
            // if the view wasn't a sort column we don't need to do anyting else
            return;
        }

        clearSearchReload();
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

        UserPreferences prefs = userState.getCurrentUsersPreferences();

        List<RecentSearch> recentProductSearches = prefs.getRecentProductSearches();

        String searchTerm = recentProductSearches.get(position).getSearchTerm();

        mSearchView.setQuery(searchTerm, false);

        onQueryTextSubmit(searchTerm);

        return true;
    }
}
