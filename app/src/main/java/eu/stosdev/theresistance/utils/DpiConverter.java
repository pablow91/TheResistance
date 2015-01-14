package eu.stosdev.theresistance.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import javax.inject.Inject;

public class DpiConverter {

    private final DisplayMetrics metrics;

    @Inject
    public DpiConverter(Context context) {
        Resources resources = context.getResources();
        metrics = resources.getDisplayMetrics();
    }

    public float dp(float dp){
        return dp * (metrics.densityDpi / 160f);
    }
}
