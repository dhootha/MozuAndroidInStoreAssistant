package com.mozu.mozuandroidinstoreassistant.app.views;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class ProductDetailImageTransformation implements Transformation {

    private boolean mIsLandScape;

    private  int mMaxDimension;

    public ProductDetailImageTransformation(boolean isLandScape,int maxDimension){
        mMaxDimension = maxDimension;
        mIsLandScape = isLandScape;
    }


    @Override
    public Bitmap transform(Bitmap source) {
        int newWidth, newHeight;
        if (mIsLandScape)
        {
            newWidth = mMaxDimension;
            newHeight = Math.round(((float) newWidth / source.getWidth()) * source.getHeight());
        } else
        {
            newHeight = mMaxDimension;
            newWidth = Math.round(((float) newHeight / source.getHeight()) * source.getWidth());
        }

        Bitmap result = Bitmap.createScaledBitmap(source, newWidth, newHeight, false);

        if (result != source)
            source.recycle();

        return result;

    }

    @Override
    public String key() {
        return "productImageTransform"+String.valueOf(mIsLandScape);
    }
}
