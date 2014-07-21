package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.mozu.mozuandroidinstoreassistant.app.fragments.ImageViewPageFragment;

import java.util.List;

public class ProductImagesPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mImageUrlList;

    public ProductImagesPagerAdapter(FragmentManager manager, List<String> imageUrlList) {
        super(manager);

        mImageUrlList = imageUrlList;
    }

    @Override
    public int getCount() {
        if (mImageUrlList == null) {
            return 0;
        }

        return mImageUrlList.size();
    }


    @Override
    public Fragment getItem(int position) {
        ImageViewPageFragment fragment = new ImageViewPageFragment();
        fragment.setImageUrl(mImageUrlList.get(position));

        return fragment;
    }

}
