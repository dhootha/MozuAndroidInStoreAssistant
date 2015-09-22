package com.mozu.mozuandroidinstoreassistant.app.product;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productadmin.ProductVariation;
import com.mozu.api.contracts.productadmin.ProductVariationOption;
import com.mozu.api.contracts.productadmin.ProductVariationPagedCollection;
import com.mozu.api.contracts.productruntime.BundledProduct;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.contracts.productruntime.ProductOption;
import com.mozu.api.resources.commerce.catalog.admin.products.ProductVariationResource;
import com.mozu.mozuandroidinstoreassistant.app.ProductDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.htmlutils.HTMLTagHandler;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.product.loaders.ProductAdminObservableManager;
import com.mozu.mozuandroidinstoreassistant.app.views.NoUnderlineClickableSpan;
import com.mozu.mozuandroidinstoreassistant.app.views.ProductOptionsLayout;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class ProductDetailOverviewFragment extends Fragment implements ProductOptionsLayout.onOptionChangeListener {


    private static final int MAX_DESC_LENGTH = 500;
    TextView mDescription;
    private CompositeSubscription mCompositeSubscription;

    HashMap<ProductOptionsContainer, Double> variationMap;
    private Product mProduct;
    private View mView;
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

    public ProductDetailOverviewFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.product_detail_overview_fragment, null);
        mCompositeSubscription = new CompositeSubscription();
        if (mProduct != null) {
            setProductOverviewViews(mView);

        }

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mProduct != null && mProduct.getVariations() != null && mProduct.getVariations().size() > 0) {
            buildVariationMap();
        }
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
        getMAPPrice(defaultFormat);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    private void getMAPPrice(final NumberFormat format) {
        UserAuthenticationStateMachine mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

        mCompositeSubscription.add(AndroidObservable.bindFragment(this, ProductAdminObservableManager.getInstance().getMAPPriceObservable(mUserState.getTenantId(), mUserState.getSiteId(), mProduct.getProductCode()))
                .subscribe(new Subscriber<com.mozu.api.contracts.productadmin.Product>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ((TextView) mView.findViewById(R.id.map_price)).setText("N/A");
                    }

                    @Override
                    public void onNext(com.mozu.api.contracts.productadmin.Product product) {
                        com.mozu.api.contracts.productadmin.ProductPrice price = product.getPrice();
                        ((TextView) mView.findViewById(R.id.map_price)).setText(format.format(price.getMap()));
                    }
                }));
    }

    private void buildVariationMap() {
        mCompositeSubscription.add(AndroidObservable.bindFragment(this, Observable.create(new Observable.OnSubscribe<ProductVariationPagedCollection>() {
                    @Override
                    public void call(Subscriber<? super ProductVariationPagedCollection> subscriber) {
                        UserAuthenticationStateMachine mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
                        ProductVariationResource productVariationResource = new ProductVariationResource(new MozuApiContext(mUserState.getTenantId(), mUserState.getSiteId()));
                        try {
                            ProductVariationPagedCollection pagedCollection = productVariationResource.getProductVariations(mProduct.getProductCode());
                            subscriber.onNext(pagedCollection);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
        ).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ProductVariationPagedCollection>() {
                    @Override
                    public void onCompleted() {
                        onOptionChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ((TextView) mView.findViewById(R.id.msrp_price)).setText("N/A");
                    }

                    @Override
                    public void onNext(ProductVariationPagedCollection product) {
                        variationMap = new HashMap<ProductOptionsContainer, Double>();
                        List<ProductVariation> productVariations = product.getItems();
                        for (ProductVariation productVariation : productVariations) {
                            ProductOptionsContainer productOptionsContainer = new ProductOptionsContainer();
                            for (ProductVariationOption option : productVariation.getOptions()) {
                                productOptionsContainer.add(option.getAttributeFQN(), option.getValue().toString());
                            }
                            variationMap.put(productOptionsContainer, productVariation.getDeltaPrice().getMsrp());
                        }
                    }
                }));
    }

    private void setProductOverviewViews(View view) {
        TextView mainPrice = (TextView) view.findViewById(R.id.main_price);
        TextView regPrice = (TextView) view.findViewById(R.id.regular_price);
        TextView includes = (TextView) view.findViewById(R.id.includes);
        mDescription = (TextView) view.findViewById(R.id.product_description);
        TextView upc = (TextView) view.findViewById(R.id.upc);
        TextView pn = (TextView) view.findViewById(R.id.pn);
        TextView distrpn = (TextView) view.findViewById(R.id.distrpn);
        TextView taxable = (TextView) view.findViewById(R.id.taxable);
        TextView recurring = (TextView) view.findViewById(R.id.recurring);
        LinearLayout includesLayout = (LinearLayout) view.findViewById(R.id.includesLayout);

        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
        if (hasSalePrice(mProduct)) {
            mainPrice.setVisibility(View.VISIBLE);
            mainPrice.setText(getSalePriceText(defaultFormat));
        } else {
            mainPrice.setVisibility(View.GONE);
        }
        regPrice.setText(getRegularPriceText(defaultFormat));

        if (mProduct.getBundledProducts() == null || mProduct.getBundledProducts().isEmpty()) {
            includesLayout.setVisibility(View.GONE);
        } else {
            includesLayout.setVisibility(View.VISIBLE);
            includes.setText(getBundledProductsStringWithClick(mProduct));
            includes.setMovementMethod(LinkMovementMethod.getInstance());
        }
        mDescription.setText(getDescriptionWithSpannableClick(false));
        mDescription.setMovementMethod(LinkMovementMethod.getInstance());

        upc.setText(getUPC(mProduct));
        pn.setText(getPartNumber(mProduct));
        distrpn.setText(getString(R.string.not_available));
        taxable.setText(mProduct.getIsTaxable() != null && mProduct.getIsTaxable() ? getString(R.string.yes) : getString(R.string.no));
        recurring.setText(mProduct.getIsRecurring() != null && mProduct.getIsRecurring() ? getString(R.string.yes) : getString(R.string.no));
        if (mProduct.getOptions() != null && !mProduct.getOptions().isEmpty()) {
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.options_layout);
            layout.setVisibility(View.VISIBLE);
            for (ProductOption option : mProduct.getOptions()) {
                ProductOptionsLayout optionsLayout = new ProductOptionsLayout(getActivity(), this);
                optionsLayout.setAttributeFQN(option.getAttributeFQN());
                optionsLayout.setTitle(option.getAttributeDetail().getName());
                optionsLayout.setSpinnerOptions(option.getValues());
                layout.addView(optionsLayout);
            }
        }

    }

    private String getUPC(Product product) {
        if (TextUtils.isEmpty(product.getUpc())) {
            return "N/A";
        } else {
            return product.getUpc();
        }
    }

    private String getPartNumber(Product product) {
        if (TextUtils.isEmpty(product.getMfgPartNumber())) {
            return "N/A";
        } else {
            return product.getMfgPartNumber();
        }
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

    private SpannableString getBundledProductsStringWithClick(final Product product) {
        SpannableString bundledSpannableString = new SpannableString("");
        for (final BundledProduct bundable : product.getBundledProducts()) {
            if (bundable.getContent() != null) {
                String buttonText = bundable.getContent().getProductName();
                SpannableString linkSpan = new SpannableString(buttonText);
                NoUnderlineClickableSpan mIncludesClickableSpan = new NoUnderlineClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                        UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());

                        intent.putExtra(ProductDetailActivity.PRODUCT_CODE_EXTRA_KEY, bundable.getProductCode());
                        intent.putExtra(ProductDetailActivity.CURRENT_TENANT_ID, userAuthenticationStateMachine.getTenantId());
                        intent.putExtra(ProductDetailActivity.CURRENT_SITE_ID, userAuthenticationStateMachine.getSiteId());
                        intent.putExtra(ProductDetailActivity.CURRENT_SITE_DOMAIN, userAuthenticationStateMachine.getSiteDomain());
                        startActivity(intent);
                    }
                };
                linkSpan.setSpan(mIncludesClickableSpan, 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                linkSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mozu_color)), 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                linkSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, buttonText.length(), 0);
                if (bundledSpannableString.toString().isEmpty()) {
                    bundledSpannableString = new SpannableString(linkSpan);
                } else {
                    bundledSpannableString = new SpannableString(TextUtils.concat(bundledSpannableString, " ,  ", linkSpan));
                }
            }
        }

        return bundledSpannableString;
    }

    private boolean showExpandButton(String text) {
        return text != null && text.length() > MAX_DESC_LENGTH;

    }

    private SpannableString getDescriptionWithSpannableClick(boolean showLargeDescription) {
        if (mProduct.getContent() == null) {
            return new SpannableString("N/A");
        }

        String htmlString;
        String buttonText;
        ClickableSpan clickableSpan;
        SpannableString spannableString;
        if (showLargeDescription) {
            if (!TextUtils.isEmpty(mProduct.getContent().getProductFullDescription())) {
                htmlString = mProduct.getContent().getProductFullDescription();
                String desc = htmlString.replaceAll("<img.+?>", "");
                buttonText = getString(R.string.show_less_click_link);
                clickableSpan = mContractClickableSpan;
                Spanned spannedText = Html.fromHtml(desc, null, new HTMLTagHandler());
                spannableString = new SpannableString(spannedText);
                if (!TextUtils.isEmpty(mProduct.getContent().getProductShortDescription())) {
                    SpannableString linkSpan = new SpannableString(buttonText);
                    linkSpan.setSpan(clickableSpan, 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    linkSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mozu_color)), 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return new SpannableString(TextUtils.concat(spannableString, linkSpan));
                } else {
                    return spannableString;
                }
            } else {
                htmlString = mProduct.getContent().getProductShortDescription();
                String desc = htmlString.replaceAll("<img.+?>", "");
                Spanned spannedText = Html.fromHtml(desc, null, new HTMLTagHandler());
                spannableString = new SpannableString(spannedText);
                return spannableString;
            }

        } else {
            if (!TextUtils.isEmpty(mProduct.getContent().getProductShortDescription())) {
                htmlString = mProduct.getContent().getProductShortDescription();
                String desc = htmlString.replaceAll("<img.+?>", "");
                buttonText = getString(R.string.full_description_click_link);
                clickableSpan = mExpandClickableSpan;
                Spanned spannedText = Html.fromHtml(desc, null, new HTMLTagHandler());
                spannableString = new SpannableString(spannedText);
                if (!TextUtils.isEmpty(mProduct.getContent().getProductFullDescription())) {
                    SpannableString linkSpan = new SpannableString(buttonText);
                    linkSpan.setSpan(clickableSpan, 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    linkSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mozu_color)), 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return new SpannableString(TextUtils.concat(spannableString, linkSpan));
                } else {
                    return spannableString;
                }
            } else {
                htmlString = mProduct.getContent().getProductFullDescription();
                String desc = htmlString.replaceAll("<img.+?>", "");
                Spanned spannedText = Html.fromHtml(desc, null, new HTMLTagHandler());
                spannableString = new SpannableString(spannedText);
                return spannableString;
            }


        }

    }

    @Override
    public void onOptionChanged() {
        if (variationMap == null || variationMap.size() < 1) {
            ((TextView) mView.findViewById(R.id.msrp_price)).setText("N/A");
            return;
        }
        if (mProduct.getOptions() != null && !mProduct.getOptions().isEmpty()) {
            LinearLayout layout = (LinearLayout) mView.findViewById(R.id.options_layout);
            final ProductOptionsContainer productOptionsContainer = new ProductOptionsContainer();
            for (int i = 0; i < layout.getChildCount(); i++) {
                if (layout.getChildAt(i) instanceof ProductOptionsLayout) {
                    ProductOptionsLayout productOptionsLayout = (ProductOptionsLayout) layout.getChildAt(i);
                    productOptionsContainer.add(productOptionsLayout.getAttributeFQN(), productOptionsLayout.getAttributeValue());
                }
            }

            final NumberFormat format = NumberFormat.getCurrencyInstance();
            if (variationMap.get(productOptionsContainer) != null) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) mView.findViewById(R.id.msrp_price)).setText(format.format(variationMap.get(productOptionsContainer)));
                    }
                });
            } else {
                ((TextView) mView.findViewById(R.id.msrp_price)).setText("N/A");
            }
        }

    }
}
