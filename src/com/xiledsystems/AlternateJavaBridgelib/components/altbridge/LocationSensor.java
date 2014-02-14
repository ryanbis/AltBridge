package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;
import com.xiledsystems.altbridge.BuildConfig;

/**
 * Sensor that can provide information on longitude, latitude, and altitude.
 * 
 */

public class LocationSensor extends AndroidNonvisibleComponent implements Component, OnStopListener, OnResumeListener, OnDestroySvcListener,
		OnStartCommandListener, Deleteable {

	/**
	 * Class that listens for changes in location, raises appropriate events, and provides properties.
	 * 
	 */
	private class MyLocationListener implements LocationListener {
		@Override
		// This sets fields longitude, latitude, altitude, hasLocationData, and
		// hasAltitude, then calls LocationSensor.LocationChanged(), alll in the
		// enclosing class LocationSensor.
		public void onLocationChanged(Location location) {
			lastLocation = location;
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			// If the current location doesn't have altitude information, the prior
			// altitude reading is retained.
			if (location.hasAltitude()) {
				hasAltitude = true;
				altitude = location.getAltitude();
			}
			hasLocationData = true;
			LocationChanged(latitude, longitude, altitude);
		}

		@Override
		public void onProviderDisabled(String provider) {
			StatusChanged(provider, "Disabled");
			stopListening();
			if (enabled) {
				RefreshProvider();
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			StatusChanged(provider, "Enabled");
			RefreshProvider();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// Ignore TEMPORARILY_UNAVAILABLE, because service usually returns quickly.
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					StatusChanged(provider, "TEMPORARILY_UNAVAILABLE");
					break;
				case LocationProvider.OUT_OF_SERVICE:
					// If the provider we were listening to is no longer available,
					// find another.
					StatusChanged(provider, "OUT_OF_SERVICE");

					if (provider.equals(providerName)) {
						stopListening();
						RefreshProvider();
					}
					break;
				case LocationProvider.AVAILABLE:
					// If another provider becomes available and is one we hadn't known
					// about see if it is better than the one we're currently using.
					StatusChanged(provider, "AVAILABLE");
					if (!provider.equals(providerName) && !allProviders.contains(provider)) {
						RefreshProvider();
					}
					break;
			}
		}
	}

	/**
	 * Constant returned by {@link #Longitude()}, {@link #Latitude()}, and {@link #Altitude()} if no value could be
	 * obtained for them. The client can find this out directly by calling {@link #HasLongitudeLatitude()} or
	 * {@link #HasAltitude()}.
	 */
	public static final int UNKNOWN_VALUE = 0;

	/**
	 * Minimum time in milliseconds between location checks. The documentation for
	 * {@link android.location.LocationManager#requestLocationUpdates} does not recommend using a location lower than
	 * 60,000 (60 seconds) because of power consumption.
	 */

	private long MIN_TIME_INTERVAL = 60000;

	public long getMinTimeInterval() {
		return MIN_TIME_INTERVAL;
	}

	// BE MINDFUL WHEN CHANGING THIS!!! Look at the comments above from the app inventor source. ONLY change this
	// if you REALLY need to.
	/**
	 * Minimum time in milliseconds between location checks. The documentation for
	 * {@link android.location.LocationManager#requestLocationUpdates} does not recommend using a location lower than
	 * 60,000 (60 seconds) because of power consumption.
	 */
	public void setMinTimeInterval(long mIN_TIME_INTERVAL) {
		MIN_TIME_INTERVAL = mIN_TIME_INTERVAL;
	}

	/**
	 * Minimum distance in meters to be reported
	 */
	private long MIN_DISTANCE_INTERVAL = 5; // 5 meters

	public void setMinDistanceInterval(long distanceInMeters) {
		MIN_DISTANCE_INTERVAL = distanceInMeters;
	}

	public long getMinDistanceInterval() {
		return MIN_DISTANCE_INTERVAL;
	}

	// These variables contain information related to the LocationProvider.
	private final Criteria locationCriteria;
	private final LocationManager locationManager;

	private boolean providerLocked = false; // if true we can't change providerName
	private String providerName;
	// Invariant: providerLocked => providerName is non-empty

	private MyLocationListener myLocationListener;

	private LocationProvider locationProvider;
	private boolean listening = false;
	// Invariant: listening <=> a myLocationListener is registered with locationManager
	// Invariant: !listening <=> locationProvider == null

	// This holds all the providers available when we last chose providerName.
	// The reported best provider is first, possibly duplicated.
	private List<String> allProviders;

	// These location-related values are set in MyLocationListener.onLocationChanged().
	private Location lastLocation;
	private double longitude = UNKNOWN_VALUE;
	private double latitude = UNKNOWN_VALUE;
	private double altitude = UNKNOWN_VALUE;
	private boolean hasLocationData = false;
	private boolean hasAltitude = false;

	// This is used in reverse geocoding.
	private Geocoder geocoder;

	// User-settable properties
	private boolean enabled = true; // the default value is true

	/**
	 * Creates a new LocationSensor component.
	 * 
	 * @param container
	 *            ignored (because this is a non-visible component)
	 */
	public LocationSensor(ComponentContainer container) {
		super(container);
		// Set up listener
		container.getRegistrar().registerForOnResume(this);
		container.getRegistrar().registerForOnStop(this);

		// Initialize location-related fields
		Context context = container.$context();
		geocoder = new Geocoder(context);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationCriteria = new Criteria();
		locationCriteria.setSpeedRequired(true);
		myLocationListener = new MyLocationListener();
	}

	public LocationSensor(SvcComponentContainer container) {
		super(container);
		// Set up listener
		sContainer.$formService().registerForOnDestroy(this);
		sContainer.$formService().registerForOnStartCommand(this);
		// Initialize location-related fields
		Context context = container.$context();
		geocoder = new Geocoder(context);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationCriteria = new Criteria();
		locationCriteria.setSpeedRequired(true);
		myLocationListener = new MyLocationListener();
	}

	// Events

	/**
	 * Indicates that a new location has been detected.
	 */

	public void LocationChanged(double latitude, double longitude, double altitude) {
		if (enabled) {
			EventDispatcher.dispatchEvent(this, "LocationChanged", latitude, longitude, altitude);
		}
	}

	/**
	 * Indicates that the status of the provider has changed.
	 */

	public void StatusChanged(String provider, String status) {
		if (enabled) {
			EventDispatcher.dispatchEvent(this, "StatusChanged", provider, status);
		}
	}

	// Properties

	/**
	 * Indicates the source of the location information. If there is no provider, the string "NO PROVIDER" is returned.
	 * This is useful primarily for debugging.
	 */

	public String ProviderName() {
		if (providerName == null) {
			return "NO PROVIDER";
		} else {
			return providerName;
		}
	}

	/**
	 * Change the location provider. If the blocks program changes the name, try to change the provider. Whatever
	 * happens now, the provider and the reported name may be switched to Android's preferred provider later. This is
	 * primarily for debugging.
	 */

	public void ProviderName(String providerName) {
		this.providerName = providerName;
		if (!empty(providerName) && startProvider(providerName)) {
			return;
		} else {
			RefreshProvider();
		}
	}

	public boolean ProviderLocked() {
		return providerLocked;
	}

	/**
	 * Indicates whether the sensor should listen for location changes and raise the corresponding events.
	 */

	public void ProviderLocked(boolean lock) {
		providerLocked = lock;
	}

	/**
	 * Indicates whether longitude and latitude information is available. (It is always the case that either both or
	 * neither are.)
	 */

	public boolean HasLongitudeLatitude() {
		return hasLocationData && enabled;
	}

	/**
	 * Indicates whether altitude information is available.
	 */

	public boolean HasAltitude() {
		return hasAltitude && enabled;
	}

	/**
	 * Indicates whether information about location accuracy is available.
	 */

	public boolean HasAccuracy() {
		return Accuracy() != UNKNOWN_VALUE && enabled;
	}

	/**
	 * The most recent available longitude value. If no value is available, 0 will be returned.
	 */

	public double Longitude() {
		return longitude;
	}

	/**
	 * The most recently available latitude value. If no value is available, 0 will be returned.
	 */

	public double Latitude() {
		return latitude;
	}

	/**
	 * The most recently available altitude value, in meters. If no value is available, 0 will be returned.
	 */

	public double Altitude() {
		return altitude;
	}

	/**
	 * The most recent measure of accuracy, in meters. If no value is available, 0 will be returned.
	 */

	public double Accuracy() {
		if (lastLocation != null && lastLocation.hasAccuracy()) {
			return lastLocation.getAccuracy();
		} else if (locationProvider != null) {
			return locationProvider.getAccuracy();
		} else {
			return UNKNOWN_VALUE;
		}
	}

	public float Speed() {
		if (lastLocation != null && lastLocation.hasSpeed()) {
			return lastLocation.getSpeed();
		} else {
			return 0;
		}
	}

	/**
	 * Indicates whether the user has specified that the sensor should listen for location changes and raise the
	 * corresponding events.
	 */

	public boolean Enabled() {
		return enabled;
	}

	/**
	 * Indicates whether the sensor should listen for location chagnes and raise the corresponding events.
	 */

	public void Enabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) {
			stopListening();
		} else {
			RefreshProvider();
		}
	}

	/**
	 * Provides a textual representation of the current address or "No address available".
	 */

	public String CurrentAddress() {
		if (hasLocationData && latitude <= 90 && latitude >= -90 && longitude <= 180 || longitude >= -180) {
			try {
				List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
				if (addresses != null && addresses.size() == 1) {
					Address address = addresses.get(0);
					if (address != null) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
							sb.append(address.getAddressLine(i));
							sb.append("\n");
						}
						return sb.toString();
					}
				}
			} catch (IOException e) {
				Log.e("LocationSensor", "Exception thrown by getFromLocation() " + e.getMessage());
			}
		}
		return "No address available";
	}

	/**
	 * Derives Latitude from Address
	 * 
	 * @param locationName
	 *            human-readable address
	 * 
	 * @return latitude in degrees, 0 if not found.
	 */

	public double LatitudeFromAddress(String locationName) {
		try {
			List<Address> addressObjs = geocoder.getFromLocationName(locationName, 1);
			if (addressObjs == null) {
				throw new IOException("");
			}
			return addressObjs.get(0).getLatitude();
		} catch (IOException e) {
			if (container == null) {
				sContainer.$formService().dispatchErrorOccurredEvent(this, "LatitudeFromAddress",
						ErrorMessages.ERROR_LOCATION_SENSOR_LATITUDE_NOT_FOUND, locationName);
			} else {
				container.getRegistrar().dispatchErrorOccurredEvent(this, "LatitudeFromAddress",
						ErrorMessages.ERROR_LOCATION_SENSOR_LATITUDE_NOT_FOUND, locationName);
			}
			return 0;
		}
	}

	/**
	 * Derives Longitude from Address
	 * 
	 * @param locationName
	 *            human-readable address
	 * 
	 * @return longitude in degrees, 0 if not found.
	 */

	public double LongitudeFromAddress(String locationName) {
		try {
			List<Address> addressObjs = geocoder.getFromLocationName(locationName, 1);
			if (addressObjs == null) {
				throw new IOException("");
			}
			return addressObjs.get(0).getLongitude();
		} catch (IOException e) {
			if (container == null) {
				sContainer.$formService().dispatchErrorOccurredEvent(this, "LongitudeFromAddress",
						ErrorMessages.ERROR_LOCATION_SENSOR_LONGITUDE_NOT_FOUND, locationName);
			} else {
				container.getRegistrar().dispatchErrorOccurredEvent(this, "LongitudeFromAddress",
						ErrorMessages.ERROR_LOCATION_SENSOR_LONGITUDE_NOT_FOUND, locationName);
			}
			return 0;
		}
	}

	public List<String> AvailableProviders() {
		return allProviders;
	}

	// Methods to stop and start listening to LocationProviders

	/**
	 * Refresh provider attempts to choose and start the best provider unless someone has set and locked the provider.
	 * Currently, blocks programmers cannot do that because the relevant methods are not declared as properties.
	 * 
	 */

	// @SimpleFunction(description = "Find and start listening to a location provider.")
	public void RefreshProvider() {
		stopListening(); // In case another provider is active.
		if (providerLocked && !empty(providerName)) {
			listening = startProvider(providerName);
			return;
		}
		allProviders = locationManager.getProviders(true); // Typically it's ("network" "gps")
		String bProviderName = locationManager.getBestProvider(locationCriteria, true);
		if (bProviderName != null && !bProviderName.equals(allProviders.get(0))) {
			allProviders.add(0, bProviderName);
		}
		// We'll now try the best first and stop as soon as one successfully starts.
		for (String providerN : allProviders) {
			listening = startProvider(providerN);
			if (listening) {
				if (!providerLocked) {
					providerName = providerN;
				}
				return;
			}
		}
	}

	/*
	 * Start listening to ProviderName. Return true iff successful.
	 */
	private boolean startProvider(String providerName) {
		this.providerName = providerName;
		LocationProvider tLocationProvider = locationManager.getProvider(providerName);
		if (tLocationProvider == null) {
			if (BuildConfig.DEBUG) {
				Log.d("LocationSensor", "getProvider(" + providerName + ") returned null");
			}
			return false;
		}
		stopListening();
		locationProvider = tLocationProvider;
		locationManager.requestLocationUpdates(providerName, MIN_TIME_INTERVAL, MIN_DISTANCE_INTERVAL, myLocationListener);
		listening = true;
		return true;
	}

	/**
	 * This unregisters {@link #myLocationListener} as a listener to location updates. It is safe to call this even if
	 * no listener had been registered, in which case it has no effect. This also sets the value of
	 * {@link #locationProvider} to {@code null} and sets {@link #listening} to {@code false}.
	 */
	private void stopListening() {
		if (listening) {
			locationManager.removeUpdates(myLocationListener);
			locationProvider = null;
			listening = false;
		}
	}

	// OnResumeListener implementation

	@Override
	public void onResume() {
		if (enabled) {
			RefreshProvider();
		}
	}

	// OnStopListener implementation

	@Override
	public void onStop() {
		stopListening();
	}

	// Deleteable implementation

	@Override
	public void onDelete() {
		stopListening();
	}

	private boolean empty(String s) {
		return s == null || s.length() == 0;
	}

	@Override
	public void onDestroy() {
		stopListening();
	}

	@Override
	public void onStartCommand() {
		if (enabled) {
			RefreshProvider();
		}
	}
}
