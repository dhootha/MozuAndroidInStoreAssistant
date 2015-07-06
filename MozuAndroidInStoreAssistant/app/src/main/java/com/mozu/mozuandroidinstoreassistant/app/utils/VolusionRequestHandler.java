package com.mozu.mozuandroidinstoreassistant.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class VolusionRequestHandler extends RequestHandler {
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    @Override public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme));
    }
    @Override
    public Result load(Request request, int networkPolicy) throws IOException {

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(request.uri.toString()).openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;
            Bitmap bmImg = BitmapFactory.decodeStream(is, null, options);
            return new Result(bmImg, Picasso.LoadedFrom.NETWORK);
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return null;
        }
    }
}
