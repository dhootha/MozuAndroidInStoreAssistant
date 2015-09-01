package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.mozu.api.contracts.commerceruntime.commerce.PackageMeasurements;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.productruntime.BundledProduct;
import com.mozu.api.contracts.productruntime.Category;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.contracts.productruntime.ProductOption;
import com.mozu.api.contracts.productruntime.ProductOptionValue;
import com.mozu.api.contracts.productruntime.ProductPrice;
import com.mozu.api.contracts.productruntime.ProductProperty;
import com.mozu.api.contracts.productruntime.ProductPropertyValue;
import com.mozu.api.contracts.productruntime.ProductSearchResult;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.bus.RxBus;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderItemRow;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.NewOrderShippingItemLayout;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.ProductSuggestionAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewOrderCreateFragment extends Fragment implements NewOrderItemEditFragment.onItemEditDoneListener, NewOrderShippingItemLayout.OrderUpdateListener {

    private View mView;
    @InjectView(R.id.product_lookup)
    public AutoCompleteTextView mProductLookup;

    @InjectView(R.id.product_listview)
    public ListView mOrderProducts;

    private int mTenantId;
    private int mSiteId;
    Subscription mSubscription;
    ProductSuggestionAdapter mAdapter;
    private com.mozu.mozuandroidinstoreassistant.app.order.NewOrderProductAdapter mProductsAdapter;
    private Order mOrder;
    private boolean mIsEditMode;
    private static final String EDIT_MODE = "editMode";


    public static NewOrderCreateFragment getInstance(boolean isEditMode) {
        NewOrderCreateFragment newOrderCreateFragment = new NewOrderCreateFragment();
        Bundle b = new Bundle();
        b.putBoolean(EDIT_MODE, isEditMode);
        newOrderCreateFragment.setArguments(b);
        return newOrderCreateFragment;
    }

    public void setOrder(Order order) {
        mOrder = order;
    }


    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() instanceof OrderDetailActivity) {
                    ((OrderDetailActivity) getActivity()).setEditModeVisibility(isVisibleToUser);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViews();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("hsjdhsj", true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        RxBus.getInstance().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getEventSubscriber());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.new_order_details_fragment, null);
        UserAuthenticationStateMachine mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mSiteId = mUserState.getSiteId();
        mTenantId = mUserState.getTenantId();
        mIsEditMode = getArguments().getBoolean(EDIT_MODE);
        ButterKnife.inject(this, mView);
        NewOrderManager.count = 0;
        return mView;
    }

    private Subscriber<Object> getEventSubscriber() {
        return new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                if (o instanceof Boolean) {
                    mIsEditMode = (Boolean) o;
                    updateEditMode(mIsEditMode);
                }
            }
        };
    }

    private void updateEditMode(Boolean editMode) {
        mProductsAdapter.setEditMode(editMode);
        mProductsAdapter.notifyDataSetChanged();
        mProductLookup.setVisibility(editMode ? View.VISIBLE : View.GONE);
    }


    private void setUpViews() {
        mAdapter = new ProductSuggestionAdapter(getActivity());
        mProductLookup.setAdapter(mAdapter);
        mProductsAdapter = new com.mozu.mozuandroidinstoreassistant.app.order.NewOrderProductAdapter(this);
        if (mOrder != null && mOrder.getItems() != null) {
            mProductsAdapter.addData(mOrder);
            updateEditMode(mIsEditMode);
        }


        mOrderProducts.setAdapter(mProductsAdapter);
        mProductLookup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int start, int before, int count) {
                if (mSubscription != null && !mSubscription.isUnsubscribed()) {
                    mSubscription.unsubscribe();
                }

                mSubscription = AndroidObservable.bindFragment(NewOrderCreateFragment.this, NewOrderManager.getInstance().getProductSuggestion(charSequence.toString(), mTenantId, mSiteId))
                        .subscribe(new Subscriber<ProductSearchResult>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getActivity(), "Error Searching for product " + charSequence, Toast.LENGTH_SHORT).show();
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
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(convertProduct(product));
                orderItem.setQuantity(1);
                NewOrderItemEditFragment newOrderItemEditFragment = NewOrderItemEditFragment.getInstance(orderItem, mOrder.getId(), false);
                newOrderItemEditFragment.setOnEditDoneListener(NewOrderCreateFragment.this);
                newOrderItemEditFragment.show(getFragmentManager(), "");
            }
        });

        mOrderProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mIsEditMode && adapterView.getItemAtPosition(position) instanceof OrderItemRow) {
                    OrderItem orderItem = ((OrderItemRow) adapterView.getItemAtPosition(position)).orderItem;
                    NewOrderItemEditFragment newOrderItemEditFragment = NewOrderItemEditFragment.getInstance(orderItem, mOrder.getId(), true);
                    newOrderItemEditFragment.setOnEditDoneListener(NewOrderCreateFragment.this);
                    newOrderItemEditFragment.show(getFragmentManager(), "");
                }
            }
        });
    }

    public static com.mozu.api.contracts.commerceruntime.products.Product convertProduct(Product inProduct) {
        com.mozu.api.contracts.commerceruntime.products.Product outProduct =
                new com.mozu.api.contracts.commerceruntime.products.Product();

        if (inProduct.getBundledProducts() != null) {
            List<com.mozu.api.contracts.commerceruntime.products.BundledProduct> outBundledProducts = new ArrayList<>();
            for (BundledProduct inBundledProduct : inProduct.getBundledProducts()) {
                outBundledProducts.add(convertBundledProduct(inBundledProduct));
            }
            outProduct.setBundledProducts(outBundledProducts);
        } else {
            outProduct.setBundledProducts(new ArrayList<com.mozu.api.contracts.commerceruntime.products.BundledProduct>());
        }

        if (inProduct.getCategories() != null) {
            List<com.mozu.api.contracts.commerceruntime.products.Category> outCategories = new ArrayList<>();
            for (Category inCategory : inProduct.getCategories()) {
                outCategories.add(convertCategory(inCategory));
            }
            outProduct.setCategories(outCategories);
        } else {
            outProduct.setCategories(new ArrayList<com.mozu.api.contracts.commerceruntime.products.Category>());
        }

        outProduct.setDescription(inProduct.getContent() != null ? inProduct.getContent().getProductShortDescription() : null);
        outProduct.setFulfillmentTypesSupported(inProduct.getFulfillmentTypesSupported());
        outProduct.setIsPackagedStandAlone(inProduct.getIsPackagedStandAlone());
        outProduct.setIsRecurring(inProduct.getIsRecurring());
        outProduct.setIsTaxable(inProduct.getIsTaxable());
        outProduct.setMeasurements(convertMeasurements(inProduct.getMeasurements()));
        outProduct.setMfgPartNumber(inProduct.getMfgPartNumber());
        outProduct.setName(inProduct.getContent() != null ? inProduct.getContent().getProductName() : null);

        if (inProduct.getOptions() != null) {
            List<com.mozu.api.contracts.commerceruntime.products.ProductOption> options = new ArrayList<>();
            for (com.mozu.api.contracts.productruntime.ProductOption inOption : inProduct.getOptions()) {
                options.add(convertOptions(inOption));
            }
            outProduct.setOptions(options);
        }

        outProduct.setPrice(convertPrice(inProduct.getPrice()));

        outProduct.setProductCode(inProduct.getProductCode());
        outProduct.setProductType(inProduct.getProductType());
        outProduct.setProductUsage(inProduct.getProductUsage());

        if (inProduct.getProperties() != null) {
            List<com.mozu.api.contracts.commerceruntime.products.ProductProperty> properties = new ArrayList<>();
            for (ProductProperty inProperty : inProduct.getProperties()) {
                properties.add(convertProperties(inProperty));
            }
            outProduct.setProperties(properties);
        }

        outProduct.setUpc(inProduct.getUpc());
        outProduct.setVariationProductCode(inProduct.getVariationProductCode());

        return outProduct;
    }

    public static com.mozu.api.contracts.commerceruntime.products.ProductProperty convertProperties(
            ProductProperty inProperty) {
        com.mozu.api.contracts.commerceruntime.products.ProductProperty outProperty =
                new com.mozu.api.contracts.commerceruntime.products.ProductProperty();
        outProperty.setAttributeFQN(inProperty.getAttributeFQN());
        if (inProperty.getAttributeDetail() != null) {
            outProperty.setDataType(inProperty.getAttributeDetail().getDataType());
            outProperty.setName(inProperty.getAttributeDetail().getName());
        }
        outProperty.setIsMultiValue(inProperty.getIsMultiValue());
        List<com.mozu.api.contracts.commerceruntime.products.ProductPropertyValue> values = new ArrayList<>();
        for (ProductPropertyValue inValue : inProperty.getValues()) {
            values.add(convertProductPropertyValue(inValue));
        }
        outProperty.setValues(values);
        return outProperty;
    }

    public static com.mozu.api.contracts.commerceruntime.products.ProductPropertyValue convertProductPropertyValue(
            ProductPropertyValue inProperty) {
        com.mozu.api.contracts.commerceruntime.products.ProductPropertyValue outProperty =
                new com.mozu.api.contracts.commerceruntime.products.ProductPropertyValue();
        outProperty.setStringValue(inProperty.getStringValue());
        outProperty.setValue(inProperty.getValue());
        return outProperty;
    }

    public static com.mozu.api.contracts.commerceruntime.products.ProductPrice convertPrice(ProductPrice inPrice) {
        com.mozu.api.contracts.commerceruntime.products.ProductPrice outPrice =
                new com.mozu.api.contracts.commerceruntime.products.ProductPrice();
        if (inPrice != null) {
            outPrice.setMsrp(inPrice.getMsrp());
            outPrice.setPrice(inPrice.getPrice());
            outPrice.setSalePrice(inPrice.getSalePrice());
        }
        return outPrice;
    }

    public static com.mozu.api.contracts.commerceruntime.products.ProductOption convertOptions(
            ProductOption inOption) {
        com.mozu.api.contracts.commerceruntime.products.ProductOption outOption =
                new com.mozu.api.contracts.commerceruntime.products.ProductOption();
        outOption.setAttributeFQN(inOption.getAttributeFQN());
        if (inOption.getAttributeDetail() != null) {
            outOption.setDataType(inOption.getAttributeDetail().getDataType());
            outOption.setName(inOption.getAttributeDetail().getName());
        }
        for (ProductOptionValue productOptionValue : inOption.getValues()) {
            if (productOptionValue.getIsSelected()) {
                outOption.setShopperEnteredValue(productOptionValue.getShopperEnteredValue());
                outOption.setValue(productOptionValue.getValue());
            }
        }

        return outOption;
    }

    public static com.mozu.api.contracts.commerceruntime.products.Category convertCategory(
            Category inCategory) {
        if (inCategory == null)
            return null;

        com.mozu.api.contracts.commerceruntime.products.Category outCategory =
                new com.mozu.api.contracts.commerceruntime.products.Category();
        outCategory.setId(inCategory.getCategoryId());
        outCategory.setParent(convertCategory(inCategory.getParentCategory()));
        return outCategory;
    }

    public static com.mozu.api.contracts.commerceruntime.products.BundledProduct convertBundledProduct(
            BundledProduct inBundledProduct) {
        com.mozu.api.contracts.commerceruntime.products.BundledProduct outBundledProduct =
                new com.mozu.api.contracts.commerceruntime.products.BundledProduct();
        outBundledProduct.setIsPackagedStandAlone(inBundledProduct.getIsPackagedStandAlone());
        outBundledProduct.setMeasurements(convertMeasurements(inBundledProduct.getMeasurements()));
        if (inBundledProduct.getContent() != null) {
            outBundledProduct.setDescription(inBundledProduct.getContent().getProductShortDescription());
            outBundledProduct.setName(inBundledProduct.getContent().getProductName());
        }

        outBundledProduct.setProductCode(inBundledProduct.getProductCode());
        outBundledProduct.setQuantity(inBundledProduct.getQuantity());
        return outBundledProduct;
    }

    public static PackageMeasurements convertMeasurements(
            com.mozu.api.contracts.productruntime.PackageMeasurements measurements) {
        com.mozu.api.contracts.commerceruntime.commerce.PackageMeasurements pkgMeasurements =
                new com.mozu.api.contracts.commerceruntime.commerce.PackageMeasurements();
        if (measurements != null) {
            pkgMeasurements.setHeight(measurements.getPackageHeight());
            pkgMeasurements.setLength(measurements.getPackageLength());
            pkgMeasurements.setWeight(measurements.getPackageWeight());
            pkgMeasurements.setWidth(measurements.getPackageWidth());
        }
        return pkgMeasurements;
    }

    @Override
    public void onEditDone(Order order) {
        mOrder = order;
        mProductsAdapter.addData(mOrder);
        mProductsAdapter.notifyDataSetChanged();
        if (getActivity() instanceof NewOrderActivity) {
            ((NewOrderActivity) getActivity()).updateOrder(mOrder);
        } else if (getActivity() instanceof OrderDetailActivity) {

        }
    }

    @Override
    public void updateOrder(Order order) {
        onEditDone(order);
    }
}
