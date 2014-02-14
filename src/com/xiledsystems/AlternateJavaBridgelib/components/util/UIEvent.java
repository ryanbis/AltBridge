package com.xiledsystems.AlternateJavaBridgelib.components.util;

import android.util.Log;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.AndroidNonvisibleComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Form;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.FormService;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;


public class UIEvent extends AndroidNonvisibleComponent {
	
	private static final String TAG = "UIEvent";	
	
	/**
	 * A helper component to run methods in the UI thread when working with
	 * ThreadTimers (so you can do things to the UI).
	 * 
	 * @param form The Form this UIEvent resides in. Always "this"
	 */
	public UIEvent(Form form) {
		super(form);
		
	}
	
	/**
	 * A helper component to run methods in the UI thread when working with
	 * ThreadTimers (so you can do things to the UI).
	 * 
	 * @param form The Form this UIEvent resides in. Always "this"
	 */
	public UIEvent(FormService formservice) {
		super(formservice);
		
	}
	
	/**
	 * Use this method to fire an event to be run in the UI thread. Whatever
	 * you set the eventName to, is what you will need to capture in your form
	 * just like any other component. The only difference is that with this, it
	 * ensures the method is run in the UI thread, and also gives you the freedom
	 * to name the Event anything you want allowing you to use one instance of this
	 * class along with several threads to send multiple different events.
	 * 
	 * @param eventName The name to pass for the event.
	 */
	public void fireEvent(final String eventName) {
		if (eventName != null && !eventName.equals("")) {			
			Runnable eventRunner = new Runnable() {				
				@Override
				public void run() {
					EventDispatcher.dispatchEvent(UIEvent.this, eventName);
				}
			};			
			if (container == null) {
			  sContainer.$formService().post(eventRunner);
			} else {
			  container.getRegistrar().post(eventRunner);
			}
		} else {
			Log.e(TAG, "The event name must be at least one character.");
		}
	}	
	
}
