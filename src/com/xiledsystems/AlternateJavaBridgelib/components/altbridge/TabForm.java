package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.Registrar;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.StateSet;
import android.widget.TabHost;
import android.widget.TabWidget;


@SuppressWarnings("deprecation")
/**
 * This class has been deprecated in favor of using Fragments. Some stuff may have been
 * broken in here since fragments have been added.
 * 
 * @author Ryan Bis
 *
 */
public class TabForm extends TabActivity implements ComponentContainer, HandlesEventDispatching {

	private final static String LOG_TAG = "TabForm";
	
	private LinearLayout layout;
	private String tabformName;
	private TabWidget tabholder;	
	private TabHost tabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tabHost = getTabHost();
		
		layout = new LinearLayout(this, ComponentConstants.LAYOUT_ORIENTATION_VERTICAL);
		tabholder = new TabWidget(this);
		layout.add(tabholder);
		
		String classname = getClass().getName();
		int lastDot = classname.lastIndexOf('.');
		tabformName = classname.substring(lastDot + 1);
		Log.d(LOG_TAG, "TabForm " + tabformName + " got onCreate");
				
		$define();
	}
	
	/**
	 * Use this method to set which tab is to be the current.
	 * 
	 * Remember, this is 0 based, so the first tab 
	 * 
	 * @param tabnumber
	 */
	public void setCurrentTab(int tabnumber) {
		tabHost.setCurrentTab(tabnumber);
	}
	
	
	/**
	 * Use this method to add a Tab to the tabForm. Each tab is it's own
	 * Form.
	 * 
	 * @param title The title of the tab
	 * @param selected - The selected drawable image ( use something like "image42" ) of the tab icon
	 * @param notselected - The not selected drawable image of the tab icon
	 * @param formtoopen - The Form to open in this tab's space
	 */
	public void addTab(String title, String selected, String notselected, Class<?> formtoopen) {
		
		int[] temp = new int[2];
		temp[0] = getResources().getIdentifier(selected, "drawable", getPackageName());
		temp[1] = getResources().getIdentifier(notselected, "drawable", getPackageName());
		StateListDrawable draw = new StateListDrawable();
		BitmapDrawable draw1 = (BitmapDrawable) getResources().getDrawable(temp[0]);
		BitmapDrawable draw2 = (BitmapDrawable) getResources().getDrawable(temp[1]);
		draw.addState(new int[] {android.R.attr.state_selected}, draw1);
		draw.addState(StateSet.WILD_CARD , draw2);
		Intent intent = new Intent().setClass(this, formtoopen);
		TabHost.TabSpec spec;
		spec = tabHost.newTabSpec(title.toLowerCase()).setIndicator(title, draw).setContent(intent);		
		tabHost.addTab(spec);		
	}	
	
	/**
	 * Use this method to add a Tab to the tabForm. Each tab is it's own
	 * Form.
	 * 
	 * @param title The title of the tab
	 * @param selected - The resourceId of the selected tab icon
	 * @param notselected - The resourceId of the not selected tab icon
	 * @param formtoopen - The Form to open in this tab's space
	 */	
	public void addTab(String title, int selectedresId, int notselectedresId, Class<?> formtoopen) {
		
		int[] temp = new int[2];
		temp[0] = selectedresId;
		temp[1] = notselectedresId;
		StateListDrawable draw = new StateListDrawable();
		BitmapDrawable draw1 = (BitmapDrawable) getResources().getDrawable(temp[0]);
		BitmapDrawable draw2 = (BitmapDrawable) getResources().getDrawable(temp[1]);
		draw.addState(new int[] {android.R.attr.state_selected}, draw1);
		draw.addState(StateSet.WILD_CARD , draw2);
		Intent intent = new Intent().setClass(this, formtoopen);
		TabHost.TabSpec spec;
		spec = tabHost.newTabSpec(title.toLowerCase()).setIndicator(title, draw).setContent(intent);		
		tabHost.addTab(spec);		
	}	
	
	void $define() {		
		throw new UnsupportedOperationException();		
	}

  @Override
  public Activity $context() {    
    return this;
  }

  @Override
  public HandlesEventDispatching getDelegate() {    
    return this;
  }

  @Override
  public Registrar getRegistrar() {    
    return null;
  }

  @Override
  public void $add(AndroidViewComponent component) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void $remove(AndroidViewComponent component) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setChildWidth(AndroidViewComponent component, int width) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setChildHeight(AndroidViewComponent component, int height) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean canDispatchEvent(Component component, String eventName) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean dispatchEvent(Component component, String componentName, String eventName,
      Object[] args) {
    // TODO Auto-generated method stub
    return false;
  }

@Override
public void removeAllViews() {
	// TODO Auto-generated method stub
	
}
	
}
