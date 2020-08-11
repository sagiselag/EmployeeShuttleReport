package com.example.employeeshuttlereport;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView textViewxt;

    public DatePickerFragment(TextView textViewInput){
        textViewxt = textViewInput;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        Calendar minimumDate = Calendar.getInstance();
        minimumDate.add(Calendar.MONTH, -1);
        minimumDate.set(Calendar.DAY_OF_MONTH, minimumDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(minimumDate.getTimeInMillis());
        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onDateSet(DatePicker view, int year, int month, int day) {
        textViewxt.setText(day + "/" + (month + 1) + "/" + year);
    }
}
