package com.gmail.dailyefforts.filemanager.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.gmail.dailyefforts.filemanager.R;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    public static void launch(Activity from) {
        final Intent intent = new Intent(from, SearchActivity.class);
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
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_search);
        setupActionBar();
        if (savedInstanceState == null) {
            final SearchResultFragment fragment = SearchResultFragment.newInstance("", "");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentContainer, fragment).commit();
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
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}