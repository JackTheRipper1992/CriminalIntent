package example.michael.com.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE   = "date";
    public static final  String EXTRA_DATE = "example.michael.com.criminalintent.date";
    DatePicker mDatePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = new Date();
        if (getArguments() != null) {
            date = (Date) getArguments().getSerializable(ARG_DATE);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.alertdialog_date_picker, null);
        mDatePicker = v.findViewById(R.id.date_picker);
        mDatePicker.init(year, month, day, null);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_titile)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date1 = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date1);
                    }
                })
                .create();

    }

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        DatePickerFragment dfp = new DatePickerFragment();
        args.putSerializable(ARG_DATE, date);
        dfp.setArguments(args);
        return dfp;
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, date);
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        }
    }
}
