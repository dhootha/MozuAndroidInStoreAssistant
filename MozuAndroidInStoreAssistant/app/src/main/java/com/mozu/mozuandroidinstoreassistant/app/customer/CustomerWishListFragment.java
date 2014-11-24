package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mozu.api.contracts.commerceruntime.wishlists.Wishlist;
import com.mozu.api.contracts.commerceruntime.wishlists.WishlistItem;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.ProductDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomerWishListFetcher;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.ArrayList;
import java.util.List;

import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CustomerWishListFragment extends Fragment {

    private static String CUSTOMER_ACCOUNT = "customerAccount";
    private CustomerAccount mCustomerAccount;
    private ListView mListView;
    private LoadingView mWishListLoading;
    private CustomerWishListAdapter mAdapter;
    private rx.Observable<List<Wishlist>> mWishListObservable;
    private CustomerWishListFetcher mCustomerWishListFetcher;

    public static CustomerWishListFragment getInstance(CustomerAccount customerAccount){
        CustomerWishListFragment customerWishListFragment = new CustomerWishListFragment();
        Bundle b = new Bundle();
        b.putSerializable(CUSTOMER_ACCOUNT,customerAccount);
        customerWishListFragment.setArguments(b);
        return customerWishListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomerAccount = (CustomerAccount) getArguments().getSerializable(CUSTOMER_ACCOUNT);
        mCustomerWishListFetcher = new CustomerWishListFetcher();
        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mWishListObservable = AndroidObservable.bindFragment(this, mCustomerWishListFetcher.getWishListsByCustomerId(userState.getTenantId(), userState.getSiteId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.customer_wishlist, container, false);
        mListView = (ListView) fragmentView.findViewById(R.id.customer_wishlist);
        mWishListLoading = (LoadingView)fragmentView.findViewById(R.id.customer_wishlist_loading);
        mAdapter = new CustomerWishListAdapter(new ArrayList<WishlistItem>());
        mListView.setAdapter(mAdapter);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 WishlistItem item = mAdapter.getItem(i);
                 String productCode = item.getProduct().getProductCode();
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());
                intent.putExtra(ProductDetailActivity.PRODUCT_CODE_EXTRA_KEY, productCode);
                intent.putExtra(ProductDetailActivity.CURRENT_TENANT_ID, userAuthenticationStateMachine.getTenantId());
                intent.putExtra(ProductDetailActivity.CURRENT_SITE_ID, userAuthenticationStateMachine.getSiteId());
                startActivity(intent);
            }
        });
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    private void loadData(){
        mCustomerWishListFetcher.setCustomerId(mCustomerAccount.getId());
        mWishListObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new WishlistSubscriber());
    }

    private class WishlistSubscriber implements rx.Observer<List<Wishlist>> {
        List<WishlistItem> mWishlistItemList = new ArrayList<WishlistItem>();
        @Override
        public void onCompleted() {
            if (mWishlistItemList.size() > 0) {
                mWishListLoading.success();
                mAdapter.setData(mWishlistItemList);
                mAdapter.notifyDataSetChanged();
            } else {
                mWishListLoading.setError(getActivity().getResources().getString(R.string.empty_wishlist_items));
            }
        }

        @Override
        public void onError(Throwable e) {
            mWishListLoading.setError(e.getMessage());
        }

        @Override
        public void onNext(List<Wishlist> wishlists) {
            for(Wishlist wishlist:wishlists){
                mWishlistItemList.addAll(wishlist.getItems());
            }
        }
    }

}
