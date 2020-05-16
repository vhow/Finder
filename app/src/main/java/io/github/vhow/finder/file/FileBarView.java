package io.github.vhow.finder.file;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import io.github.vhow.finder.App;

import java.io.File;

public class FileBarView extends AppCompatButton implements View.OnClickListener {
    private static final String TAG = "FileNaviTextView";

    private File mFile;

    public FileBarView(Context context) {
        super(context);
    }

    public FileBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FileBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFile(File file) {
        mFile = file;
        setText(file.getName());
        setOnClickListener(this);
    }

    private int dp2px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void onClick(View v) {
        if (mFile == null) {
            Log.e(TAG, "onClick: mFile is null.");
            return;
        }
        final FileActionEvent event = new FileActionEvent(FileActionEvent.Action.OPEN, mFile);
        App.getBus().post(event);
    }
}
