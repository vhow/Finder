package io.github.vhow.finder.file;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gmail.dailyefforts.filemanager.R;
import io.github.vhow.finder.setting.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class DirListAdapter extends RecyclerView.Adapter<DirListAdapter.ViewHolder> {

    private static final String TAG = "DirListAdapter";
    private final DirPickerDiaFragment.OnPickListener mListener;
    private List<File> mFileList = new ArrayList<>();

    DirListAdapter(DirPickerDiaFragment.OnPickListener listener) {
        mListener = listener;
    }

    void setFiles(final File[] files) {
        Log.d(TAG, "setFiles() called with: files = [" + Arrays.toString(files) + "]");
        mFileList.clear();
        Collections.addAll(mFileList, files);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dir_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder() called with: " + "holder = [" + holder + "], position = [" + position + "]");
        final File file = mFileList.get(position);
        holder.mNameView.setText(file.getName());
        final Context context = holder.mView.getContext();
        final boolean isDir = file.isDirectory();
        if (isDir) {
            final boolean showHidden = Settings.getInstance().showHidden(context);
            final File[] files = file.listFiles(Filter.getInstance().get(showHidden));
            final int subFileNum = files == null ? 0 : files.length;
            holder.mSizeView.setText(context.getString(R.string.x_files, subFileNum));
            if (subFileNum > 0) {
                holder.mIconView.setImageResource(R.drawable.ic_folder);
            } else {
                holder.mIconView.setImageResource(R.drawable.ic_folder_empty_24dp);
            }
        } else {
            holder.mSizeView.setText(Formatter.formatFileSize(context, file.length()));
            final int resId = FileHelper.getIconResId(file.getName());
            holder.mIconView.setImageResource(resId);
        }

        holder.mTimeView.setText(DateUtils.getRelativeTimeSpanString(file.lastModified()));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPick(file);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mFileList == null) {
            return 0;
        }
        final int size = mFileList.size();
        Log.d(TAG, "getItemCount: len: " + size);
        return size;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mNameView;
        final TextView mSizeView;
        final TextView mTimeView;
        final ImageView mIconView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIconView = view.findViewById(R.id.icon);
            mNameView = view.findViewById(R.id.name);
            mSizeView = view.findViewById(R.id.size);
            mTimeView = view.findViewById(R.id.time);
        }

    }
}
