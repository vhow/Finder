package io.github.vhow.finder.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.gmail.dailyefforts.filemanager.BuildConfig;
import com.gmail.dailyefforts.filemanager.R;

public class SettingsActivity extends AppCompatActivity {

    public static void launch(Activity from) {
        final Intent intent = new Intent(from, SettingsActivity.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(from);
            ActivityCompat.startActivity(from, intent, options.toBundle());
        } else {
            from.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.contentContainer, new SettingsFragment()).commit();
        }
    }

    private void setupActionBar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        private static final String TAG = SettingsFragment.class.getSimpleName();
        private SwitchPreference prefShowHidden;
        private Preference versionPref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settting);
            prefShowHidden = (SwitchPreference) findPreference(getString(R.string.pref_show_hidden_file));
            prefShowHidden.setChecked(Settings.getInstance().showHidden(getActivity()));
            prefShowHidden.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "onPreferenceChange() called with: " + "preference = [" + preference + "], newValue = [" + newValue + "]");
                    Settings.getInstance().setShowHidden((Boolean) newValue, getActivity());
                    return true;
                }
            });
            versionPref = findPreference(getString(R.string.pref_key_version));
            versionPref.setSummary(BuildConfig.VERSION_NAME);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }
    }
}
