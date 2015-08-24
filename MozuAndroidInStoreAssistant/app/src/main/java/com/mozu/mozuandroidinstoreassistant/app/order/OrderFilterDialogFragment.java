package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderFilterDialogFragment extends DialogFragment {

    @InjectView(R.id.order_date_range_start)
    TextView mStartDate;
    @InjectView(R.id.order_date_range_end)
    TextView mEndDate;
    @InjectView(R.id.order_filter_status_list)
    RadioGroup mStatusList;
    @InjectView(R.id.order_filter_payment_status_list)
    RadioGroup mPaymentStatusList;
    private OrderFilterListener callback;
    private String[] mStatus;
    private String[] mPaymentStatus;
    private String start;
    private String end;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (OrderFilterListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement OrderFilterListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.order_filter_dialog_fragment, null);
        ButterKnife.inject(this, view);
        mStatus = getActivity().getResources().getStringArray(R.array.order_statuses);
        mPaymentStatus = getActivity().getResources().getStringArray(R.array.order_payment_statuses);
        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogMozu_Alert, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mStartDate.setText(monthOfYear + "/" + dayOfMonth + "/" + year);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        start = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(calendar.getTime());

                    }
                }, 0, 0, 0);
                datePickerDialog.getDatePicker().setSpinnersShown(false);
                datePickerDialog.show();
            }
        });
        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogMozu_Alert, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mEndDate.setText(monthOfYear + "/" + dayOfMonth + "/" + year);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        end = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(calendar.getTime());
                    }
                }, 0, 0, 0);
                datePickerDialog.getDatePicker().setSpinnersShown(false);
                datePickerDialog.show();
            }
        });
        populateRadioGroup(mStatusList, mStatus);
        populateRadioGroup(mPaymentStatusList, mPaymentStatus);
        b.setView(view);
        mStatusList.check(0);
        mPaymentStatusList.check(0);
        mStatusList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String status = mStatusList.getCheckedRadioButtonId() != 0 ? mStatus[mStatusList.getCheckedRadioButtonId()] : null;
                String paymentStatus = mPaymentStatusList.getCheckedRadioButtonId() != 0 ? mPaymentStatus[mPaymentStatusList.getCheckedRadioButtonId()] : null;

                callback.filter(start, end, status, paymentStatus);
            }
        });
        return b.create();
    }

    private void populateRadioGroup(RadioGroup radioGroup, String[] data) {
        radioGroup.removeAllViews();
        for (int i = 0; i < data.length; i++) {
            RadioButton button = new RadioButton(getActivity());
            button.setText(data[i]);
            button.setId(i);
            radioGroup.addView(button);
        }
    }

}
