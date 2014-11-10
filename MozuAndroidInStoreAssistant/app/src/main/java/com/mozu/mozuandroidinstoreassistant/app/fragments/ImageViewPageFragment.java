package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageViewPageFragment extends Fragment {


    private String mImageUrl;

    View view;
    ImageView image;

    public ImageViewPageFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_item_for_viewpager, null);
        image = (ImageView) view.findViewById(R.id.image_for_viewpager);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        new BitmapWorkerTask(image).execute(mImageUrl);
        return view;
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            int imageMax =  getResources().getDimensionPixelSize(R.dimen.image_max);
            return getBitmapFromURL(data, imageMax, imageMax);
        }
        // Once complete, see if ImageView is still AROUND and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }


    /*

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
*/

    public static Bitmap getBitmapFromURL(String src,int reqWidth,int reqHeight) {
        Bitmap bmImg;
        URL myFileUrl = null;
        try {
            myFileUrl = new URL(src);
            HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;
            bmImg = BitmapFactory.decodeStream(is, null, options);
            return bmImg;
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return null;
        }
    }



    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

}
