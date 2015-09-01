package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.products.ProductOption;
import com.mozu.api.contracts.productadmin.ProductVariation;
import com.mozu.api.contracts.productadmin.ProductVariationOption;
import com.mozu.api.contracts.productadmin.ProductVariationPagedCollection;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.product.FulfillmentInfo;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;
import com.mozu.mozuandroidinstoreassistant.app.utils.ProductUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.subscriptions.CompositeSubscription;

public class NewOrderItemEditFragment extends DialogFragment {


    private static final String ORDER_ITEM = "orderItem";

    private static final String ORDER_ID = "orderId";
    private static final String EDIT_MODE = "editmode";
    private OrderItem mOrderItem;
    private String mOrderId;
    private View mView;

    @InjectView(R.id.product_code)
    public TextView productCode;

    @InjectView(R.id.product_name)
    public TextView productName;

    @InjectView(R.id.product_price)
    public TextView productPrice;
    @InjectView(R.id.product_quantity)
    public EditText productQuantity;
    @InjectView(R.id.product_total)
    public TextView productTotal;

    @InjectView(R.id.product_save)
    public Button productSave;

    private boolean isEditMode = false;


    @InjectView(R.id.fulfillment_type)
    public Spinner fulfillmentType;

    @InjectView(R.id.product_variation)
    public Spinner productVariationSpinner;

    @InjectView(R.id.product_variation_layout)
    public LinearLayout productVariationLayout;
    @InjectView(R.id.product_variation_progress)
    public ProgressBar productVariationProgress;
    @InjectView(R.id.fulfillment_spinner_progress)
    public ProgressBar fulfillmentSpinnerProgress;


    private int mTenantId;
    private int mSiteId;
    private onItemEditDoneListener mEditDoneListener;
    private SpinnerAdapter mSpinnerAdapter;
    private ProductVariationAdapter mProductVariationAdapter;

    private CompositeSubscription mCompositeSubscription;


    public static NewOrderItemEditFragment getInstance(OrderItem orderItem, String orderId, Boolean isEditMode) {
        NewOrderItemEditFragment newOrderItemEditFragment = new NewOrderItemEditFragment();
        Bundle b = new Bundle();
        b.putSerializable(ORDER_ITEM, orderItem);
        b.putString(ORDER_ID, orderId);
        b.putBoolean(EDIT_MODE, isEditMode);
        newOrderItemEditFragment.setArguments(b);
        return newOrderItemEditFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mCompositeSubscription = new CompositeSubscription();
        mTenantId = userAuthenticationStateMachine.getTenantId();
        mSiteId = userAuthenticationStateMachine.getSiteId();
        if (getArguments() != null) {
            mOrderId = getArguments().getString(ORDER_ID);
            mOrderItem = (OrderItem) getArguments().getSerializable(ORDER_ITEM);
            isEditMode = getArguments().getBoolean(EDIT_MODE);
        }
        mView = inflater.inflate(R.layout.neworder_item_edit_fragment, null);
        ButterKnife.inject(this, mView);
        if (mOrderItem != null) {
            setupViews();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadLocations();
        if (ProductUtils.isProductConfigurable(mOrderItem.getProduct())) {
            if (!isEditMode) {
                loadProductVariations(mOrderItem.getProduct().getProductCode());
            } else {
                List<ProductVariation> productVariations = new ArrayList<>();
                ProductVariation productVariation = new ProductVariation();
                if (!TextUtils.isEmpty(mOrderItem.getProduct().getVariationProductCode())) {
                    productVariation.setVariationProductCode(mOrderItem.getProduct().getVariationProductCode());
                } else {
                    productVariation.setVariationProductCode(mOrderItem.getProduct().getProductCode());
                }
                List<ProductVariationOption> productVariationOptions = new ArrayList<>();
                for (ProductOption productOption : mOrderItem.getProduct().getOptions()) {
                    ProductVariationOption productVariationOption = new ProductVariationOption();
                    productVariationOption.setAttributeFQN(productOption.getAttributeFQN());
                    productVariationOption.setValue(productOption.getValue());
                    productVariationOptions.add(productVariationOption);
                }

                productVariation.setOptions(productVariationOptions);
                productVariations.add(productVariation);
                mProductVariationAdapter.setData(productVariations);
                mProductVariationAdapter.notifyDataSetChanged();
                productVariationSpinner.setEnabled(false);
                productVariationSpinner.setClickable(false);

            }
        } else {
            productVariationLayout.setVisibility(View.GONE);
        }

    }


    private void loadProductVariations(String productCode) {
        productSave.setEnabled(false);
        productVariationProgress.setVisibility(View.VISIBLE);
        mCompositeSubscription.add(AndroidObservable.bindFragment(this, NewOrderManager.getInstance().getProductVariationCodes(mTenantId, mSiteId, productCode))
                .subscribe(new Subscriber<ProductVariationPagedCollection>() {
                    @Override
                    public void onCompleted() {
                        productVariationProgress.setVisibility(View.GONE);
                        productSave.setEnabled(true);
                        if (mOrderItem.getProduct().getVariationProductCode() != null) {
                            setProductSelection();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(),e.getMessage());

                    }

                    @Override
                    public void onNext(ProductVariationPagedCollection productVariationPagedCollection) {
                        if (productVariationPagedCollection != null) {
                            mProductVariationAdapter.setData(productVariationPagedCollection.getItems());
                            mProductVariationAdapter.notifyDataSetChanged();
                        }

                    }
                }));
    }

    private void setProductSelection() {
        for (int i = 0; i < mProductVariationAdapter.getCount(); i++) {
            if (mProductVariationAdapter.getItem(i).getVariationProductCode().equals(mOrderItem.getProduct().getVariationProductCode())) {
                productVariationSpinner.setSelection(i);
                return;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();

        }
    }

    private void loadLocations() {
        productSave.setEnabled(false);
        fulfillmentSpinnerProgress.setVisibility(View.VISIBLE);
        mCompositeSubscription.add(AndroidObservable.bindFragment(this, NewOrderManager.getInstance().getInstoreLocationCodes(mTenantId, mSiteId, mOrderItem.getProduct().getVariationProductCode()))
                .subscribe(new Subscriber<List<FulfillmentInfo>>() {
                    @Override
                    public void onCompleted() {
                        fulfillmentSpinnerProgress.setVisibility(View.GONE);
                        productSave.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), e.getMessage()).show();
                    }

                    @Override
                    public void onNext(List<FulfillmentInfo> fulfillmentInfos) {
                        mSpinnerAdapter.setData(fulfillmentInfos);
                        mSpinnerAdapter.notifyDataSetChanged();

                    }
                }));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private Subscriber<Order> getOrderUpdateSubscriber() {
        return new Subscriber<Order>() {
            @Override
            public void onCompleted() {
                getDialog().dismiss();
            }

            @Override
            public void onError(Throwable e) {
                ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), e.getMessage()).show();
            }

            @Override
            public void onNext(Order order) {
                mEditDoneListener.onEditDone(order);
            }
        };
    }

    private void setupViews() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        Double productPriceVal = mOrderItem.getProduct().getPrice().getPrice();
        productPrice.setText(format.format(productPriceVal));
        productQuantity.setText(String.valueOf(mOrderItem.getQuantity()));
        productCode.setText(mOrderItem.getProduct().getProductCode());
        productName.setText(mOrderItem.getProduct().getName());
        mProductVariationAdapter = new ProductVariationAdapter();
        productVariationSpinner.setAdapter(mProductVariationAdapter);
        productTotal.setText(format.format(mOrderItem.getQuantity() * productPriceVal));
        mSpinnerAdapter = new SpinnerAdapter();
        fulfillmentType.setAdapter(mSpinnerAdapter);
        productSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditMode) {
                    String updatedProductQuantityVal = productQuantity.getText().toString();
                    FulfillmentInfo updatedFulFillmentType = (FulfillmentInfo) fulfillmentType.getSelectedItem();

                    if (updatedFulFillmentType.mType.equals(mOrderItem.getFulfillmentMethod().toLowerCase()) && updatedFulFillmentType.mLocation.equals(updatedFulFillmentType.mLocation)) {
                        updatedFulFillmentType = null;
                    }
                    if (TextUtils.isEmpty(updatedProductQuantityVal) || updatedProductQuantityVal.equals(String.valueOf(mOrderItem.getQuantity()))) {
                        updatedProductQuantityVal = null;

                    }
                    if (updatedProductQuantityVal != null || updatedFulFillmentType != null) {
                        AndroidObservable.bindFragment(NewOrderItemEditFragment.this, NewOrderManager
                                .getInstance()
                                .getOrderItemUpdateQuantityObservable(mTenantId, mSiteId, mOrderItem, mOrderId, updatedProductQuantityVal == null ? null : Integer.valueOf(updatedProductQuantityVal), updatedFulFillmentType))
                                .subscribe(getOrderUpdateSubscriber());
                    }
                } else {
                    FulfillmentInfo updatedFulFillmentType = (FulfillmentInfo) fulfillmentType.getSelectedItem();
                    mOrderItem.setFulfillmentLocationCode(updatedFulFillmentType.mLocation);
                    mOrderItem.setFulfillmentMethod(updatedFulFillmentType.mType);
                    mOrderItem.setQuantity(Integer.valueOf(productQuantity.getText().toString()));
                    if (ProductUtils.isProductConfigurable(mOrderItem.getProduct())) {
                        ProductVariation productVariation = ((ProductVariation) productVariationSpinner.getSelectedItem());
                        mOrderItem.getProduct().setVariationProductCode(productVariation.getVariationProductCode());
                        for (ProductOption productOption : mOrderItem.getProduct().getOptions()) {
                            productOption.getAttributeFQN();
                            for (ProductVariationOption productVariationOption : productVariation.getOptions()) {
                                if (productVariationOption.getAttributeFQN().equals(productOption.getAttributeFQN())) {
                                    productOption.setShopperEnteredValue(productVariationOption.getValue());
                                    productOption.setStringValue(productVariationOption.getContent().getStringValue());
                                    productOption.setValue(productVariationOption.getValue());
                                }
                            }
                        }
                    } else {
                        mOrderItem.getProduct().setVariationProductCode(mOrderItem.getProduct().getProductCode());
                    }


                    AndroidObservable.bindFragment(NewOrderItemEditFragment.this, NewOrderManager
                            .getInstance()
                            .getOrderItemCreateObservable(mTenantId, mSiteId, mOrderItem, mOrderId))
                            .subscribe(getOrderUpdateSubscriber());

                }

            }
        });
    }

    public void setOnEditDoneListener(onItemEditDoneListener onEditDoneListener) {
        mEditDoneListener = onEditDoneListener;

    }

    class ProductVariationAdapter extends BaseAdapter {

        private List<ProductVariation> mData;

        public ProductVariationAdapter() {
            mData = new ArrayList<>();
        }

        public void setData(List<ProductVariation> data) {
            mData = data;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.orderfulfillment_spinner_item, parent, false);
            }
            TextView mTextView = (TextView) convertView.findViewById(R.id.order_fulfillment);
            ProductVariation productVariation = getItem(position);
            StringBuffer fulFillmentDisplay = new StringBuffer(productVariation.getVariationProductCode());
            for (ProductVariationOption option : productVariation.getOptions()) {
                fulFillmentDisplay.append("- " + option.getValue());
            }

            mTextView.setText(fulFillmentDisplay.toString());
            return convertView;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public ProductVariation getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(parent.getContext(), R.layout.orderfulfillment_dropdown_resource, null);
            ProductVariation productVariation = getItem(position);

            StringBuffer fulFillmentDisplay = new StringBuffer(productVariation.getVariationProductCode());
            for (ProductVariationOption option : productVariation.getOptions()) {
                fulFillmentDisplay.append("- " + option.getValue());
            }
            textView.setText(fulFillmentDisplay.toString());
            return textView;

        }

    }

    class SpinnerAdapter extends BaseAdapter {

        private List<FulfillmentInfo> mData;

        public SpinnerAdapter() {
            mData = new ArrayList<>();
        }

        public void setData(List<FulfillmentInfo> data) {
            mData = data;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.orderfulfillment_spinner_item, parent, false);
            }
            TextView mTextView = (TextView) convertView.findViewById(R.id.order_fulfillment);
            FulfillmentInfo fulfillmentInfo = getItem(position);
            String fulFillmentDisplay = fulfillmentInfo.mType + "_" + fulfillmentInfo.mLocation;
            mTextView.setText(fulFillmentDisplay);
            return convertView;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public FulfillmentInfo getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(parent.getContext(), R.layout.orderfulfillment_dropdown_resource, null);
            FulfillmentInfo fulfillmentInfo = getItem(position);
            String fulFillmentDisplay = fulfillmentInfo.mType + "_" + fulfillmentInfo.mLocation;
            textView.setText(fulFillmentDisplay);
            return textView;

        }

    }


    public interface onItemEditDoneListener {
        public void onEditDone(Order order);
    }
}
