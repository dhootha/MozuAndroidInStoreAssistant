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
import android.widget.ImageView;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class OrderFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Order>>, AbsListView.OnScrollListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, SearchManager.OnCancelListener, SearchManager.OnDismissListener, SearchView.OnSuggestionListener, MenuItem.OnActionExpandListener, AdapterView.OnItemClickListener {

    private static final int MAX_NUMBER_OF_ORDER_SEARCHES = 200;

    private static final String CURRENT_SORT_COLUMN_EXTRA = "currensortcolumnextra";

    private static final int LOADER_ORDERS = 523;

    private Integer mTenantId;
    private Integer mSiteId;

    @InjectView(R.id.order_list) ListView mOrdersList;
    @InjectView(R.id.order_list_progress) LinearLayout mProgress;

    private OrdersLoader mOrdersLoader;

    private OrdersAdapter mAdapter;

    private SearchView mSearchView;

    private MenuItem mSearchMenuItem;

    private OrderListener mListener;

    @InjectView(R.id.order_number_header) TextView mOrderNumberHeader;
    @InjectView(R.id.order_date_header) TextView mOrderDateHeader;
    @InjectView(R.id.order_email_header) TextView mOrderEmailHeader;
    @InjectView(R.id.order_status_header) TextView mOrderStatusHeader;
    @InjectView(R.id.order_total_header) TextView mOrderTotalHeader;

    @InjectView(R.id.order_number_header_sort_image) ImageView mOrderNumberHeaderSortImage;
    @InjectView(R.id.order_date_header_sort_image) ImageView mOrderDateHeaderSortImage;
    @InjectView(R.id.order_email_header_sort_image) ImageView mOrderEmailHeaderSortImage;
    @InjectView(R.id.order_status_header_sort_image) ImageView mOrderStatusHeaderSortImage;
    @InjectView(R.id.order_total_header_sort_image) ImageView mOrderTotalHeaderSortImage;

    private int mResourceOfCurrentSelectedColumn = -1;

    public OrderFragment() {

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.inject(this, view);

        mOrdersList.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        getLoaderManager().initLoader(LOADER_ORDERS, null, this).forceLoad();

        mOrdersList.setOnItemClickListener(this);

        if (savedInstanceState != null) {
            mResourceOfCurrentSelectedColumn = savedInstanceState.getInt(CURRENT_SORT_COLUMN_EXTRA, -1);
        }

        if (mResourceOfCurrentSelectedColumn != -1) {
            initializeSortColumn();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_SORT_COLUMN_EXTRA, mResourceOfCurrentSelectedColumn);

        super.onSaveInstanceState(outState);
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
            initSuggestions();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initSuggestions() {
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

    public void setTenantId(Integer tenantId) {
        mTenantId = tenantId;
    }

    public void setSiteId(Integer siteId) {
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
        mSearchView.clearFocus();

        saveSearchToList(query);

        initSuggestions();

        getOrdersLoader().reset();
        getOrdersLoader().setFilter(query);
        getOrdersLoader().startLoading();
        getOrdersLoader().forceLoad();

        mProgress.setVisibility(View.VISIBLE);
        mOrdersList.setVisibility(View.GONE);

        return false;
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
        mSearchView.setQuery("", false);
        mListener.orderSelected((Order)mOrdersList.getAdapter().getItem(position));
    }

    public void setListener(OrderListener listener) {
        mListener = listener;
    }

    @OnClick({R.id.order_number_header, R.id.order_date_header, R.id.order_email_header,R.id.order_status_header,R.id.order_total_header})
    public void determineSortActionForView(View v) {

        setTextViewNormalStyle(mOrderNumberHeader);
        setTextViewNormalStyle(mOrderDateHeader);
        setTextViewNormalStyle(mOrderEmailHeader);
        setTextViewNormalStyle(mOrderStatusHeader);
        setTextViewNormalStyle(mOrderTotalHeader);

        mOrderNumberHeaderSortImage.setVisibility(View.INVISIBLE);
        mOrderDateHeaderSortImage.setVisibility(View.INVISIBLE);
        mOrderEmailHeaderSortImage.setVisibility(View.INVISIBLE);
        mOrderStatusHeaderSortImage.setVisibility(View.INVISIBLE);
        mOrderTotalHeaderSortImage.setVisibility(View.INVISIBLE);


        if (v.getId() == mOrderNumberHeader.getId()) {
            getOrdersLoader().orderByNumber();
            setTextViewBoldStyle(mOrderNumberHeader);
            mResourceOfCurrentSelectedColumn = mOrderNumberHeader.getId();
            if(getOrdersLoader().isSortAsc()){
                mOrderNumberHeaderSortImage.setImageResource(R.drawable.icon_sort_up);
            }else{
                mOrderNumberHeaderSortImage.setImageResource(R.drawable.icon_sort_down);
            }

            mOrderNumberHeaderSortImage.setVisibility(View.VISIBLE);
        } else if (v.getId() == mOrderDateHeader.getId()) {
            getOrdersLoader().orderByDate();
            setTextViewBoldStyle(mOrderDateHeader);
            mResourceOfCurrentSelectedColumn = mOrderDateHeader.getId();
            mOrderDateHeaderSortImage.setVisibility(View.VISIBLE);
            if(getOrdersLoader().isSortAsc()){
                mOrderDateHeaderSortImage.setImageResource(R.drawable.icon_sort_up);
            }else{
                mOrderDateHeaderSortImage.setImageResource(R.drawable.icon_sort_down);
            }
        } else if (v.getId() == mOrderEmailHeader.getId()) {
            //TODO: NOT CURRENTLY A WAY TO SORT BY EMAIL
            //            getOrdersLoader().orderByEmail();
            //            clearSearchReload();
            return;
        } else if (v.getId() == mOrderStatusHeader.getId()) {
            getOrdersLoader().orderByStatus();
            setTextViewBoldStyle(mOrderStatusHeader);
            mResourceOfCurrentSelectedColumn = mOrderStatusHeader.getId();
            mOrderStatusHeaderSortImage.setVisibility(View.VISIBLE);
            if(getOrdersLoader().isSortAsc()){
                mOrderStatusHeaderSortImage.setImageResource(R.drawable.icon_sort_up);
            }else{
                mOrderStatusHeaderSortImage.setImageResource(R.drawable.icon_sort_down);
            }
        } else if (v.getId() == mOrderTotalHeader.getId()) {
            getOrdersLoader().orderByTotal();
            setTextViewBoldStyle(mOrderTotalHeader);
            mResourceOfCurrentSelectedColumn = mOrderTotalHeader.getId();
            mOrderTotalHeaderSortImage.setVisibility(View.VISIBLE);
            if(getOrdersLoader().isSortAsc()){
                mOrderTotalHeaderSortImage.setImageResource(R.drawable.icon_sort_up);
            }else{
                mOrderTotalHeaderSortImage.setImageResource(R.drawable.icon_sort_down);
            }
        } else {
            // if the view wasn't a sort column we don't need to do anyting else
            return;
        }

        clearSearchReload();
    }

    private void initializeSortColumn() {

        setTextViewNormalStyle(mOrderNumberHeader);
        setTextViewNormalStyle(mOrderDateHeader);
        setTextViewNormalStyle(mOrderEmailHeader);
        setTextViewNormalStyle(mOrderStatusHeader);
        setTextViewNormalStyle(mOrderTotalHeader);

        mOrderNumberHeaderSortImage.setVisibility(View.INVISIBLE);
        mOrderDateHeaderSortImage.setVisibility(View.INVISIBLE);
        mOrderEmailHeaderSortImage.setVisibility(View.INVISIBLE);
        mOrderStatusHeaderSortImage.setVisibility(View.INVISIBLE);
        mOrderTotalHeaderSortImage.setVisibility(View.INVISIBLE);

        if (mResourceOfCurrentSelectedColumn == mOrderNumberHeader.getId()) {

            setTextViewBoldStyle(mOrderNumberHeader);
            mOrderNumberHeaderSortImage.setVisibility(View.VISIBLE);
        } else if (mResourceOfCurrentSelectedColumn == mOrderDateHeader.getId()) {

            setTextViewBoldStyle(mOrderDateHeader);
            mOrderDateHeaderSortImage.setVisibility(View.VISIBLE);
        } else if (mResourceOfCurrentSelectedColumn == mOrderEmailHeader.getId()) {

            return;
        } else if (mResourceOfCurrentSelectedColumn == mOrderStatusHeader.getId()) {

            setTextViewBoldStyle(mOrderStatusHeader);
            mOrderStatusHeaderSortImage.setVisibility(View.VISIBLE);
        } else if (mResourceOfCurrentSelectedColumn == mOrderTotalHeader.getId()) {

            setTextViewBoldStyle(mOrderTotalHeader);
            mOrderTotalHeaderSortImage.setVisibility(View.VISIBLE);
        }
    }

    private void setTextViewBoldStyle(TextView textView) {
        textView.setTextAppearance(getActivity(), R.style.boldText);
        textView.setBackgroundResource(android.R.color.transparent);
    }

    private void setTextViewNormalStyle(TextView textView) {
        textView.setTextAppearance(getActivity(), R.style.normalText);
        textView.setBackgroundResource(android.R.color.transparent);
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return true;
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
