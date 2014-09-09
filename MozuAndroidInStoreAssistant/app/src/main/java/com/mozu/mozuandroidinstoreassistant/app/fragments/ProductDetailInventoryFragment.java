package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.contracts.productadmin.LocationInventory;
import com.mozu.api.contracts.productadmin.LocationInventoryCollection;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.ProdDetailLocationInventoryAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.InventoryRetriever;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class ProductDetailInventoryFragment extends DialogFragment implements Observer<LocationInventoryCollection> {

    private Product mProduct;

    private int mTenantId;
    private int mSiteId;

    private List<LocationInventory> mInventory;

    @InjectView(R.id.inventory_list) ListView mInventoryList;
    @InjectView(R.id.inventory_progress) ProgressBar mProgress;
    @InjectView(R.id.dialog_header) LinearLayout mDialogLayout;

    private Subscription mSubscription = Subscriptions.empty();
    private Observable<LocationInventoryCollection> mInventoryObservable;

    public ProductDetailInventoryFragment() {
        // Required empty public constructor

        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInventoryObservable = AndroidObservable.bindFragment(this, new InventoryRetriever().getInventoryData(mProduct, mTenantId, mSiteId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public void onResume() {
        super.onResume();

        mSubscription = mInventoryObservable.subscribe(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_inventory_fragment, null);

        ButterKnife.inject(this, view);

        if (mInventory == null) {
            mProgress.setVisibility(View.VISIBLE);
            mInventoryList.setVisibility(View.GONE);
        } else {
            onCompleted();
        }

        setupForDialog();

        return view;
    }

    public void onPause() {
        super.onPause();

        mSubscription.unsubscribe();
    }

    @Override


    public void onNext(LocationInventoryCollection inventoryCollection) {
        mInventory = inventoryCollection.getItems();
    }

    @Override
    public void onCompleted() {
        mProgress.setVisibility(View.GONE);
        mInventoryList.setVisibility(View.VISIBLE);

        mInventoryList.setAdapter(new ProdDetailLocationInventoryAdapter(getActivity(), mInventory,mTenantId,mSiteId));
    }

    public void onError(Throwable error) {

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
            mDialogLayout.setVisibility(View.VISIBLE);

            TextView productCode = (TextView)mDialogLayout.findViewById(R.id.productCode);
            TextView productName = (TextView)mDialogLayout.findViewById(R.id.productName);
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

}
