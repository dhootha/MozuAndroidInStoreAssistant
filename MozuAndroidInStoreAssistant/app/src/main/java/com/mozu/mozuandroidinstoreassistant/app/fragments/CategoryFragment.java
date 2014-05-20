package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.mozu.api.contracts.productadmin.Category;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.CategoryAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.CategoryLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import java.util.List;

public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Category>>, GridView.OnItemClickListener {

    private static final int CATEGORY_LOADER = 0;

    private GridView mGridOfCategories;

    private UserAuthenticationStateMachine mUserState;

    private CategoryAdapter mCategoryAdapter;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_category_product_grid, container, false);

        mGridOfCategories = (GridView) fragmentView.findViewById(R.id.category_grid);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(CATEGORY_LOADER, savedInstanceState, this).forceLoad();
    }

    @Override
    public Loader<List<Category>> onCreateLoader(int id, Bundle args) {

        if (id == CATEGORY_LOADER) {
            AuthenticationProfile authProfile = mUserState.getAuthProfile();
            UserPreferences prefs = mUserState.getCurrentUsersPreferences();

            //rework this to always have appropriate fragment values
            return new CategoryLoader(getActivity(), prefs.getDefaultTenantId() != null ? Integer.parseInt(prefs.getDefaultTenantId()) : null, prefs.getDefaultSiteId() != null ? Integer.parseInt(prefs.getDefaultSiteId()) : null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {

        if (loader.getId() == CATEGORY_LOADER) {
            if (mCategoryAdapter == null) {
                mCategoryAdapter = new CategoryAdapter(getActivity());
                mCategoryAdapter.addAll(data);
            }

            mGridOfCategories.setAdapter(mCategoryAdapter);
            mGridOfCategories.setOnItemClickListener(this);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Category>> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
    }
}
