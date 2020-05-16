package io.github.vhow.finder.file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gmail.dailyefforts.filemanager.BuildConfig;
import com.gmail.dailyefforts.filemanager.R;
import io.github.vhow.finder.widget.SuperRecyclerView;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.Arrays;

public class DirPickerDiaFragment extends DialogFragment {

    private static final String TAG = "DirPickerDiaFragment";

    private static final String OK_RES_ID = "ok_res_id";

    private static File sRoot;
    private static File mDir;

    private DirListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FileListFragment.OnTransferListener mListener;

    public static DirPickerDiaFragment newInstance(int okResId, String root) {
        DirPickerDiaFragment fragment = new DirPickerDiaFragment();
        Bundle args = new Bundle();
        args.putInt(OK_RES_ID, okResId);
        fragment.setArguments(args);
        sRoot = new File(root);
        mDir = sRoot;
        return fragment;
    }

    public boolean goBack() {
        if (mDir == null) {
            return false;
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onBackPressed() called with: " + mDir + ", " + sRoot + ", " + mDir.equals(sRoot));
        }
        if (mDir.equals(sRoot)) {
            return false;
        }

        final File parent = mDir.getParentFile();

        if (parent != null) {
            mDir = parent;
            new FileRefresher().execute(mDir);
            return true;
        }

        return false;
    }

    public void setListener(FileListFragment.OnTransferListener listener) {
        mListener = listener;
    }

    public void onRefresh() {
        new FileRefresher().execute(mDir);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_dir_list, null);
        final Context context = view.getContext();
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setEnabled(false);
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new DirListAdapter(new OnPickListener() {
            @Override
            public void onPick(File file) {
                mDir = file;
                onRefresh();
            }
        });
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreateView: DirListAdapter: " + mAdapter);
        recyclerView.setAdapter(mAdapter);
        if (recyclerView instanceof SuperRecyclerView) {
            View emptyView = view.findViewById(R.id.viewEmpty);
            ((SuperRecyclerView) recyclerView).setEmptyView(emptyView);
        }
        builder.setTitle(R.string.select_folder);
        builder.setView(view);
        builder.setPositiveButton(getArguments().getInt(OK_RES_ID), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onTransfer(mDir);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setNeutralButton(R.string.back, null);

        final Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goBack();
                    }
                });
            }
        });

        onRefresh();

        return dialog;
    }

    @Subscribe
    public void onFileAction(final FileActionEvent event) {
        Log.d(TAG, "onFileAction() called with: event = [" + event + "]");
        switch (event.action) {
            case GET_FILE_LIST:
                new FileRefresher().execute(mDir);
                break;
            case OPEN:
                mDir = event.fileList.get(0);
                new FileRefresher().execute(mDir);
                break;
        }
    }

    interface OnPickListener {
        void onPick(File file);
    }

    private class FileRefresher extends AsyncTask<File, Void, File[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected void onPostExecute(File[] files) {
            super.onPostExecute(files);
            if (mAdapter != null) {
                mAdapter.setFiles(files);
            }
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        protected File[] doInBackground(File... params) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground() called with: " + "params = [" + params + "]" + params[0]);
            }

            final File[] arr = params[0].listFiles(Filter.getInstance().getDirFilter());

            if (arr != null) {
                Arrays.sort(arr, Sorter.getInstance().get());
            }
            return arr;
        }
    }

}
