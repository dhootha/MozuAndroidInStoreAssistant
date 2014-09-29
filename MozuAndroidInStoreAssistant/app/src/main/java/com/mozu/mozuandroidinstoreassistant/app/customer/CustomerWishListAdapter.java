package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.wishlists.Wishlist;
import com.mozu.api.contracts.commerceruntime.wishlists.WishlistItem;
import com.mozu.api.contracts.customer.credit.Credit;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.utils.DateUtils;

import java.text.NumberFormat;
import java.util.List;

public class CustomerWishListAdapter extends BaseAdapter {

    private List<WishlistItem> mData;

    public CustomerWishListAdapter(List<WishlistItem> data){
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public WishlistItem getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WishlistItem wishlistItem = getItem(position);
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            view = inflater.inflate(R.layout.customer_wishlist_item,null);
        } else {
            view = convertView;
        }
        TextView dateAdded = (TextView)view.findViewById(R.id.customer_wishlist_date_added_value);
        TextView price = (TextView)view.findViewById(R.id.customer_wishlist_price_value);
        TextView product = (TextView)view.findViewById(R.id.customer_wishlist_product_value);
        TextView salePrice = (TextView)view.findViewById(R.id.customer_wishlist_saleprice_value);
        TextView quantity = (TextView)view.findViewById(R.id.customer_wishlist_qty_value);
        TextView purchasable = (TextView)view.findViewById(R.id.customer_wishlist_purchasable_value);


        dateAdded.setText(DateUtils.getFormattedDate(wishlistItem.getAuditInfo().getCreateDate().getMillis()));

        product.setText(wishlistItem.getProduct().getName());
        if(wishlistItem.getProduct().getPrice() != null) {
            if (wishlistItem.getProduct().getPrice().getSalePrice() != null) {
                salePrice.setText(NumberFormat.getCurrencyInstance().format(wishlistItem.getProduct().getPrice().getSalePrice()));
            }else{
                salePrice.setText("N/A");
            }

            if (wishlistItem.getProduct().getPrice().getPrice() != null) {
                price.setText(NumberFormat.getCurrencyInstance().format(wishlistItem.getProduct().getPrice().getPrice()));
            } else {
                price.setText("N/A");
            }


        }else{
            salePrice.setText("N/A");
            price.setText("N/A");

        }
        quantity.setText(String.valueOf(wishlistItem.getQuantity()));
        if (wishlistItem.getPurchasableStatusType() != null) {
            if (wishlistItem.getPurchasableStatusType().equalsIgnoreCase("purchasable")) {
                purchasable.setText("Y");

            } else {
                purchasable.setText("N");
            }
        } else {
            purchasable.setText("N/A");
        }
        return view;
    }


    public void setData(List<WishlistItem> data) {
        mData = data;
    }


}
