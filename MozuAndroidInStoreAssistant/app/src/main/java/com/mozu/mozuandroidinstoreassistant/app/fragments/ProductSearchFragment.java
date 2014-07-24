package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Loader;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.ProductAdapter;
import com.mozu.mozuandroidinstoreassistant.app.adapters.ProductSearchSuggestionsCursorAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.ProductSearchLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.RecentProductSearch;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import java.util.ArrayList;
import java.util.List;

public class ProductSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Product>>, GridView.OnScrollListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, AbsListView.OnItemClickListener {

    public static final int MAX_NUMBER_OF_SEARCHES = 5;

    private static final int PRODUCT_SEARCH_LOADER = 2;

    private UserAuthenticationStateMachine mUserState;

    private Integer mCategoryId;

    private GridView mProductGridView;
    private ListView mProductListView;

    private ProductAdapter mAdapter;

    private ProgressBar mProgressBar;

    private ProductSearchLoader mProductSearchLoader;

    private boolean mIsGridVisible = true;

    private MenuItem mToggleGridItem;

    private SearchView mSearchView;

    private MenuItem mSearchMenuItem;

    private String mQueryString;

    private TextView mEmptyListMessageView;

    private ProductListListener mListener;

    public ProductSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(PRODUCT_SEARCH_LOADER, savedInstanceState, this).forceLoad();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (ProductListListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = new ProductListListener() {
            @Override
            public void onProductSelected(String productCodeSelected) {
                Log.d("ProductSearchFragment", "WARN WARN WARN ----------- THIS LISTENER NOT CONNECTED TO ANYTHING!!!!");
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_product, container, false);

        mProductGridView = (GridView) fragmentView.findViewById(R.id.product_grid);
        mProgressBar = (ProgressBar) fragmentView.findViewById(R.id.progress);
        mProductListView = (ListView) fragmentView.findViewById(R.id.product_list);

        mProductListView.setOnItemClickListener(this);
        mProductGridView.setOnItemClickListener(this);

        mEmptyListMessageView = (TextView) fragmentView.findViewById(R.id.empty_list);

        mEmptyListMessageView.setVisibility(View.GONE);
        mProductGridView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mProductListView.setVisibility(View.GONE);

        return fragmentView;
    }


    @Override
    public Loader<List<Product>> onCreateLoader(int id, Bundle args) {

        if (id == PRODUCT_SEARCH_LOADER) {
            UserPreferences prefs = mUserState.getCurrentUsersPreferences();

            return new ProductSearchLoader(getActivity(), prefs.getDefaultTenantId() != null ? Integer.parseInt(prefs.getDefaultTenantId()) : null, prefs.getDefaultSiteId() != null ? Integer.parseInt(prefs.getDefaultSiteId()) : null, mCategoryId, mQueryString);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Product>> loader, List<Product> data) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        if (loader.getId() == PRODUCT_SEARCH_LOADER) {
            if (mAdapter == null) {

                mAdapter = new ProductAdapter(getActivity(), prefs.getDefaultTenantId() != null ? Integer.parseInt(prefs.getDefaultTenantId()) : null, prefs.getDefaultSiteId() != null ? Integer.parseInt(prefs.getDefaultSiteId()) : null);
            }

            mAdapter.clear();
            mAdapter.addAll(data);

            if (mProductGridView.getAdapter() == null) {
                mProductGridView.setAdapter(mAdapter);
                mProductListView.setAdapter(mAdapter);
            }

            mProductGridView.setOnScrollListener(this);

            if (prefs.getShowAsGrids()) {
                mProductGridView.setVisibility(View.VISIBLE);
                mProductListView.setVisibility(View.GONE);
                mAdapter.setIsGrid(true);
                mAdapter.notifyDataSetChanged();
            } else {
                mProductListView.setVisibility(View.VISIBLE);
                mProductGridView.setVisibility(View.GONE);
                mAdapter.setIsGrid(false);
                mAdapter.notifyDataSetChanged();
            }

            mProgressBar.setVisibility(View.GONE);

            if (mAdapter == null || mAdapter.getCount() < 1) {
                mProductGridView.setVisibility(View.GONE);
                mProductListView.setVisibility(View.GONE);
                mEmptyListMessageView.setVisibility(View.VISIBLE);
            } else {
                mEmptyListMessageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        inflater.inflate(R.menu.product, menu);

        mToggleGridItem = menu.findItem(R.id.toggle_view);

        mIsGridVisible = prefs.getShowAsGrids();

        if (mIsGridVisible) {

            mToggleGridItem.setIcon(R.drawable.list);
            mToggleGridItem.setTitle(getString(R.string.view_as_list_menu_item_text));
        } else {

            mToggleGridItem.setIcon(R.drawable.grid);
            mToggleGridItem.setTitle(getString(R.string.view_as_grid_menu_item_text));
        }

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        if (item.getItemId() == R.id.toggle_view) {
            return updateViewToggleState(prefs);
        } else if (item.getItemId() == R.id.action_search) {
            showSuggestions();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean updateViewToggleState(UserPreferences prefs) {
        if (mAdapter == null) {
            return false;
        }

        if (!mIsGridVisible) {
            mIsGridVisible = true;
            mProductListView.setVisibility(View.GONE);
            mProductGridView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mAdapter.setIsGrid(true);
            mAdapter.notifyDataSetChanged();
            prefs.setShowAsGrids(true);
            mUserState.updateUserPreferences();

            mToggleGridItem.setIcon(R.drawable.list);
            mToggleGridItem.setTitle(getString(R.string.view_as_list_menu_item_text));

            return true;
        } else {
            mIsGridVisible = false;
            mProductListView.setVisibility(View.VISIBLE);
            mProductGridView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mAdapter.setIsGrid(false);
            mAdapter.notifyDataSetChanged();
            prefs.setShowAsGrids(false);
            mUserState.updateUserPreferences();

            mToggleGridItem.setIcon(R.drawable.grid);
            mToggleGridItem.setTitle(getString(R.string.view_as_grid_menu_item_text));

            return true;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Product>> loader) {

    }

    public void setCategoryId(Integer categoryId) {
        mCategoryId = categoryId;
    }

    public void setQueryString(String query) {
        mQueryString = query;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        //if the user has scrolled half way through the list and we can load more, then load more
        if (firstVisibleItem + visibleItemCount > totalItemCount / 2 && getProductLoader() != null && mProductSearchLoader.hasMoreResults() && !mProductSearchLoader.isLoading()) {
            getProductLoader().forceLoad();
        }

    }

    private ProductSearchLoader getProductLoader() {
        if (mProductSearchLoader == null) {

            Loader<List<Product>> loader = getLoaderManager().getLoader(PRODUCT_SEARCH_LOADER);

            mProductSearchLoader = (ProductSearchLoader) loader;
        }

        return mProductSearchLoader;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchMenuItem.collapseActionView();

        if (mProductGridView != null && mProductListView != null && mProgressBar != null) {
            mProductGridView.setAdapter(null);
            mProductListView.setAdapter(null);
            mAdapter.clear();
            mAdapter = null;

            mProductGridView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mProductListView.setVisibility(View.GONE);
            mEmptyListMessageView.setVisibility(View.GONE);
        }

        mQueryString = query;

        getProductLoader().reset();
        getProductLoader().setSearchQuery(query);
        getLoaderManager().restartLoader(PRODUCT_SEARCH_LOADER, null, this).forceLoad();

        saveSearchToList(query);

        return true;
    }

    private void showSuggestions() {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        List<RecentProductSearch> recentProductSearches = prefs.getRecentProductSearches();

        // Load data from list to cursor
        String[] columns = new String[] { "_id", "text" };
        Object[] temp = new Object[] { 0, "default" };

        MatrixCursor cursor = new MatrixCursor(columns);

        if (recentProductSearches == null || recentProductSearches.size() < 1) {
            return;
        }

        for(int i = 0; i < recentProductSearches.size(); i++) {

            temp[0] = i;
            temp[1] = recentProductSearches.get(i);

            cursor.addRow(temp);

        }

        mSearchView.setSuggestionsAdapter(new ProductSearchSuggestionsCursorAdapter(getActivity(), cursor, recentProductSearches));

        mSearchView.setOnSuggestionListener(this);
    }
    private void saveSearchToList(String query) {
        //save search to list
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        List<RecentProductSearch> recentProductSearches = prefs.getRecentProductSearches();

        if (recentProductSearches == null) {
            recentProductSearches = new ArrayList<RecentProductSearch>();
        }

        //if search already exists then dont add it again
        for (int i = 0; i < recentProductSearches.size(); i++) {
            if (recentProductSearches.get(i).getSearchTerm().equalsIgnoreCase(query)) {
                recentProductSearches.remove(i);
                break;
            }
        }

        RecentProductSearch search = new RecentProductSearch();
        search.setSearchTerm(query);

        recentProductSearches.add(0, search);

        if (recentProductSearches.size() > MAX_NUMBER_OF_SEARCHES) {
            recentProductSearches.remove(recentProductSearches.size() - 1);
        }

        prefs.setRecentProductSearchs(recentProductSearches);

        mUserState.updateUserPreferences();
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        List<RecentProductSearch> recentProductSearches = prefs.getRecentProductSearches();

        onQueryTextSubmit(recentProductSearches.get(position).getSearchTerm());

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onProductSelected(mAdapter.getItem(position).getProductCode());
    }
}