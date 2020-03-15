package com.gmail.dailyefforts.filemanager.file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gmail.dailyefforts.filemanager.R;

import java.io.File;

public class FileInfoDialogFragment extends DialogFragment {
    private static final String KEY_FILE = "file";

    public static FileInfoDialogFragment newInstance(File file) {
        final FileInfoDialogFragment fragment = new FileInfoDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(KEY_FILE, file);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final File file = (File) getArguments().getSerializable(KEY_FILE);
        if (file != null) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            final View container = inflater.inflate(R.layout.fragment_file_info, null);
            builder.setView(container);
            final TextView tvFileName = container.findViewById(R.id.tvFileName);
            final TextView tvFileType = container.findViewById(R.id.tvFileType);
            final TextView tvFileSize = container.findViewById(R.id.tvFileSize);
            final TextView tvFileModifiedTime = container.findViewById(R.id.tvFileLastModifiedTime);
            final TextView tvFilePath = container.findViewById(R.id.tvFilePath);
            final String name = file.getName();
            tvFileName.setText(name);
            final boolean isDir = file.isDirectory();
            final String type = isDir ? getString(R.string.folder) : FileHelper.getMimeType(file.getName());
            tvFileType.setText(type);
            if (isDir) {
                final File[] files = file.listFiles();
                final int size = files == null ? 0 : files.length;
                tvFileSize.setText(getString(R.string.x_files, size));
            } else {
                final String size = Formatter.formatFileSize(getContext(), file.length());
                tvFileSize.setText(size);
            }
            tvFileModifiedTime.setText(DateUtils.getRelativeTimeSpanString(file.lastModified()));
            tvFilePath.setText(file.getParent());
        }
        return builder.create();
    }
}
