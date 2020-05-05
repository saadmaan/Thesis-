package ac.sust.saimon.sachetan.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.io.Serializable;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private static String ARG_PARAM_LISTENER = "fragment.param.listener";
    private static String ARG_PARAM_CALENDAR = "fragment.param.calendar";
    private TimeSetListener timeSetListener;
    private Calendar c;

    public static TimePickerFragment newInstance(TimeSetListener timeSetListener, Calendar c){
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_LISTENER, timeSetListener);
        args.putSerializable(ARG_PARAM_CALENDAR, c);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        if (getArguments() != null) {
            timeSetListener = (TimeSetListener) getArguments().get(ARG_PARAM_LISTENER);
            c = (Calendar) getArguments().get(ARG_PARAM_CALENDAR);
        }
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        timeSetListener.onTimeSet(c);
    }

    public interface TimeSetListener extends Serializable {
        void onTimeSet(Calendar c);
    }
}