package example.michael.com.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
    public static final String EXTRA_CRIME_ID = "example.michael.com.criminalintent.crime_id";

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }

    public static Intent newIntent(Context packageContent, UUID id){
        Intent intent = new Intent();
        intent.setClass(packageContent,CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,id);
        return intent;
    }
}
