package com.mozu.mozuandroidinstoreassistant.app.search.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.contracts.productruntime.ProductSearchResult;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.api.contracts.productruntime.Product;

import java.text.NumberFormat;


public class GlobalSearchProductAdapter extends BaseAdapter {

    private ProductSearchResult mProductSearchResult;
    public GlobalSearchProductAdapter(ProductSearchResult productSearchResult){
        mProductSearchResult = productSearchResult;
    }
    @Override
    public int getCount() {
        if(mProductSearchResult.getItems() != null){
            return mProductSearchResult.getItems().size();
        }
        return 0;
    }

    public void setData(ProductSearchResult productSearchResult){
        mProductSearchResult = productSearchResult;
    }
    @Override
    public Product getItem(int i) {
        if (mProductSearchResult.getItems() != null) {
            return mProductSearchResult.getItems().get(i);
        } else {

            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GlobalSearchOrderViewHolder viewHolder;
        if (convertview == null) {
            convertview = inflater.inflate(R.layout.globalsearch_product_item, parent,false);
            viewHolder = new GlobalSearchOrderViewHolder();
            viewHolder.productName = (TextView) convertview.findViewById(R.id.productName);
            viewHolder.productCode = (TextView)convertview.findViewById(R.id.productCode);
            viewHolder.productPrice = (TextView)convertview.findViewById(R.id.productPrice);
            convertview.setTag(viewHolder);
        }
        else{
            viewHolder = (GlobalSearchOrderViewHolder) convertview.getTag();
        }

        Product product = getItem(i);
        if (product != null) {
            viewHolder.productCode.setText(product.getProductCode());
            viewHolder.productPrice.setText(product.getPrice() != null && product.getPrice().getPrice() != null && product.getPrice().getPrice() > 0.0 ? NumberFormat.getCurrencyInstance().format(product.getPrice().getPrice()) : "");
            viewHolder.productName.setText(product.getContent().getProductName());
        }
        return convertview;
    }

    static class GlobalSearchOrderViewHolder{
        TextView productName;
        TextView productCode;
        TextView productPrice;
    }


}
