package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Sets;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SdkLevel;

@TargetApi(11)
public class Prefs extends AndroidNonvisibleComponent implements Deleteable {

  private SharedPreferences prefs;
  public final static String BOOL = "Boolean";
  public final static String INT = "Integer";
  public final static String LONG = "Long";
  public final static String SHORT = "Short";
  public final static String FLOAT = "Float";
  public final static String STRING = "String";
  

  /**
   * Constructor for Prefs component. This uses android's SharedPreferences to
   * store data. It's great for preferences, but not for large amounts of data.
   * 
   * @param container
   */
  public Prefs(ComponentContainer container) {
    super(container);
    prefs = container.$context().getSharedPreferences("AB_Prefs", Context.MODE_PRIVATE);
  }

  public Prefs(SvcComponentContainer container) {
    super(container);
    prefs = container.$context().getSharedPreferences("AB_Prefs", Context.MODE_PRIVATE);
  }
  
  public void UseOldTinyDBFile(boolean useOld) {
	  if (useOld) {
		  prefs = container.$context().getSharedPreferences("TinyDB", Context.MODE_PRIVATE);
	  } else {
		  prefs = container.$context().getSharedPreferences("AB_Prefs", Context.MODE_PRIVATE);
	  }
  }

  public void StoreBoolean(String tag, boolean valueToStore) {
    final SharedPreferences.Editor edit = prefs.edit();
    edit.putBoolean(tag, valueToStore);
    edit.commit();
  }

  public void StoreInt(String tag, int valueToStore) {
    final SharedPreferences.Editor edit = prefs.edit();
    edit.putInt(tag, valueToStore);
    edit.commit();
  }

  public void StoreLong(String tag, long valueToStore) {
    final SharedPreferences.Editor edit = prefs.edit();
    edit.putLong(tag, valueToStore);
    edit.commit();
  }

  public void StoreString(String tag, String valueToStore) {
    final SharedPreferences.Editor edit = prefs.edit();
    edit.putString(tag, valueToStore);
    edit.commit();
  }

  /**
   * This method requires level API 11 or higher. If it is called from a lower
   * API, nothing will happen (nothing will get saved, and you won't get any
   * error)
   * 
   * @param tag
   * @param valueToStore
   */
  public void StoreStringSet(String tag, Set<String> valueToStore) {
    if (SdkLevel.getLevel() >= SdkLevel.LEVEL_HONEYCOMB) {
      final SharedPreferences.Editor edit = prefs.edit();
      edit.putStringSet(tag, valueToStore);
      edit.commit();
    }
  }

  public boolean GetBoolean(String tag) {
    return prefs.getBoolean(tag, false);
  }

  public int GetInt(String tag) {
    return prefs.getInt(tag, 0);
  }

  public long GetLong(String tag) {
    return prefs.getLong(tag, 0);
  }

  public String GetString(String tag) {
    return prefs.getString(tag, "");
  }
  
  public boolean GetBoolean(String tag, boolean defValue) {
    return prefs.getBoolean(tag, defValue);
  }

  public int GetInt(String tag, int defValue) {
    return prefs.getInt(tag, defValue);
  }

  public long GetLong(String tag, long defValue) {
    return prefs.getLong(tag, defValue);
  }

  public String GetString(String tag, String defValue) {
    return prefs.getString(tag, defValue);
  }

  /**
   * This method requires API level 11 or higher. If it is used with a lower API
   * level, it will always return null.
   * 
   * @param tag
   * @return
   */
  public Set<String> GetStringSet(String tag) {
    Set<String> set = Sets.newHashSet();
    if (SdkLevel.getLevel() >= SdkLevel.LEVEL_HONEYCOMB) {
      return prefs.getStringSet(tag, set);
    }
    return null;
  }

  /**
   * Store an ArrayList persistently. If the list is going to be very large,
   * consider using SimpleSQL instead.
   * 
   * @param tag
   * @param list
   */
  public void StoreArrayList(String tag, ArrayList<?> list) {
    if (list != null && list.size() > 0) {      
      JSONArray a = new JSONArray();      
      for (int i = 0; i < list.size(); i++) {
        a.put(list.get(i));
      }
      if (!list.isEmpty()) {
        final Editor edit = prefs.edit();
        edit.putString(tag, a.toString());
        edit.commit();
      }
    }
  }

  /**
   * 
   * @param tag
   * @return the stored ArrayList
   */
  public ArrayList<String> GetArrayList(String tag) {    
    ArrayList<String> list = new ArrayList<String>();
    String json = prefs.getString(tag, null);
    if (json != null) {
      try {
        JSONArray a = new JSONArray(json);        
        for (int i = 0; i < a.length(); i++) {
          String val = a.optString(i);
          list.add(val);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return list;
  }
  

  @Override
  public void onDelete() {
    final SharedPreferences.Editor edit = prefs.edit();
    edit.clear();
    edit.commit();
  }

}
