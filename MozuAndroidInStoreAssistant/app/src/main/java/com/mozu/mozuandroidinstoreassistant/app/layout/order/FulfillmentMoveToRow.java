package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class FulfillmentMoveToRow extends RelativeLayout implements IRowLayout {
    private MoveToListener mListener;

    public FulfillmentMoveToRow(Context context) {
        super(context);
    }

    public FulfillmentMoveToRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FulfillmentMoveToRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(final IData data) {
        Button moveTo = (Button) findViewById(R.id.fulfillment_move_to);
        moveTo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMoveToClicked(data);
            }
        });
    }

    public void setResponseListener(MoveToListener listener) {
        mListener = listener;
    }

    public interface MoveToListener {
        void onMoveToClicked(IData data);
    }
}
