package com.mozu.mozuandroidinstoreassistant.app.product;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.contracts.productadmin.LocationInventory;
import com.mozu.api.contracts.productadmin.LocationInventoryCollection;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.contracts.productruntime.VariationOption;
import com.mozu.api.contracts.productruntime.VariationSummary;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.product.adapter.ProdDetailLocationInventoryAdapter;
import com.mozu.mozuandroidinstoreassistant.app.product.loaders.InventoryRetriever;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class ProductDetailInventoryFragment extends DialogFragment implements Observer<LocationInventoryCollection> {

    @InjectView(R.id.inventory_list)
    ListView mInventoryList;
    @InjectView(R.id.inventory_loading)
    LoadingView mProgress;
    @InjectView(R.id.dialog_header)
    LinearLayout mDialogLayout;
    @InjectView(R.id.mainlayout)
    LinearLayout mMainLayout;
    @InjectView(R.id.product_variation_spinner)
    Spinner mProductVariation;
    @InjectView(R.id.product_variation_layout)
    LinearLayout mProductVariationLayout;
    private Product mProduct;
    private int mTenantId;
    private int mSiteId;
    private List<LocationInventory> mInventory;
    private String mVariatonProductCode;
    private InventoryRetriever mInventoryRetriever;


    private Subscription mSubscription = Subscriptions.empty();
    private Observable<LocationInventoryCollection> mInventoryObservable;

    public ProductDetailInventoryFragment() {
        // Required empty public constructor

        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_inventory_fragment, null);
        ButterKnife.inject(this, view);
        if (mInventory != null) {
            onCompleted();
        }
        if (mProduct.getVariations() != null && mProduct.getVariations().size() > 0) {
            mProductVariationLayout.setVisibility(View.VISIBLE);
            mVariatonProductCode = mProduct.getVariations().get(0).getProductCode();
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.productinventory_spinner_item, R.id.product_option_name, mProduct.getVariations());
            mProductVariation.setAdapter(spinnerAdapter);
            mProductVariation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    mVariatonProductCode = ((VariationSummary) adapterView.getItemAtPosition(position)).getProductCode();
                    loadData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            mProductVariationLayout.setVisibility(View.GONE);
            mVariatonProductCode = mProduct.getProductCode();
        }

        setupForDialog();
        loadData();
        return view;
    }

    private void loadData() {
        AndroidObservable.bindFragment(this, new InventoryRetriever().getInventoryData(mVariatonProductCode, mTenantId, mSiteId))
                .subscribeOn(Schedulers.io())
                .subscribe(this);
    }

    public void onPause() {
        super.onPause();
        mSubscription.unsubscribe();
    }

    @Override
    public void onNext(LocationInventoryCollection inventoryCollection) {
        mInventory = inventoryCollection.getItems();
        if (mInventory.size() > 0) {
            mProgress.success();
        } else {
            mProgress.setError(getString(R.string.no_inventory));
        }
    }

    @Override
    public void onCompleted() {
        mInventoryList.setAdapter(new ProdDetailLocationInventoryAdapter(getActivity(), mInventory, mTenantId, mSiteId));
    }

    public void onError(Throwable error) {
        mProgress.setError(getString(R.string.inventory_load_error));
        Crashlytics.logException(error);
    }

    public void setProduct(Product product) {
        mProduct = product;
    }

    public void setTenantId(int tenantId) {
        mTenantId = tenantId;
    }

    public void setSiteId(int siteId) {
        mSiteId = siteId;
    }

    private void setupForDialog() {
        if (getShowsDialog()) {
            int padding = (int) getResources().getDimension(R.dimen.inventory_dialog_padding);
            mMainLayout.setPadding(padding, padding, padding, padding);
            mDialogLayout.setVisibility(View.VISIBLE);
            TextView productCode = (TextView) mDialogLayout.findViewById(R.id.productCode);
            TextView productName = (TextView) mDialogLayout.findViewById(R.id.productName);
            productCode.setText(mProduct.getProductCode());
            productName.setText(mProduct.getContent().getProductName());
            ImageView dismissView = (ImageView) mDialogLayout.findViewById(R.id.imageView_close);
            dismissView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                }
            });
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    class SpinnerAdapter extends ArrayAdapter<VariationSummary> {
        public SpinnerAdapter(Context context, int res, int textViewResourceId, List<VariationSummary> objects) {
            super(context, res, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.productinventory_spinner_item, parent, false);
            }
            TextView mTextView = (TextView) convertView.findViewById(R.id.product_option_name);
            VariationSummary variationSummary = getItem(position);
            mTextView.setText(getDisplayText(variationSummary));
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(parent.getContext(), R.layout.product_inventory_dropdown, null);
            textView.setText(getDisplayText(getItem(position)));
            return textView;
        }

        private String getDisplayText(VariationSummary variationSummary) {
            StringBuffer optionString = new StringBuffer();
            optionString.append(variationSummary.getProductCode());
            optionString.append("  ");
            optionString.append(mProduct.getContent().getProductName());
            optionString.append("(");
            for (VariationOption option : variationSummary.getOptions()) {
                optionString.append(option.getValue());
                optionString.append(" ");
            }
            optionString.append(")");
            return optionString.toString();
        }

    }

}
