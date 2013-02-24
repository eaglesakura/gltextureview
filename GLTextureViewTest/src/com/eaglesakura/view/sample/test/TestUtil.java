package com.eaglesakura.view.sample.test;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

public class TestUtil {

    public static void sleep(long timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (Exception e) {

        }
    }

    public static void toggleOrientationFixed(Context context) {
        try {
            Activity activity = (Activity) context;
            int ori = context.getResources().getConfiguration().orientation;
            if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } catch (ClassCastException cce) {
            return;
        }
    }
}
