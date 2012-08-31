package com.xiledsystems.altbridge.eclipse;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AB_HorizontalArrangement extends LinearLayout {

	public AB_HorizontalArrangement(Context context) {
		super(context);
		setOrientation(LinearLayout.HORIZONTAL);
	}
	
	public AB_HorizontalArrangement(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
	}
}