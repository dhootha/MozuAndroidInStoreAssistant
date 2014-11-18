package com.mozu.mozuandroidinstoreassistant.app.product;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;


public class ProductDetailExtrasFragment extends Fragment {

    private Product mProduct;

    public ProductDetailExtrasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_extras_fragment, null);

        if (mProduct != null) {
            setViewToProduct(view);
        }

        return view;
    }

    private void setViewToProduct(View view) {
        ListView list = (ListView) view.findViewById(R.id.extras_list);
        //list.setAdapter(new ProdDetailExtrasAdapter(getActivity(), mProduct.getContent().ge));
    }

    public void setProduct(Product product) {
        mProduct = product;
    }

}
