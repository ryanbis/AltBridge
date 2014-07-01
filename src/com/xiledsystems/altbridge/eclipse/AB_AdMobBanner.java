package com.xiledsystems.altbridge.eclipse;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class AB_AdMobBanner extends LinearLayout {
	
	public AB_AdMobBanner(Context context) {
		super(context);
	}
	
	public AB_AdMobBanner(Context context, AttributeSet attrs) {
		super(context, attrs);		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public AB_AdMobBanner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}	

}
