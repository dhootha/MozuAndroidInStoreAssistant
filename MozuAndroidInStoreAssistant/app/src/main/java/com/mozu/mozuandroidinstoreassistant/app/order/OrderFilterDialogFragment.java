package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.Date;

import butterknife.ButterKnife;

/**
 * Created by chris_pound on 7/16/15.
 */
public class OrderFilterDialogFragment extends DialogFragment {

    TextView mStartDate;
    TextView mEndDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_filter_dialog_fragment, null);
        mStartDate = ButterKnife.findById(view, R.id.order_date_range_start);
        mEndDate = ButterKnife.findById(view, R.id.order_date_range_end);
        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    }
                }, 0, 0, 0);
                datePickerDialog.getDatePicker().setSpinnersShown(false);
                datePickerDialog.show();
            }
        });
        return view;
    }

    interface onFilterSelectedListener {
        void onFilterSelected(String status);

        void onDateRangeSelected(Date start, Date end);
    }
}
