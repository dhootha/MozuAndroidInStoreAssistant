package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.squareup.picasso.Picasso;


public class ImageViewPageFragment extends Fragment {


    private String mImageUrl;

    public ImageViewPageFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_item_for_viewpager, null);

        ImageView image = (ImageView) view.findViewById(R.id.image_for_viewpager);

        Picasso.with(getActivity())
                .load(mImageUrl)
                .into(image);

        return view;
    }


    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

}
