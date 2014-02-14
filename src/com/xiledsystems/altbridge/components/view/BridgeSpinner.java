package com.xiledsystems.altbridge.components.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;


public class BridgeSpinner extends Spinner {

	private OnItemSelectedListener listener;
	
	public BridgeSpinner(Context context) {
		super(context);
	}

	public BridgeSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BridgeSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void setSelection(int position) {
		super.setSelection(position);
		if (listener != null) {
			listener.onItemSelected(null, null, position, 0);
		}
	}
	
	public void setOnItemSelectedAlwaysListener(OnItemSelectedListener listener) {
		this.listener = listener;
	}
	
}
