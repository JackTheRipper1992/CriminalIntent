package example.michael.com.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtil {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight != -1 && srcWidth != -1) {
            if (srcHeight > destHeight || srcWidth > destWidth) {
                float widthScale = srcWidth / destWidth;
                float heightScale = srcHeight / destHeight;
                inSampleSize = Math.round(widthScale > heightScale ? widthScale : heightScale);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);

        return getScaledBitmap(path,point.x,point.y);
    }
}
