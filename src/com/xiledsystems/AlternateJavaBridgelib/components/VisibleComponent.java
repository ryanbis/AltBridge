package com.xiledsystems.AlternateJavaBridgelib.components;

import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;


/**
 * Superclass of visible components in the runtime libraries.
 * <p>
 * Defines standard properties and events.
 *
 */

public abstract class VisibleComponent implements Component, EventListener {
	
	protected Events.Event eventListener;
	
  protected VisibleComponent() {
  }
  
  public void setEventListener(Events.Event event) {
	  eventListener = event;
  }
  
  @Override
  public Events.Event getEventListener() {
	  return eventListener;
  }

  /**
   * Width property getter method.
   *
   * @return  width property used by the layout
   */
  
  public abstract int Width();

  /**
   * Width property setter method.
   *
   * @param width  width property used by the layout
   */
  
  public abstract void Width(int width);

  /**
   * Height property getter method.
   *
   * @return  height property used by the layout
   */
  
  public abstract int Height();

  /**
   * Height property setter method.
   *
   * @param height  height property used by the layout
   */
  
  public abstract void Height(int height);
}

