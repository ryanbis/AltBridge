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

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.Registrar;

/**
 * Persistently store values on the phone using tags to store and retrieve.
 * 
 */

public class TinyDB extends AndroidNonvisibleComponent implements Component, Deleteable {

	private boolean logErrors = true;
	private Context context;

	/**
	 * Creates a new TinyDB component.
	 * 
	 * @param container
	 *            the Form that this component is contained in.
	 */
	public TinyDB(ComponentContainer container) {
		super(container);
	}

	public TinyDB(SvcComponentContainer container) {
		super(container);
	}

	/**
	 * Use this constructor at your own risk. Not everything will work, as there is no parent container. (Only the store
	 * and get values from SD will work)
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
			public HandlesEventDispatching getDelegate() {
				return null;
			}
			@Override
			public Activity $context() {
				return null;
			}
			@Override
			public Registrar getRegistrar() {
				return null;
			}
			@Override
			public void $add(AndroidViewComponent component) {
			}
			@Override
			public void $remove(AndroidViewComponent component) {
			}
			@Override
			public void removeAllViews() {				
			}
		});

	}

	/**
	 * Use this constructor at your own risk. This is a constructor to use if you are outside of the AltBridge
	 * environment. (Using it in an activity).
	 */
	public TinyDB(final Context context, boolean non) {		
		super(new ComponentContainer() {
			@Override
			public void setChildWidth(AndroidViewComponent component, int width) {
			}
			@Override
			public void setChildHeight(AndroidViewComponent component, int height) {
			}
			@Override
			public HandlesEventDispatching getDelegate() {
				return null;
			}
			@Override
			public Activity $context() {
				return null;
			}
			@Override
			public Registrar getRegistrar() {
				return null;
			}
			@Override
			public void $add(AndroidViewComponent component) {
			}
			@Override
			public void $remove(AndroidViewComponent component) {
			}
			@Override
			public void removeAllViews() {	
			}
		});
		this.context = context;
	}
	
	@Override
	protected Context getContext() {
		if (context == null) {
			return super.getContext();
		} else {
			return context;
		}
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

	public static void StoreValue(final Context context, final String tag, final Object valueToStore) {
		try {
			FileOutputStream fos = context.openFileOutput(tag, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(valueToStore);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
	
	/**
	 * This method allows you to store items to the app's cache directory.
	 * 
	 * @param context
	 * @param tag
	 * @param valueToStore
	 */
	public static void StoreValueToCache(Context context, final String tag, final Object valueToStore) {
		File file = new File(context.getCacheDir(), tag);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method allows you to retrieve items stores to the app's cache directory.
	 * @param context
	 * @param tag
	 * @param defValue
	 * @return
	 */
	public static Object GetValueFromCache(Context context, String tag, Object defValue) {
		Object value = new Object();
		try {
			File file = new File(context.getCacheDir(), tag);
			if (!file.exists()) {				
				return defValue;
			}
			FileInputStream filestream = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(filestream);
			value = ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
			return defValue;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return defValue;
		}
		return value;
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
					Log.e("TinyDB", "File does not exist, or incorrect path " + tag);
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
			e.printStackTrace();
			return "null";
		}
		return value;
	}

	public Object GetValueFromSD(final String tag, final Object defValue) {
		Object value = defValue;
		try {
			File file = new File(Environment.getExternalStorageDirectory(), tag);
			if (!file.exists()) {
				if (logErrors) {
					Log.e("TinyDB", "File does not exist, or incorrect path " + tag);
				}
			}
			FileInputStream filestream = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(filestream);
			value = ois.readObject();
			ois.close();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		return value;
	}

	public boolean TagExists(String tag, boolean sdCard) {
		if (sdCard) {
			return new File(Environment.getExternalStorageDirectory(), "/" + tag).exists();
		} else {
			return new File(getContext().getFilesDir().getAbsolutePath(), "/" + tag).exists();
		}
	}
	
	public static boolean TagExistsInCache(Context context, String tag) {
		return new File(context.getCacheDir(), "/" + tag).exists();
	}

	public void DeleteTag(String tag, boolean sdCard) {
		File f;
		if (sdCard) {
			f = new File(Environment.getExternalStorageDirectory(), "/" + tag);
		} else {
			f = new File(getContext().getFilesDir().getAbsoluteFile(), "/" + tag);
		}
		if (f.exists()) {
			f.delete();
		}
	}

	/**
	 * 
	 * @param tag
	 *            - The tag name to get the path for
	 * @return - The absolute path location of the saved tag. If the tag doesn't exist "" will be returned.
	 */
	public String getAbsoluteFilePath(String tag) {
		if (!tag.startsWith("/")) {
			tag = "/" + tag;
		}
		File file = new File(getContext().getFilesDir().getAbsolutePath() + tag);
		if (file.exists()) {
			return file.getAbsolutePath();
		} else {
			return "";
		}
	}

	public Object GetValue(final String tag) {
		Object value = new Object();
		try {
			Context context;
			context = getContext();
			FileInputStream filestream = context.openFileInput(tag);
			ObjectInputStream ois = new ObjectInputStream(filestream);
			value = ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			if (logErrors) {
				Log.e("TinyDB", "File not found!" + " " + tag);
			}
			return "null";
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return value;
	}

	public Object GetValue(final String tag, Object defValue) {
		Object value = defValue;
		try {
			Context context;
			context = getContext();
			FileInputStream filestream = context.openFileInput(tag);
			ObjectInputStream ois = new ObjectInputStream(filestream);
			value = ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			if (logErrors) {
				Log.e("TinyDB", "File not found!" + " " + tag);
			}
		} catch (StreamCorruptedException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		return value;
	}

	public static Object GetValue(final Context context, final String tag) {
		Object value = new Object();
		try {
			FileInputStream filestream = context.openFileInput(tag);
			ObjectInputStream ois = new ObjectInputStream(filestream);
			value = ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "null";
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static Object GetValue(final Context context, final String tag, final Object defValue) {
		Object value = defValue;
		try {
			FileInputStream filestream = context.openFileInput(tag);
			ObjectInputStream ois = new ObjectInputStream(filestream);
			value = ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
		} catch (StreamCorruptedException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		return value;
	}

	@Override
	public void onDelete() {

	}
}
