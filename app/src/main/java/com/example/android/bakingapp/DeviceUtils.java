package com.example.android.bakingapp;

import android.content.Context;

/**
 * Created by adamzarn on 8/28/17.
 */

public class DeviceUtils {

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.is_tablet);
    }

    public static Boolean isLandscape(Context context) {
        return context.getResources().getBoolean(R.bool.is_landscape);
    }

}
