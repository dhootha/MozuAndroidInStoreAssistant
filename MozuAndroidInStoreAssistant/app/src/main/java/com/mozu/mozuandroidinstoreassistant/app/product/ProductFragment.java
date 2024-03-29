package com.mozu.mozuandroidinstoreassistant.app.product;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Loader;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.MainActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.SearchSuggestionsCursorAdapter;
import com.mozu.mozuandroidinstoreassistant.app.models.RecentSearch;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.product.adapter.ProductAdapter;
import com.mozu.mozuandroidinstoreassistant.app.product.loaders.ProductLoader;
import com.mozu.mozuandroidinstoreassistant.app.tasks.InventoryButtonClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProductFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Product>>, AbsListView.OnScrollListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, AbsListView.OnItemClickListener, InventoryButtonClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int MAX_NUMBER_OF_SEARCHES = 5;
    private static final int PRODUCT_LOADER = 0;

    private UserAuthenticationStateMachine mUserState;

    private Integer mCategoryId;

    private boolean mIsRefreshing = false;

    @InjectView(R.id.product_grid)
    GridView mProductGridView;
    @InjectView(R.id.product_list)
    ListView mProductListView;

    @InjectView(R.id.product_grid_container)
    SwipeRefreshLayout mPullToRefresh;

    @InjectView(R.id.product_list_headers)
    LinearLayout mHeadersView;
    @InjectView(R.id.list_view_border)
    LinearLayout mHeadersBorderView;

    private ProductAdapter mAdapter;
    @InjectView(R.id.progress)
    LinearLayout mProgressBar;

    private ProductLoader mProductLoader;

    private boolean mIsGridVisible = true;

    private MenuItem mToggleGridItem;

    private SearchView mSearchView;

    private MenuItem mSearchMenuItem;
    private final static String PRODUCT_INVENTORY_DIALOG_TAG = "product_inventory_tag";

    private ProductFragmentListener mListener = sProductListener;

    @InjectView(R.id.empty_list)
    TextView mEmptyListMessageView;

    public ProductFragment() {
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (ProductFragmentListener) activity;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((MainActivity) getActivity()).setProductSelected();
        }
    }

    @Override
    public void onDetach() {
        mListener = sProductListener;
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PRODUCT_LOADER, savedInstanceState, this).forceLoad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_product, container, false);

        ButterKnife.inject(this, fragmentView);

        mPullToRefresh.setEnabled(false);
        mPullToRefresh.setOnRefreshListener(this);
        mPullToRefresh.setColorScheme(R.color.first_color_swipe_refresh,
                R.color.second_color_swipe_refresh,
                R.color.third_color_swipe_refresh,
                R.color.fourth_color_swipe_refresh);


        mProductListView.setOnItemClickListener(this);
        mProductGridView.setOnItemClickListener(this);

        mProgressBar.setVisibility(View.VISIBLE);
        mHeadersView.setVisibility(View.GONE);
        mHeadersBorderView.setVisibility(View.GONE);

        mEmptyListMessageView.setVisibility(View.GONE);

        return fragmentView;
    }


    @Override
    public Loader<List<Product>> onCreateLoader(int id, Bundle args) {

        if (id == PRODUCT_LOADER) {
            mPullToRefresh.setRefreshing(true);
            return new ProductLoader(getActivity(), mUserState.getTenantId(), mUserState.getSiteId(), mCategoryId);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Product>> loader, List<Product> data) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        if (loader.getId() == PRODUCT_LOADER) {
            mPullToRefresh.setRefreshing(false);

            if (mIsRefreshing) {
                mIsRefreshing = false;
                mAdapter = null;
                mProductGridView.setAdapter(null);
                mProductListView.setAdapter(null);
            }

            if (mAdapter == null) {

                mAdapter = new ProductAdapter(getActivity(), mUserState.getTenantId(), mUserState.getSiteId(), mUserState.getSiteDomain(), this);
            }

            mAdapter.clear();
            mAdapter.addAll(data);

            if (mProductGridView.getAdapter() == null) {
                mProductGridView.setAdapter(mAdapter);
                mProductListView.setAdapter(mAdapter);
            }

            mProductGridView.setOnScrollListener(this);
            mProductListView.setOnScrollListener(this);

            if (prefs.getShowAsGrids()) {
                mProductGridView.setVisibility(View.VISIBLE);
                mProductListView.setVisibility(View.GONE);
                mHeadersView.setVisibility(View.GONE);
                mHeadersBorderView.setVisibility(View.GONE);
                mAdapter.setIsGrid(true);
                mAdapter.notifyDataSetChanged();
            } else {
                mProductGridView.setVisibility(View.GONE);
                mProductListView.setVisibility(View.VISIBLE);
                mHeadersView.setVisibility(View.VISIBLE);
                mHeadersBorderView.setVisibility(View.VISIBLE);
                mAdapter.setIsGrid(false);
                mAdapter.notifyDataSetChanged();
            }

            mProgressBar.setVisibility(View.GONE);

            if (mAdapter == null || mAdapter.getCount() < 1) {
                mProductGridView.setVisibility(View.GONE);
                mProductListView.setVisibility(View.GONE);

                mHeadersView.setVisibility(View.GONE);
                mHeadersBorderView.setVisibility(View.GONE);
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

            mToggleGridItem.setIcon(R.drawable.actionbar_icon_list_button);
            mToggleGridItem.setTitle(getString(R.string.view_as_list_menu_item_text));
        } else {

            mToggleGridItem.setIcon(R.drawable.actionbar_icon_grid_button);
            mToggleGridItem.setTitle(getString(R.string.view_as_grid_menu_item_text));
        }

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.product_search_hint));
        mSearchView.setMaxWidth(1500);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        if (item.getItemId() == R.id.toggle_view) {
            return updateViewToggleState(prefs);
        } else if (item.getItemId() == R.id.action_search) {
            showSuggestions();
        } else if (item.getItemId() == R.id.refresh_product) {
            mPullToRefresh.setRefreshing(true);
            onRefresh();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSuggestions() {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        List<RecentSearch> recentProductSearches = prefs.getRecentProductSearches();

        // Load data from list to cursor
        String[] columns = new String[]{"_id", "text"};
        Object[] temp = new Object[]{0, "default"};

        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < recentProductSearches.size(); i++) {

            temp[0] = i;
            temp[1] = recentProductSearches.get(i);

            cursor.addRow(temp);

        }

        mSearchView.setSuggestionsAdapter(new SearchSuggestionsCursorAdapter(getActivity(), cursor, recentProductSearches));

        mSearchView.setOnSuggestionListener(this);
    }

    private boolean updateViewToggleState(UserPreferences prefs) {
        if (mAdapter == null) {
            return false;
        }

        if (!mIsGridVisible) {
            mIsGridVisible = true;
            mProductListView.setVisibility(View.GONE);
            mHeadersView.setVisibility(View.GONE);
            mHeadersBorderView.setVisibility(View.GONE);
            mProductGridView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mAdapter.setIsGrid(true);
            mAdapter.notifyDataSetChanged();
            prefs.setShowAsGrids(true);
            mUserState.updateUserPreferences();

            mToggleGridItem.setIcon(R.drawable.actionbar_icon_list_button);
            mToggleGridItem.setTitle(getString(R.string.view_as_list_menu_item_text));

            return true;
        } else {
            mIsGridVisible = false;
            mProductListView.setVisibility(View.VISIBLE);
            mHeadersView.setVisibility(View.VISIBLE);
            mHeadersBorderView.setVisibility(View.VISIBLE);
            mProductGridView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mAdapter.setIsGrid(false);
            mAdapter.notifyDataSetChanged();
            prefs.setShowAsGrids(false);
            mUserState.updateUserPreferences();

            mToggleGridItem.setIcon(R.drawable.actionbar_icon_grid_button);
            mToggleGridItem.setTitle(getString(R.string.view_as_grid_menu_item_text));

            return true;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Product>> loader) {
        mPullToRefresh.setRefreshing(false);
    }

    public void setCategoryId(Integer categoryId) {
        mCategoryId = categoryId;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        //if the user has scrolled half way through the list and we can load more, then load more
        if (firstVisibleItem + visibleItemCount > totalItemCount / 2 && getProductLoader() != null && mProductLoader.hasMoreResults() && !mProductLoader.isLoading()) {
            getProductLoader().forceLoad();
        }

    }

    private ProductLoader getProductLoader() {
        if (mProductLoader == null) {

            Loader<List<Product>> loader = getLoaderManager().getLoader(PRODUCT_LOADER);

            mProductLoader = (ProductLoader) loader;
        }

        return mProductLoader;
    }

    private static final ProductFragmentListener sProductListener = new ProductFragmentListener() {

        @Override
        public void onSearchPerformedFromProduct(int currentCategoryId, String query) {

        }

        @Override
        public void onProductChoosentFromProuct(String productCode) {

        }

    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchMenuItem.collapseActionView();


        mListener.onSearchPerformedFromProduct(mCategoryId, query);
        saveSearchToList(query);

        return true;
    }

    private void saveSearchToList(String query) {
        //save search to list
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        List<RecentSearch> recentProductSearches = prefs.getRecentProductSearches();

        if (recentProductSearches == null) {
            recentProductSearches = new ArrayList<RecentSearch>();
        }

        //if search already exists then dont add it again
        for (int i = 0; i < recentProductSearches.size(); i++) {
            if (recentProductSearches.get(i).getSearchTerm().equalsIgnoreCase(query)) {
                recentProductSearches.remove(i);
                break;
            }
        }

        RecentSearch search = new RecentSearch();
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

        List<RecentSearch> recentProductSearches = prefs.getRecentProductSearches();
        if (recentProductSearches.size() > 0) {
            onQueryTextSubmit(recentProductSearches.get(position).getSearchTerm());
        } else {
            showSuggestions();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mListener.onProductChoosentFromProuct(mAdapter.getItem(position).getProductCode());
    }

    @Override
    public void onClick(Product product) {
        FragmentManager manager = getFragmentManager();
        ProductDetailInventoryFragment inventoryFragment = (ProductDetailInventoryFragment) manager.findFragmentByTag(PRODUCT_INVENTORY_DIALOG_TAG);
        if (inventoryFragment == null) {
            inventoryFragment = new ProductDetailInventoryFragment();
            inventoryFragment.setProduct(product);
            inventoryFragment.setTenantId(mUserState.getTenantId());
            inventoryFragment.setSiteId(mUserState.getSiteId());
        }
        inventoryFragment.show(manager, PRODUCT_INVENTORY_DIALOG_TAG);
    }

    public void onRefresh() {
        mSearchMenuItem.collapseActionView();

        mIsRefreshing = true;

        getProductLoader().reset();
        getProductLoader().init();
        getProductLoader().startLoading();
        getProductLoader().forceLoad();
    }

}
