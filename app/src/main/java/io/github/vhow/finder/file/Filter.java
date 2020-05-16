package io.github.vhow.finder.file;

import android.util.Log;

import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileFilter;

class Filter {
    private static final String TAG = Filter.class.getSimpleName();
    private static Filter ourInstance = new Filter();
    private static FileFilter DIR = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory() && !file.isHidden();
        }
    };


    private Filter() {
    }

    public static Filter getInstance() {
        return ourInstance;
    }

    FileFilter getDirFilter() {
        return DIR;
    }

    FileFilter get(boolean showHidden) {
        Log.d(TAG, "doInBackground: showHidden: " + showHidden);
        if (showHidden) {
            return TrueFileFilter.TRUE;
        }
        return HiddenFileFilter.VISIBLE;
    }
}
