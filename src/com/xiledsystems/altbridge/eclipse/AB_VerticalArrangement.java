package com.xiledsystems.altbridge.eclipse;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AB_VerticalArrangement extends LinearLayout {

	public AB_VerticalArrangement(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
	}
	
	public AB_VerticalArrangement(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);
	}
}