package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Activity;
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

import com.mozu.api.contracts.productruntime.Category;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.CategoryAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.CategoryLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Category>>, AdapterView.OnItemClickListener {

    private static final int CATEGORY_LOADER = 0;

    private GridView mGridOfCategories;

    private UserAuthenticationStateMachine mUserState;

    private CategoryAdapter mCategoryAdapter;

    private List<Category> mAllCategories = new ArrayList<Category>();

    private CategoryFragmentListener mListener = sCategoryListener;

    private Stack<Category> mCategoryStack = new Stack<Category>();

    private static CategoryFragmentListener sCategoryListener = new CategoryFragmentListener() {

        @Override
        public void onLeafCategoryChosen(Category parent) {

        }

    };

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_category_product_grid, container, false);

        mGridOfCategories = (GridView) fragmentView.findViewById(R.id.category_grid);
        mGridOfCategories.setOnItemClickListener(this);

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
                mAllCategories.addAll(data);
                initiateAdapterToTopRootCategoires();
            }

            mGridOfCategories.setAdapter(mCategoryAdapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Category>> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //traverse categories adapter, if leaf then, tell activity this is a leaf and to show and retrieve products
        Category categoryAtPosition = mCategoryAdapter.getItem(position);
        mCategoryStack.add(categoryAtPosition);
        initiateAdapterToCategory(categoryAtPosition);
    }

    private void initiateAdapterToCategory(Category parent) {

        if (parent.getChildrenCategories() == null || parent.getChildrenCategories().size() == 0) {
            mListener.onLeafCategoryChosen(parent);

            return;
        }

        mCategoryAdapter.clear();
        mCategoryAdapter.addAll(parent.getChildrenCategories());
        mCategoryAdapter.notifyDataSetChanged();
    }

    private void initiateAdapterToTopRootCategoires() {

        mCategoryAdapter.clear();
        mCategoryAdapter.addAll(mAllCategories);
        mCategoryAdapter.notifyDataSetChanged();
    }

    public boolean shouldHandleBackPressed() {

        //the top node has already been popped off
        if (mCategoryStack.size() == 1) {
            return false;
        }

        //only one level deep in hiearchy, so pop off last item and return true
        if (mCategoryStack.size() == 2) {
            mCategoryStack.pop();
            initiateAdapterToTopRootCategoires();

            return true;
        }

        if (mCategoryStack.size() > 2) {
            mCategoryStack.pop();
            initiateAdapterToCategory(mCategoryStack.peek());

            return true;
        }

        return false;
    }
}
