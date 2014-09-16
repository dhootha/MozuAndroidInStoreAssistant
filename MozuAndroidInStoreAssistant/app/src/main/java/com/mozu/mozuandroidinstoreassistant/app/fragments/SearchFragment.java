package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.contracts.customer.CustomerAccountCollection;
import com.mozu.api.contracts.productruntime.ProductSearchResult;
import com.mozu.mozuandroidinstoreassistant.app.MainActivity;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.ProductDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.GlobalSearchCustomerAdapter;
import com.mozu.mozuandroidinstoreassistant.app.adapters.GlobalSearchOrderAdapter;
import com.mozu.mozuandroidinstoreassistant.app.adapters.GlobalSearchProductAdapter;
import com.mozu.mozuandroidinstoreassistant.app.adapters.SearchSuggestionsCursorAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.SearchFetcher;
import com.mozu.mozuandroidinstoreassistant.app.models.RecentSearch;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class SearchFragment extends Fragment implements  SearchView.OnSuggestionListener{

    public static final int MAX_NUMBER_OF_SEARCHES = 50;

    private View mView;
    private ListView mOrdersSearchView;
    private ListView mProductsSearchView;
    private ListView mCustomersSearchView;
    private Observable<OrderCollection> mOrderObservable;
    private Observable<ProductSearchResult> mProductObservable;
    private Observable<CustomerAccountCollection> mCustomerObservable;
    private LoadingView mOrderLoadingView;
    private LoadingView mProductLoadingView;
    private LoadingView mCustomerLoadingView;

    private LinearLayout mOrderLayout;
    private LinearLayout mProductLayout;
    private LinearLayout mCustomerLayout;
    private EditText mSearchEditText;
    private ImageView mSearchImageView;
    private Button mOrdersViewButton;
    private Button mProductsViewButton;
    private Button mCustomersViewButton;
    private String mCustomerSearchString;
    private String mOrderSearchString;
    private String mProductSearchString;

    private static String TENANT_ID = "tenantId";
    private static String SITE_ID = "siteId";

    private Integer mTenantId;
    private Integer mSiteId;
    Subscription mSubscription = Subscriptions.empty();

    private SearchFetcher mSearchFetcher;
    private GlobalSearchOrderAdapter mOrderAdapter;
    private GlobalSearchProductAdapter mProductAdapter;

    private GlobalSearchCustomerAdapter mCustomerAdapter;
    private SearchView mSearchView;
    private UserAuthenticationStateMachine mUserState;

    public static SearchFragment getInstance(Integer tenantId,Integer siteId){
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TENANT_ID, tenantId);
        bundle.putInt(SITE_ID, siteId);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        mTenantId = b.getInt(TENANT_ID);
        mSiteId = b.getInt(SITE_ID);
        mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mSearchFetcher = new SearchFetcher();
        mOrderObservable = AndroidObservable.bindFragment(this, mSearchFetcher.searchOrder(mTenantId, mSiteId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mProductObservable = AndroidObservable.bindFragment(this, mSearchFetcher.searchProduct(mTenantId,mSiteId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        mCustomerObservable = AndroidObservable.bindFragment(this, mSearchFetcher.searchCustomer(mTenantId, mSiteId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        setRetainInstance(true);

    }

    private void showSuggestions() {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        List<RecentSearch> recentGlobalSearchs= prefs.getRecentGlobalSearchs();

        // Load data from list to cursor
        String[] columns = new String[] { "_id", "text" };
        Object[] temp = new Object[] { 0, "default" };

        MatrixCursor cursor = new MatrixCursor(columns);

        if (recentGlobalSearchs == null || recentGlobalSearchs.size() < 1) {
            return;
        }

        for(int i = 0; i < recentGlobalSearchs.size(); i++) {

            temp[0] = i;
            temp[1] = recentGlobalSearchs.get(i);

            cursor.addRow(temp);

        }

        mSearchView.setSuggestionsAdapter(new SearchSuggestionsCursorAdapter(getActivity(), cursor, recentGlobalSearchs));

        mSearchView.setOnSuggestionListener(this);
    }

    private void saveSearchToList(String query) {
        //save search to list
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();

        List<RecentSearch> recentGlobalSearchs = prefs.getRecentGlobalSearchs();

        if (recentGlobalSearchs == null) {
            recentGlobalSearchs = new ArrayList<RecentSearch>();
        }

        //if search already exists then dont add it again
        for (int i = 0; i < recentGlobalSearchs.size(); i++) {
            if (recentGlobalSearchs.get(i).getSearchTerm().equalsIgnoreCase(query)) {
                recentGlobalSearchs.remove(i);
                break;
            }
        }

        RecentSearch search = new RecentSearch();
        search.setSearchTerm(query);

        recentGlobalSearchs.add(0, search);

        if (recentGlobalSearchs.size() > MAX_NUMBER_OF_SEARCHES) {
            recentGlobalSearchs.remove(recentGlobalSearchs.size() - 1);
        }

        prefs.setRecentGlobalSearchs(recentGlobalSearchs);

        mUserState.updateUserPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_search, container, false);
            mOrderLoadingView = (LoadingView) mView.findViewById(R.id.order_loading_view);
            mProductLoadingView = (LoadingView) mView.findViewById(R.id.product_loading_view);
            mCustomerLoadingView = (LoadingView) mView.findViewById(R.id.customer_loading_view);
            mSearchEditText = (EditText) mView.findViewById(R.id.global_search);

            mOrdersViewButton = (Button) mView.findViewById(R.id.order_view_all);
            //mOrdersViewButton.setOnClickListener(new OrderViewListener());
            mProductsViewButton = (Button) mView.findViewById(R.id.product_view_all);
            //mProductsViewButton.setOnClickListener(new ProductViewListener());
            mCustomersViewButton = (Button) mView.findViewById(R.id.customer_view_all);

            mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String s = mSearchEditText.getText().toString();
                        performSearch(s);
                    }
                    return false;
                }
            });
            mSearchImageView = (ImageView) mView.findViewById(R.id.global_search_button);
            mSearchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = mSearchEditText.getText().toString();
                    performSearch(s);
                }
            });

            mOrderLayout = (LinearLayout) mView.findViewById(R.id.order_layout_view);
            mProductLayout = (LinearLayout) mView.findViewById(R.id.product_layout_view);
            mCustomerLayout = (LinearLayout) mView.findViewById(R.id.customer_layout_view);

            mOrdersSearchView = (ListView) mView.findViewById(R.id.orders_search_list);
            mProductsSearchView = (ListView) mView.findViewById(R.id.products_search_list);
            mCustomersSearchView = (ListView) mView.findViewById(R.id.customers_search_list);
            mOrderAdapter = new GlobalSearchOrderAdapter(new OrderCollection());
            mOrdersSearchView.setAdapter(mOrderAdapter);
            mOrdersSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Order order = mOrderAdapter.getItem(position);
                    UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());

                    Intent intent = new Intent(getActivity(), OrderDetailActivity.class);

                    intent.putExtra(OrderDetailActivity.ORDER_NUMBER_EXTRA_KEY, order.getId());
                    intent.putExtra(OrderDetailActivity.CURRENT_TENANT_ID, userAuthenticationStateMachine.getTenantId());
                    intent.putExtra(OrderDetailActivity.CURRENT_SITE_ID, userAuthenticationStateMachine.getSiteId());

                    startActivity(intent);
                }
            });

            mProductAdapter = new GlobalSearchProductAdapter(new ProductSearchResult());
            mProductsSearchView.setAdapter(mProductAdapter);
            mProductsSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String productCode = mProductAdapter.getItem(position).getProductCode();
                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                    UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());

                    intent.putExtra(ProductDetailActivity.PRODUCT_CODE_EXTRA_KEY, productCode);
                    intent.putExtra(ProductDetailActivity.CURRENT_TENANT_ID, userAuthenticationStateMachine.getTenantId());
                    intent.putExtra(ProductDetailActivity.CURRENT_SITE_ID, userAuthenticationStateMachine.getSiteId());

                    startActivity(intent);

                }
            });


            mCustomerAdapter = new GlobalSearchCustomerAdapter(new CustomerAccountCollection());
            mCustomersSearchView.setAdapter(mCustomerAdapter);
            setHasOptionsMenu(true);
        }else{
            if(mView.getParent() != null) {
                ((ViewGroup) mView.getParent()).removeView(mView);
            }
        }

        return mView;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();
        List<RecentSearch> recentProductSearches = prefs.getRecentProductSearches();
        String selectedStr = recentProductSearches.get(position).getSearchTerm();
        performSearch(selectedStr);
        mSearchView.setQuery(selectedStr,false);
        return true;
    }

    private class OrderViewListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(!TextUtils.isEmpty(mOrderSearchString)){
               // ((MainActivity) getActivity()).initializeOrdersFragment(mOrderSearchString);

            }

        }
    }

    private class ProductViewListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(!TextUtils.isEmpty(mProductSearchString)){
                //((ProductFragmentListener) getActivity()).onSearchPerformedFromProduct(0,mProductSearchString);
            }
        }
    }

    private class CustomerViewListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(!TextUtils.isEmpty(mCustomerSearchString)){

            }

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint(getString(R.string.global_search_hint));
        mSearchView.setMaxWidth(1500);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                performSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        int searchPlateId = mSearchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = mSearchView.findViewById(searchPlateId);
        if (searchPlate!=null) {
            searchPlate.setBackgroundResource(R.drawable.product_detail_images_background);
        }
       mSearchView.setIconified(false);
       mSearchMenuItem.expandActionView();
       mSearchView.requestFocus();
       showSuggestions();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if (item.getItemId() == R.id.action_search) {
       }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroyView() {
        mSubscription.unsubscribe();
        super.onDestroyView();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
    }


    public void performSearch(final String s){
        mSearchView.clearFocus();
        saveSearchToList(s);
        hideKeyboard();
        mSearchFetcher.setQueryString(s);
        mOrderLayout.setVisibility(View.VISIBLE);
        mOrderLoadingView.setLoading();
        mOrdersViewButton.setVisibility(View.INVISIBLE);
        mOrderObservable.subscribe(new Observer<OrderCollection>(){
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mOrderLoadingView.setError("Failed to fetch Orders");
                mOrdersViewButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNext(OrderCollection orderCollection) {
                if (orderCollection.getItems() != null && !orderCollection.getItems().isEmpty()) {
                    mOrderLoadingView.success();
                    mOrderAdapter.setData(orderCollection);
                    mOrderAdapter.notifyDataSetChanged();
                    mOrdersViewButton.setVisibility(View.VISIBLE);
                    mOrderSearchString = s;
                } else {
                    mOrderLoadingView.setError("No Results found");
                    mOrdersViewButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        mProductLayout.setVisibility(View.VISIBLE);
        mProductLoadingView.setLoading();
        mProductsViewButton.setVisibility(View.INVISIBLE);
        mProductObservable.subscribe(new Observer<ProductSearchResult>(){

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mOrderLoadingView.setError("Failed to fetch Product");
                mProductsViewButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNext(ProductSearchResult productSearchResult) {
                if (productSearchResult.getItems() != null && !productSearchResult.getItems().isEmpty()) {
                    mProductLoadingView.success();
                    mProductAdapter.setData(productSearchResult);
                    mProductAdapter.notifyDataSetChanged();
                    mProductsViewButton.setVisibility(View.VISIBLE);
                    mProductSearchString = s;

                } else {
                    mProductLoadingView.setError("No Results found");
                    mProductsViewButton.setVisibility(View.INVISIBLE);
                }

            }
        });

        mCustomerLayout.setVisibility(View.VISIBLE);
        mCustomerLoadingView.setLoading();
        mCustomersViewButton.setVisibility(View.INVISIBLE);
        mCustomerObservable.subscribe(new Observer<CustomerAccountCollection>(){

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mCustomerLoadingView.setError("Failed to fetch Customers");
                mCustomersViewButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNext(CustomerAccountCollection customerAccountCollection) {
                if (customerAccountCollection.getItems() != null && !customerAccountCollection.getItems().isEmpty()) {
                    mCustomerLoadingView.success();
                    mCustomerAdapter.setData(customerAccountCollection);
                    mCustomerAdapter.notifyDataSetChanged();
                    mCustomersViewButton.setVisibility(View.VISIBLE);
                    mCustomerSearchString = s;
                } else {
                    mCustomerLoadingView.setError("No Results found");
                    mCustomersViewButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }



}
