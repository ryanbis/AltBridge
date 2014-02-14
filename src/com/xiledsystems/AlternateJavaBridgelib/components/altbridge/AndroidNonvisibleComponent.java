package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.Context;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.EventListener;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

/**
 * Base class for all non-visible components.
 *
 */

public abstract class AndroidNonvisibleComponent implements Component, EventListener {

  protected final ComponentContainer container;
  protected final SvcComponentContainer sContainer;
  protected Events.Event eventListener;

  /**
   * Creates a new AndroidNonvisibleComponent.
   *
   * @param container  container, component will be placed in
   */
  protected AndroidNonvisibleComponent(ComponentContainer container) {
    this.container = container;
    this.sContainer = null;
  }
  
  protected AndroidNonvisibleComponent(SvcComponentContainer sContainer) {
	    this.sContainer = sContainer;
	    this.container = null;	    
	  }
  
  protected Context getContext() {
	  if (container == null) {
		  return sContainer.$context();
	  } else {
		  return container.$context();
	  }
  }
  
  public void setEventListener(Events.Event event) {
	  eventListener = event;
  }
  
  @Override
  public Events.Event getEventListener() {
	  return eventListener;
  }
  
  /**
   * Convenience method for posting an action to the UI thread. As a non-visible
   * component can exist in a Form, or FormService, this method calls the appropriate
   * post() method from the appropriate container. This is used internally by components.
   * 
   * @param action
   */
  public void post(Runnable action) {
	  if (container == null) {
		  sContainer.$formService().post(action);
	  } else {
		  container.getRegistrar().post(action);
	  }
  }

  // Component implementation

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
	  if (container==null) {
		  return sContainer.$formService();
	  } else {
		  return container.getDelegate();
	  }
  }
}
