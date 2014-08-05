package com.mozu.mozuandroidinstoreassistant.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.productruntime.Category;
import com.mozu.mozuandroidinstoreassistant.app.fragments.CategoryFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.CategoryFragmentListener;
import com.mozu.mozuandroidinstoreassistant.app.fragments.CustomersFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.OrderFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.OrderListener;
import com.mozu.mozuandroidinstoreassistant.app.fragments.ProductFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.ProductFragmentListener;
import com.mozu.mozuandroidinstoreassistant.app.fragments.ProductListListener;
import com.mozu.mozuandroidinstoreassistant.app.fragments.ProductSearchFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.SearchFragment;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

public class MainActivity extends Activity implements View.OnClickListener, CategoryFragmentListener, ProductFragmentListener, ProductListListener, OrderListener {

    private static final String CATEGORY_FRAGMENT = "category_fragment_taggy_tag_tag";
    private static final String SEARCH_FRAGMENT = "search_fragment_taggy_tag_tag";
    private static final String PRODUCTS_FRAGMENT = "products_fragment_taggy_tag_tag";
    private static final String ORDERS_FRAGMENT = "orders_fragment_taggy_tag_tag";
    private static final String CUSTOMERS_FRAGMENT = "customers_fragment_taggy_tag_tag";
    private static final String PRODUCTS_SEARCH_FRAGMENT_BACKSTACK = "product_PRODUCTS_SEARCH_FRAGMENT_BACKSTACK";

    private static final String CATEGORY_FRAGMENT_BACKSTACK = "category_fragment_taggy_tag_tag_BACKSTACK";
    private static final String SEARCH_FRAGMENT_BACKSTACK = "search_fragment_taggy_tag_tag_BACKSTACK";
    private static final String PRODUCTS_FRAGMENT_BACKSTACK = "products_fragment_taggy_tag_tag_BACKSTACK";
    private static final String ORDERS_FRAGMENT_BACKSTACK = "orders_fragment_taggy_tag_tag_BACKSTACK";
    private static final String CUSTOMERS_FRAGMENT_BACKSTACK = "customers_fragment_taggy_tag_tag_BACKSTACK";
    private static final String CURRENTLY_SELECTED_NAV_VIEW_ID = "CURRENTLY_SELECTED_NAV_VIEW_ID";

    private LinearLayout mSearchMenuLayout;
    private LinearLayout mProductsLayout;
    private LinearLayout mOrdersLayout;
    private LinearLayout mCustomersLayout;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    //private boolean mWasCreatedInPortrait = false;

    private int mCurrentlySelectedNavItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSearchMenuLayout = (LinearLayout) findViewById(R.id.menu_search_layout);
        mSearchMenuLayout.setOnClickListener(this);

        mProductsLayout = (LinearLayout) findViewById(R.id.menu_products_layout);
        mProductsLayout.setOnClickListener(this);

        mOrdersLayout = (LinearLayout) findViewById(R.id.menu_orders_layout);
        mOrdersLayout.setOnClickListener(this);

        mCustomersLayout = (LinearLayout) findViewById(R.id.menu_customers_layout);
        mCustomersLayout.setOnClickListener(this);

        getActionBar().setTitle(R.string.menu_products_text);
        getActionBar().setIcon(R.drawable.logo_actionbar);
        mProductsLayout.setSelected(true);

        if (savedInstanceState == null) {
            mCurrentlySelectedNavItem = R.id.menu_products_layout;
            initializeCategoryFragment();
        } else {
            updateNavView(savedInstanceState.getInt(CURRENTLY_SELECTED_NAV_VIEW_ID, R.id.menu_products_layout));
        }

        //mWasCreatedInPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        setupDrawer();
    }

    private void setupDrawer() {
        //holder, will show whatever fragment is in main content area
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.mozu_ic_navigation_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //if (mWasCreatedInPortrait) {
            mDrawerToggle.syncState();
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            UserAuthenticationStateMachineProducer.getInstance(getApplicationContext()).getCurrentUserAuthState().signOutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        updateNavView(v.getId());

        if (v.getId() == R.id.menu_search_layout) {
            initializeSearchFragment();
        } else if (v.getId() == R.id.menu_products_layout) {
            initializeCategoryFragment();
        } else if (v.getId() == R.id.menu_orders_layout) {
            initializeOrdersFragment();
        } else if (v.getId() == R.id.menu_customers_layout) {
            initializeCustomersFragment();
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }
    }

    private void updateNavView(int viewId) {
        mCurrentlySelectedNavItem = viewId;

        mSearchMenuLayout.setSelected(false);
        mProductsLayout.setSelected(false);
        mOrdersLayout.setSelected(false);
        mCustomersLayout.setSelected(false);

        if (viewId == R.id.menu_search_layout) {
            getActionBar().setTitle(R.string.menu_search_text);
            mSearchMenuLayout.setSelected(true);
        } else if (viewId == R.id.menu_products_layout) {
            getActionBar().setTitle(R.string.menu_products_text);
            mProductsLayout.setSelected(true);
        } else if (viewId == R.id.menu_orders_layout) {
            getActionBar().setTitle("");
            mOrdersLayout.setSelected(true);
        } else if (viewId == R.id.menu_customers_layout) {
            getActionBar().setTitle(R.string.menu_customers_text);
            mCustomersLayout.setSelected(true);
        }
    }

    private void initializeCategoryFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        clearBackstack(fragmentManager);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CategoryFragment fragment = new CategoryFragment();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.content_fragment_holder, fragment, CATEGORY_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void initializeSearchFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        clearBackstack(fragmentManager);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SearchFragment fragment = new SearchFragment();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.content_fragment_holder, fragment);
        fragmentTransaction.commit();
    }

    private void initializeOrdersFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        clearBackstack(fragmentManager);

        UserPreferences prefs = UserAuthenticationStateMachineProducer.getInstance(this).getCurrentUsersPreferences();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        OrderFragment fragment = new OrderFragment();
        fragment.setTenantId(Integer.parseInt(prefs.getDefaultTenantId()));
        fragment.setSiteId(Integer.parseInt(prefs.getDefaultSiteId()));
        fragment.setListener(this);
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.content_fragment_holder, fragment);
        fragmentTransaction.commit();
    }

    private void initializeCustomersFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        clearBackstack(fragmentManager);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CustomersFragment fragment = new CustomersFragment();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.content_fragment_holder, fragment);
        fragmentTransaction.commit();
    }

    private void initializeProductFragment(Category category) {
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ProductFragment fragment = new ProductFragment();
        fragment.setCategoryId(category.getCategoryId());
        fragmentTransaction.setCustomAnimations(R.animator.slide_right_in, R.animator.scale_fade_out,R.animator.slide_left_in, R.animator.scale_fade_out);
        fragmentTransaction.replace(R.id.content_fragment_holder, fragment, PRODUCTS_FRAGMENT);
        fragmentTransaction.addToBackStack(PRODUCTS_FRAGMENT_BACKSTACK);
        fragmentTransaction.commit();
    }

    private void clearBackstack(FragmentManager fragmentManager) {
        while (getFragmentManager().getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    private void addChildCategoryFragment(Category category) {
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CategoryFragment fragment = new CategoryFragment();
        fragment.setCategories(category.getChildrenCategories());
        fragmentTransaction.addToBackStack(CATEGORY_FRAGMENT + String.valueOf(category.getCategoryId()));
        fragmentTransaction.setCustomAnimations(R.animator.slide_right_in, R.animator.scale_fade_out,R.animator.slide_left_in, R.animator.scale_fade_out);
        fragmentTransaction.replace(R.id.content_fragment_holder, fragment, CATEGORY_FRAGMENT + String.valueOf(category.getCategoryId()));
        fragmentTransaction.commit();
    }

    @Override
    public void onCategoryChosen(Category category) {
        if (category.getChildrenCategories() != null && category.getChildrenCategories().size() > 0) {
            addChildCategoryFragment(category);
        } else {
            initializeProductFragment(category);
        }
    }

    @Override
    public void onSearchPerformedFromCategory(int currentCategoryId, String query) {
        showProductSearchFragment(currentCategoryId, query);
    }

    @Override
    public void onSearchPerformedFromProduct(int currentCategoryId, String query) {
        showProductSearchFragment(currentCategoryId, query);
    }

    private void showProductSearchFragment(int categoryId, String query) {
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ProductSearchFragment fragment = new ProductSearchFragment();
        fragment.setCategoryId(categoryId);
        fragment.setQueryString(query);
        fragmentTransaction.setCustomAnimations(R.animator.slide_right_in, R.animator.scale_fade_out,R.animator.slide_left_in, R.animator.scale_fade_out);
        fragmentTransaction.replace(R.id.content_fragment_holder, fragment, PRODUCTS_SEARCH_FRAGMENT_BACKSTACK);
        fragmentTransaction.addToBackStack(PRODUCTS_SEARCH_FRAGMENT_BACKSTACK);
        fragmentTransaction.commit();
    }

    @Override
    public void onProductSelected(String productCodeSelected) {
        onProductChoosen(productCodeSelected);
    }

    @Override
    public void onProductChoosentFromProuct(String productCode) {
        onProductChoosen(productCode);
    }

    private void onProductChoosen(String productCode) {
        UserPreferences prefs = UserAuthenticationStateMachineProducer.getInstance(this).getCurrentUsersPreferences();

        Intent intent = new Intent(this, ProductDetailActivity.class);

        intent.putExtra(ProductDetailActivity.PRODUCT_CODE_EXTRA_KEY, productCode);
        intent.putExtra(ProductDetailActivity.CURRENT_TENANT_ID, Integer.parseInt(prefs.getDefaultTenantId()));
        intent.putExtra(ProductDetailActivity.CURRENT_SITE_ID, Integer.parseInt(prefs.getDefaultSiteId()));

        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(CURRENTLY_SELECTED_NAV_VIEW_ID, mCurrentlySelectedNavItem);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void orderSelected(Order order) {
        UserPreferences prefs = UserAuthenticationStateMachineProducer.getInstance(this).getCurrentUsersPreferences();

        Intent intent = new Intent(this, OrderDetailActivity.class);

        intent.putExtra(OrderDetailActivity.ORDER_NUMBER_EXTRA_KEY, order.getId());
        intent.putExtra(OrderDetailActivity.CURRENT_TENANT_ID, Integer.parseInt(prefs.getDefaultTenantId()));
        intent.putExtra(OrderDetailActivity.CURRENT_SITE_ID, Integer.parseInt(prefs.getDefaultSiteId()));

        startActivity(intent);
    }
}
