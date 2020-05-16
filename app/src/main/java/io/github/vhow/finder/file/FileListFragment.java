package io.github.vhow.finder.file;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.github.vhow.finder.App;
import com.gmail.dailyefforts.filemanager.BuildConfig;
import io.github.vhow.finder.FileOperator;
import com.gmail.dailyefforts.filemanager.R;
import io.github.vhow.finder.event.CopyApkEvent;
import io.github.vhow.finder.event.OpenDirEvent;
import io.github.vhow.finder.search.SearchActivity;
import io.github.vhow.finder.setting.Settings;
import io.github.vhow.finder.widget.SuperRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.comparator.SizeFileComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = FileListFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static File sRoot;
    private static File mDir;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private FileListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mNavBar;
    private ProgressBar mUsagePb;
    private HorizontalScrollView mNavHSV;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileListFragment() {
    }

    public static FileListFragment newInstance(int columnCount, String root) {
        final FileListFragment fragment = new FileListFragment();
        final Bundle args = new Bundle();
        sRoot = new File(root);
        mDir = sRoot;
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public void remount(String root) {
        sRoot = new File(root);
        mDir = sRoot;
        onRefresh();
        updateTitle();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateTitle();
    }

    private void updateTitle() {
        final Activity activity = getActivity();
        if (activity == null) {
            Log.e(TAG, "updateTitle: activity is null.");
            return;
        }
        if (Environment.getExternalStorageDirectory().equals(sRoot)) {
            activity.setTitle(R.string.phone_storage);
        } else {
            activity.setTitle(R.string.sdcard);
        }
        updateUsage();
    }

    private void updateUsage() {
        final Context context = getActivity();
        if (context == null) {
            Log.e(TAG, "updateUsage: context is null.");
            return;
        }
        if (mUsagePb == null) {
            Log.e(TAG, "updateTitle: mUsagePb is null.");
            return;
        }
        if (sRoot == null) {
            Log.e(TAG, "updateTitle: sRoot is null.");
            return;
        }
//        final String free = Formatter.formatFileSize(context, sRoot.getFreeSpace());
//        final String total = Formatter.formatFileSize(context, sRoot.getTotalSpace());
//        final String usable = Formatter.formatFileSize(context, sRoot.getUsableSpace());
//        Logger.d(TAG, "updateUsage: free " + free);
//        Logger.d(TAG, "updateUsage: total " + total);
//        Logger.d(TAG, "updateUsage: usable " + usable);
//        mNavTextView.setText(getString(R.string.disk_usage, total, free));

        final long totalSpace = sRoot.getTotalSpace();
        final long usedSpace = totalSpace - sRoot.getFreeSpace();
        final double rate = (double) usedSpace / (double) totalSpace;
        final int progress = (int) (rate * 100);
        Log.d(TAG, "updateUsage: totalSpace " + totalSpace);
        Log.d(TAG, "updateUsage: usedSpace " + usedSpace);
        Log.d(TAG, "updateUsage: progress " + progress);
        mUsagePb.setMax(100);
        mUsagePb.setProgress(progress);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.file_action, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_new_folder:
                showNewFolderDialog();
                return true;
            case R.id.action_sort_by_time:
                sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                return true;
            case R.id.action_sort_by_name:
                sort(NameFileComparator.NAME_COMPARATOR);
                return true;
            case R.id.action_sort_by_size:
                sort(SizeFileComparator.SIZE_REVERSE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void tryShare(File file) {
        FileOperator.getInstance().tryShare(getActivity(), file);
    }

    private void sort(Comparator<File> comparator) {
        Sorter.getInstance().set(comparator);
        onRefresh();
    }

    private void tryOpen(final File file) {
        if (file == null) {
            Log.e(TAG, "tryOpen() file is null.");
            return;
        }
        if (file.isDirectory()) {
            mDir = file;
            new FileRefresher().execute(mDir);
            return;
        }
        FileOperator.getInstance().tryOpen(getActivity(), file);
    }

    @Subscribe
    public void onOpenDir(OpenDirEvent event) {
        Log.d(TAG, "onOpenDir() called with: event = [" + event + "]");
        mDir = event.file.getParentFile();
        new FileRefresher().execute(mDir);
    }

    @Subscribe
    public void onCopyApk(CopyApkEvent event) {
        Log.d(TAG, "onCopyApk() called with: event = [" + event + "]");
        final TransferEvent e = new TransferEvent(TransferEvent.Type.COPY_FILE, event.apkFile, event.destFile);
        new Deliver().execute(e);
    }

    @Subscribe
    public void onFileAction(final FileActionEvent event) {
        Log.d(TAG, "onFileAction() called with: event = [" + event + "]");
        switch (event.action) {
            case OPEN: {
                final File file = event.fileList.get(0);
                if (file.isDirectory()) {
                    tryOpen(file);
                }
            }
            break;
            case GET_FILE_LIST:
                new FileRefresher().execute(mDir);
                break;
            case DELETE:
                tryDel(event);
                break;
            case RENAME: {
                final File file = event.fileList.get(0);
                final EditTextFragment et = EditTextFragment.newInstance(R.string.rename, R.string.rename, file.getName());
                et.setOnSubmitListener(new EditTextFragment.OnSubmitListener() {
                    @Override
                    public void onSubmit(String name) {
                        final File dest = new File(file.getParent(), name);
                        mAdapter.rename(file, dest);
                    }
                });
                et.show(getFragmentManager(), "");
            }
            break;
            case SHARE: {
                final File file = event.fileList.get(0);
                tryShare(file);
            }
            break;
            case COPY_APK: {
                final File file = event.fileList.get(0);
                if (file != null && file.exists() && file.canRead()) {
                    final File dest = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);
                    TransferEvent e = new TransferEvent(TransferEvent.Type.COPY_TO_DIR, file, dest);
                    new Deliver().execute(e);
                }
            }
            break;
            case COPY_TO: {
                final File file = event.fileList.get(0);
                final DirPickerDiaFragment fragment = DirPickerDiaFragment.newInstance(R.string.copy, sRoot.getPath());
                fragment.setListener(new OnTransferListener() {
                    @Override
                    public void onTransfer(File dest) {
                        TransferEvent event = new TransferEvent(TransferEvent.Type.COPY_TO_DIR, file, dest);
                        new Deliver().execute(event);
                    }
                });
                fragment.show(getFragmentManager(), "");
            }
            break;
            case MOVE_TO: {
                final File file = event.fileList.get(0);
                final DirPickerDiaFragment fragment = DirPickerDiaFragment.newInstance(R.string.move, sRoot.getPath());
                fragment.setListener(new OnTransferListener() {
                    @Override
                    public void onTransfer(File dest) {
                        TransferEvent event = new TransferEvent(TransferEvent.Type.MOVE_TO_DIR, file, dest);
                        new Deliver().execute(event);
                    }
                });
                fragment.show(getFragmentManager(), "");
            }
            break;
            case INFO: {
                final File file = event.fileList.get(0);
                FileOperator.getInstance().showInfo(getFragmentManager(), file);
            }
            break;
        }
    }

    private void showOptionDialog(File file) {
        FileActionFragment.newInstance(file).show(getFragmentManager(), "");
    }

    private void tryDel(final FileActionEvent event) {
        if (DelConfirmDiaFragment.needTip()) {
            final File file = event.fileList.get(0);
            final CharSequence[] arr = new CharSequence[1];
            arr[0] = file.getName();
            final DelConfirmDiaFragment fragment = DelConfirmDiaFragment.newInstance(arr);
            fragment.setListener(new DelConfirmDiaFragment.DelConfirmDialogListener() {
                @Override
                public void onConfirm() {
                    new FileWorker().execute(event);
                }

                @Override
                public void onCancel() {
                    mAdapter.refresh(file);
                }
            });
            fragment.show(getFragmentManager(), "");
        } else {
            new FileWorker().execute(event);
        }
    }

    @Override
    public void onRefresh() {
        App.getBus().post(new FileActionEvent(FileActionEvent.Action.GET_FILE_LIST));
    }

    public boolean onBackPressed() {
        if (mDir == null) {
            return false;
        }
        Log.d(TAG, "onBackPressed() called with: " + mDir + ", " + sRoot + ", " + mDir.equals(sRoot));
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

    private void setRefresh(final boolean refreshing) {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    private void updateNavi() {
        final Context context = getContext();
        if (context == null) {
            Log.e(TAG, "updateNavi: context is null.");
            return;
        }
        Log.d(TAG, "updateNavi: " + sRoot + ", " + mDir);
        if (mNavBar == null) {
            Log.e(TAG, "updateNavi: mNavBar is null.");
            return;
        }
        final List<File> list = new ArrayList<>();
        File dir = mDir;
        while (!sRoot.equals(dir)) {
            list.add(dir);
            dir = dir.getParentFile();
        }
        mNavBar.removeAllViews();
        list.add(sRoot);
        final LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = list.size() - 1; i >= 0; i--) {
            final FileBarView view = (FileBarView) inflater
                    .inflate(R.layout.view_file_navi_item, mNavBar, false);
            final File file = list.get(i);
            Log.d(TAG, "updateNavi: file: " + file);
            view.setFile(file);
            mNavBar.addView(view);
        }

        if (mNavHSV != null) {
            mNavHSV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mNavBar == null || mNavHSV == null) {
                        return;
                    }
                    final int count = mNavBar.getChildCount();
                    if (count > 0) {
                        final View lastView = mNavBar.getChildAt(count - 1);
                        mNavHSV.smoothScrollTo(lastView.getRight(), 0);
                    }
                }
            }, 100);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        }
        final View view = inflater.inflate(R.layout.fragment_file_list, container, false);
        mUsagePb = view.findViewById(R.id.usagePb);
        mNavHSV = view.findViewById(R.id.navHSV);
        mNavBar = view.findViewById(R.id.fileNavBar);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        final Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "onSwiped() called with: viewHolder = [" + viewHolder + "], direction = [" + direction + "]");
                final int pos = viewHolder.getAdapterPosition();
                final FileActionEvent event = new FileActionEvent(FileActionEvent.Action.DELETE, mAdapter.getFile(pos));
                tryDel(event);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        mAdapter = new FileListAdapter(ActivityCompat.getColor(getContext(), R.color.iconDark));
        mAdapter.setListener(new OnActionListener() {
            @Override
            public void onOpen(File file) {
                tryOpen(file);
            }

            @Override
            public void onShowOption(File file) {
                showOptionDialog(file);
            }
        });

        Log.d(TAG, "onCreateView: FileListAdapter: " + mAdapter);
        recyclerView.setAdapter(mAdapter);

        if (recyclerView instanceof SuperRecyclerView) {
            View emptyView = view.findViewById(R.id.viewEmpty);
            ((SuperRecyclerView) recyclerView).setEmptyView(emptyView);
        }

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showNewFolderDialog();
                gotoSearch();
            }
        });
        new FileRefresher().execute(mDir);
        return view;
    }

    private void gotoSearch() {
        SearchActivity.launch(getActivity());
    }

    private void showNewFolderDialog() {
        final EditTextFragment et = EditTextFragment.newInstance(R.string.create_new_folder, R.string.create, null);
        et.setOnSubmitListener(new EditTextFragment.OnSubmitListener() {
            @Override
            public void onSubmit(String name) {
                File file = new File(mDir, name);
                file.mkdir();
                onRefresh();
            }
        });
        et.show(getFragmentManager(), "");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach() called with: context = [" + context + "]");
        App.getBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDetach() called");
        }
        App.getBus().unregister(this);
    }

    interface OnTransferListener {
        void onTransfer(File dest);
    }

    interface OnActionListener {
        void onOpen(File file);

        void onShowOption(File file);
    }

    private class Deliver extends AsyncTask<TransferEvent, Void, TransferEvent> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRefresh(true);
        }

        @Override
        protected void onPostExecute(TransferEvent event) {
            super.onPostExecute(event);
            if (event.dst.isFile()) {
                mDir = event.dst.getParentFile();
            } else {
                mDir = event.dst;
            }
            setRefresh(false);
            onRefresh();
        }

        @Override
        protected TransferEvent doInBackground(TransferEvent... params) {
            TransferEvent event = params[0];
            try {
                switch (event.type) {
                    case COPY_FILE:
                        FileUtils.copyFile(event.src, event.dst);
                        break;
                    case COPY_TO_DIR:
                        if (event.src.isDirectory()) {
                            FileUtils.copyDirectoryToDirectory(event.src, event.dst);
                        } else {
                            FileUtils.copyFileToDirectory(event.src, event.dst);
                        }
                        break;
                    case MOVE_TO_DIR:
                        if (event.src.isDirectory()) {
                            FileUtils.moveDirectoryToDirectory(event.src, event.dst, false);
                        } else {
                            FileUtils.moveFileToDirectory(event.src, event.dst, false);
                        }
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground: event: " + event);
            }
            return event;
        }
    }

    private class FileRefresher extends AsyncTask<File, Void, File[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRefresh(true);
        }

        @Override
        protected void onPostExecute(File[] files) {
            super.onPostExecute(files);
            if (mAdapter != null) {
                mAdapter.setFiles(files);
            }
            updateNavi();
            setRefresh(false);
        }

        @Override
        protected File[] doInBackground(File... params) {
            boolean showHidden = Settings.getInstance().showHidden(getActivity());
            final File[] arr = params[0].listFiles(Filter.getInstance().get(showHidden));
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground: " + Arrays.toString(arr));
            }

            if (arr != null) {
                Arrays.sort(arr, Sorter.getInstance().get());
            }
            return arr;
        }
    }

    private class FileWorker extends AsyncTask<FileActionEvent, Void, FileActionEvent> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRefresh(true);
        }

        @Override
        protected void onPostExecute(FileActionEvent event) {
            super.onPostExecute(event);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onPostExecute() called with: event = [" + event + "]");
            }
            switch (event.action) {
                case DELETE:
                    if (event.fail) {
                        if (event.fileList != null && event.fileList.size() > 0) {
                            mAdapter.refresh(event.fileList.get(0));
                        }
                        Toast.makeText(getActivity(), R.string.delete_failed, Toast.LENGTH_SHORT).show();
                    } else {
                        mAdapter.remove(event.fileList);
                    }
                    break;
                case GET_FILE_LIST:
                    break;
            }
            setRefresh(false);
        }

        @Override
        protected FileActionEvent doInBackground(FileActionEvent... params) {
            final FileActionEvent event = params[0];
            switch (event.action) {
                case DELETE:
                    for (File file : event.fileList) {
                        try {
                            FileUtils.forceDelete(file);
                        } catch (IOException e) {
                            Log.e(TAG, "doInBackground: del: " + e);
                            event.fail = true;
                        }
                    }
                    break;
                case GET_FILE_LIST:
                    break;
            }
            return event;
        }
    }
}
