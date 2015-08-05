package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mozu.api.contracts.productruntime.Category;
import com.mozu.mozuandroidinstoreassistant.app.R;

/**
 * Created by chris_pound on 8/5/15.
 */
public class CustomerLookupFragment extends Fragment {

    public static CustomerLookupFragment getInstance(){
        CustomerLookupFragment fragment = new CustomerLookupFragment();
//        Bundle bundle = new Bundle();
//        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_lookup, container, false);
        return view;
    }
}
