package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.view.View;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.AndroidViewComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.ComponentContainer;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

public class DummyComponent extends AndroidViewComponent {
  
  private final View view;  
  
  public DummyComponent(ComponentContainer container, View view, boolean addToContainer) {
    super(container);
    this.view = view;
    if (addToContainer) {
    	container.$add(this);
    }
  }

  @Override
  public View getView() {    
    return view;
  }

  @Override
  public void postAnimEvent() {
    EventDispatcher.dispatchEvent(this, Events.ANIM_MIDDLE);
  }

}
