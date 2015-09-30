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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderFilterDialogFragment extends DialogFragment {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    @InjectView(R.id.order_date_range_start)
    TextView mStartDate;
    @InjectView(R.id.order_date_range_end)
    TextView mEndDate;
    @InjectView(R.id.order_filter_status_group)
    RadioGroup mStatusGroup;
    @InjectView(R.id.order_filter_payment_status_group)
    RadioGroup mPaymentStatusGroup;
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
        String[] previousFilter = getArguments().getStringArray("options");
        mStatus = getActivity().getResources().getStringArray(R.array.order_statuses);
        mPaymentStatus = getActivity().getResources().getStringArray(R.array.order_payment_statuses);
        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogMozu_Alert, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mStartDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        start = new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime());

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
                        mEndDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        end = new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime());
                    }
                }, 0, 0, 0);
                datePickerDialog.getDatePicker().setSpinnersShown(false);
                datePickerDialog.show();
            }
        });
        populateRadioGroup(mStatusGroup, mStatus);
        populateRadioGroup(mPaymentStatusGroup, mPaymentStatus);
        b.setView(view);
        mStatusGroup.check(0);
        mPaymentStatusGroup.check(0);
        if (previousFilter != null) {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            if (previousFilter[0] != null && !previousFilter[0].isEmpty()) {
                try {
                    Date startDate = format.parse(previousFilter[0]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);
                    mStartDate.setText((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR));
                    start = previousFilter[0];
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (previousFilter[1] != null && !previousFilter[1].isEmpty()) {
                try {
                    Date startDate = format.parse(previousFilter[1]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);
                    mEndDate.setText((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR));
                    end = previousFilter[1];
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (previousFilter[2] == null || previousFilter[2].isEmpty()) {
                mStatusGroup.check(0);
            }
            for (int i = 0; i < mStatus.length; i++) {
                if (mStatus[i].equalsIgnoreCase(previousFilter[2])) {
                    mStatusGroup.check(i);
                }
            }
            if (previousFilter[3] == null || previousFilter[3].isEmpty()) {
                mPaymentStatusGroup.check(0);
            } else {
                for (int i = 0; i < mPaymentStatus.length; i++) {
                    if (mPaymentStatus[i].equalsIgnoreCase(previousFilter[3])) {
                        mPaymentStatusGroup.check(i);
                    }
                }
            }

        }
        b.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        b.setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mStartDate.setText("");
                mEndDate.setText("");
                mPaymentStatusGroup.clearCheck();
                mStatusGroup.clearCheck();
                callback.filter(null, null, null, null);
            }
        });
        b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String status = mStatusGroup.getCheckedRadioButtonId() != 0 ? mStatus[mStatusGroup.getCheckedRadioButtonId()] : null;
                String paymentStatus = mPaymentStatusGroup.getCheckedRadioButtonId() != 0 ? mPaymentStatus[mPaymentStatusGroup.getCheckedRadioButtonId()] : null;
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
