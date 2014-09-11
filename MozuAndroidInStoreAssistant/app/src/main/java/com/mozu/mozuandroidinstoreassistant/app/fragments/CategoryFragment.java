package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Loader;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.mozu.api.contracts.productruntime.Category;
import com.mozu.mozuandroidinstoreassistant.app.BuildConfig;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.CategoryAdapter;
import com.mozu.mozuandroidinstoreassistant.app.adapters.SearchSuggestionsCursorAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.CategoryLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.RecentSearch;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.tasks.CategoryImageUpdateListener;
import com.mozu.mozuandroidinstoreassistant.app.tasks.CategoryImageUpdateTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Category>>, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private static final int CATEGORY_LOADER = 0;
    public static final int MAX_NUMBER_OF_SEARCHES = 5;
    public static final String NULL = "null";
    private static final int CATEGORY_IMAGELOADER_MENU_ID = 100;

    @InjectView(R.id.category_grid) GridView mGridOfCategories;
    @InjectView(R.id.category_list) ListView mListOfCategories;

    private UserAuthenticationStateMachine mUserState;

    private CategoryAdapter mCategoryAdapter;

    private List<Category> mCategories = new ArrayList<Category>();

    private CategoryFragmentListener mListener = sCategoryListener;

    private boolean mIsGridVisible = true;

    private MenuItem mToggleGridItem;

    private SearchView mSearchView;

    private MenuItem mSearchMenuItem;
    private ProgressDialog mImageUpdateProgress;

    @InjectView(R.id.empty_list) TextView mEmptyListMessageView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (CategoryFragmentListener) activity;
    }

    @Override
    public void onDetach() {
        mListener = sCategoryListener;

        super.onDetach();
    }

    public CategoryFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_category_product_grid, container, false);

        ButterKnife.inject(this, fragmentView);

        mGridOfCategories.setOnItemClickListener(this);
        mListOfCategories.setOnItemClickListener(this);

        mEmptyListMessageView.setVisibility(View.GONE);

        return fragmentView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //if no categories are set, i.e., they weren't initialized to the fragment
        // then load top level of categories for drilldown
        if (mCategories == null || mCategories.size() < 1) {

            getLoaderManager().initLoader(CATEGORY_LOADER, savedInstanceState, this).forceLoad();
        } else {

            updateListGridsToCategory();
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

        if (BuildConfig.DEBUG) {
            menu.add(0, CATEGORY_IMAGELOADER_MENU_ID, Menu.NONE, "Load Category Images");
        }

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.product_search_hint));
        mSearchView.setMaxWidth(1500);

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnQueryTextListener(this);
    }

    private void showSuggestions() {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        List<RecentSearch> recentProductSearches = prefs.getRecentProductSearches();

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

        mSearchView.setSuggestionsAdapter(new SearchSuggestionsCursorAdapter(getActivity(), cursor, recentProductSearches));

        mSearchView.setOnSuggestionListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        if (item.getItemId() == R.id.toggle_view) {
            if (mCategoryAdapter == null) {
                return false;
            }

            if (!mIsGridVisible) {
                mIsGridVisible = true;
                mListOfCategories.setVisibility(View.GONE);
                mGridOfCategories.setVisibility(View.VISIBLE);
                mCategoryAdapter.setIsGrid(true);
                mCategoryAdapter.notifyDataSetChanged();
                prefs.setShowAsGrids(true);
                mUserState.updateUserPreferences();

                mToggleGridItem.setIcon(R.drawable.list);
                mToggleGridItem.setTitle(getString(R.string.view_as_list_menu_item_text));

                return true;

            } else {
                mIsGridVisible = false;
                mListOfCategories.setVisibility(View.VISIBLE);
                mGridOfCategories.setVisibility(View.GONE);
                mCategoryAdapter.setIsGrid(false);
                mCategoryAdapter.notifyDataSetChanged();
                prefs.setShowAsGrids(false);
                mUserState.updateUserPreferences();

                mToggleGridItem.setIcon(R.drawable.grid);
                mToggleGridItem.setTitle(getString(R.string.view_as_grid_menu_item_text));

                return true;
            }
        } else if (item.getItemId() == R.id.action_search) {
            showSuggestions();
        } else if(item.getItemId() == CATEGORY_IMAGELOADER_MENU_ID){
            loadCategoryImages(mCategories);
        }

        return super.onOptionsItemSelected(item);
    }



    private void loadCategoryImages(List<Category> mCategories){
        for(Category category:mCategories) {
            if (category.getContent().getCategoryImages() == null || category.getContent().getCategoryImages().size() <= 0) {
                CategoryImageUpdateTask task = new CategoryImageUpdateTask(mCategoryImageUpdateListener,mUserState.getTenantId(), mUserState.getSiteId(),category.getCategoryId());
                task.execute();
            }
        }
    }

    private CategoryImageUpdateListener mCategoryImageUpdateListener = new CategoryImageUpdateListener() {
        @Override
        public void onImageUpdateSucces(String categoryName, String categoryId) {
            Toast.makeText(getActivity(),"Done updating image for "+categoryName,Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onImageUpdateFailure(String message) {
            Toast.makeText(getActivity(),"Failed to update image for ",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public Loader<List<Category>> onCreateLoader(int id, Bundle args) {

        if (id == CATEGORY_LOADER) {
            //rework this to always have appropriate fragment values
            Integer tenantId = mUserState.getTenantId();
            Integer siteId = mUserState.getSiteId();
            return new CategoryLoader(getActivity(), tenantId , siteId);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {

        if (loader.getId() == CATEGORY_LOADER) {
            mCategories = data;
            updateListGridsToCategory();
        }
    }

    public void setCategories(List<Category> categories) {

        mCategories = categories;
    }

    private void updateListGridsToCategory() {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        if (mCategoryAdapter == null) {
            mCategoryAdapter = new CategoryAdapter(getActivity(), mUserState.getTenantId(), mUserState.getSiteId());
            mCategoryAdapter.addAll(mCategories);
        }

        mGridOfCategories.setAdapter(mCategoryAdapter);
        mListOfCategories.setAdapter(mCategoryAdapter);

        if (prefs.getShowAsGrids()) {
            mGridOfCategories.setVisibility(View.VISIBLE);
            mListOfCategories.setVisibility(View.GONE);
            mCategoryAdapter.setIsGrid(true);
            mCategoryAdapter.notifyDataSetChanged();
        } else {
            mGridOfCategories.setVisibility(View.GONE);
            mListOfCategories.setVisibility(View.VISIBLE);
            mCategoryAdapter.setIsGrid(false);
            mCategoryAdapter.notifyDataSetChanged();
        }

        if (mCategories == null || mCategories.size() < 1) {
            mGridOfCategories.setVisibility(View.GONE);
            mListOfCategories.setVisibility(View.GONE);
            mEmptyListMessageView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListMessageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Category>> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //traverse categories adapter, if leaf then, tell activity this is a leaf and to show and retrieve products
        Category categoryAtPosition = mCategoryAdapter.getItem(position);
        mListener.onCategoryChosen(categoryAtPosition);
    }

    private static CategoryFragmentListener sCategoryListener = new CategoryFragmentListener() {

        @Override
        public void onCategoryChosen(Category category) {
            Log.e("TAG", "not implemented");
        }

        @Override
        public void onSearchPerformedFromCategory(int currentCategory, String query) {
            Log.e("TAG", "not implemented");
        }

    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchMenuItem.collapseActionView();

        int categoryId = -1;

        if (mCategories != null && mCategories.size() > 0 && mCategories.get(0).getParentCategory() != null) {
            categoryId = mCategories.get(0).getParentCategory().getCategoryId();
        }

        mListener.onSearchPerformedFromCategory(categoryId, query);

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

        return true;
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

        onQueryTextSubmit(recentProductSearches.get(position).getSearchTerm());

        return true;
    }
}
