package example.michael.com.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.widget.CompoundButton.OnCheckedChangeListener;
import static android.widget.CompoundButton.OnClickListener;

public class CrimeFragment extends Fragment {

    private Crime       mCrime;
    private EditText    mTitleField;
    private Button      mDateButton;
    private Button      mTimeButton;
    private CheckBox    mSolvedCheckBox;
    private Button      mReportButton;
    private Button      mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView   mPhotoView;
    private File        mPhotoFile;
    private static final String ARG_CRIME_ID             = "crime_id";
    private static final String DIALOG_DATE              = "dialog_date";
    private static final String DIALOG_TIME              = "dialog_time";
    private static final int    REQUEST_CODE_DIALOG_DATE = 0;
    private static final int    REQUEST_CODE_DIALOG_TIME = 1;
    private static final int    REQUEST_CONTACT          = 2;
    private static final int    REQUEST_PHOTO            = 3;

    public static CrimeFragment newInstance(UUID id) {
        CrimeFragment instance = new CrimeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CRIME_ID, id);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dpf = DatePickerFragment.newInstance(mCrime.getDate());
                dpf.setTargetFragment(CrimeFragment.this, REQUEST_CODE_DIALOG_DATE);
                dpf.show(fragmentManager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mTimeButton = view.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment tpd = TimePickerFragment.newInstance(mCrime.getDate());
                tpd.setTargetFragment(CrimeFragment.this, REQUEST_CODE_DIALOG_TIME);
                tpd.show(fragmentManager, DIALOG_DATE);
            }
        });

        mReportButton = view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        //        boolean hasSuspectButton = pickContact.resolveActivity(getActivity().getPackageManager()) != null;
        //        mSuspectButton.setEnabled(hasSuspectButton);
        mSuspectButton = view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mPhotoButton = view.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(getActivity().getPackageManager()) != null;
        //        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "example.michael.com.criminalintent.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        mPhotoView = view.findViewById(R.id.crime_photo);
        updatePhotoView();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_DIALOG_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_DIALOG_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_ART_TIME);
            mCrime.setDate(date);
            updateTime();
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "example.michael.com.criminalintent.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss\tE\ta", Locale.CHINA);
        mTimeButton.setText(sdf.format(mCrime.getDate()));
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        mDateButton.setText(sdf.format(mCrime.getDate()));
    }

    private void updatePhotoView() {
        if (mPhotoView == null || !mPhotoFile.exists()) {
            mPhotoView.setImageBitmap(null);
        } else {
            Bitmap bitmap = PictureUtil.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE,MMM dd";
        String dateString = new SimpleDateFormat(dateFormat, Locale.CHINA).format(mCrime.getDate());
        String suspectString = mCrime.getSuspect();
        if (suspectString == null) {
            suspectString = getString(R.string.crime_report_no_suspect);
        } else {
            suspectString = getString(R.string.crime_report_suspect, suspectString);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspectString);
        return report;
    }
}
