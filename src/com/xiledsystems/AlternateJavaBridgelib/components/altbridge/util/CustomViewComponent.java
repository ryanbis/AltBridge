package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.view.View;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.AndroidViewComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.ComponentContainer;


public abstract class CustomViewComponent extends AndroidViewComponent {
    
  private final View view;  

  
  protected CustomViewComponent(ComponentContainer container) {
    super(container);
    view = setLayout();
    container.$add(this);    
  }
  
  protected CustomViewComponent(ComponentContainer container, int resourceId) {
    super(container, resourceId);
    view = container.$context().findViewById(resourceId);
  }
  

  /**
   * Use container.getRegistrar().inflateLayout(int layoutResid) to
   * have this method return your custom view in the constructor. If this
   * is not done, the view backing this component will be null.
   * 
   * @return
   */
  protected abstract View setLayout();
  
  public View getLayout(int layoutResId) {
    return container.getRegistrar().inflateLayout(layoutResId);
  }
  
  public View findViewById(int resourceId) {
    return view.findViewById(resourceId);
  }
  
  @Override
  public View getView() {
    return view;
  }
  
}
