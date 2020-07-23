package com.ashwinkanchana.healthcalls;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class MonthYearPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2030;
    private ExampleDialogListener listener;





    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_date_picker, null);
        final NumberPicker monthPicker = (NumberPicker) view.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) view.findViewById(R.id.picker_year);
        Calendar cal = Calendar.getInstance();
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH));

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(year);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);

        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Set expiry")
                .setMessage("Pick month and year")
                .setPositiveButton("OK",null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {

            Button button = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                listener.expiry(monthPicker.getValue(), yearPicker.getValue());
                dialog.dismiss();


            });


        });
        return dialog;
    }





    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogListener");
        }
    }

    public interface ExampleDialogListener {
        void expiry(int month,int year);
    }
}
