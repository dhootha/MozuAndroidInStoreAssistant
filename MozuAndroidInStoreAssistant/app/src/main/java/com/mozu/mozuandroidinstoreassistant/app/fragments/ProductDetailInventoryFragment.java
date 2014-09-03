package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Loader;
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

import com.mozu.api.contracts.productadmin.LocationInventory;
import com.mozu.api.contracts.productadmin.LocationInventoryCollection;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.ProdDetailLocationInventoryAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.LocationInventoryLoader;

import java.util.List;


public class ProductDetailInventoryFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<LocationInventoryCollection> {

    private static final int LOADER_PRODUCT_INVENTORY = 123;
    private Product mProduct;

    private int mTenantId;
    private int mSiteId;

    private List<LocationInventory> mInventory;

    private ListView mInventoryList;
    private ProgressBar mProgress;
    private LinearLayout mDialogLayout;

    public ProductDetailInventoryFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_inventory_fragment, null);

        mInventoryList = (ListView) view.findViewById(R.id.inventory_list);
        mProgress = (ProgressBar) view.findViewById(R.id.inventory_progress);

        if (mProduct != null) {
            mProgress.setVisibility(View.VISIBLE);
            mInventoryList.setVisibility(View.GONE);
            getLoaderManager().initLoader(LOADER_PRODUCT_INVENTORY, null, this).forceLoad();
        }

        if (getShowsDialog()) {
            mDialogLayout = (LinearLayout) view.findViewById(R.id.dialog_header);
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
        return view;
    }


    private void setProductToView(View view) {

        mProgress.setVisibility(View.GONE);
        mInventoryList.setVisibility(View.VISIBLE);

        ListView inventoryList = (ListView) view.findViewById(R.id.inventory_list);
        inventoryList.setAdapter(new ProdDetailLocationInventoryAdapter(getActivity(), mInventory,mTenantId,mSiteId));

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

    @Override
    public Loader<LocationInventoryCollection> onCreateLoader(int id, Bundle args) {
        return new LocationInventoryLoader(getActivity(), mTenantId, mSiteId, mProduct);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onLoadFinished(Loader<LocationInventoryCollection> loader, LocationInventoryCollection data) {

        if (data == null || data.getItems() == null) {
            return;
        }

        mInventory = data.getItems();

        setProductToView(getView());
    }

    @Override
    public void onLoaderReset(Loader<LocationInventoryCollection> loader) {

    }
}
