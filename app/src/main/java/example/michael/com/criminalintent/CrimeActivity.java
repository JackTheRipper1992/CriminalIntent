package example.michael.com.criminalintent;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID = "example.michael.com.criminalintent.crime_id";

    @Override
    protected Fragment createFragment() {
        return CrimeFragment.newInstance((UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID));
    }

    public static Intent newIntent(Context packageContent, UUID CrimeId) {
        Intent intent = new Intent(packageContent, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, CrimeId);
        return intent;
    }
}
