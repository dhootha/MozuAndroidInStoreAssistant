package com.mozu.mozuandroidinstoreassistant.app.product;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;


public class ProductDetailShippingFragment extends Fragment {


    private Product mProduct;

    public ProductDetailShippingFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_shipping_fragment, null);

        if (mProduct != null) {
            setViewToProduct(view);
        }

        return view;
    }

    private void setViewToProduct(View view) {
        TextView weight = (TextView) view.findViewById(R.id.weight_value);
        TextView measure = (TextView) view.findViewById(R.id.measurement_value);

        if (mProduct.getMeasurements() == null) {
            weight.setText("N/A");
            measure.setText("N/A");
        }

        if (mProduct.getMeasurements().getPackageWeight() == null) {
            weight.setText("N/A");
        } else {
            weight.setText(mProduct.getMeasurements().getPackageWeight().getValue() + " " + mProduct.getMeasurements().getPackageWeight().getUnit());
        }

        if (mProduct.getMeasurements().getPackageHeight() == null || mProduct.getMeasurements().getPackageWidth() == null || mProduct.getMeasurements().getPackageLength() == null) {
            measure.setText("N/A");
        } else {
            measure.setText(mProduct.getMeasurements().getPackageLength().getValue() + " " + mProduct.getMeasurements().getPackageLength().getUnit() + " x " + mProduct.getMeasurements().getPackageWidth().getValue() + " " + mProduct.getMeasurements().getPackageWidth().getUnit() + " x " + mProduct.getMeasurements().getPackageHeight().getValue() + " " + mProduct.getMeasurements().getPackageHeight().getUnit() + " ");
        }
    }

    public void setProduct(Product product) {
        mProduct = product;
    }

}
