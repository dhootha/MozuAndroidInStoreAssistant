package com.mozu.mozuandroidinstoreassistant.app.views;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


public class NoUnderlineClickableSpan extends ClickableSpan {

    public NoUnderlineClickableSpan() {
        super();
    }

    @Override
    public void onClick(View widget) {

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setUnderlineText(false);
    }

}
