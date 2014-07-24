package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.ProdDetailPropertiesAdapter;


public class ProductDetailPropertiesFragment extends Fragment {


    private Product mProduct;

    public ProductDetailPropertiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_properties_fragment, null);

        if (mProduct != null) {
            setViewToProduct(view);
        }

        return view;
    }

    private void setViewToProduct(View view) {
        ListView list = (ListView) view.findViewById(R.id.properties_list);
        list.setAdapter(new ProdDetailPropertiesAdapter(getActivity(), mProduct.getProperties()));
    }


    public void setProduct(Product product) {
        mProduct = product;
    }

}
