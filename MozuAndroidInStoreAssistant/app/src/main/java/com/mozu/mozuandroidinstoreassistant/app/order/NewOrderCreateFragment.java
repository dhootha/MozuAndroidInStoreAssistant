package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.products.ProductPrice;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.contracts.productruntime.ProductSearchResult;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.NewOrderProductAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.ProductSuggestionAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewOrderCreateFragment extends Fragment {

    private static final String NEW_ORDER = "neworder";
    @InjectView(R.id.product_lookup)
    public AutoCompleteTextView mProductLookup;

    @InjectView(R.id.product_listview)
    public ListView mOrderProducts;
    Subscription mSubscription;
    ProductSuggestionAdapter mAdapter;
    private View mView;
    private int mTenantId;
    private int mSiteId;
    private NewOrderProductAdapter mProductsAdapter;
    private Order mOrder;

    public static NewOrderCreateFragment getInstance(Order order) {
        NewOrderCreateFragment newOrderCreateFragment = new NewOrderCreateFragment();
        Bundle b = new Bundle();
        b.putSerializable(NEW_ORDER, order);
        newOrderCreateFragment.setArguments(b);
        return newOrderCreateFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViews();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOrder = (Order) getArguments().getSerializable(NEW_ORDER);
        mView = inflater.inflate(R.layout.new_order_details_fragment, null);
        UserAuthenticationStateMachine mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mSiteId = mUserState.getSiteId();
        mTenantId = mUserState.getTenantId();
        ButterKnife.inject(this, mView);
        NewOrderManager.count = 0;

        return mView;
    }

    private void setUpViews() {
        mAdapter = new ProductSuggestionAdapter(getActivity());
        mProductLookup.setAdapter(mAdapter);
        mProductsAdapter = new NewOrderProductAdapter();
        mOrderProducts.setAdapter(mProductsAdapter);
        mProductLookup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (mSubscription != null && !mSubscription.isUnsubscribed()) {
                    mSubscription.unsubscribe();
                }
                mSubscription = AndroidObservable.bindFragment(NewOrderCreateFragment.this, NewOrderManager.getInstance().getProductSuggestion(charSequence.toString(), mTenantId, mSiteId))
                        .subscribe(new Subscriber<ProductSearchResult>() {
                            @Override
                            public void onCompleted() {
                                //TODO

                            }

                            @Override
                            public void onError(Throwable e) {
                                //TODO
                            }

                            @Override
                            public void onNext(ProductSearchResult productSearchResult) {
                                List<Product> data = productSearchResult.getItems();
                                mAdapter.clear();
                                mAdapter.addAll(data);
                                mAdapter.notifyDataSetChanged();

                            }
                        });

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mProductLookup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Product product = (Product) adapterView.getItemAtPosition(position);
                com.mozu.api.contracts.commerceruntime.products.Product orderProduct = new com.mozu.api.contracts.commerceruntime.products.Product();
                orderProduct.setProductCode(product.getProductCode());
                ProductPrice productPrice = new ProductPrice();
                productPrice.setPrice(product.getPrice().getPrice());
                orderProduct.setVariationProductCode(product.getVariationProductCode());

                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(orderProduct);
                orderItem.setQuantity(1);
                orderItem.setTotal(productPrice.getPrice());
                orderItem.setFulfillmentMethod(OrderDetailFullfillmentFragment.SHIP);

                AndroidObservable.bindFragment(NewOrderCreateFragment.this, NewOrderManager.getInstance().getOrderUpdate(mTenantId, mSiteId, orderItem, mOrder == null ? null : mOrder.getId())).
                        subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Order>() {
                            @Override
                            public void onCompleted() {
                                mProductsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Order order) {
                                mOrder = order;
                                mProductsAdapter.addData(order);
                            }
                        });

            }
        });
    }

}
