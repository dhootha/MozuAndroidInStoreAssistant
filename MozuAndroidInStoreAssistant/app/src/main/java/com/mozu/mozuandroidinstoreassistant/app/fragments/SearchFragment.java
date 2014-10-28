package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.contracts.customer.CustomerAccount;
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
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.loaders.SearchFetcher;
import com.mozu.mozuandroidinstoreassistant.app.models.RecentSearch;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.ArrayList;
import java.util.Iterator;
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
    private MenuItem mSearchMenuItem;
    private TextView mCustomersResultCount;
    private TextView mOrdersResultCount;
    private TextView mProductsResultCount;

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
        mOrderObservable = AndroidObservable.bindFragment(this, mSearchFetcher.searchOrder(mTenantId, mSiteId));
        mProductObservable = AndroidObservable.bindFragment(this, mSearchFetcher.searchProduct(mTenantId,mSiteId));
        mCustomerObservable = AndroidObservable.bindFragment(this, mSearchFetcher.searchCustomer(mTenantId, mSiteId));
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

    private List<RecentSearch> removeDuplicateSearchItem(List<RecentSearch> searchItems, String query) {
        Iterator<RecentSearch> i = searchItems.iterator();
        while (i.hasNext()) {
            RecentSearch item = i.next();
            if (item.getSearchTerm().equalsIgnoreCase(query)) {
                i.remove();
            }
        }
        return searchItems;
    }

    private void saveSearchToList(String query) {
        //save search to list
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();
        List<RecentSearch> recentGlobalSearchs = prefs.getRecentGlobalSearchs();
        if (recentGlobalSearchs == null) {
            recentGlobalSearchs = new ArrayList<RecentSearch>();
        }
        recentGlobalSearchs = removeDuplicateSearchItem(recentGlobalSearchs,query);
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((MainActivity) getActivity()).setSearchSelected();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            mView = inflater.inflate(R.layout.fragment_search, container, false);
            mOrderLoadingView = (LoadingView) mView.findViewById(R.id.order_loading_view);
            mProductLoadingView = (LoadingView) mView.findViewById(R.id.product_loading_view);
            mCustomerLoadingView = (LoadingView) mView.findViewById(R.id.customer_loading_view);
            mSearchEditText = (EditText) mView.findViewById(R.id.global_search);

            mOrdersViewButton = (Button) mView.findViewById(R.id.order_view_all);
            mOrdersResultCount = (TextView)mView.findViewById(R.id.order_result_count);
            mOrdersViewButton.setOnClickListener(new OrderViewListener());
            mProductsViewButton = (Button) mView.findViewById(R.id.product_view_all);
            mProductsResultCount = (TextView)mView.findViewById(R.id.product_result_count);
            mProductsViewButton.setOnClickListener(new ProductViewListener());
            mCustomersViewButton = (Button) mView.findViewById(R.id.customer_view_all);
            mCustomersResultCount = (TextView)mView.findViewById(R.id.customer_result_count);
            mCustomersViewButton.setOnClickListener(new CustomerViewListener());

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
                    intent.putExtra(ProductDetailActivity.CURRENT_SITE_DOMAIN, userAuthenticationStateMachine.getSiteDomain());
                    startActivity(intent);

                }
            });


            mCustomerAdapter = new GlobalSearchCustomerAdapter(new CustomerAccountCollection());
            mCustomersSearchView.setAdapter(mCustomerAdapter);
            mCustomersSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CustomerAccount customerAccount = mCustomerAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
                intent.putExtra(CustomerDetailActivity.CUSTOMER_ID, customerAccount.getId());
                startActivity(intent);
            }
        });
            setHasOptionsMenu(true);


        return mView;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        UserPreferences prefs = mUserState.getCurrentUsersPreferences();
        List<RecentSearch> recentGlobalSearchs = prefs.getRecentGlobalSearchs();
        String selectedStr = recentGlobalSearchs.get(position).getSearchTerm();
        performSearch(selectedStr);
        mSearchView.setQuery(selectedStr,false);
        return true;
    }

    public void onBackPressed() {
        if (TextUtils.isEmpty(mSearchView.getQuery()) && mOrderLayout.getVisibility() == View.INVISIBLE && mCustomerLayout.getVisibility() == View.INVISIBLE && mProductLayout.getVisibility() == View.INVISIBLE) {
            getFragmentManager().popBackStack();
        }
    }

    private class OrderViewListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (!TextUtils.isEmpty(mOrderSearchString)) {
                ((GlobalSearchListener) getActivity()).onOrderLaunch(mOrderSearchString);
            }

        }
    }

    private class ProductViewListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(!TextUtils.isEmpty(mProductSearchString)){
                mSearchMenuItem.collapseActionView();
                ((GlobalSearchListener) getActivity()).launchProductSearch(mProductSearchString);
            }
        }
    }

    private class CustomerViewListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(!TextUtils.isEmpty(mCustomerSearchString)){
                mSearchMenuItem.collapseActionView();
                ((GlobalSearchListener) getActivity()).launchCustomerSearch(mCustomerSearchString);
            }

        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem  = menu.findItem(R.id.search);
       mSearchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                onBackPressed();
                return true;
            }
        });
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
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
        mSearchMenuItem.expandActionView();
        showSuggestions();

        mSearchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackPressed();
                }
                return true;
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if (item.getItemId() == R.id.search) {
           showSuggestions();
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
        mOrdersResultCount.setVisibility(View.INVISIBLE);
        mOrderObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<OrderCollection>() {
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
                            if (orderCollection.getTotalCount() > 3) {
                                mOrdersViewButton.setVisibility(View.VISIBLE);
                            } else {
                                mOrdersViewButton.setVisibility(View.INVISIBLE);
                            }
                            mOrderSearchString = s;
                            mOrdersResultCount.setVisibility(View.VISIBLE);
                            mOrdersResultCount.setText("(" + orderCollection.getTotalCount() + " items found) ");
                        } else {
                            mOrderLoadingView.setError("No Results found");
                            mOrdersViewButton.setVisibility(View.INVISIBLE);
                            mOrdersResultCount.setVisibility(View.INVISIBLE);

                        }
                    }
                });

        mProductLayout.setVisibility(View.VISIBLE);
        mProductLoadingView.setLoading();
        mProductsViewButton.setVisibility(View.INVISIBLE);
        mProductsResultCount.setVisibility(View.INVISIBLE);
        mProductObservable.
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<ProductSearchResult>() {

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
                            if (productSearchResult.getTotalCount() > 3) {
                                mProductsViewButton.setVisibility(View.VISIBLE);
                            } else {
                                mProductsViewButton.setVisibility(View.INVISIBLE);
                            }
                            mProductSearchString = s;
                            mProductsResultCount.setVisibility(View.VISIBLE);
                            mProductsResultCount.setText("(" + productSearchResult.getTotalCount() + " items found) ");

                        } else {
                            mProductLoadingView.setError("No Results found");
                            mProductsViewButton.setVisibility(View.INVISIBLE);
                            mProductsResultCount.setVisibility(View.INVISIBLE);
                        }

                    }
                });

        mCustomerLayout.setVisibility(View.VISIBLE);
        mCustomerLoadingView.setLoading();
        mCustomersViewButton.setVisibility(View.INVISIBLE);
        mCustomersResultCount.setVisibility(View.INVISIBLE);
        mCustomerObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<CustomerAccountCollection>() {

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
                            if (customerAccountCollection.getTotalCount() > 3) {
                                mCustomersViewButton.setVisibility(View.VISIBLE);
                            } else {
                                mCustomersViewButton.setVisibility(View.INVISIBLE);
                            }

                            mCustomerSearchString = s;
                            mCustomersResultCount.setVisibility(View.VISIBLE);
                            mCustomersResultCount.setText("(" + customerAccountCollection.getTotalCount() + " items found) ");
                        } else {
                            mCustomerLoadingView.setError("No Results found");
                            mCustomersViewButton.setVisibility(View.INVISIBLE);
                            mCustomersResultCount.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    public interface GlobalSearchListener {

        public void onOrderLaunch(String searchQuery);

        public void launchProductSearch(String searchQuery);

        public void launchCustomerSearch(String searchQuery);
    }

}
