package com.mozu.mozuandroidinstoreassistant.app.product;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.utils.VolusionRequestHandler;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;


public class ImageViewPageFragment extends Fragment {


    private String mImageUrl;

    public ImageViewPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_item_for_viewpager, null);
        ImageView image = (ImageView) view.findViewById(R.id.image_for_viewpager);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        final WeakReference<ImageView> imageViewReference = new WeakReference<ImageView>(image);
        new Picasso.Builder(getActivity()).addRequestHandler(new VolusionRequestHandler()).build().load(mImageUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageViewReference.get());
        return view;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

}
