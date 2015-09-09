package com.mozu.mozuandroidinstoreassistant.app.product;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.products.BundledProduct;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.api.contracts.commerceruntime.products.ProductOption;
import com.mozu.api.contracts.productruntime.ProductOptionValue;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.htmlutils.HTMLTagHandler;
import com.mozu.mozuandroidinstoreassistant.app.models.ImageURLConverter;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.product.loaders.ProductAdminObservableManager;
import com.mozu.mozuandroidinstoreassistant.app.views.NoUnderlineClickableSpan;
import com.mozu.mozuandroidinstoreassistant.app.views.ProductOptionsLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;


public class ProductDetailOverviewDialogFragment extends DialogFragment {

    private static final int MAX_DESC_LENGTH = 500;
    private static final String PRODUCT_CONFIGURABLE = "Configurable";
    @InjectView(R.id.main_price)
    TextView mMainPrice;
    @InjectView(R.id.regular_price)
    TextView mRegPrice;
    @InjectView(R.id.msrp_price)
    TextView mMsrpPrice;
    @InjectView(R.id.map_price)
    TextView mMapPrice;
    @InjectView(R.id.includes)
    TextView mIncludes;
    @InjectView(R.id.product_description)
    TextView mDescription;
    @InjectView(R.id.includesLayout)
    LinearLayout mIncludesLayout;
    @InjectView(R.id.main_product_image)
    ImageView mMainProductImage;
    @InjectView(R.id.sku)
    TextView mSku;
    @InjectView(R.id.name)
    TextView mName;
    private Product mProduct;
    private int mTenantId;
    private int mSiteId;
    private String mSiteDomain;
    private ImageURLConverter mImageUrlConverter;
    private NoUnderlineClickableSpan mContractClickableSpan = new NoUnderlineClickableSpan() {

        @Override
        public void onClick(View widget) {
            mDescription.setText(getDescriptionWithSpannableClick(false));
        }

    };
    private NoUnderlineClickableSpan mExpandClickableSpan = new NoUnderlineClickableSpan() {

        @Override
        public void onClick(View widget) {
            mDescription.setText(getDescriptionWithSpannableClick(true));
        }

    };

    public ProductDetailOverviewDialogFragment() {
        // Required empty public constructor

        setStyle(STYLE_NO_TITLE, 0);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_overview_dialog_fragment, null);
        ImageView overviewClose = (ImageView) view.findViewById(R.id.product_detail_overview_close);
        overviewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        ButterKnife.inject(this, view);

        if (mProduct != null) {
            mImageUrlConverter = new ImageURLConverter(mTenantId, mSiteId, mSiteDomain);

            setProductOverviewViews(view);
        }

        return view;
    }

    private void setProductOverviewViews(View view) {
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();

        setImage();

        mSku.setText(mProduct.getProductCode());
        mName.setText(mProduct.getName());

        if (hasSalePrice(mProduct)) {
            mMainPrice.setVisibility(View.VISIBLE);
            mMainPrice.setText(getSalePriceText(defaultFormat));
        } else {
            mMainPrice.setVisibility(View.GONE);
        }

        mRegPrice.setText(getRegularPriceText(defaultFormat));
        mMsrpPrice.setText(getMSRPPriceText(defaultFormat));
        getMAPPriceText(defaultFormat);

        if (isProductConfigurable(mProduct)) {
            mIncludesLayout.setVisibility(View.GONE);
        } else {
            mIncludesLayout.setVisibility(View.VISIBLE);
            mIncludes.setText(getBundledProductsString());
        }

        mDescription.setText(getDescriptionWithSpannableClick(false));
        mDescription.setMovementMethod(LinkMovementMethod.getInstance());

        showProductOptionsIfNecessary(view);
    }

    private void setImage() {
        String imageUrl = mImageUrlConverter.getFullImageUrl(mProduct.getImageUrl());

        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }

        RequestCreator creator = Picasso.with(getActivity())
                .load(imageUrl);

        creator = creator.placeholder(R.drawable.icon_noproductphoto).fit().centerInside();

        mMainProductImage.setBackgroundColor(getActivity().getResources().getColor(R.color.darker_grey));

        creator.into(mMainProductImage, new Callback() {

            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) mMainProductImage.getDrawable()).getBitmap();
                mMainProductImage.setBackgroundColor(bitmap.getPixel(0, 0));
            }

            @Override
            public void onError() {
            }

        });
    }

    private void showProductOptionsIfNecessary(View view) {
        if (mProduct.getOptions() != null && !mProduct.getOptions().isEmpty()) {
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.options_layout);
            layout.setVisibility(View.VISIBLE);

            for (ProductOption option : mProduct.getOptions()) {
                ProductOptionsLayout optionsLayout = new ProductOptionsLayout(getActivity(), null);
                optionsLayout.setTitle(option.getName());

                List<ProductOptionValue> optionValues = new ArrayList<ProductOptionValue>();

                ProductOptionValue value = new ProductOptionValue();
                value.setValue(option.getValue());
                value.setStringValue(option.getStringValue());
                optionValues.add(value);

                optionsLayout.setSpinnerOptions(optionValues);
                layout.addView(optionsLayout);
            }
        }
    }

    private boolean isProductConfigurable(Product product) {
        return PRODUCT_CONFIGURABLE.equalsIgnoreCase(product.getProductUsage());
    }

    public void setProduct(Product product) {

        mProduct = product;
    }

    private boolean hasSalePrice(Product product) {
        return product.getPrice() != null && product.getPrice().getSalePrice() != null;

    }

    private String getSalePriceText(NumberFormat format) {
        String saleString = "N/A";
        if (hasSalePrice(mProduct)) {
            saleString = format.format(mProduct.getPrice().getSalePrice());
        }

        return saleString;
    }

    private String getRegularPriceText(NumberFormat format) {
        String regularPrice = "N/A";

        if (mProduct.getPrice() != null && mProduct.getPrice().getPrice() != null) {

            regularPrice = format.format(mProduct.getPrice().getPrice());
        }

        return regularPrice;
    }

    private String getMSRPPriceText(NumberFormat format) {
        String msrpPriceString = "N/A";

        if (mProduct.getPrice() != null && mProduct.getPrice().getMsrp() != null) {

            msrpPriceString = format.format(mProduct.getPrice().getMsrp());
        }

        return msrpPriceString;
    }

    private void getMAPPriceText(final NumberFormat format) {
        UserAuthenticationStateMachine mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

        AndroidObservable.bindFragment(this, ProductAdminObservableManager.getInstance().getMAPPriceObservable(mUserState.getTenantId(), mUserState.getSiteId(), mProduct.getProductCode()))
                .subscribe(new Subscriber<com.mozu.api.contracts.productadmin.Product>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMapPrice.setText("N/A");
                    }

                    @Override
                    public void onNext(com.mozu.api.contracts.productadmin.Product product) {
                        Log.d("", "");
                        mMapPrice.setText(format.format(product.getPrice().getMap()));
                    }
                });
    }

    private String getBundledProductsString() {
        String bundledString = "N/A";

        if (mProduct.getBundledProducts() == null || mProduct.getBundledProducts().size() < 1) {
            return bundledString;
        }

        bundledString = "";

        for (BundledProduct bundable : mProduct.getBundledProducts()) {
            bundledString += bundable.getName() + ", ";
        }

        if (bundledString.length() > 3) {
            bundledString = bundledString.substring(0, bundledString.length() - 3);
        }

        return bundledString;
    }

    private SpannableString getDescriptionWithSpannableClick(boolean showLargeDescription) {
        if (mProduct == null) {
            return new SpannableString("N/A");
        }

        String desc = mProduct.getDescription();
        String buttonText;
        ClickableSpan clickableSpan;
        SpannableString spannableString;

        if (showLargeDescription) {
            buttonText = getString(R.string.show_less_click_link);
            clickableSpan = mContractClickableSpan;
            Spanned spannedText = Html.fromHtml(desc, null, new HTMLTagHandler());
            spannableString = new SpannableString(spannedText);
        } else {
            buttonText = getString(R.string.full_description_click_link);
            clickableSpan = mExpandClickableSpan;

            if (desc.length() > MAX_DESC_LENGTH) {
                desc = desc.subSequence(0, MAX_DESC_LENGTH).toString();
            }

            Spanned spannedText = Html.fromHtml(desc, null, new HTMLTagHandler());
            spannableString = new SpannableString(spannedText);
        }

        //never show the spannable link if the description isn't long enough
        SpannableString linkSpan = new SpannableString("");
        if (desc.length() > MAX_DESC_LENGTH) {
            linkSpan = new SpannableString(buttonText);
            linkSpan.setSpan(clickableSpan, 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            linkSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mozu_color)), 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return new SpannableString(TextUtils.concat(spannableString, linkSpan));
    }

    public void setTenantId(int tenantId) {
        mTenantId = tenantId;
    }

    public void setSiteId(int siteId) {
        mSiteId = siteId;
    }

    public void setSiteDomain(String siteDomain) {
        mSiteDomain = siteDomain;
    }


}
