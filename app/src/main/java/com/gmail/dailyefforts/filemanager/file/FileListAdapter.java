package com.gmail.dailyefforts.filemanager.file;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.dailyefforts.filemanager.BuildConfig;
import com.gmail.dailyefforts.filemanager.R;
import com.gmail.dailyefforts.filemanager.setting.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    public static final String TAG = FileListAdapter.class.getSimpleName();

    private LruCache<String, Bitmap> mMemoryCache;


    private FileListFragment.OnActionListener mListener;
    private int mIconColor;
    private int mIconSize;
    private List<File> mFileList = new ArrayList<>();

    FileListAdapter(int tintColor) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        mIconColor = tintColor;
    }

    void setListener(FileListFragment.OnActionListener listener) {
        mListener = listener;
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            Log.e(TAG, key + ", " + bitmap);
            return;
        }
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void loadBitmap(File file, ImageView imageView) {
        final String imageKey = file.getAbsolutePath();
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        Log.d(TAG, "loadBitmap() " + file + ", " + imageView + ", " + bitmap);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_image);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(file);
        }
    }

    void setFiles(final File[] files) {
        mFileList.clear();
        if (files == null) {
            return;
        }
        for (File f : files) {
            Log.d(TAG, "setFiles: " + f);
            mFileList.add(f);
        }
        notifyDataSetChanged();
    }

    void rename(File file, File newPath) {
        final int pos = mFileList.indexOf(file);
        Log.d(TAG, "rename() called with: " + "file = [" + file + "], newPath = [" + newPath + "], pos: " + pos);
        if (pos >= 0) {
            final boolean success = file.renameTo(newPath);
            Log.d(TAG, "rename: success: " + success);
            mFileList.set(pos, newPath);
            notifyItemChanged(pos);
        }
    }

    void remove(List<File> list) {
        if (list == null) {
            return;
        }
        for (File file : list) {
            final int pos = mFileList.indexOf(file);
            Log.d(TAG, "remove: " + file + ", " + pos);
            mFileList.remove(file);
            notifyItemRemoved(pos);
        }
    }

    void refresh(File file) {
        final int pos = mFileList.indexOf(file);
        if (pos < 0) {
            return;
        }
        notifyItemChanged(pos);
    }

    File getFile(int pos) {
        if (mFileList == null || mFileList.size() <= pos) {
            return null;
        }
        return mFileList.get(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_file_item, parent, false);
        if (mIconSize == 0) {
            mIconSize = view.getContext().getResources().getDimensionPixelSize(
                    R.dimen.list_item_icon_size);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        final File file = mFileList.get(position);
        final String name = file.getName();
        holder.mNameView.setText(name);
        final Context context = holder.mView.getContext();
        holder.mIconView.setColorFilter(mIconColor);
        final boolean isDir = file.isDirectory();
        if (isDir) {
            boolean showHidden = Settings.getInstance().showHidden(context);
            File[] files = file.listFiles(Filter.getInstance().get(showHidden));
            final int subFileNum = files == null ? 0 : files.length;
            holder.mSizeView.setText(context.getString(R.string.x_files, subFileNum));
            if (subFileNum > 0) {
                holder.mIconView.setImageResource(R.drawable.ic_folder);
            } else {
                holder.mIconView.setImageResource(R.drawable.ic_folder_empty_24dp);
            }
        } else {
            holder.mSizeView.setText(Formatter.formatFileSize(context, file.length()));
            final int resId = FileHelper.getIconResId(name);
            holder.mIconView.setImageResource(resId);
            if (FileHelper.isImage(name)) {
                holder.mIconView.clearColorFilter();
                loadBitmap(file, holder.mIconView);
            } else if (FileHelper.isApk(name)) {
                holder.mIconView.clearColorFilter();
                final PackageManager pm = holder.itemView.getContext().getPackageManager();
                final String pathName = file.getAbsolutePath();
                final PackageInfo info = pm.getPackageArchiveInfo(pathName, 0);
                if (info != null) {
                    final ApplicationInfo appInfo = info.applicationInfo;
                    appInfo.sourceDir = pathName;
                    appInfo.publicSourceDir = pathName;
                    final Drawable drawable = appInfo.loadIcon(pm);
                    if (drawable instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        holder.mIconView.setImageBitmap(bitmap);
                    }
                }
            }
        }

        holder.mTimeView.setText(DateUtils.getRelativeTimeSpanString(file.lastModified()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOpen(file);
            }
        });

        holder.mOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShowOption(file);
                Log.d(TAG, "onClick() called with: " + "v = [" + v + "]");
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

    private class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
        private ImageView mImageView;

        BitmapWorkerTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (BuildConfig.DEBUG) Log.d(TAG, "onPostExecute() " + mImageView + ", " + bitmap);
            if (mImageView != null && bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(File... params) {
            File file = params[0];
            if (file == null) {
                return null;
            }
            final Bitmap bitmap = ImageHelper.getThumbnail(file, mIconSize, mIconSize);
            Log.d(TAG, "doInBackground() " + file);
            addBitmapToMemoryCache(file.getAbsolutePath(), bitmap);
            return bitmap;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mNameView;
        final TextView mSizeView;
        final TextView mTimeView;
        final ImageView mIconView;
        final View mOptionView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIconView = view.findViewById(R.id.icon);
            mNameView = view.findViewById(R.id.name);
            mSizeView = view.findViewById(R.id.size);
            mTimeView = view.findViewById(R.id.time);
            mOptionView = view.findViewById(R.id.ivOption);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
