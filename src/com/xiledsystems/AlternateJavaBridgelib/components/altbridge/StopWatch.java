package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;


import android.annotation.SuppressLint;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;


public class StopWatch extends Label implements OnDestroyListener {

	private static final String TAG = "StopWatch";
	private static final int TICK_WHAT = 3;
	private StringBuilder mRecycle = new StringBuilder(8);
	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;
	private Locale mFormatterLocale;
	private Object[] mFormatterArgs = new Object[1];
	private String mFormat;
	private boolean mLogged;
	private boolean mRunning;			//when true, stopwatch is running
	private String displayText;	
    private boolean mStarted;			//Set to true when stopwatch starts, and false when stopped, or paused.
	private long initialTime;
	private long currentTime;
	
	private float displayFontSize;
	private long stopWatchTime;
	private boolean timerStarted;
	private long startTime;
	private long stopTime;
	private long elapsedTime;
	private long timerOffSetValue;
		
	
	/**
	 * Constructor for stopwatch component.
	 * 
	 * @param container
	 */
	public StopWatch(ComponentContainer container) {
		super(container);
		
		container.getRegistrar().registerForOnDestroy(this);
		
		setTime(SystemClock.elapsedRealtime());	
		
		timerStarted = false;	//initialize the timerStarted boolean to false
		startTime = SystemClock.elapsedRealtime();
		elapsedTime = 0;
		timerOffSetValue = 0;
		
	//	displayFontSize = -1;	//Inititalize the dispalyFontSize float
	}
	
	/**
	 * Constructor for stopwatch component when using the GLE.
	 * 
	 * @param container
	 * @param resourceId 
	 */
	public StopWatch(ComponentContainer container, int resourceId) {
		super(container, resourceId);
								
		container.getRegistrar().registerForOnDestroy(this);
		
		setTime(SystemClock.elapsedRealtime());
		
		timerStarted = false;	//initialize the timerStarted boolean to false
		startTime = SystemClock.elapsedRealtime();
		elapsedTime = 0;
		timerOffSetValue = 0;

	//	displayFontSize = -1;	//Inititalize the dispalyFontSize float
	}
	
	/**
	 * Start the stop watch.
	 * 
	 * Note: This will continue to run even if the Form loses focus. It
	 * will only stop if the form is destroyed.
	 * 
	 */
	public void start() {
		mStarted = true;
		startTime = SystemClock.elapsedRealtime();
		updateRunning();
		
		if (timerStarted == false)
			setTime(SystemClock.elapsedRealtime());
		
		
	}
	
	/**
	 * Stop the stop watch
	 */
	public void stop() {
		mStarted = false;
		stopTime = SystemClock.elapsedRealtime();
		
		elapsedTime = elapsedTime + (stopTime - startTime);
		updateRunning();
	}
	
	/**NOTE: RIGHT NOW THIS ALWAYS RESETS TO ZERO, THIS NEEDS TO BE FIXED TO REFLECT BELOW
	 * 
	 * Reset the stopwatch to the initial time. (What you set with
	 * setTime(), or 0 if it wasn't set). This does NOT affect if the
	 * stop watch is started or stopped. It just resets the time (and
	 * updates the display).
	 * 
	 */
	public void reset() {
		currentTime = initialTime = stopWatchTime = elapsedTime = startTime = stopTime = 0;
		timerStarted = false;
		updateText(currentTime);
		
	}
	
	/** This method simply forces an update of the StopWatch display
	 *  This is helpful if you have adjusted the setTime or other items but have not started the watch
	 * 
	 */
	public void updateDisplay() {
		
	}
	
	/**This sets the timerOffSetValue, in case you want to start the timer somewhere other than zero
	 * 
	 * @param offSetValue - value in millisecond to start with
	 */
	public void setTimerOffset(long offSetValue) {
		timerOffSetValue = offSetValue;
		setTime(SystemClock.elapsedRealtime());
	}
	
	/** Getter method for timerOffSetValue
	 * 
	 * @return - long timerOffSetValue
	 */
	public long setTimerOffset() {
		return timerOffSetValue;
	}
	
	/**
	 * Change the format of the display of the time.
	 * Default is MM:SS, or H:MM:SS if none is set.
	 * 
	 * @param format
	 */
	public void Format(String format) {
		mFormat = format;
        if (format != null && mFormatBuilder == null) {
            mFormatBuilder = new StringBuilder(format.length() * 2);
        }
	}
	
	/**
	 * 
	 * @return the format, if one was manually set.
	 */
	public String Format() {
		return mFormat;
	}
	
	/**Checks if the StopWatch is currently running
	 * 
	 * @return - true if the StopWatch is running, false if it is not running
	 */
	public boolean isRunning() {
		return mRunning;
	}
	
	/**This method sets the font size of the display text
	 * 
	 */
	public void setFontSize(float size) {
		displayFontSize = size;
		//updateText(get)
	}
	
	/** This method returns the font size of the display text
	 * 
	 */
	public float getFontSize() {
		return displayFontSize;
	}
	
	private synchronized void updateText(long now) {
		now = now + timerOffSetValue;
		
//		long seconds = now - initialTime;
		long seconds = now - startTime + elapsedTime;
		currentTime = now - startTime + elapsedTime;				
//		currentTime = now - initialTime;				
  //      seconds /= 1000;
        String text = DateUtils.formatElapsedTime(mRecycle, seconds);

        if (mFormat != null) {
            Locale loc = Locale.getDefault();
            if (mFormatter == null || !loc.equals(mFormatterLocale)) {
                mFormatterLocale = loc;
                mFormatter = new Formatter(mFormatBuilder, loc);
            }
            mFormatBuilder.setLength(0);
            mFormatterArgs[0] = text;
            try {
                mFormatter.format(mFormat, mFormatterArgs);
                text = mFormatBuilder.toString();
            } catch (IllegalFormatException ex) {
                if (!mLogged) {
                    Log.w(TAG, "Illegal format string: " + mFormat);
                    mLogged = true;
                }
            }
        }
        Text(text);
        
        if (timerStarted == false) {
        	Text(DateUtils.formatElapsedTime(mRecycle, timerOffSetValue));
        }
        
        //Check if a font size was set, if it was, use it
        if (displayFontSize != 0)
        	FontSize(displayFontSize);
        
        displayText = text;
	}
	
	/**
	 * 
	 * @return The text displayed by the stop watch, including formatting.
	 */
	public String getDisplayTime() {
		return displayText;
	}
	
	/**
	 * This allows you to get the exact current time of the stop watch,
	 * in case you want to format it differently.
	 * 
	 * @param format
	 * @return the formatted time
	 */
	public String getCurrentTime(String format) {
		//Format(format);
		long seconds = currentTime /1000;
		String text = DateUtils.formatElapsedTime(mRecycle, seconds);

        if (format != null) {
            Locale loc = Locale.getDefault();
            if (mFormatter == null || !loc.equals(mFormatterLocale)) {
                mFormatterLocale = loc;
                mFormatter = new Formatter(mFormatBuilder, loc);
            }
            mFormatBuilder.setLength(0);
            mFormatterArgs[0] = text;
            try {
                mFormatter.format(format, mFormatterArgs);
                text = mFormatBuilder.toString();
            } catch (IllegalFormatException ex) {
                if (!mLogged) {
                    Log.w(TAG, "Illegal format string: " + format);
                    mLogged = true;
                }
            }
        }
        return text;
	}
	
	/**
	 * Set the initial time of the stop watch.
	 * Default is 0.
	 * 
	 * @param time
	 */
	public void setTime(long time) {
		initialTime = time;
		currentTime = time;		
		updateText(time);
		
		timerStarted = true;
	}
	
	private void updateRunning() {
        boolean running = Visible() && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());                
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 1000);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }	
		
	@Override
	public void onDestroy() {
		super.onDestroy();
		Visible(false);
		updateRunning();
//		mHandler = null;
	}	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message m) {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());                
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), 1000);
            }
        }
	};	
	
}
