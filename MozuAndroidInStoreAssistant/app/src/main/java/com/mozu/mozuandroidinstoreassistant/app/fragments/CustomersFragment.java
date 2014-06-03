package com.mozu.mozuandroidinstoreassistant.app.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mozu.mozuandroidinstoreassistant.app.R;

public class CustomersFragment extends Fragment {


    public CustomersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_customer, container, false);
    }


}
