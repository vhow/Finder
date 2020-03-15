package com.gmail.dailyefforts.filemanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

class Helper {
    private static final String TAG = "Helper";

    private Helper() {
        //no instance
    }

    static void composeEmail(Activity context) {
        final Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        final String[] addresses = {"vhow@163.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        final String subject = context.getString(R.string.app_name)
                + "v" + BuildConfig.VERSION_NAME
                + context.getString(R.string.feedback);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, R.string.no_email_app, Toast.LENGTH_LONG).show();
        }
    }
}
