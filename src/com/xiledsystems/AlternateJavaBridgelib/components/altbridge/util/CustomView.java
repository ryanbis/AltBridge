package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.view.View;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.AndroidViewComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.ComponentContainer;


public class CustomView extends AndroidViewComponent {
	
	private final View view;
	
	public CustomView(ComponentContainer container, View view) {
		super(container);
		this.view = view;
		container.$add(this);		
	}

	@Override
	public View getView() {
		return view;
	}

}
