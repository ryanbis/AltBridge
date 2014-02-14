package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;


public class TimeClock extends AndroidViewComponent implements OnChronometerTickListener, OnDestroyListener {

	private final Chronometer view;
	private boolean sendEvent;
	private boolean started;
	private StringBuilder mRecycle = new StringBuilder(8);

	public TimeClock(ComponentContainer container) {
		super(container);
		view = new Chronometer(container.$context());
		view.setOnChronometerTickListener(this);
		container.$add(this);
		container.getRegistrar().registerForOnDestroy(this);
	}

	public TimeClock(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = (Chronometer) container.$context().findViewById(resourceId);
		view.setOnChronometerTickListener(this);
		container.getRegistrar().registerForOnDestroy(this);
	}

	/**
	 * This sets the base time for the time clock.
	 * 
	 * @param time
	 *            the base time in ms
	 */
	public void BaseTime(long time) {
		view.setBase(time);
	}

	/**
	 * This returns the current base time for the time clock.
	 * 
	 * @return the current base time in ms
	 */
	public long BaseTime() {
		return view.getBase();
	}

	/**
	 * This returns the actual text that is displayed in the TimeClock. This is
	 * useful for getting what the current time is in the TimeClock (as when
	 * start() is called, it only updates the display, and doesn't update the
	 * base time).
	 * 
	 * @return String representing the displays time
	 */
	// This should just get removed, actually.
	public String DisplayTime() {
		long time;
		time = SystemClock.elapsedRealtime() - view.getBase();
		return DateUtils.formatElapsedTime(mRecycle, time);
	}

	/**
	 * Starts the time clock. Note that the android Chronometer which is the
	 * base of this component, will tick the time up on the display, but it will
	 * not affect the base time you set.
	 * 
	 */
	public void start() {
		view.start();
		started = true;
	}

	/**
	 * Stops the time clock.
	 */
	public void stop() {
		view.stop();
		started = false;
	}

	/**
	 * Use this to easily reset the TimeClock's display to 0. This is the same
	 * as doing BaseTime(SystemClock.elapsedRealtime())
	 * 
	 */
	public void reset() {
		view.setBase(SystemClock.elapsedRealtime());
	}

	/**
	 * Use this method to set the format of the time displayed.
	 * 
	 * @param format
	 */
	public void Format(String format) {
		view.setFormat(format);
	}

	/**
	 * 
	 * @return the string format of how the time is displayed
	 * 
	 */
	public String Format() {
		return view.getFormat();
	}

	/**
	 * Set this to true if you want the "Tick" event to be thrown upon each tick
	 * of the chronometer. This is off by default.
	 * 
	 * @param enable
	 */
	public void EnableEvent(boolean enable) {
		this.sendEvent = enable;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void onChronometerTick(Chronometer chronometer) {
		if (sendEvent) {
			EventDispatcher.dispatchEvent(this, "Tick");
		}
	}

	@Override
	public void onDestroy() {
		if (started) {
			view.stop();
			started = false;
		}
	}
}
