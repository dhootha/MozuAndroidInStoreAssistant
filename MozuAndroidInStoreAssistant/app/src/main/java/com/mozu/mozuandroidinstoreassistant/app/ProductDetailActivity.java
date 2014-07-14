package com.mozu.mozuandroidinstoreassistant.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.loaders.ProductDetailLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.ImageURLConverter;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Product> {

    public static final String PRODUCT_CODE_EXTRA_KEY = "PRODUCT_CODE";
    public static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    public static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";
    public static final int LOADER_PRODUCT_DETAIL = 4;

    private String mProductCode;

    private ImageView mMainImageView;
    private TextView mProductCodeTextView;
    private TextView mProductDescription;
    private TextView mProductName;

    private Product mProduct;

    private int mTenantId;

    private int mSiteId;

    private ImageURLConverter mImageUrlConverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (getIntent() != null) {
            mProductCode = getIntent().getStringExtra(PRODUCT_CODE_EXTRA_KEY);
            mTenantId = getIntent().getIntExtra(CURRENT_TENANT_ID, -1);
            mSiteId = getIntent().getIntExtra(CURRENT_SITE_ID, -1);
        } else if (savedInstanceState != null) {
            mProductCode = savedInstanceState.getString(PRODUCT_CODE_EXTRA_KEY);
            mTenantId = savedInstanceState.getInt(CURRENT_TENANT_ID, -1);
            mSiteId = savedInstanceState.getInt(CURRENT_SITE_ID, -1);
        }

        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setTitle("");

        mMainImageView = (ImageView) findViewById(R.id.mainImageView);
        mProductCodeTextView = (TextView) findViewById(R.id.productCode);
        mProductDescription = (TextView) findViewById(R.id.productDescription);
        mProductName = (TextView) findViewById(R.id.productName);

        mImageUrlConverter = new ImageURLConverter(mTenantId, mSiteId);

        getLoaderManager().initLoader(LOADER_PRODUCT_DETAIL, null, this).forceLoad();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PRODUCT_CODE_EXTRA_KEY, mProductCode);
        outState.putInt(CURRENT_TENANT_ID, mTenantId);
        outState.putInt(CURRENT_SITE_ID, mSiteId);

        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Product> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_PRODUCT_DETAIL) {

            return new ProductDetailLoader(this, mTenantId, mSiteId, mProductCode);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Product> loader, Product data) {

        mProduct = data;

        if (mProduct.getContent().getProductImages() != null && mProduct.getContent().getProductImages().size() > 0) {
            Picasso.with(this)
                    .load(mImageUrlConverter.getFullImageUrl(mProduct.getContent().getProductImages().get(0).getImageUrl()))
                    .fit()
                    .into(mMainImageView);
        }

        mProductCodeTextView.setText(data.getProductCode());
        mProductName.setText(data.getContent().getProductName());
        mProductDescription.setText(data.getContent().getProductFullDescription());

    }

    @Override
    public void onLoaderReset(Loader<Product> loader) {

    }
}
