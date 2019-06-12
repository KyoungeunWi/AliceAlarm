package com.alice.alicetimer.countdowmTimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.NumberPicker;


/* This class is for showing dialog activity
that allow user pick time they want to set */

public class TimeSettingDialog extends DialogFragment {
    private  NumberPicker.OnValueChangeListener mValueChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final android.widget.NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMaxValue(60);
        numberPicker.setMinValue(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Time setting");
        builder.setMessage("Select the time (seconds)");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mValueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(),
                        numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCLE", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mValueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(),
                        numberPicker.getValue());
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return mValueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener){
        mValueChangeListener = valueChangeListener;
    }

}
