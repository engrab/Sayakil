package com.oman.sayakil.dialoge;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oman.sayakil.R;
import com.oman.sayakil.ui.activities.MainActivity;
import com.oman.sayakil.utils.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DialogPaymentSuccessFragment extends DialogFragment {

    private View root_view;
    TextView tvTime;
    TextView tvDate;
    TextView tvAmount;
    int year;
    int monthOfYear;
    int dayOfMonth;

    private int price;

    public DialogPaymentSuccessFragment(int price) {
        this.price = price;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.dialog_payment_success, container, false);

        tvTime = root_view.findViewById(R.id.tv_time);
        tvDate = root_view.findViewById(R.id.tv_date);
        tvAmount = root_view.findViewById(R.id.tv_amount);

        ((FloatingActionButton) root_view.findViewById(R.id.fab_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
                dismiss();
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String currentDateandTime = sdf.format(new Date());

        tvTime.setText(Tools.getFormattedTimeEvent(System.currentTimeMillis()));
        tvDate.setText(currentDateandTime);
        tvAmount.setText("OMR "+price);

        return root_view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}