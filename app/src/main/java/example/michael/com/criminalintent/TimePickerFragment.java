package example.michael.com.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {

    private static final String ARG_TIME       = "crime_time";
    public static final  String EXTRA_ART_TIME = "example.michael.com.criminalintent.crime_time";
    private TimePicker mTimePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.alertdialog_time_picker, null);
        mTimePicker = view.findViewById(R.id.alert_dialog_time_picker);
        final Date Date = (Date) getArguments().getSerializable(ARG_TIME);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }
        mTimePicker.setIs24HourView(true);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.crime_time_title)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int hour = mTimePicker.getHour();
                            int minute = mTimePicker.getMinute();
                            Date date1 = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, minute).getTime();
                            sendResult(Activity.RESULT_OK, date1);
                        }

                    }
                })
                .create();
    }

    public static TimePickerFragment newInstance(Date date) {
        TimePickerFragment tpd = new TimePickerFragment();
        Bundle arg = new Bundle();
        arg.putSerializable(ARG_TIME, date);
        tpd.setArguments(arg);
        return tpd;
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_ART_TIME, date);
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        }
    }
}
