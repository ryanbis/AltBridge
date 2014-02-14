package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.IOException;
import java.util.ArrayList;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.CustomListItem;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * ListPickerActivity class - Brings up a list of items specified in an intent
 * and returns the selected item as the result.
 * 
 */
public class ListPickerActivity extends ListActivity {

  private boolean twoline;
  private int rowLayoutId;
  private int text1;
  private int text2;
  private int imgView;
  private ArrayList<CustomListItem> cItems;
  private boolean custom;
  private Context enclosingClass = this;   	//When this gets set, it uses the pickerClass from ListPicker.  Hence, you can extend ListActivity

  @Override
  public void onCreate(Bundle savedInstanceState) {
	  setActionBarProperties();
	  
    super.onCreate(savedInstanceState);
    

    String items[] = null;
    int layout = 0;
    int textViewId = 0;
    Intent myIntent = getIntent();
    if (myIntent.hasExtra(ListPicker.LIST_ACTIVITY_CACHEHINT)) {
      getListView().setCacheColorHint(0);
    }
    if (myIntent.hasExtra(ListPicker.LIST_ACTIVITY_ARG_NAME)) {
      items = getIntent().getStringArrayExtra(ListPicker.LIST_ACTIVITY_ARG_NAME);
      if (myIntent.hasExtra(ListPicker.LIST_ACTIVITY_HEADERS)) {
        twoline = true;
        layout = android.R.layout.simple_list_item_2;
        textViewId = android.R.id.text2;
        String[] headers = myIntent.getStringArrayExtra(ListPicker.LIST_ACTIVITY_HEADERS);
        MatrixCursor c = new MatrixCursor(new String[] { "_id", "Header", "Content" });

        int size = items.length;
        for (int i = 0; i < size; i++) {
          c.addRow(new Object[] { i, headers[i], items[i] });
        }
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c,
            new String[] { "Header", "Content" }, new int[] { android.R.id.text1,
                android.R.id.text2 }));
        getListView().setTextFilterEnabled(true);
      } else if (myIntent.hasExtra(ListPicker.LIST_CUSTOM_ROWID)) {
        custom = true;
        layout = getIntent().getIntExtra(ListPicker.LIST_ACTIVITY_LAYOUT,
            android.R.layout.simple_list_item_2);
        setContentView(layout);
        rowLayoutId = myIntent.getIntExtra(ListPicker.LIST_CUSTOM_ROWID, 0);
        text1 = myIntent.getIntExtra(ListPicker.LIST_CUSTOM_TEXT1, 0);
        text2 = myIntent.getIntExtra(ListPicker.LIST_CUSTOM_TEXT2, 0);
        imgView = myIntent.getIntExtra(ListPicker.LIST_CUSTOM_IMGV, 0);

        cItems = myIntent.getExtras().getParcelable(ListPicker.LIST_CUSTOM_ITEMS);
        ListPickAdapter mAdapter = new ListPickAdapter(this, rowLayoutId, cItems);
        setListAdapter(mAdapter);

      } else {
        layout = getIntent().getIntExtra(ListPicker.LIST_ACTIVITY_LAYOUT,
            android.R.layout.simple_list_item_1);
        textViewId = getIntent().getIntExtra(ListPicker.LIST_ACTIVITY_TEXTVIEWID,
            android.R.id.text1);
        // setListAdapter(new ArrayAdapter<String>(this,
        // android.R.layout.simple_list_item_1, items));
        setListAdapter(new ArrayAdapter<String>(this, layout, textViewId, items));
        getListView().setTextFilterEnabled(true);
      }
    } else {
      setResult(RESULT_CANCELED);
      finish();
    }
  }

  private class ListPickAdapter extends ArrayAdapter<CustomListItem> {

    private ArrayList<CustomListItem> items;

    public ListPickAdapter(Context context, int rowLayoutId, ArrayList<CustomListItem> items) {
      super(context, rowLayoutId, items);
      this.items = items;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;
      if (v == null) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(rowLayoutId, null);
      }
      CustomListItem item = items.get(position);
      if (item != null) {
        TextView t1 = (TextView) v.findViewById(text1);
        TextView t2 = (TextView) v.findViewById(text2);
        ImageView img = (ImageView) v.findViewById(imgView);

        if (t1 != null) {
          t1.setText(item.Text());
        }
        if (t2 != null) {
          t2.setText(item.SecondText());
        }
        if (img != null) {
          if (item.ImageResource() != 0) {
            img.setImageDrawable(getResources().getDrawable(item.ImageResource()));
          } else {
            Drawable draw = null;
            try {
            	//String test = this.getClass().getName();
            	draw = MediaUtil.getDrawable(enclosingClass, item.ImageFile());  //This should help allow you to extend ListPickerActivity without a problem    
            	//draw = MediaUtil.getDrawable(ListPickerActivity.this, item.ImageFile());		//This is the original way to do it
            } catch (IOException e) {
              e.printStackTrace();
            }
            img.setImageDrawable(draw);
          }
        }
      }
      return v;
    }
  }

  @Override
  public void onListItemClick(ListView lv, View v, int position, long id) {
    Intent resultIntent = new Intent();
    if (!twoline && !custom) {
      resultIntent.putExtra(ListPicker.LIST_ACTIVITY_RESULT_NAME, (String) getListView()
          .getItemAtPosition(position));
    } else {

    }
    resultIntent.putExtra(ListPicker.LIST_ACTIVITY_RESULT_INDEX, position + 1);
    setResult(RESULT_OK, resultIntent);
    finish();
  }
  
      /**Override this method to set action bar properties for this ListPicker
      *  Technically this method can be used to do anything you want to do before 
      *  super.onCreate gets called.
      *
      */
      public void setActionBarProperties() {
  
    }

}
