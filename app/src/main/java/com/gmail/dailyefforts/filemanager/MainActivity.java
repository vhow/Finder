package com.gmail.dailyefforts.filemanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gmail.dailyefforts.filemanager.app.AppListActivity;
import com.gmail.dailyefforts.filemanager.file.FileHelper;
import com.gmail.dailyefforts.filemanager.file.FileListFragment;
import com.gmail.dailyefforts.filemanager.setting.SettingsActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final boolean DOUBLE_BACK_CHECK = false;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private FileListFragment mFileListFragment;
    private int mBackKeyPressedTime;
    private Handler mHandler = new Handler();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(this, R.string.cannot_work_without_permission, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            init();
        }
    }

    private void init() {
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mFileListFragment = FileListFragment.newInstance(1, Environment.getExternalStorageDirectory().getPath());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentMain, mFileListFragment).commitAllowingStateLoss();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final File sdCardPath = FileHelper.getSdCardPath(this);
        if (sdCardPath == null) {
            final Menu menu = navigationView.getMenu();
            menu.removeItem(R.id.nav_device);
        }
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (mFileListFragment.onBackPressed()) {
            return;
        }
        Log.d(TAG, "onBackPressed: mBackKeyPressedTime: " + mBackKeyPressedTime);
        if (DOUBLE_BACK_CHECK && mBackKeyPressedTime == 0) {
            Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBackKeyPressedTime = 0;
                }
            }, 2000);
            mBackKeyPressedTime++;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_feedback) {
            Helper.composeEmail(this);
        } else if (id == R.id.nav_setting) {
            SettingsActivity.launch(this);
        } else if (id == R.id.nav_sdcard) {
            File root = FileHelper.getSdCardPath(this);
            mFileListFragment.remount(root.getPath());
        } else if (id == R.id.nav_phone_storage) {
            File root = Environment.getExternalStorageDirectory();
            mFileListFragment.remount(root.getPath());
        } else if (id == R.id.nav_installed_apps) {
            AppListActivity.launch(this);
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
