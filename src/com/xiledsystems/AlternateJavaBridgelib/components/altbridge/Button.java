package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

/**
 * Button with the ability to launch events on initialization, focus
 * change, or a user click.  It is implemented using
 * {@link android.widget.Button}.
 *
 */

public class Button extends ButtonBase {
	
	
  /**
   * Creates a new Button component.
   *
   * @param container container, component will be placed in
   */
  public Button(ComponentContainer container) {
    super(container);
  }
  
  public Button(ComponentContainer container, int resourceId) {
	    super(container, resourceId);
  }

 @Override
  public void click() {
    // Call the users Click event handler. Note that we distinguish the click() abstract method
    // implementation from the Click() event handler method.
    Click();
  }
 
  /**
   * Indicates a user has clicked on the button.
   */
  
  public void Click() {
	  if (eventListener != null) {
		  eventListener.eventDispatched(Events.CLICK);
	  } else {
		  EventDispatcher.dispatchEvent(this, Events.CLICK);
	  }    
  }

  @Override
  public boolean longClick() {
    // Call the users Click event handler. Note that we distinguish the longclick() abstract method
    // implementation from the LongClick() event handler method.
    return LongClick();
  }

  /**
   * Indicates a user has long clicked on the button.
   */
  
  public boolean LongClick() {
	  if (eventListener != null) {
		  eventListener.eventDispatched(Events.LONG_CLICK);
		  return true;
	  }
    return EventDispatcher.dispatchEvent(this, Events.LONG_CLICK);
  }

}
