package com.gmail.dailyefforts.filemanager.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gmail.dailyefforts.filemanager.R;

import java.util.List;

public class AppListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<AppEntry>> {

    private static final String TAG = "AppListActivity";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppListAdapter mAdapter;
    private boolean mShowSysApps;

    public static void launch(Activity from) {
        final Intent intent = new Intent(from, AppListActivity.class);
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
        setContentView(R.layout.activity_app_list);
        setupActionBar();

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        mSwipeRefreshLayout.setEnabled(false);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        mAdapter = new AppListAdapter(this);
        recyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG, "onContextItemSelected() called with: item = [" + item + "]");
        return super.onContextItemSelected(item);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_show_sys_app:
                if (!mShowSysApps) {
                    mShowSysApps = true;
                    getLoaderManager().restartLoader(0, null, this);
                }
                break;
            case R.id.action_hide_sys_app:
                if (mShowSysApps) {
                    mShowSysApps = false;
                    getLoaderManager().restartLoader(0, null, this);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() called with: id = [" + id + "], args = [" + args + "]");
        mSwipeRefreshLayout.setRefreshing(true);
        return new AppListLoader(AppListActivity.this, mShowSysApps);
    }

    @Override
    public void onLoadFinished(Loader<List<AppEntry>> loader, List<AppEntry> data) {
        Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
        mAdapter.setData(data);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<AppEntry>> loader) {
        Log.d(TAG, "onLoaderReset() called with: loader = [" + loader + "]");
        mAdapter.setData(null);
    }
}
