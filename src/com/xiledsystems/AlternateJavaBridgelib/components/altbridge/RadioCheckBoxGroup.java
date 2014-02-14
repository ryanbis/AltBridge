package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class RadioCheckBoxGroup extends AndroidViewComponent implements OnCheckedChangeListener {

	private RadioGroup view;

	public RadioCheckBoxGroup(ComponentContainer container) {
		super(container);
		view = new RadioGroup(container.$context());
		view.setOnCheckedChangeListener(this);
	}

	public RadioCheckBoxGroup(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = (RadioGroup) container.$context().findViewById(resourceId);
		view.setOnCheckedChangeListener(this);
	}

	/**
	 * 
	 * This method is called when a radio checkbox is changed in the group.
	 * 
	 */
	public void Changed(int id) {
		EventDispatcher.dispatchEvent(this, "Changed", id);
	}

	/**
	 * 
	 * Resets the group so that none are checked.
	 * 
	 */
	public void clearAll() {
		if (view != null) {
			view.clearCheck();
		} else {
			RadioGroup g = (RadioGroup) container.$context().findViewById(resourceId);
			g.clearCheck();
		}
	}

	/**
	 * 
	 * @return True if none of the radio checkbox's in this group are checked.
	 */
	public boolean isClear() {
		int x;
		x = view.getChildCount();
		for (int i = 0; i < x; i++) {
			if (((RadioButton) view.getChildAt(i)).isChecked()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method returns the id of the selected RadioCheckBox in the group
	 * 
	 * @return
	 */
	public int SelectedRadio() {
		return view.getCheckedRadioButtonId();
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Changed(checkedId);
	}
}
