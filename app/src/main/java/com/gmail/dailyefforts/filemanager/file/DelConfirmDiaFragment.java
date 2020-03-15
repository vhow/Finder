package com.gmail.dailyefforts.filemanager.file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.gmail.dailyefforts.filemanager.R;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DelConfirmDiaFragment extends DialogFragment {

    public static final String KEY_FILE_NAME_ARR = "file_name_arr";
    private static final String TAG = "DelConfirmDiaFragment";
    private static long mLastTipTime;
    private DelConfirmDialogListener mListener;

    public static boolean needTip() {
        return System.currentTimeMillis() - mLastTipTime >= TimeUnit.HOURS.toMillis(1);
    }

    public static DelConfirmDiaFragment newInstance(CharSequence[] fileNameArr) {
        final DelConfirmDiaFragment fragment = new DelConfirmDiaFragment();
        final Bundle args = new Bundle();
        args.putCharSequenceArray(KEY_FILE_NAME_ARR, fileNameArr);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(DelConfirmDialogListener mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DelConfirmDiaFragment.this.mListener.onConfirm();
            }
        });
        final CharSequence[] arr = getArguments().getCharSequenceArray(KEY_FILE_NAME_ARR);
        Log.d(TAG, "onCreateDialog: " + Arrays.toString(arr));
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DelConfirmDiaFragment.this.mListener.onCancel();
            }
        });
        return builder.create();
    }

    public interface DelConfirmDialogListener {
        void onConfirm();

        void onCancel();
    }
}
