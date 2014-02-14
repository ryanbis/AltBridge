package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import java.util.Arrays;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;


/**
 * A basic list box class. This is what ListPicker uses, only fullscreen. This component
 * doesn't have to be the fullscreen. When an item is clicked, the Click event is thrown, along
 * with the position of the item in the list (NOTE: This is 0 based, which means the first item in
 * the list is at index 0).
 * 
 * @author Ryan Bis
 *
 */
public class ListBox extends AndroidViewComponent {
  
  private ListView view;
  
  private ArrayAdapter<String> listAdapter;
  private BaseAdapter customAdapter;
  
  private boolean custAdapter;
  
  private ArrayList<String> elements;
  
  
  public ListBox(ComponentContainer container) {
    super(container);
    view = new ListView(container.$context());
    view.setOnItemClickListener(new ItemClicked());
    container.$add(this);
  }
  
  public ListBox(ComponentContainer container, int resourceId) {
    super(container, resourceId);
    view = (ListView) container.$context().findViewById(resourceId);
    view.setOnItemClickListener(new ItemClicked());
  }
  
  public void Elements(ArrayList<String> elements) {
    this.elements = elements;
    setupAdapter();
  }
  
  public void Elements(String[] elements) {
    this.elements.clear();
    this.elements.addAll(Arrays.asList(elements));
    setupAdapter();
  }
  
  public void CustomAdapter(BaseAdapter custAdapter) {
    this.custAdapter = true;
    customAdapter = custAdapter;
  }
  
  private void setupAdapter() {
    if (!custAdapter) {
      listAdapter = new ArrayAdapter<String>(container.$context(), android.R.layout.simple_list_item_1, elements);
      view.setAdapter(listAdapter);
    } else {
      view.setAdapter(customAdapter);
    }    
  }

  @Override
  public View getView() {
    return view;
  }
  
  private class ItemClicked implements OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      EventDispatcher.dispatchEvent(ListBox.this, Events.CLICK, position);
    }
    
  }

}
