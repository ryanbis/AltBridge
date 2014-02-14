package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.util.Log;
import android.view.View;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class AdMobBanner extends AndroidViewComponent implements OnStopListener, OnDestroyListener {

	private final AdView view;
	private AdRequest adreq;
	private boolean stopInOnStop = false;

	/**
	 * 
	 * Constructor for the AdModBanner component. You need to have the AdMob
	 * library (jar file) added to the build path of your project for this to
	 * work. It is NOT included in the AltBridge.
	 * 
	 * @param container
	 *            The container to put the component into
	 * @param dev_id
	 *            Your AdMob developer id for this application
	 */
	public AdMobBanner(ComponentContainer container, String dev_id) {
		super(container);
		view = new AdView(container.$context(), AdSize.BANNER, dev_id);
		adreq = new AdRequest();
		container.$add(this);
	}

	public AdMobBanner(ComponentContainer container, String dev_id, int resourceId) {
		super(container, resourceId);
		view = new AdView(container.$context(), AdSize.BANNER, dev_id);
		adreq = new AdRequest();
		android.widget.LinearLayout layout = (android.widget.LinearLayout) container.$context().findViewById(resourceId);
		layout.addView(view);
	}
	
	public AdMobBanner(ComponentContainer container, String dev_id, int resourceId, boolean gAdView) {
		super(container, resourceId);
		if (gAdView) {
			view = (AdView) container.getRegistrar().findViewById(resourceId);
		} else {
			view = new AdView(container.$context(), AdSize.BANNER, dev_id);
			android.widget.LinearLayout layout = (android.widget.LinearLayout) container.$context().findViewById(resourceId);
			layout.addView(view);
		}
		adreq = new AdRequest();		
	}

	/**
	 * 
	 * Manually stop the ad from displaying. This is automatically run when the
	 * Form loses focus, if UnloadWhenFocusLost is set to true.
	 * 
	 */
	public void stopLoadingAd() {
		try {
			view.stopLoading();
		} catch (RuntimeException e) {
			Log.e("AdMobBanner", "Failed to stop loading ad. Probably because it wasn't loaded.");
		}
	}
	
	public void UnloadWhenFocusLost(boolean unload) {
		stopInOnStop = unload;
	}
	
	public boolean UnloadWhenFocusLost() {
		return stopInOnStop;
	}

	/**
	 * 
	 * Use this method when testing your app to make sure ads are working, and
	 * the app is not yet released in the market. To get your device ID, run the
	 * app without running this method, then check logcat.
	 * 
	 * @param dev_id
	 *            The device id to add as a test device.
	 */
	public void addTestDevice(String dev_id) {
		adreq.addTestDevice(dev_id);
	}

	/**
	 * 
	 * Start to display the ad. This submits an ad request, then displays the
	 * ad. It may take a bit for the ad to show up the first time you try your
	 * app.
	 * 
	 */
	public void startAd() {
		view.loadAd(adreq);
	}

	public void destroy() {
		view.destroy();
	}

	@Override
	public View getView() {
		return view;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		destroy();
	}

	@Override
	public void onStop() {
		if (stopInOnStop && view.isShown()) {
			view.stopLoading();
		}
	}	
}
