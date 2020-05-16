package io.github.vhow.finder.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images;
import android.util.Log;

import java.io.File;

class ImageHelper {
    private static final String TAG = "ImageHelper";

    static Bitmap getThumbnail(File file, int reqWidth, int reqHeight) {
        final String pathName = file.getAbsolutePath().toLowerCase();
        if (pathName.endsWith(".mp4") || pathName.endsWith(".flv")) {
            return getVideoThumbnail(file);
        }
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        final Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
        if (bitmap == null) {
            Log.e(TAG, "getThumbnail: bitmap is null.");
            return null;
        }
        return ThumbnailUtils.extractThumbnail(bitmap, reqWidth, reqHeight);
    }

    private static Bitmap getVideoThumbnail(File file) {
        final String filePath = file.getAbsolutePath();
        return ThumbnailUtils.createVideoThumbnail(filePath,
                Images.Thumbnails.MICRO_KIND);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
