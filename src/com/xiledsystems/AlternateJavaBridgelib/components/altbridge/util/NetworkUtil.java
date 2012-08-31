package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Static class for detecting the network type.
 * Most of this is from here http://stackoverflow.com/questions/2802472/detect-network-connection-type-on-android
 * 
 * Make sure you have the ACCESS_NETWORK_STATE permissions in your Manifest.
 * 
 * @author Ryan Bis
 *
 */
public class NetworkUtil {
	
	
	
	private static final int NETWORK_TYPE_EHRPD=14; // Added in API 11
	private static final int NETWORK_TYPE_EVDO_B=12; //Added in API 9
	private static final int NETWORK_TYPE_HSPAP=15; // Level 13
    private static final int NETWORK_TYPE_IDEN=11; // Level 8
    private static final int NETWORK_TYPE_LTE=13; // Level 11
    
    public static final String MOBILE_SLOW = "MobileSlow";
    public static final String MOBILE_FAST = "MobileFast";
    public static final String WIFI = "Wifi";
    public static final String LTE = "LTE";
    public static final String UNKNOWN = "Unknown";

	
	private NetworkUtil() {		
	}
	
	/**
	 * Check if there is any connection active.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}
	
	/**
	 * Checks to see what kind of connection the phone has.
	 * This seperates it out to 5 different Strings:
	 * Unknown - A type wasn't able to be identified
	 * Wifi - Wifi
	 * LTE - LTE ( ~ 10+ Mbps )
	 * MobileFast ( ~ 400kbps on up depending on network ) 
	 * MobileSlow ( ~ 14kbps - 100kbps )
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkType(Context context) {
	
		String type = "";
		
		if (isConnected(context)) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			int ntype = info.getType();
			int nsubtype = info.getSubtype();
			if (ntype == ConnectivityManager.TYPE_WIFI) {
				return WIFI;
			} else /*if (ntype == ConnectivityManager.TYPE_MOBILE)*/ {
				switch (nsubtype) {
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					return MOBILE_SLOW;
				case TelephonyManager.NETWORK_TYPE_CDMA:
					return MOBILE_SLOW;
				case TelephonyManager.NETWORK_TYPE_EDGE:
					return MOBILE_SLOW;
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					return MOBILE_FAST;
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					return MOBILE_FAST;
				case TelephonyManager.NETWORK_TYPE_GPRS:
					return MOBILE_SLOW;
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					return MOBILE_FAST;
				case TelephonyManager.NETWORK_TYPE_HSPA:
					return MOBILE_FAST;
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					return MOBILE_FAST;
				case TelephonyManager.NETWORK_TYPE_UMTS:
					return MOBILE_FAST;
				case NetworkUtil.NETWORK_TYPE_EHRPD:
					return MOBILE_FAST;
				case NetworkUtil.NETWORK_TYPE_EVDO_B:
					return MOBILE_FAST;
				case NetworkUtil.NETWORK_TYPE_HSPAP:
					return MOBILE_FAST;
				case NetworkUtil.NETWORK_TYPE_IDEN:
					return MOBILE_FAST;
				case NetworkUtil.NETWORK_TYPE_LTE:
					return LTE;
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
					return UNKNOWN;
				}
			}
		}
		
		return type;
		
	}

}
