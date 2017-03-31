package com.github.privacystreams.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.github.privacystreams.core.UQI;
import com.github.privacystreams.location.LatLng;
import com.github.privacystreams.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * An abstraction of image data.
 */

public class ImageData {
    private final int type;

    private static final int TYPE_TEMP_FILE = 0;
    private static final int TYPE_LOCAL_FILE = 1;
    private static final int TYPE_REMOTE_FILE = 1;

    private transient File imageFile;
    private transient ExifInterface exifInterface;
    private transient LatLng latLng;
    private transient Bitmap bitmap;
    private transient String filePath;
    private transient ImageData blurredImageData;

    private ImageData(int type) {
        this.type = type;
    }

    static ImageData newTempImage(File tempImageFile) {
        ImageData imageData = new ImageData(TYPE_TEMP_FILE);
        imageData.imageFile = tempImageFile;
        return imageData;
    }

    static ImageData newTempImage(Bitmap tempBitmap) {
        ImageData imageData = new ImageData(TYPE_TEMP_FILE);
        imageData.bitmap = tempBitmap;
        return imageData;
    }

    static ImageData newLocalImage(File localImageFile) {
        ImageData imageData = new ImageData(TYPE_LOCAL_FILE);
        imageData.imageFile = localImageFile;
        return imageData;
    }

    private String getFilepath() {
        if (this.filePath != null) return this.filePath;

        if (this.type == TYPE_LOCAL_FILE || this.type == TYPE_TEMP_FILE)
            this.filePath = this.imageFile.getAbsolutePath();

        return this.filePath;
    }

    ExifInterface getExif() {
        if (this.exifInterface != null) return this.exifInterface;

        String filePath = this.getFilepath();
        if (filePath == null) return null;

        try {
            this.exifInterface = new ExifInterface(filePath);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return this.exifInterface;
    }

    LatLng getLatLng() {
        if (this.latLng != null) return this.latLng;

        ExifInterface exifInterface = this.getExif();
        if (exifInterface == null) return null;
        float[] latLong = new float[2];
        boolean hasLatLong = exifInterface.getLatLong(latLong);
        if(hasLatLong) {
            this.latLng = new LatLng((double) latLong[0], (double) latLong[1]);
        }
        return this.latLng;
    }

    Bitmap getBitmap() {
        if (this.bitmap != null) return this.bitmap;

        String filePath = this.getFilepath();
        if (filePath == null) return null;

        this.bitmap = BitmapFactory.decodeFile(filePath);
        return this.bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    ImageData getBlurred(Context context) {
        if (this.blurredImageData != null) return this.blurredImageData;

        Bitmap bitmap = this.getBitmap();
        if (bitmap == null) return null;
        Bitmap blurredBitmap = ImageUtils.blur(context, bitmap);
        return ImageData.newTempImage(blurredBitmap);
    }

    public String toString() {
        if (this.type == TYPE_TEMP_FILE)
            return String.format(Locale.getDefault(), "<Image@camera%d>", this.hashCode());
        else if (this.type == TYPE_LOCAL_FILE)
            return String.format(Locale.getDefault(), "<Image@local%d>", this.hashCode());
        else
            return String.format(Locale.getDefault(), "<Image@%d>", this.hashCode());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (this.type == TYPE_TEMP_FILE) {
            if (this.imageFile != null && this.imageFile.exists()) {
                this.imageFile.deleteOnExit();
            }
        }
    }
}
