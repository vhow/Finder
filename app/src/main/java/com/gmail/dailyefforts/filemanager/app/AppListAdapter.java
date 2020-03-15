package com.gmail.dailyefforts.filemanager.app;

import android.app.Activity;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.dailyefforts.filemanager.App;
import com.gmail.dailyefforts.filemanager.R;
import com.gmail.dailyefforts.filemanager.event.CopyApkEvent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private static final String TAG = "AppListAdapter";
    private final Activity mActivity;
    private List<AppEntry> mList;

    AppListAdapter(Activity activity) {
        mActivity = activity;
    }

    public void setData(List<AppEntry> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_item, parent, false);
        return new ViewHolder(v, mActivity);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppEntry entry = mList.get(position);
        Log.d(TAG, "onBindViewHolder: " + entry);
        holder.mNameTextView.setText(entry.getLabel());
        holder.mIconImageView.setImageDrawable(entry.getIcon());
        final File apkFile = entry.getApkFile();
        if (apkFile != null && apkFile.exists()) {
            final String formatFileSize = Formatter.formatFileSize(holder.mSizeTextView.getContext(), apkFile.length());
            holder.mSizeTextView.setText(formatFileSize);
            holder.mPkgTextView.setText(entry.packageName);
            holder.apkFile = apkFile;
            final File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            holder.destFile = new File(dir, entry.packageName + ".apk");
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final ImageView mIconImageView;
        final TextView mNameTextView;
        final TextView mPkgTextView;
        final TextView mSizeTextView;
        final WeakReference<Activity> mRef;
        File apkFile;
        File destFile;

        ViewHolder(View itemView, Activity activity) {
            super(itemView);
            mIconImageView = itemView.findViewById(R.id.icon);
            mNameTextView = itemView.findViewById(R.id.name);
            mPkgTextView = itemView.findViewById(R.id.pkg);
            mSizeTextView = itemView.findViewById(R.id.size);
            itemView.setOnCreateContextMenuListener(this);
            mRef = new WeakReference<>(activity);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            final MenuItem copy = menu.add(0, v.getId(), 0, R.string.extract_apk);
            copy.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final Activity activity = mRef == null ? null : mRef.get();
                    if (activity != null) {
                        activity.finishAfterTransition();
                    }
                    App.getBus().post(new CopyApkEvent(apkFile, destFile));
                    return false;
                }
            });
        }
    }
}
