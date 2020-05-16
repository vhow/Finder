package io.github.vhow.finder;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.squareup.otto.Bus;

public class App extends Application {

    private static final String TAG = "App";

    private static final App APP = new App();
    private static final Bus BUS = new Bus();

    public static App getInstance() {
        return APP;
    }

    public static Bus getBus() {
        return BUS;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }
}
