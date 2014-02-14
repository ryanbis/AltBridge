package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.EventListener;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;


	/**
	 * Base class for all Service components
	 * @author Ryan Bis - www.xiledsystems.com
	 *
	 */

public abstract class AndroidServiceComponent implements Component, EventListener {
	
	protected FormService formService;
	protected Events.Event eventListener;
	
	/*
	 *  Creates a new AndroidServiceComponent.
	 *  
	 */
	
	protected AndroidServiceComponent(FormService formService) {
		this.formService = formService;
	}
	
	public void setEventListener(Events.Event event) {
		eventListener = event;
	}
	
	// Component implementation
	
	@Override
	public HandlesEventDispatching getDispatchDelegate() {
		return formService;
	}

}
