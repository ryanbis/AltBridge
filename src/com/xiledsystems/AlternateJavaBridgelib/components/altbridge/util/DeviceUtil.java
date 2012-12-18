package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class DeviceUtil {
  
  private DeviceUtil() {    
  }
  
  public final static String[] SCREEN_SIZES = { "small", "normal", "large", "xlarge", "unknown" };
  
  public final static String[] DENSITIES = { "low", "medium", "high", "xhigh" };
  
  public static void showScreenSize(Context context) {
    Configuration config = context.getResources().getConfiguration();
    int layout = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    String size = "unknown";
    switch (layout) {
      case Configuration.SCREENLAYOUT_SIZE_SMALL:
        size = SCREEN_SIZES[0];
        break;
      case Configuration.SCREENLAYOUT_SIZE_NORMAL:
        size = SCREEN_SIZES[1];
        break;
      case Configuration.SCREENLAYOUT_SIZE_LARGE:
        size = SCREEN_SIZES[2];
        break;
        // We do 4 here for devices lower than API 9. (4 is the static number of xlarge)
      case 4:
        size = SCREEN_SIZES[3];
        break;
      default:
        size = SCREEN_SIZES[4];
    }
    Toast.makeText(context, "Screen Size: "+size, Toast.LENGTH_LONG).show();
  }
  
  public static void showDensity(Activity context) {
    DisplayMetrics metrics = new DisplayMetrics();
    context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    int density = metrics.densityDpi;
    String dense = "unknown";
    switch (density) {
      case DisplayMetrics.DENSITY_LOW:
        dense = DENSITIES[0];
        break;
      case DisplayMetrics.DENSITY_MEDIUM:
        dense = DENSITIES[1];
        break;
      case DisplayMetrics.DENSITY_HIGH:
        dense = DENSITIES[2];
        break;
        // DisplayMetrics.DENSITY_XHIGH == 320. This is to support APIs less
        // than 9.
      case 320:
        dense = DENSITIES[3];
        break;     
    }
    Toast.makeText(context, "Screen density: "+dense, Toast.LENGTH_LONG).show();
  }

}
