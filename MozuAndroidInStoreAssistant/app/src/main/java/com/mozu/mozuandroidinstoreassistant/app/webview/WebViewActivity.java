package com.mozu.mozuandroidinstoreassistant.app.webview;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;

import com.mozu.mozuandroidinstoreassistant.app.R;

public class WebViewActivity extends Activity {

    private WebView webView;
    public static final String URL = "domainurl";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(URL);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.webview_layout);
        if (TextUtils.isEmpty(url)) {
            url = "www.mozu.com";
        }
        webView = (WebView) findViewById(R.id.webView);

        webView.setWebViewClient(new DomainWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview_menu, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            webView.reload();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


}