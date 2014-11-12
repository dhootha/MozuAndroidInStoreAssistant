package com.mozu.mozuandroidinstoreassistant.app;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.mozu.mozuandroidinstoreassistant.app.adapters.ProductImagesPagerAdapter;
import com.mozu.mozuandroidinstoreassistant.app.views.ZoomOutPageTransformer;

import java.util.ArrayList;

public class ImagePagerActivity extends Activity implements ViewTreeObserver.OnPreDrawListener {

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

    private static final int ANIM_DURATION = 250;

    public static final String IMAGE_URLS_FOR_PRODUCTS = "IMAGE_URLS_FOR_PRODUCTS";
    public static final String IMAGE_PAGER_INDEX = "IMAGE_PAGER_INDEX";

    public static final String ANIM_START_LEFT = "ANIM_START_LEFT";
    public static final String ANIM_START_TOP = "ANIM_START_TOP";
    public static final String ANIM_START_WIDTH = "ANIM_START_WIDTH";
    public static final String ANIM_START_HEIGHT = "ANIM_START_HEIGHT";

    private ViewPager mPager;

    private ProductImagesPagerAdapter mPagerAdapter;

    private ArrayList<String> mImagesUrls;

    private int mIndex;

    private int mAnimStartLeft;
    private int mAnimStartTop;
    private int mAnimStartWidth;
    private int mAnimStartHeight;

    private float mLeftDelta;
    private float mTopDelta;
    private float mWidthScale;
    private float mHeightScale;

    private FrameLayout mTopLevelLayout;
    private ColorDrawable mBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getResources().getBoolean(R.bool.allow_portrait)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_image_list);

        mTopLevelLayout = (FrameLayout) findViewById(R.id.top_level_layout);

        mBackground = new ColorDrawable(Color.BLACK);


        if (Build.VERSION.SDK_INT >= 16) {
            mTopLevelLayout.setBackground(mBackground);

            // Fade in the black background
            ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 200);
            bgAnim.setDuration(ANIM_DURATION);
            bgAnim.start();
        } else {
            mTopLevelLayout.setBackgroundColor(getResources().getColor(R.color.semi_transparent_image_view_background));
        }


        mPager = (ViewPager) findViewById(R.id.product_viewpager);

        setupActivityFromInstanceOrIntent(savedInstanceState);

        mPagerAdapter = new ProductImagesPagerAdapter(getFragmentManager(), mImagesUrls);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mIndex);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                // *Now* go ahead and exit the activity
                finish();
            }
        });
    }

    private void setupActivityFromInstanceOrIntent(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getStringArrayList(IMAGE_URLS_FOR_PRODUCTS) != null) {
            mImagesUrls = savedInstanceState.getStringArrayList(IMAGE_URLS_FOR_PRODUCTS);
        } else if (getIntent() != null && getIntent().getStringArrayListExtra(IMAGE_URLS_FOR_PRODUCTS) != null) {
            mImagesUrls = getIntent().getStringArrayListExtra(IMAGE_URLS_FOR_PRODUCTS);
        }

        if (getIntent() != null) {
            mIndex = getIntent().getIntExtra(IMAGE_PAGER_INDEX, 0);
        }

        if (savedInstanceState != null && mIndex == 0) {
            mIndex = savedInstanceState.getInt(IMAGE_PAGER_INDEX, 0);
        }

        //load animation related things for first launch
        if (getIntent() != null && savedInstanceState == null) {
            mAnimStartLeft = getIntent().getIntExtra(ANIM_START_LEFT, 0);
            mAnimStartTop = getIntent().getIntExtra(ANIM_START_TOP, 0);
            mAnimStartWidth = getIntent().getIntExtra(ANIM_START_WIDTH, 0);
            mAnimStartHeight = getIntent().getIntExtra(ANIM_START_HEIGHT, 0);

            if (savedInstanceState == null) {
                ViewTreeObserver observer = mPager.getViewTreeObserver();
                observer.addOnPreDrawListener(this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(IMAGE_URLS_FOR_PRODUCTS, mImagesUrls);
        outState.putInt(IMAGE_PAGER_INDEX, mPager.getCurrentItem());

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onPreDraw() {
        mPager.getViewTreeObserver().removeOnPreDrawListener(ImagePagerActivity.this);

        int [] screenLoacation = new int[2];

        mPager.getLocationOnScreen(screenLoacation);

        mLeftDelta = mAnimStartLeft - screenLoacation[0];
        mTopDelta = mAnimStartTop - screenLoacation[1];

        mWidthScale = (float) mAnimStartWidth / mPager.getMeasuredWidth();
        mHeightScale = (float) mAnimStartHeight / mPager.getMeasuredHeight();

        runEnterAnimation();

        return true;
    }

    private void runEnterAnimation() {

        final long duration = (long) ANIM_DURATION;

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        mPager.setPivotX(0);
        mPager.setPivotY(0);
        mPager.setScaleX(mWidthScale);
        mPager.setScaleY(mHeightScale);
        mPager.setTranslationX(mLeftDelta);
        mPager.setTranslationY(mTopDelta);

        // Animate scale and translation to go from thumbnail to full size
        mPager.animate().setDuration(duration).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator);
    }

    public void runExitAnimation(final Runnable endAction) {

        final long duration = (long) ANIM_DURATION;

        mPager.setPivotX(mPager.getWidth() / 2);
        mPager.setPivotY(mPager.getHeight() / 2);
        mLeftDelta = 0;
        mTopDelta = 0;

        mPager.animate().setDuration(duration).
                scaleX(mWidthScale).scaleY(mHeightScale).
                translationX(mLeftDelta).translationY(mTopDelta).
                setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        endAction.run();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        endAction.run();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });


        mPager.animate().alpha(0);
    }

}
