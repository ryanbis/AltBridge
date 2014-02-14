package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.view.View;

public class Space extends AndroidViewComponent {

  private final View view;
  
  protected Space(ComponentContainer container) {
    super(container);    
    view = new View(container.$context());    
    container.$add(this);
  }
  
  protected Space(ComponentContainer container, int resourceId) {
    super(container, resourceId);
    view = container.getRegistrar().findViewById(resourceId);
  }

  @Override
  public View getView() {    
    return view;
  }

}
