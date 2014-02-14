package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import com.xiledsystems.altbridge.eclipse.AB_HeaderBar;
import com.xiledsystems.altbridge.eclipse.AB_HeaderBar.OptionsListener;

import android.view.View;
import android.view.View.OnClickListener;

public class HeaderBar extends AndroidViewComponent {
  
  private AB_HeaderBar view;
  
  private HorizontalArrangement headerCont;
  private VerticalArrangement optionCont;
      
  private int optionLayout = -1;
  
  public HeaderBar(ComponentContainer container) {
    super(container);
  }
  
  public HeaderBar(ComponentContainer container, int resourceId) {
    super(container, resourceId);
    view = (AB_HeaderBar) container.getRegistrar().findViewById(resourceId);
    view.setOptionsListener(new OptListen());
    view.setLeftIconClick(new LeftClickListener());
  }
  
  public HeaderBar(ComponentContainer container, int layoutResId, int buttonLayoutId) {    
    super(container, layoutResId);
    optionLayout = buttonLayoutId;
    view = (AB_HeaderBar) container.getRegistrar().findViewById(layoutResId);
    view.setOptionsListener(new OptListen());
    view.setLeftIconClick(new LeftClickListener());
  }
  
  /**
   * Pass an array list of the resource ids of the buttons
   * that reside in your custom button layout. The id of the
   * button that is clicked is returned in the Click event (which
   * will be thrown by the HeaderBar class).
   * 
   * @param buttonIds
   */
  public void OptionButtons(ArrayList<Integer> buttonIds) {
    for (Integer i : buttonIds) {
      View b = view.findViewById(i);
      b.setOnClickListener(new OnClickListener() {        
        @Override
        public void onClick(View v) {
          optionButtonClick(v.getId());
        }
      });
    }
  }
  
  public void AddOption(String buttonText) {
    view.addOptionButton(buttonText);
  }
  
  public ArrayList<String> Options() {
    return view.getOptionButtons();
  }
  
  public void showOptionsButton(boolean show) {
    view.showOptionsBtn(show);
  }
      
  public void optionButtonClick(Object id) {    
      EventDispatcher.dispatchEvent(this, Events.CLICK, id);    
  }
  
  public void leftIconClicked() {
    EventDispatcher.dispatchEvent(this, Events.LEFT_ICON_CLICK);
  }

  @Override
  public View getView() {
    return view;   
  }  
  
  private class LeftClickListener implements OnClickListener {
    @Override
    public void onClick(View v) {
      leftIconClicked();
    }    
  }
  
  private class OptListen implements OptionsListener {
    @Override
    public void buttonClicked(String buttonText) {
      optionButtonClick(buttonText);
    }    
  }
  
}
