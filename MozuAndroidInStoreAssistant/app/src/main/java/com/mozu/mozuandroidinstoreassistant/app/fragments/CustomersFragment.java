package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Loader;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.CustomersAdapter;
import com.mozu.mozuandroidinstoreassistant.app.adapters.SearchSuggestionsCursorAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.CustomersLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.RecentSearch;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CustomersFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<CustomerAccount>>, AbsListView.OnScrollListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, SearchManager.OnCancelListener, SearchManager.OnDismissListener, SearchView.OnSuggestionListener, MenuItem.OnActionExpandListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int MAX_NUMBER_OF_ORDER_SEARCHES = 200;

    private static final String CURRENT_SORT_COLUMN_EXTRA = "currensortcolumnextra";

    private static final int LOADER_CUSTOMERS = 522;

    private Integer mTenantId;
    private Integer mSiteId;

    @InjectView(R.id.customer_list_container) SwipeRefreshLayout mCustomerRefreshLayout;
    @InjectView(R.id.customer_list) ListView mCustomersList;
    @InjectView(R.id.customer_list_progress) LinearLayout mProgress;

    private CustomersLoader mCustomersLoader;

    private CustomersAdapter mAdapter;

    private SearchView mSearchView;

    private MenuItem mSearchMenuItem;

    private CustomerListener mListener;

    @InjectView(R.id.customer_number_header) TextView mCustomerNumberHeader;
    @InjectView(R.id.customer_last_name_header) TextView mCustomerLastNameHeader;
    @InjectView(R.id.customer_first_name_header) TextView mcCustomerFirstNameHeader;
    @InjectView(R.id.customer_email_header) TextView mCustomerEmailHeader;
    @InjectView(R.id.customer_lifetime_value_header) TextView mCustomerLifetimeValueHeader;

    @InjectView(R.id.customer_number_header_sort_image) ImageView mCustomerNumberHeaderSortImage;
    @InjectView(R.id.customer_last_name_header_sort_image) ImageView mCustomerLastNameHeaderSortImage;
    @InjectView(R.id.customer_first_name_sort_image) ImageView mCustomerFirstNameHeaderSortImage;
    @InjectView(R.id.customer_status_header_email_image) ImageView mCustomerEmailHeaderSortImage;
    @InjectView(R.id.customer_lifetime_value_header_sort_image) ImageView mCustomerLifetimeValueHeaderSortImage;

    @InjectView(R.id.customers_header) LinearLayout mCustomerHeaderLayout;
    @InjectView(R.id.customer_search_query) TextView mCustomerSearchQuery;

    private int mResourceOfCurrentSelectedColumn = -1;
    private String mDefaultSearchQuery;
    private boolean mLauncedFromSearch;

    public CustomersFragment() {

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    public void setLauncedFromSearch(){
        mLauncedFromSearch = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer, container, false);

        ButterKnife.inject(this, view);

        mCustomerRefreshLayout.setOnRefreshListener(this);
        mCustomerRefreshLayout.setColorScheme(R.color.first_color_swipe_refresh,
                R.color.second_color_swipe_refresh,
                R.color.third_color_swipe_refresh,
                R.color.fourth_color_swipe_refresh);

        mCustomerRefreshLayout.setRefreshing(true);
        getLoaderManager().initLoader(LOADER_CUSTOMERS, null, this).forceLoad();

        mCustomersList.setOnItemClickListener(this);

        if (savedInstanceState != null) {
            mResourceOfCurrentSelectedColumn = savedInstanceState.getInt(CURRENT_SORT_COLUMN_EXTRA, -1);
        }

        if (mResourceOfCurrentSelectedColumn != -1) {
            initializeSortColumn();
        }

        if (mLauncedFromSearch) {
            setHasOptionsMenu(false);
            mCustomerHeaderLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mDefaultSearchQuery)) {
                mCustomerSearchQuery.setText(mDefaultSearchQuery);
            }

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

        inflater.inflate(R.menu.customers, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnCloseListener(this);
        mSearchView.setQueryHint(getString(R.string.customer_search_hint_text));
        mSearchView.setMaxWidth(1500);

        mSearchMenuItem.setOnActionExpandListener(this);
        searchManager.setOnCancelListener(this);
        searchManager.setOnDismissListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            initSuggestions();
        } else if (item.getItemId() == R.id.refresh_customers) {
            onRefresh();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initSuggestions() {
        UserPreferences prefs = UserAuthenticationStateMachineProducer.getInstance(getActivity()).getCurrentUsersPreferences();

        List<RecentSearch> recentCustomerSearches = prefs.getRecentCustomerSearches();

        // Load data from list to cursor
        String[] columns = new String[] { "_id", "text" };
        Object[] temp = new Object[] { 0, "default" };

        MatrixCursor cursor = new MatrixCursor(columns);

        if (recentCustomerSearches == null || recentCustomerSearches.size() < 1) {
            return;
        }

        for(int i = 0; i < recentCustomerSearches.size(); i++) {

            temp[0] = i;
            temp[1] = recentCustomerSearches.get(i);

            cursor.addRow(temp);

        }

        mSearchView.setSuggestionsAdapter(new SearchSuggestionsCursorAdapter(getActivity(), cursor, recentCustomerSearches));

        mSearchView.setOnSuggestionListener(this);
    }

    private void saveSearchToList(String query) {
        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

        //save search to list
        UserPreferences prefs = userState.getCurrentUsersPreferences();

        List<RecentSearch> recentCustomerSearches = prefs.getRecentCustomerSearches();

        if (recentCustomerSearches == null) {
            recentCustomerSearches = new ArrayList<RecentSearch>();
        }

        //if search already exists then dont add it again
        for (int i = 0; i < recentCustomerSearches.size(); i++) {
            if (recentCustomerSearches.get(i).getSearchTerm().equalsIgnoreCase(query)) {
                recentCustomerSearches.remove(i);
                break;
            }
        }

        RecentSearch search = new RecentSearch();
        search.setSearchTerm(query);

        recentCustomerSearches.add(0, search);

        if (recentCustomerSearches.size() > MAX_NUMBER_OF_ORDER_SEARCHES) {
            recentCustomerSearches.remove(recentCustomerSearches.size() - 1);
        }

        prefs.setRecentCustomerSearchs(recentCustomerSearches);

        userState.updateUserPreferences();
    }

    public void setTenantId(Integer tenantId) {
        mTenantId = tenantId;
    }

    public void setSiteId(Integer siteId) {
        mSiteId = siteId;
    }

    @Override
    public Loader<List<CustomerAccount>> onCreateLoader(int id, Bundle args) {
        CustomersLoader loader =  new CustomersLoader(getActivity(), mTenantId, mSiteId);
        if (!TextUtils.isEmpty(mDefaultSearchQuery)) {
            loader.setFilter(mDefaultSearchQuery);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<CustomerAccount>> loader, List<CustomerAccount> data) {
        mCustomerRefreshLayout.setRefreshing(false);

        if (loader.getId() == LOADER_CUSTOMERS) {
            if (mAdapter == null) {

                mAdapter = new CustomersAdapter(getActivity());
            }

            mAdapter.clear();
            mAdapter.addAll(data);

            if (mCustomersList.getAdapter() == null) {
                mCustomersList.setAdapter(mAdapter);
            }

            mCustomersList.setOnScrollListener(this);

            mProgress.setVisibility(View.GONE);
            mCustomersList.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CustomerAccount>> loader) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        //if the user has scrolled half way through the list and we can load more, then load more
        if (firstVisibleItem + visibleItemCount > totalItemCount / 2 && getcCustomersLoader() != null && mCustomersLoader.hasMoreResults() && !mCustomersLoader.isLoading()) {
            getcCustomersLoader().forceLoad();
        }

    }

    private CustomersLoader getcCustomersLoader() {
        if (mCustomersLoader == null) {

            Loader<List<CustomerAccount>> loader = getLoaderManager().getLoader(LOADER_CUSTOMERS);

            mCustomersLoader = (CustomersLoader) loader;
        }

        return mCustomersLoader;
    }

    public void setDefaultSearchQuery(String searchQuery) {
        mDefaultSearchQuery = searchQuery;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();

        saveSearchToList(query);

        initSuggestions();

        getcCustomersLoader().reset();
        getcCustomersLoader().setFilter(query);
        getcCustomersLoader().startLoading();
        getcCustomersLoader().forceLoad();

        mCustomerRefreshLayout.setRefreshing(true);

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
        getcCustomersLoader().removeFilter();

        getcCustomersLoader().reset();
        getcCustomersLoader().startLoading();
        getcCustomersLoader().forceLoad();

        mCustomerRefreshLayout.setRefreshing(true);
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
        mListener.customerSelected((CustomerAccount) mCustomersList.getAdapter().getItem(position));
    }

    public void setListener(CustomerListener listener) {
        mListener = listener;
    }

    @OnClick({R.id.customer_number_header, R.id.customer_last_name_header, R.id.customer_first_name_header, R.id.customer_email_header, R.id.customer_lifetime_value_header})
    public void determineSortActionForView(View v) {

        setTextViewNormalStyle(mCustomerNumberHeader);
        setTextViewNormalStyle(mCustomerLastNameHeader);
        setTextViewNormalStyle(mCustomerEmailHeader);
        setTextViewNormalStyle(mcCustomerFirstNameHeader);
        setTextViewNormalStyle(mCustomerLifetimeValueHeader);

        mCustomerNumberHeaderSortImage.setVisibility(View.INVISIBLE);
        mCustomerLastNameHeaderSortImage.setVisibility(View.INVISIBLE);
        mCustomerFirstNameHeaderSortImage.setVisibility(View.INVISIBLE);
        mCustomerEmailHeaderSortImage.setVisibility(View.INVISIBLE);
        mCustomerLifetimeValueHeaderSortImage.setVisibility(View.INVISIBLE);

        if (v.getId() == mCustomerNumberHeader.getId()) {
            getcCustomersLoader().customerByNumber();
            setTextViewBoldStyle(mCustomerNumberHeader);
            mResourceOfCurrentSelectedColumn = mCustomerNumberHeader.getId();
            if(getcCustomersLoader().isSortAsc()){
                mCustomerNumberHeaderSortImage.setImageResource(R.drawable.icon_sort_up);
            }else{
                mCustomerNumberHeaderSortImage.setImageResource(R.drawable.icon_sort_down);
            }
            mCustomerNumberHeaderSortImage.setVisibility(View.VISIBLE);
        } else if (v.getId() == mCustomerLastNameHeader.getId()) {
            return;
//            getcCustomersLoader().customerByLastName();
//            setTextViewBoldStyle(mCustomerLastNameHeader);
//            mResourceOfCurrentSelectedColumn = mCustomerLastNameHeader.getId();
//            mCustomerLastNameHeaderSortImage.setVisibility(View.VISIBLE);
//            if(getcCustomersLoader().isSortAsc()){
//                mCustomerLastNameHeaderSortImage.setImageResource(R.drawable.icon_sort_up);
//            }else{
//                mCustomerLastNameHeaderSortImage.setImageResource(R.drawable.icon_sort_down);
//            }
        } else if (v.getId() == mCustomerEmailHeader.getId()) {
            return;
//            getcCustomersLoader().customerByEmail();
//            setTextViewBoldStyle(mCustomerEmailHeader);
//            mResourceOfCurrentSelectedColumn = mCustomerEmailHeader.getId();
//            mCustomerEmailHeaderSortImage.setVisibility(View.VISIBLE);
//            if(getcCustomersLoader().isSortAsc()){
//                mCustomerEmailHeaderSortImage.setImageResource(R.drawable.icon_sort_up);
//            }else{
//                mCustomerEmailHeaderSortImage.setImageResource(R.drawable.icon_sort_down);
//            }
        } else if (v.getId() == mcCustomerFirstNameHeader.getId()) {
            return;
//            getcCustomersLoader().customerByFirstName();
//            setTextViewBoldStyle(mcCustomerFirstNameHeader);
//            mResourceOfCurrentSelectedColumn = mcCustomerFirstNameHeader.getId();
//            mCustomerFirstNameHeaderSortImage.setVisibility(View.VISIBLE);
//            if(getcCustomersLoader().isSortAsc()){
//                mCustomerFirstNameHeaderSortImage.setImageResource(R.drawable.icon_sort_up);
//            }else{
//                mCustomerFirstNameHeaderSortImage.setImageResource(R.drawable.icon_sort_down);
//            }
        } else if (v.getId() == mCustomerLifetimeValueHeader.getId()) {
            getcCustomersLoader().customerByTotal();
            setTextViewBoldStyle(mCustomerLifetimeValueHeader);
            mResourceOfCurrentSelectedColumn = mCustomerLifetimeValueHeader.getId();
            mCustomerLifetimeValueHeaderSortImage.setVisibility(View.VISIBLE);
            if(getcCustomersLoader().isSortAsc()){
                mCustomerLifetimeValueHeaderSortImage.setImageResource(R.drawable.icon_sort_up);
            }else{
                mCustomerLifetimeValueHeaderSortImage.setImageResource(R.drawable.icon_sort_down);
            }
        } else {
            // if the view wasn't a sort column we don't need to do anyting else
            return;
        }

        clearSearchReload();
    }

    private void initializeSortColumn() {

        setTextViewNormalStyle(mCustomerNumberHeader);
        setTextViewNormalStyle(mCustomerLastNameHeader);
        setTextViewNormalStyle(mCustomerEmailHeader);
        setTextViewNormalStyle(mcCustomerFirstNameHeader);
        setTextViewNormalStyle(mCustomerLifetimeValueHeader);

        mCustomerNumberHeaderSortImage.setVisibility(View.INVISIBLE);
        mCustomerLastNameHeaderSortImage.setVisibility(View.INVISIBLE);
        mCustomerFirstNameHeaderSortImage.setVisibility(View.INVISIBLE);
        mCustomerEmailHeaderSortImage.setVisibility(View.INVISIBLE);
        mCustomerLifetimeValueHeaderSortImage.setVisibility(View.INVISIBLE);

        if (mResourceOfCurrentSelectedColumn == mCustomerNumberHeader.getId()) {

            setTextViewBoldStyle(mCustomerNumberHeader);
            mCustomerNumberHeaderSortImage.setVisibility(View.VISIBLE);
        } else if (mResourceOfCurrentSelectedColumn == mCustomerLastNameHeader.getId()) {

            setTextViewBoldStyle(mCustomerLastNameHeader);
            mCustomerLastNameHeaderSortImage.setVisibility(View.VISIBLE);
        } else if (mResourceOfCurrentSelectedColumn == mCustomerEmailHeader.getId()) {

            setTextViewBoldStyle(mCustomerEmailHeader);
            mCustomerEmailHeaderSortImage.setVisibility(View.VISIBLE);
            return;
        } else if (mResourceOfCurrentSelectedColumn == mcCustomerFirstNameHeader.getId()) {

            setTextViewBoldStyle(mcCustomerFirstNameHeader);
            mCustomerFirstNameHeaderSortImage.setVisibility(View.VISIBLE);
        } else if (mResourceOfCurrentSelectedColumn == mCustomerLifetimeValueHeader.getId()) {

            setTextViewBoldStyle(mCustomerLifetimeValueHeader);
            mCustomerLifetimeValueHeaderSortImage.setVisibility(View.VISIBLE);
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

        List<RecentSearch> recentProductSearches = prefs.getRecentCustomerSearches();

        String searchTerm = recentProductSearches.get(position).getSearchTerm();

        mSearchView.setQuery(searchTerm, false);

        onQueryTextSubmit(searchTerm);

        return true;
    }

    @Override
    public void onRefresh() {
        mSearchMenuItem.collapseActionView();

        mCustomerRefreshLayout.setRefreshing(true);

        clearSearchReload();
    }
}
