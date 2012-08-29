package com.bottleworks.dailymoney.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.DatePicker;

public class CustomizeDatePickerDialog extends DatePickerDialog {

    private Calendar c;
    private SimpleDateFormat sdf;

    public CustomizeDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, 0, callBack, year, monthOfYear, dayOfMonth);
        char[] order = DateFormat.getDateFormatOrder(context);
        StringBuffer formatString = new StringBuffer();
        for (char ch : order) {
            switch (ch) {
            case DateFormat.DATE:
                formatString.append("-dd");
                break;
            case DateFormat.MONTH:
                formatString.append("-MM");
                break;
            case DateFormat.YEAR:
                formatString.append("-yyyy");
                break;
            }
        }
        formatString.append(" EEE").deleteCharAt(0);
        sdf = new SimpleDateFormat(formatString.toString());
        c = Calendar.getInstance();
        updateTitle(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        // To call super.onDateChanged will remove itself as DatePicker listener
        // in 4.0
        // super.onDateChanged(view, year, month, day);
        view.init(year, month, day, this);
        updateTitle(year, month, day);
    }

    private void updateTitle(int year, int month, int day) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        setTitle(sdf.format(c.getTime()));
    }

}
