package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ORderFulfillmentMoveToItemAdapter extends RecyclerView.Adapter<ORderFulfillmentMoveToItemAdapter.ViewHolder> {

    List<OrderItem> mData;
    MoveToListListener mListener;

    public ORderFulfillmentMoveToItemAdapter(List<OrderItem> mData, MoveToListListener mListener) {
        this.mData = mData;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.move_to_item, parent, false);
        return new ORderFulfillmentMoveToItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderItem item = mData.get(position);
        holder.code.setText(item.getProduct().getProductCode());
        holder.product.setText(item.getProduct().getName());
        holder.location.setText(item.getFulfillmentLocationCode());
        holder.quantity.setText(item.getQuantity() + "");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface MoveToListListener {
        void onItemSelected(Integer position, boolean isSelected);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.ordered_item_code)
        TextView code;
        @InjectView(R.id.ordered_item_product)
        TextView product;
        @InjectView(R.id.location_code)
        TextView location;
        @InjectView(R.id.ordered_item_quantity)
        TextView quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            Log.d("intellij", "");
            itemView.setActivated(!itemView.isActivated());
            mListener.onItemSelected(getAdapterPosition(), itemView.isActivated());
        }
    }

}
