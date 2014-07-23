package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;


public class ProductDetailShippingFragment extends Fragment {


    private Product mProduct;

    public ProductDetailShippingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_shipping_fragment, null);


        return view;
    }


    public void setProduct(Product product) {
        mProduct = product;
    }

}
