package com.mozu.mozuandroidinstoreassistant.app.webview;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DomainWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
