package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.BundledProduct;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;


public class ProductDetailOverviewFragment extends Fragment {


    private Product mProduct;

    public ProductDetailOverviewFragment() {
        // Required empty public constructor
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
        TextView description = (TextView) view.findViewById(R.id.product_description);

        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();

        mainPrice.setText(getSalePriceText(defaultFormat));
        regPrice.setText(getRegularPriceText(defaultFormat));
        msrpPrice.setText(getMSRPPriceText(defaultFormat));
        mapPrice.setText(getMAPPriceText(defaultFormat));

        includes.setText(getBundledProductsString());
        description.setText(mProduct.getContent().getMetaTagDescription());
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
}
