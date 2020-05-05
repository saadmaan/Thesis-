package ac.sust.saimon.sachetan.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static String ARG_PARAM_LISTENER = "fragment.param.listener";
    private static String ARG_PARAM_CALENDAR = "fragment.param.calendar";
    private static long DATE_MIN_LIMIT = 946706400000L;
    private DateSetListener dateSetListener;
    private Calendar c;

    public static DatePickerFragment newInstance(DateSetListener dateSetListener, Calendar c){
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_LISTENER, dateSetListener);
        args.putSerializable(ARG_PARAM_CALENDAR, c);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default values for the picker
        if (getArguments() != null) {
            dateSetListener = (DateSetListener) getArguments().get(ARG_PARAM_LISTENER);
            c = (Calendar) getArguments().get(ARG_PARAM_CALENDAR);
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMinDate(DATE_MIN_LIMIT);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        c.set(i, i1, i2);
        dateSetListener.onDateSet(c);
    }

    public interface DateSetListener extends Serializable {
        void onDateSet(Calendar c);
    }
}