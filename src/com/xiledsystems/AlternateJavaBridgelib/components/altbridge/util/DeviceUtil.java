package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class DeviceUtil {

	private DeviceUtil() {
	}

	public final static String[] SCREEN_SIZES = { "small", "normal", "large", "xlarge", "unknown" };

	public final static String[] DENSITIES = { "low", "medium", "high", "xhigh", "xxhigh" };

	/**
	 * Displays a toast message stating the screen size. One of five options:
	 * small, normal, large, xlarge, and unknown
	 * 
	 * @param context
	 */
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
			// We do 4 here for devices lower than API 9. (4 is the static
			// number of xlarge)
			case 4:
				size = SCREEN_SIZES[3];
				break;
			default:
				size = SCREEN_SIZES[4];
		}
		Toast.makeText(context, "Screen Size: " + size, Toast.LENGTH_LONG).show();
	}

	/**
	 * 
	 * @param context
	 * @return a String stating the screen size (small, normal, large, xlarge, unknown)
	 */
	public static String getScreenSize(Context context) {
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
			// We do 4 here for devices lower than API 9. (4 is the static
			// number of xlarge)
			case 4:
				size = SCREEN_SIZES[3];
				break;
			default:
				size = SCREEN_SIZES[4];
		}
		return size;
	}

	/**
	 * Displays a toast message stating the density of this device. One of
	 * four options:
	 * low, medium, high, xhigh
	 * 
	 * @param context
	 */
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
			// DisplayMetrics.DENSITY_XXHIGH == 480. This is to support APIs less than
			// 16				
			case 480:
				dense = DENSITIES[4];
				break;
		}
		Toast.makeText(context, "Screen density: " + dense, Toast.LENGTH_LONG).show();
	}

	/**
	 * 
	 * @param context
	 * @return a String stating the density (low, medium, high, xhigh)
	 */
	public static String getDensity(Activity context) {
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
		return dense;
	}
	
	/**
	 * Use this method to check if the device supports OpenGL ES 2.0.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean supportsOpenGLES20(Context context) {
		final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo info = manager.getDeviceConfigurationInfo();
		return info.reqGlEsVersion >= 0x20000;
	}
	
	/**
	 * Checks to see if a FormService is running. This will only check the current app's FormServices.
	 * 
	 * @param context
	 * @param className The name of the service you want to check.
	 * @return
	 */
	public static boolean isFormServiceRunning(Context context, String className) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String serviceFullName = context.getPackageName() + className;
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceFullName.equalsIgnoreCase(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks to see if a Form is running. This only checks to see if the Form is the "top Activity".
	 * @param context
	 * @param className The name of the Form to check
	 * @return
	 */
	public static boolean isFormRunning(Context context, String className) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		StringBuilder b = new StringBuilder();
		b.append("ComponentInfo{");
		b.append(context.getPackageName());
		b.append("/");
		b.append(context.getPackageName());
		b.append(".");
		b.append(className);
		b.append("}");
		String actName = b.toString();
		List<RunningTaskInfo> activities = manager.getRunningTasks(Integer.MAX_VALUE);		
		int size = activities.size();
		for (int i = 0; i < size; i++) {
			if (activities.get(i).topActivity.toString().equalsIgnoreCase(actName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the device is plugged in to AC, or USB. On devices with API 17 or higher,
	 * this will return true if the device is being charging wirelessly as well.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isPluggedIn(Context context) {
		Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		// 4 = Wireless charging added in API 17
		return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == 4;
	}

}
