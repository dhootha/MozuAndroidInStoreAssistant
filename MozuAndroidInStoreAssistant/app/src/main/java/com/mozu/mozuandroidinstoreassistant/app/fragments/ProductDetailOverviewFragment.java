package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.BundledProduct;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.views.NoUnderlineClickableSpan;

import java.text.NumberFormat;


public class ProductDetailOverviewFragment extends Fragment {


    private Product mProduct;

    TextView mDescription;

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

        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();

        mainPrice.setText(getSalePriceText(defaultFormat));
        regPrice.setText(getRegularPriceText(defaultFormat));
        msrpPrice.setText(getMSRPPriceText(defaultFormat));
        mapPrice.setText(getMAPPriceText(defaultFormat));

        includes.setText(getBundledProductsString());
        mDescription.setText(getSmallDescriptionWithSpannableClick());
        mDescription.setMovementMethod(LinkMovementMethod.getInstance());

        upc.setText(mProduct.getUpc());
        pn.setText(mProduct.getMfgPartNumber());
        distrpn.setText("N/A");
        taxable.setText(mProduct.getIsTaxable() != null && mProduct.getIsTaxable() ? getString(R.string.yes) : getString(R.string.no));
        recurring.setText(mProduct.getIsRecurring() != null && mProduct.getIsRecurring() ? getString(R.string.yes) : getString(R.string.no));

    }


    public void setProduct(Product product) {
        mProduct = product;
    }

    private String getSalePriceText(NumberFormat format) {
        String saleString = "N/A";

        if (mProduct.getPrice() != null && mProduct.getPrice().getSalePrice() != null) {

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

    private String getBundledProductsString() {
        String bundledString = "N/A";

        if (mProduct.getBundledProducts() == null || mProduct.getBundledProducts().size() < 1) {
            return bundledString;
        }

        bundledString = "";

        for (BundledProduct bundable: mProduct.getBundledProducts()) {
            if (bundable.getContent() != null) {
                bundledString += bundable.getContent().getProductName() + ", ";
            }
        }

        if (bundledString.length() > 3) {
            bundledString = bundledString.substring(0, bundledString.length() - 3);
        }

        return bundledString;
    }

    private SpannableString getSmallDescriptionWithSpannableClick() {
        if (mProduct.getContent() == null) {
            return new SpannableString("N/A");
        }

        String smallDesc = mProduct.getContent().getProductShortDescription();
        String fullDescpButtonText = getString(R.string.full_description_click_link);
        smallDesc = smallDesc + fullDescpButtonText;

        SpannableString spannableString = new SpannableString(smallDesc);
        spannableString.setSpan(mExpandClickableSpan, smallDesc.length() - fullDescpButtonText.length(), smallDesc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mozu_color)), smallDesc.length() - fullDescpButtonText.length(), smallDesc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    private SpannableString getLargeDescriptionWithSpannableClick() {
        if (mProduct.getContent() == null) {
            return new SpannableString("N/A");
        }

        String smallDesc = mProduct.getContent().getProductFullDescription();
        String showLessButtonText = getString(R.string.show_less_click_link);
        smallDesc = smallDesc + showLessButtonText;

        SpannableString spannableString = new SpannableString(smallDesc);
        spannableString.setSpan(mContractClickableSpan, smallDesc.length() - showLessButtonText.length(), smallDesc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mozu_color)), smallDesc.length() - showLessButtonText.length(), smallDesc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    private NoUnderlineClickableSpan mExpandClickableSpan = new NoUnderlineClickableSpan() {

        @Override
        public void onClick(View widget) {
            mDescription.setText(getLargeDescriptionWithSpannableClick());
        }

    };

    private NoUnderlineClickableSpan mContractClickableSpan = new NoUnderlineClickableSpan() {

        @Override
        public void onClick(View widget) {
            mDescription.setText(getSmallDescriptionWithSpannableClick());
        }

    };
}
