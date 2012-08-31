package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.Context;
import android.content.SyncResult;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

/**
 * Sensor that can measure absolute orientation in 3 dimensions.
 *
 * TODO(user): This implementation does not correct for acceleration
 * of the phone.  Make a better version that does this.
 */

public class CopyOfOrientationSensor extends AndroidNonvisibleComponent
    implements SensorEventListener, Deleteable, OnResumeListener {
  private final SensorManager sensorManager;
  private Sensor orientationSensor;
  //private Sensor accelSensor;
  private Sensor magneticSensor;
  
  private boolean enabled;
  private float yaw;
  private float pitch;
  private float roll;
  private int accuracy;
  
  private float[] mR = new float[9];
  private float[] mRemappedR = new float[9];
  private float[] mOrientationVector = new float[3];
  private float[] mAccelVector = new float[3];
  private float[] mMagneticVector = new float[3];
  
  private boolean accSet;
  private boolean magSet;
  
  private int rotateDegrees;
  
    
  private boolean remapCoordinates;

  /**
   * Creates a new OrientationSensor component.
   *
   * @param container  ignored (because this is a non-visible component)
   */
  public CopyOfOrientationSensor(ComponentContainer container) {
    super(container.$form());
    sensorManager =
      (SensorManager) container.$context().getSystemService(Context.SENSOR_SERVICE);
    orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    //magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);    
    sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
    //accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    //sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
    //sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
    enabled = true;
    container.$form().registerForOnResume(this);
  }

  // Events

  /**
   * Default OrientationChanged event handler.
   *
   * <p>This event is signalled when the device's orientation has changed.  It
   * reports the new values of yaw, pich, and roll, and it also sets the Yaw, Pitch,
   * and roll properties.</p>
   * <p>Yaw is the compass heading in degrees, pitch indicates how the device
   * is tilted from top to bottom, and roll indicates how much the device is tilted from
   * side to side.</p>
   */
  
  public void OrientationChanged(float yaw, float pitch, float roll) {
    EventDispatcher.dispatchEvent(this, Events.ORIENTATION_CHANGED, yaw, pitch, roll);
  }

  // Properties

  /**
   * Available property getter method (read-only property).
   *
   * @return {@code true} indicates that an orientation sensor is available,
   *         {@code false} that it isn't
   */
  
  public boolean Available() {
    //List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
	  List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
    return (sensors.size() > 0);
  }

  /**
   * Enabled property getter method.
   *
   * @return {@code true} indicates that the sensor generates events,
   *         {@code false} that it doesn't
   */
  
  public boolean Enabled() {
    return enabled;
  }

  /**
   * Enabled property setter method.
   *
   * @param enabled  {@code true} enables sensor event generation,
   *                 {@code false} disables it
   */
  
  public void Enabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Pitch property getter method (read-only property).
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return  current pitch
   */
  
  public float Pitch() {
    return pitch;
  }

  /**
   * Roll property getter method (read-only property).
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return  current roll
   */
  
  public float Roll() {
    return roll;
  }

  /**
   * Yaw property getter method (read-only property).
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return  current yaw
   */
 
  public float Yaw() {
    return yaw;
  }

  /**
   * Angle property getter method (read-only property).  Specifically, this
   * provides the angle in which the orientation sensor is tilted, treating
   * {@link #Roll()} as the x-coordinate and {@link #Pitch()} as the
   * y-coordinate.  For the amount of the tilt, use {@link #Magnitude()}.
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return the angle in degrees
   */
  
  public float Angle() {
    return (float) (180.0 - Math.toDegrees(Math.atan2(pitch, roll)));
  }

  /**
   * Magnitude property getter method (read-only property).  Specifically, this
   * returns a number between 0 and 1, indicating how much the device
   * is tilted.  For the angle of tilt, use {@link #Angle()}.
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return the magnitude of the tilt, from 0 to 1
   */
  
  public float Magnitude() {
    // Limit pitch and roll to 90; otherwise, the phone is upside down.
    // The official documentation falsely claims that the range of pitch and
    // roll is [-90, 90].  If the device is upside-down, it can range from
    // -180 to 180.  We restrict it to the range [-90, 90].
    // With that restriction, if the pitch and roll angles are P and R, then
    // the force is given by 1 - cos(P)cos(R).  I have found a truly wonderful
    // proof of this theorem, but the margin enforced by Lint is too small to
    // contain it.
    final int MAX_VALUE = 90;
    double npitch = Math.toRadians(Math.min(MAX_VALUE, Math.abs(pitch)));
    double nroll = Math.toRadians(Math.min(MAX_VALUE, Math.abs(roll)));
    return (float) (1.0 - Math.cos(npitch) * Math.cos(nroll));
  }
  
  /**
   * This will remap the orientation sensor's yaw value +/- 90 degrees
   * if the device's orientation is 90 or 270 degrees from it's natural
   * orientation. On phones, when in landscape, the yaw values will be
   * modified. On tablets, the yaw values will be modified when in
   * portrait mode.
   * 
   * @param remap
   */
  public void remapCoordinates(boolean remap) {
	  remapCoordinates = remap;
	  checkCoordinates();
  }
  
  private int getRotation() {
	  return container.$context().getWindowManager().getDefaultDisplay().getRotation();
  }

  // SensorListener implementation

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
//    Log.d("OrientationSensor", "SensorEvent: " + sensorEvent.sensor.getName() + ":" + sensorEvent.toString());
	  synchronized (this) {
		  if (enabled) {
			  if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			 // if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		    		final float[] values = sensorEvent.values;    		
		    		yaw = values[0];
		    		if (remapCoordinates) {
		    			yaw += rotateDegrees;
		    			// Adjust for overage/underage issues, ie we don't go
		    			// above 360 degrees, or less than 0.    			
		    			if (yaw > 360) {
		    				yaw -= 360;
		    			}
		    			if (yaw < 0) {
		    				yaw += 360;
		    			}
		    			
			    	}
		    		pitch = values[1];
		    		roll = values[2];
		    		accuracy = sensorEvent.accuracy;
		    		OrientationChanged(yaw, pitch, roll);
		    	} /*else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
		    		mMagneticVector = sensorEvent.values;
		    		magSet = true;		    		
		    	}
			  if (accSet && magSet) {
				SensorManager.getRotationMatrix(mR, null, mAccelVector, mMagneticVector);
				SensorManager.remapCoordinateSystem(mR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRemappedR);
				SensorManager.getOrientation(mR, mOrientationVector);
		  		yaw = mOrientationVector[0];
		  		pitch = mOrientationVector[1];
		  		roll = mOrientationVector[2];
		  		OrientationChanged(yaw, pitch, roll);
			  }*/
			  
		    }
	}
    
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // TODO(user): Figure out if we actually need to do something here.
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    sensorManager.unregisterListener(this);    
  }
  
  private void checkCoordinates() {
	  if (remapCoordinates) {
			
			int rotation = getRotation();
		
			if (rotation == Surface.ROTATION_90) {
				rotateDegrees = 90;
			} else if (rotation == Surface.ROTATION_270) {
				rotateDegrees = -90;
			}
			
	  }
  }
 

  @Override
  public void onResume() {
	checkCoordinates();	
  }
}
