package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

public class GridToggleArrayAdapter<T> extends ArrayAdapter<T> {

    private boolean mIsGrid = true;

    private int mGridResource;
    private int mListResource;

    protected GridToggleArrayAdapter(Context context, int gridResource, int listResource) {
        super(context, gridResource);

        if (gridResource == 0 || listResource == 0) {
            throw new IllegalArgumentException("Grid Resource and List Resource Must By Set on GridToggleArrayAdapter");
        }

        mGridResource = gridResource;
        mListResource = listResource;
    }

    protected boolean isGrid() {
        return mIsGrid;
    }

    public void setIsGrid(boolean isGrid) {

        mIsGrid = isGrid;
    }

    protected int getGridResource() {
        return mGridResource;
    }

    protected int getListResource() {
        return mListResource;
    }

    protected int getCurrentResource() {
        if (mIsGrid) {
            return getGridResource();
        } else {
            return getListResource();
        }
    }
}
