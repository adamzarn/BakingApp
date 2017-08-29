package com.example.android.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by adamzarn on 8/28/17.
 */

public class DeviceUtils {

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static Boolean isLandscape(Context context) {
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return false;
            case Surface.ROTATION_90:
                return true;
            case Surface.ROTATION_180:
                return false;
            case Surface.ROTATION_270:
                return true;
            default:
                return true;
        }
    }

}
