package com.mozu.mozuandroidinstoreassistant.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.mozu.mozuandroidinstoreassistant.app.R;

public class LoadingView extends ViewFlipper {

    TextView mTextView;
    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.loading_view,this);
        mTextView = (TextView) findViewById(R.id.loading_view_text);
    }

    public void setLoading(){
        setDisplayedChild(0);
    }

    public void setError(String str){
        mTextView.setText(str);
        setDisplayedChild(1);

    }

    public void success(){
        setDisplayedChild(2);
    }
}
