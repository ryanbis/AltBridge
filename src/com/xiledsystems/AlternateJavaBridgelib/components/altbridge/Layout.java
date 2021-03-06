package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.view.ViewGroup;

/**
 * The Layout interface provides methods for working with Simple
 * component layouts.
 *
 */
public interface Layout {

  /**
   * Returns the view group (which is a container with a layout manager)
   * associated with the layout.
   *
   * @return  view group
   */
  ViewGroup getLayoutManager();

  /**
   * Adds the specified component to this layout.
   *
   * @param component  component to add
   */
  void add(AndroidViewComponent component);
  
  void remove(AndroidViewComponent component);
}
