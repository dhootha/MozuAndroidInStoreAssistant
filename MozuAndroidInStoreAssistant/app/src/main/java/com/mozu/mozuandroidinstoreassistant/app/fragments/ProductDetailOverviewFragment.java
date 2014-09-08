package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.mozu.api.contracts.productruntime.BundledProduct;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.contracts.productruntime.ProductOption;
import com.mozu.mozuandroidinstoreassistant.app.ProductDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.htmlutils.HTMLTagHandler;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.views.NoUnderlineClickableSpan;
import com.mozu.mozuandroidinstoreassistant.app.views.ProductOptionsLayout;

import java.text.NumberFormat;


public class ProductDetailOverviewFragment extends Fragment {


    private Product mProduct;

    TextView mDescription;

    private static final int MAX_DESC_LENGTH = 500;
    private static final String PRODUCT_CONFIGURABLE = "Configurable";
    private static final String PRODUCT_BUNDLE = "Bundle";

    public ProductDetailOverviewFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_overview_fragment, null);

        if (mProduct != null) {
            setProductOverviewViews(view);
        }

        return view;
    }

    private void setProductOverviewViews(View view) {
        TextView mainPrice = (TextView) view.findViewById(R.id.main_price);
        TextView regPrice = (TextView) view.findViewById(R.id.regular_price);
        TextView msrpPrice = (TextView) view.findViewById(R.id.msrp_price);
        TextView mapPrice = (TextView) view.findViewById(R.id.map_price);
        TextView includes = (TextView) view.findViewById(R.id.includes);
        mDescription = (TextView) view.findViewById(R.id.product_description);
        TextView upc = (TextView) view.findViewById(R.id.upc);
        TextView pn = (TextView) view.findViewById(R.id.pn);
        TextView distrpn = (TextView) view.findViewById(R.id.distrpn);
        TextView taxable = (TextView) view.findViewById(R.id.taxable);
        TextView recurring = (TextView) view.findViewById(R.id.recurring);
        LinearLayout includesLayout = (LinearLayout)view.findViewById(R.id.includesLayout);

        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
        if (hasSalePrice(mProduct)) {
            mainPrice.setVisibility(View.VISIBLE);
            mainPrice.setText(getSalePriceText(defaultFormat));
        } else {
            mainPrice.setVisibility(View.GONE);
        }
        regPrice.setText(getRegularPriceText(defaultFormat));
        msrpPrice.setText(getMSRPPriceText(defaultFormat));
        mapPrice.setText(getMAPPriceText(defaultFormat));
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
        distrpn.setText("N/A");
        taxable.setText(mProduct.getIsTaxable() != null && mProduct.getIsTaxable() ? getString(R.string.yes) : getString(R.string.no));
        recurring.setText(mProduct.getIsRecurring() != null && mProduct.getIsRecurring() ? getString(R.string.yes) : getString(R.string.no));
        if (mProduct.getOptions() != null && !mProduct.getOptions().isEmpty()) {
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.options_layout);
            layout.setVisibility(View.VISIBLE);
            for(ProductOption option: mProduct.getOptions()){
                ProductOptionsLayout optionsLayout = new ProductOptionsLayout(getActivity());
                optionsLayout.setTitle(option.getAttributeDetail().getName());
                optionsLayout.setSpinnerOptions(option.getValues());
                layout.addView(optionsLayout);
            }

        }

    }

    private String getUPC(Product product){
        if (TextUtils.isEmpty(product.getUpc())) {
            return "N/A";
        } else {
            return product.getUpc();
        }
    }

    private String getPartNumber(Product product){
        if (TextUtils.isEmpty(product.getMfgPartNumber())) {
            return "N/A";
        } else {
            return product.getMfgPartNumber();
        }
    }
    public void setProduct(Product product) {
        mProduct = product;
    }

    private boolean hasSalePrice(Product product){
        if (product.getPrice() != null && product.getPrice().getSalePrice() != null) {
            return true;
        }
        return false;
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

    private String getMAPPriceText(NumberFormat format) {
        String mapString = "N/A";

//MAP Price unprovided currently
//        if (mProduct.getPrice() != null && mProduct.getPrice(). != null) {
//
//            mapString = format.format(mProduct.getPrice().getSalePrice());
//        }

        return mapString;
    }


    private SpannableString getBundledProductsStringWithClick(final Product product) {
        SpannableString bundledSpannableString = new SpannableString("");
        for (final BundledProduct bundable: product.getBundledProducts()) {
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

    private SpannableString getDescriptionWithSpannableClick(boolean showLargeDescription){
        if (mProduct.getContent() == null) {
            return new SpannableString("N/A");
        }

        String desc = mProduct.getContent().getProductFullDescription();
        String buttonText;
        ClickableSpan clickableSpan;
        SpannableString spannableString;
        if (showLargeDescription) {
            buttonText = getString(R.string.show_less_click_link);
            clickableSpan = mContractClickableSpan;
            Spanned spannedText = Html.fromHtml(desc,null,new HTMLTagHandler());
            spannableString = new SpannableString(spannedText);
        } else {
            buttonText = getString(R.string.full_description_click_link);
            clickableSpan = mExpandClickableSpan;

            if (desc.length() > MAX_DESC_LENGTH) {
                desc = desc.subSequence(0, MAX_DESC_LENGTH).toString();
            }
            Spanned spannedText = Html.fromHtml(desc,null,new HTMLTagHandler());
            spannableString = new SpannableString(spannedText);
        }
        SpannableString linkSpan = new SpannableString(buttonText);
        linkSpan.setSpan(clickableSpan, 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linkSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mozu_color)), 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new SpannableString(TextUtils.concat(spannableString,linkSpan));
    }

    private NoUnderlineClickableSpan mExpandClickableSpan = new NoUnderlineClickableSpan() {

        @Override
        public void onClick(View widget) {
            mDescription.setText(getDescriptionWithSpannableClick(true));
        }

    };

    private NoUnderlineClickableSpan mContractClickableSpan = new NoUnderlineClickableSpan() {

        @Override
        public void onClick(View widget) {
            mDescription.setText(getDescriptionWithSpannableClick(false));
        }

    };
}
