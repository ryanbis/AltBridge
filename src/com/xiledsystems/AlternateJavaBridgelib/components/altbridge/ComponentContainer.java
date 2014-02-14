package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;


import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.Registrar;

import android.app.Activity;

/**
 * Components that can contain other components need to implement this
 * interface.
 *
 */
public interface ComponentContainer {
  /**
   * Returns the activity context (which can be retrieved from the root
   * container - aka the form).
   *
   * @return  activity context
   */
  Activity $context();
   
  HandlesEventDispatching getDelegate();
  
  Registrar getRegistrar();

  /**
   * Adds a component to a container.
   *
   * <p/>After this method is finished executing, the given component's view
   * must have LayoutParams, even if the component cannot be added to the
   * container until later.
   *
   * @param component  component associated with view
   */
  void $add(AndroidViewComponent component);
  
  void $remove(AndroidViewComponent component);
  
  void removeAllViews();
    
  void setChildWidth(AndroidViewComponent component, int width);

  void setChildHeight(AndroidViewComponent component, int height);
}
