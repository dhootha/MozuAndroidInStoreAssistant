package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.LoaderManager;
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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.ProductAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.ProductLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import java.util.List;

public class ProductFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Product>>, GridView.OnScrollListener {

    private static final int PRODUCT_LOADER = 0;
    private static final int VIEW_AS_GRID_MENU_ITEM_ID = 1232;
    private static final int VIEW_AS_LIST_MENU_ITEM_ID = 3212;

    private UserAuthenticationStateMachine mUserState;

    private Integer mCategoryId;

    private GridView mProductGridView;
    private ListView mProductListView;

    private ProductAdapter mAdapter;

    private ProgressBar mProgressBar;

    private ProductLoader mProductLoader;

    private boolean mIsGridVisible = true;

    private MenuItem mToggleGridItem;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(PRODUCT_LOADER, savedInstanceState, this).forceLoad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_product, container, false);

        mProductGridView = (GridView) fragmentView.findViewById(R.id.product_grid);
        mProgressBar = (ProgressBar) fragmentView.findViewById(R.id.progress);
        mProductListView = (ListView) fragmentView.findViewById(R.id.product_list);

        mProductGridView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mProductListView.setVisibility(View.GONE);

        return fragmentView;
    }


    @Override
    public Loader<List<Product>> onCreateLoader(int id, Bundle args) {

        if (id == PRODUCT_LOADER) {
            UserPreferences prefs = mUserState.getCurrentUsersPreferences();

            return new ProductLoader(getActivity(), prefs.getDefaultTenantId() != null ? Integer.parseInt(prefs.getDefaultTenantId()) : null, prefs.getDefaultSiteId() != null ? Integer.parseInt(prefs.getDefaultSiteId()) : null, mCategoryId);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Product>> loader, List<Product> data) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        if (loader.getId() == PRODUCT_LOADER) {
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
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        inflater.inflate(R.menu.product, menu);

        mToggleGridItem = menu.findItem(R.id.toggle_view);

        mIsGridVisible = prefs.getShowAsGrids();

        if (mIsGridVisible) {

            mToggleGridItem.setIcon(R.drawable.icon_listview_actionbar);
            mToggleGridItem.setTitle(getString(R.string.view_as_list_menu_item_text));
        } else {

            mToggleGridItem.setIcon(R.drawable.icon_gridview_actionbar);
            mToggleGridItem.setTitle(getString(R.string.view_as_grid_menu_item_text));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        if (item.getItemId() == R.id.toggle_view) {
            if (!mIsGridVisible) {
                mIsGridVisible = true;
                mProductListView.setVisibility(View.GONE);
                mProductGridView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mAdapter.setIsGrid(true);
                mAdapter.notifyDataSetChanged();
                prefs.setShowAsGrids(true);
                mUserState.updateUserPreferences();

                mToggleGridItem.setIcon(R.drawable.icon_listview_actionbar);
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

                mToggleGridItem.setIcon(R.drawable.icon_gridview_actionbar);
                mToggleGridItem.setTitle(getString(R.string.view_as_grid_menu_item_text));

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoaderReset(Loader<List<Product>> loader) {

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
}
