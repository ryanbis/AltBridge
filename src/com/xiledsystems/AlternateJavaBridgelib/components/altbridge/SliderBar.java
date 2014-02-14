package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class SliderBar extends AndroidViewComponent implements OnSeekBarChangeListener {

	private final SeekBar view;

	/**
	 * 
	 * Non GLE component constructor
	 *
	 * @param container
	 *            The container to put the SliderBar into
	 */
	public SliderBar(ComponentContainer container) {
		super(container);
		view = new SeekBar(container.$context());
		view.setOnSeekBarChangeListener(this);
		container.$add(this);
	}

	/**
	 * 
	 * GLE component constructor.
	 *
	 * @param container
	 *            Use this
	 * @param resourceId
	 *            The resource id of the sliderbar you placed in the GLE
	 */
	public SliderBar(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = (SeekBar) container.getRegistrar().findViewById(resourceId);
		view.setOnSeekBarChangeListener(this);
	}

	/**
	 * 
	 * @return The int of the max position (all the way to the right)
	 */
	public int PositionMax() {
		return view.getMax() + 1;
	}

	/**
	 * 
	 * @param max
	 *            integer representing the max of the bar (default is 100)
	 */
	public void PositionMax(int max) {
		view.setMax(max - 1);
	}

	/**
	 * 
	 * @return the position of the bar
	 */
	public int Position() {
		return view.getProgress() + 1;
	}

	/**
	 * 
	 * @param position
	 *            the position of the bar
	 */
	public void Position(int position) {
		if (position < 1) {
			position = 1;
		}
		view.setProgress(position - 1);
		view.invalidate();
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		EventDispatcher.dispatchEvent(this, Events.POSITION_CHANGED, fromUser, progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

}
