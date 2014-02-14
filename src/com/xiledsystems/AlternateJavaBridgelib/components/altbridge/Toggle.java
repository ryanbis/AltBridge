package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;


public class Toggle extends AndroidViewComponent implements OnCheckedChangeListener {

	private ToggleButton view;

	/**
	 * Constructor for Toggle Button component.
	 * 
	 * @param container
	 */
	public Toggle(ComponentContainer container) {
		super(container);
		view = new ToggleButton(container.$context());
		view.setOnCheckedChangeListener(this);
		container.$add(this);
	}

	/**
	 * Constructor for Toggle Button component when using the GLE.
	 * 
	 * @param container
	 * @param resourceId
	 */
	public Toggle(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = (ToggleButton) container.$context().findViewById(resourceId);
		if (view == null) {
			throw new IllegalArgumentException("View with resource Id " + resourceId + " is coming up null!");
		}
		view.setOnCheckedChangeListener(this);
	}

	/**
	 * Set the text that is displayed when the toggler is in the on state.
	 * 
	 * @param text
	 */
	public void TextOn(String text) {
		view.setTextOn(text);
	}

	/**
	 * 
	 * @return The text that is displayed when toggled on
	 */
	public String TextOn() {
		return view.getTextOn().toString();
	}

	/**
	 * Set the text that is displayed when the toggler is toggled off.
	 * 
	 * @param text
	 */
	public void TextOff(String text) {
		view.setTextOff(text);
	}

	/**
	 * 
	 * @return The text displayed when the toggler is off.
	 */
	public String TextOff() {
		return view.getTextOff().toString();
	}

	/**
	 * Set if the toggler is on or off.
	 * 
	 * @param on
	 */
	public void ToggledOn(boolean toggledOn) {
		view.setChecked(toggledOn);
	}

	/**
	 * 
	 * @return whether the toggler is on or off
	 */
	public boolean ToggledOn() {
		return view.isChecked();
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		EventDispatcher.dispatchEvent(this, "Changed", isChecked);
	}

}
