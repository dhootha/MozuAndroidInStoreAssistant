package com.mozu.mozuandroidinstoreassistant.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.contracts.productruntime.ProductImage;
import com.mozu.mozuandroidinstoreassistant.app.adapters.DetailSectionPagerAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.ProductDetailLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.ImageURLConverter;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Product>, View.OnClickListener {

    public static final String PRODUCT_CODE_EXTRA_KEY = "PRODUCT_CODE";
    public static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    public static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";
    public static final int LOADER_PRODUCT_DETAIL = 4;
    public static final int FIRST_SUB_IMAGE = 1;
    public static final int NUM_OF_COLUMNS_DIVISOR = 2;

    private String mProductCode;

    private ImageView mMainImageView;
    private TextView mProductCodeTextView;
    private TextView mProductName;

    private Product mProduct;

    private int mTenantId;

    private int mSiteId;

    private ImageURLConverter mImageUrlConverter;

    private LinearLayout mProductImagesLayout;

    private List<ProductImage> mImages;

    private HorizontalScrollView mHorizontalScrollView;

    private ViewPager mProductSectionViewPager;

    private TabPageIndicator mTabIndicator;

    private List<String> mTitles;

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
        //mProductDescription = (TextView) findViewById(R.id.productDescription);
        mProductName = (TextView) findViewById(R.id.productName);

        mImageUrlConverter = new ImageURLConverter(mTenantId, mSiteId);

        mProductImagesLayout = (LinearLayout) findViewById(R.id.product_images_layout);

        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_image_preview);

        mTitles = new ArrayList<String>();
        mTitles.add(getString(R.string.overview_tab_name));
        mTitles.add(getString(R.string.properties_tab_name));
        mTitles.add(getString(R.string.extras_tab_name));
        mTitles.add(getString(R.string.shipping_tab_name));
        mTitles.add(getString(R.string.inventory_tab_name));

        mProductSectionViewPager = (ViewPager) findViewById(R.id.product_detail_sections_viewpager);
        mTabIndicator = (TabPageIndicator) findViewById(R.id.product_detail_sections);

        DetailSectionPagerAdapter adapter = new DetailSectionPagerAdapter(getFragmentManager(), mProduct, mTitles);
        mProductSectionViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mTabIndicator.setViewPager(mProductSectionViewPager);

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

        if (mProduct == null) {
            return;
        }

        mImages = null;

        if (mProduct.getContent() != null) {
            mImages = mProduct.getContent().getProductImages();
        }

        if (mImages != null && mProduct.getContent().getProductImages().size() > 0) {
            Picasso.with(this)
                    .load(mImageUrlConverter.getFullImageUrl(mProduct.getContent().getProductImages().get(0).getImageUrl()))
                    .fit()
                    .into(mMainImageView);
        }

        if (mImages != null && mProduct.getContent().getProductImages().size() > 1) {
            addSecondaryImagesToLayout(mProduct.getContent().getProductImages());
        }

        mProductCodeTextView.setText(data.getProductCode());

        if (mProduct.getContent() != null) {
            mProductName.setText(mProduct.getContent().getProductName());
           // mProductDescription.setText(mProduct.getContent().getProductFullDescription());
        }

        mMainImageView.setOnClickListener(this);

        DetailSectionPagerAdapter adapter = new DetailSectionPagerAdapter(getFragmentManager(), mProduct, mTitles);

        mProductSectionViewPager.setAdapter(adapter);
        mTabIndicator.setViewPager(mProductSectionViewPager);
    }

    @Override
    public void onLoaderReset(Loader<Product> loader) {

    }

    private void addSecondaryImagesToLayout(List<ProductImage> productImageList) {

        //go through each image
        for (int i = FIRST_SUB_IMAGE; i <= Math.ceil(productImageList.size() / NUM_OF_COLUMNS_DIVISOR); i++) {

            //add a view to the layout top, bottom, next
            int imageWidth = (int) getResources().getDimension(R.dimen.main_product_image_size) / NUM_OF_COLUMNS_DIVISOR;
            int imageHeight = (int) getResources().getDimension(R.dimen.main_product_image_size) / NUM_OF_COLUMNS_DIVISOR;

            int firstImagePosition = i + (i - 1);
            int secondImagePosition = 2 * i;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, mProductImagesLayout.getMeasuredHeight());

            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(params);
            layout.setOrientation(LinearLayout.VERTICAL);

            //add two image views if you can
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(imageWidth, imageHeight);
            int margin = (int) getResources().getDimension(R.dimen.sub_product_image_margin);
            imageLayoutParams.setMargins(margin, margin, margin, margin);

            //add two text views with the position of the image in question
            LinearLayout firstImageLayout = new LinearLayout(this);
            firstImageLayout.setLayoutParams(imageLayoutParams);

            TextView textViewPositionOne = new TextView(this);
            textViewPositionOne.setText(String.valueOf(firstImagePosition));
            textViewPositionOne.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

            ImageView imageViewTop = new ImageView(this);
            imageViewTop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            firstImageLayout.setOnClickListener(this);

            firstImageLayout.addView(textViewPositionOne);
            firstImageLayout.addView(imageViewTop);
            layout.addView(firstImageLayout);

            Picasso.with(this)
                    .load(mImageUrlConverter.getFullImageUrl(mProduct.getContent().getProductImages().get(firstImagePosition).getImageUrl()))
                    .into(imageViewTop);

            if (productImageList.size() > secondImagePosition) {
                LinearLayout secondImageLayout = new LinearLayout(this);
                secondImageLayout.setLayoutParams(imageLayoutParams);

                ImageView imageViewBottom = new ImageView(this);
                imageViewBottom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                TextView textViewPositionTwo = new TextView(this);
                textViewPositionTwo.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                textViewPositionTwo.setText(String.valueOf(secondImagePosition));

                secondImageLayout.setOnClickListener(this);

                secondImageLayout.addView(textViewPositionTwo);
                secondImageLayout.addView(imageViewBottom);
                layout.addView(secondImageLayout);

                Picasso.with(this)
                        .load(mImageUrlConverter.getFullImageUrl(mProduct.getContent().getProductImages().get(secondImagePosition).getImageUrl()))
                        .into(imageViewBottom);
            }

            mProductImagesLayout.addView(layout);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mMainImageView.getId()) {
            startImageViewPagerActivity(0, v);
        } else if (v instanceof LinearLayout) {
            LinearLayout layout = (LinearLayout)v;

            for (int i = 0; i < layout.getChildCount(); i++) {
                if (layout.getChildAt(i) instanceof TextView) {
                   TextView positionText = (TextView)layout.getChildAt(i);
                   startImageViewPagerActivity(Integer.parseInt(positionText.getText().toString()), positionText);
                }
            }
        }
    }

    private void startImageViewPagerActivity(int index, View view) {
        Intent intent = new Intent(this, ImagePagerActivity.class);

        ArrayList<String> imageUrls = getImageUrlsArrayList();

        intent.putStringArrayListExtra(ImagePagerActivity.IMAGE_URLS_FOR_PRODUCTS, imageUrls);
        intent.putExtra(ImagePagerActivity.IMAGE_PAGER_INDEX, index);

        int [] screenLocation = new int[2];

        view.getLocationOnScreen(screenLocation);

        intent.putExtra(ImagePagerActivity.ANIM_START_LEFT, screenLocation[0]);
        intent.putExtra(ImagePagerActivity.ANIM_START_TOP, screenLocation[1]);
        intent.putExtra(ImagePagerActivity.ANIM_START_WIDTH, view.getMeasuredWidth());
        intent.putExtra(ImagePagerActivity.ANIM_START_HEIGHT, view.getMeasuredHeight());

        startActivity(intent);

        overridePendingTransition(0, 0);
    }

    private ArrayList<String> getImageUrlsArrayList() {
        ArrayList<String> list = new ArrayList<String>();

        for (int i= 0; i < mImages.size(); i++) {
            list.add(mImageUrlConverter.getFullImageUrl(mImages.get(i).getImageUrl()));
        }

        return list;
    }

}