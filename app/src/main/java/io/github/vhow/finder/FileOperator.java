package io.github.vhow.finder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import com.gmail.dailyefforts.filemanager.BuildConfig;
import com.gmail.dailyefforts.filemanager.R;

import io.github.vhow.finder.file.FileHelper;
import io.github.vhow.finder.file.FileInfoDialogFragment;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.List;

public class FileOperator {
    private static final String TAG = "FileOperator";

    private static FileOperator INSTANCE = new FileOperator();

    private FileOperator() {
        //no instance
    }

    public static FileOperator getInstance() {
        return INSTANCE;
    }

    private static boolean isAvailable(final Context context, final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        final int size = list.size();
        return size > 0;
    }

    public void tryShare(Activity activity, File file) {
        if (file == null) {
            Log.e(TAG, "tryShare() file is null.");
            return;
        }
        final ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(activity);
        builder.setStream(getUri(activity, file));
        final String extension = FilenameUtils.getExtension(file.getName());
        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (mimeType == null) {
            builder.setType("*/*");
        } else {
            builder.setType(mimeType);
        }
        final Intent intent = builder.getIntent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (isAvailable(activity, intent)) {
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share)));
        } else {
            Toast.makeText(activity, R.string.unable_to_share_this_file,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void showInfo(FragmentManager fragmentManager, File file) {
        FileInfoDialogFragment.newInstance(file).show(fragmentManager, FileInfoDialogFragment.class.getSimpleName());
    }

    private Uri getUri(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        }
        return uri;
    }

    public void tryOpen(Activity activity, File file) {
        if (file == null) {
            Log.e(TAG, "tryOpen() file is null.");
            return;
        }
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        final String mime = FileHelper.getMimeType(file.getName());
        intent.setDataAndType(getUri(activity, file), mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (BuildConfig.DEBUG) {
            Toast.makeText(activity, file.getAbsolutePath() + ", " + mime,
                    Toast.LENGTH_SHORT).show();
        }
        if (isAvailable(activity, intent)) {
            activity.startActivity(Intent.createChooser(intent,
                    activity.getString(R.string.open)));
        } else {
            Toast.makeText(activity, R.string.unable_to_open_this_file,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
