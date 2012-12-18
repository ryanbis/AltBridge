package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;


/**
 * Persistently store values on the phone using tags to store and retrieve.
 *
 */

public class TinyDB extends AndroidNonvisibleComponent implements Component, Deleteable {

  
	private boolean logErrors = true;
	
	
  /**
   * Creates a new TinyDB component.
   *
   * @param container the Form that this component is contained in.
   */
  public TinyDB(ComponentContainer container) {
    super(container);    
  }
  
  public TinyDB(SvcComponentContainer container) {
	  super(container);	  
  }
  
  /**
   * Use this constructor at your own risk. Not everything will work, as there
   * is no parent container. (Only the store and get values from SD will work)
   */
  public TinyDB() {    
    super(new ComponentContainer() {      
      @Override
      public void setChildWidth(AndroidViewComponent component, int width) {
      }
      
      @Override
      public void setChildHeight(AndroidViewComponent component, int height) {
      }
      
      @Override
      public Form $form() {
        return null;
      }
      
      @Override
      public Activity $context() {
        return null;
      }
      
      @Override
      public void $add(AndroidViewComponent component) {        
      }
    });
        
  }

  
  
  public void StoreValue(final String tag, final Object valueToStore) {
	  try {
			Context context;
		    context = getContext();		    
			FileOutputStream fos = context.openFileOutput(tag, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(valueToStore);
			os.flush();
			os.close();
							
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (logErrors) {
				Log.e("TinyDB", "File not found! Which is strange because we're trying to save.");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} 
  }
  
  public void LogErrors(boolean logerrors) {
	  logErrors = logerrors;
  }
  
  public boolean LogErrors() {
	  return logErrors;
  }
  
  public void StoreValueToSD(final String tag, final Object valueToStore) {
	  File file = new File(Environment.getExternalStorageDirectory(), tag);
	  if (file.exists()) {
		  file.delete();
	  }
	  OutputStream out;
	  try {
		  out = new FileOutputStream(file);
		  ObjectOutputStream os = new ObjectOutputStream(out);
		  os.writeObject(valueToStore);
		  os.flush();
		  os.close();
	  } catch (FileNotFoundException e) {
		  e.printStackTrace();
		  if (logErrors) {
			  Log.e("TinyDB", "File not found! Which is strange because we're trying to save.");
		  }
		  
	  } catch (IOException e) {
		  e.printStackTrace();
		  
	  }
	  
  }
  
  public Object GetValueFromSD(final String tag) {
	  Object value = new Object();
	  try {		  
		  File file = new File(Environment.getExternalStorageDirectory(), tag);
		  if (!file.exists()) {
			  if (logErrors) {
				  Log.e("TinyDB", "File does not exist, or incorrect path "+ tag);
			  }
			  return "null";
		  }
		  FileInputStream filestream = new FileInputStream(file);
		  ObjectInputStream ois = new ObjectInputStream(filestream);
		  value = ois.readObject();
		  ois.close();
	  } catch (IOException e) {
		  e.printStackTrace();
		  return "null";
	  } catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return "null";
	}
	  return value;
  }

  
  
  public Object GetValue(final String tag) {
	  Object value=new Object();
		try {
			Context context;			
			context = getContext();			
			FileInputStream filestream = context.openFileInput(tag);	
			ObjectInputStream ois = new ObjectInputStream(filestream); 	 
			value = ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			if (logErrors) {
				Log.e("TinyDB", "File not found!"+ " "+tag);
			}
			//e.printStackTrace();
			return "null";
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (value instanceof Integer) {
			return Integer.parseInt(value.toString());
		} else if (value instanceof Boolean){
			return Boolean.parseBoolean(value.toString());
		} else if (value instanceof Double) {
			return Double.parseDouble(value.toString());
		} else if (value instanceof Long) {
			return Long.parseLong(value.toString());
		} else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else if (value instanceof String) {
			
				return value.toString();
			
		} else if (value instanceof ArrayList<?>) {
			return (ArrayList<?>) value;
		}
		 
		return value;		
  }

  @Override
  public void onDelete() {
   
  }
}
