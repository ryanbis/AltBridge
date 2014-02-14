package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.CustomDialog;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.CustomDialog.DialogIds;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.Registrar;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

/**
 * The Notifier component displays alert messages. The kinds of messages are: (a) ShowMessageDialog: user must dismiss
 * the message by pressing a button. (b) ShowChooseDialog): displays two buttons to let the user choose one of two
 * responses, for example, yes or no. (c) ShowTextDialog: lets the user enter text in response to the message. (d)
 * AlertUser: displays an alert that goes away by itself after a short time. ShowChooseDialog raises the event
 * AfterChoosing, whose argument is the text on the button that was pressed. ShowTextDialog raises the event
 * AfterTextInput, whose argument is the text the user supplied.
 * 
 */

// TODO(user): Change the dialog methods to be synchronous and return values rather
// than signaling events; or at least to use one-shot events, when we implement those.

// TODO(user): Figure out how/if these dialogs should deal with onPause.

public final class Notifier extends AndroidNonvisibleComponent implements Component {

	public final static int AUTO_CANCEL = Notification.FLAG_AUTO_CANCEL;
	public final static int ONGOING = Notification.FLAG_ONGOING_EVENT;
	public final static int INSISTENT = Notification.FLAG_INSISTENT;
	public final static int NO_CLEAR = Notification.FLAG_NO_CLEAR;
	private CustomDialog.DialogIds customIds;
	private static final String LOG_TAG = "Notifier";
	private static final int NOTIFIER_ID = 66;
	private final Handler handler;
	private boolean isaService = false;
	private Class<?> classToSpawn;
	private boolean spawnService;
	private Notification notification;
	private int notificationFlag = -1;
	private int intentFlag = -1;
	private int noteColor = -1;
	private int flashOn;
	private int flashOff;
	private int layoutRes;
	private int textRes;
	private int rootRes;
	private Uri dataUri;

	/**
	 * Creates a new Notifier component.
	 * 
	 * @param container
	 *            the enclosing component
	 */
	public Notifier(ComponentContainer container) {
		super(container);

		// We'll specifically get the main looper, in case this is constructed in
		// a thread other than the UI thread.
		handler = new Handler(Looper.getMainLooper());

		isaService = false;
		classToSpawn = container.getRegistrar().getClass();
	}

	public Notifier(SvcComponentContainer container) {
		super(container);

		// We'll specifically get the main looper, in case this is constructed in
		// a thread other than the UI thread.
		handler = new Handler(Looper.getMainLooper());

		isaService = true;
		classToSpawn = container.$formService().getApplication().getClass();

	}

	/**
	 * Display an alert dialog with a single button
	 * 
	 * This method is now deprecated. Use ShowDialog instead.
	 * 
	 * @param message
	 *            the text in the alert box
	 * @param title
	 *            the title for the alert box
	 * @param buttonText
	 *            the text on the button
	 */
	@Deprecated
	public void ShowMessageDialog(String message, String title, String buttonText) {
		if (customIds != null) {
			showCustomAlert(message, title, null, new String[] { buttonText });
		} else {
			showStandardAlert(message, title, null, new String[] { buttonText });
		}
	}

	/**
	 * Use this to display a dialog to the user. The buttontext is an array. It cannot be empty, or larger than 3. This
	 * will dictate how many buttons will appear in the dialog.
	 * 
	 * @param message
	 *            - The message to display in the body
	 * @param title
	 *            - The title of the dialog
	 * @param buttonText
	 *            - The text to display on the button(s)
	 */	
	public void ShowDialog(String message, String title, final String... buttonText) {
		int btns = buttonText.length;
		if (btns < 1) {
			throw new IllegalArgumentException("Button text must have at least one item!");
		} else if (btns > 3) {
			throw new IllegalArgumentException("Too many buttons for the notifier. Three is the max!");
		}
		if (customIds != null) {
			showCustomAlert(message, title, null, buttonText);
		} else {
			showStandardAlert(message, title, null, buttonText);
		}
	}

	/**
	 * Use this to display a dialog to the user. The buttontext is an array. It cannot be empty, or larger than 3. This
	 * will dictate how many buttons will appear in the dialog.
	 * 
	 * @param message
	 *            - The message to display in the body
	 * @param title
	 *            - The title of the dialog
	 * @param listener
	 *            - A listener to run, instead of using the Event system
	 * @param buttonText
	 *            - The text to display on the button(s)
	 */	
	public void ShowDialog(String message, String title, final NotifierButtonListener listener, final String... buttonText) {
		int btns = buttonText.length;
		if (btns < 1) {
			throw new IllegalArgumentException("Button text must have at least one item!");
		} else if (btns > 3) {
			throw new IllegalArgumentException("Too many buttons for the notifier. Three is the max!");
		}
		if (customIds != null) {
			showCustomAlert(message, title, listener, buttonText);
		} else {
			showStandardAlert(message, title, listener, buttonText);
		}
	}

	/**
	 * Display an alert with two buttons. Raises the AfterChoosing event when the choice has been made.
	 * 
	 * This is now deprecated. Use ShowDialog instead.
	 * 
	 * @param message
	 *            the text in the alert box
	 * @param title
	 *            the title for the alert box
	 * @param button1Text
	 *            the text on the left-hand button
	 * @param button2Text
	 *            the text on the right-hand button
	 */
	@Deprecated
	public void ShowChooseDialog(String message, String title, String button1Text, String button2Text) {
		if (customIds != null) {
			showCustomAlert(message, title, null, new String[] { button1Text, button2Text });
		} else {
			showStandardAlert(message, title, null, new String[] { button1Text, button2Text });
		}		
	}
		
	@SuppressWarnings("deprecation")
	private void showStandardAlert(String message, String title, final NotifierButtonListener listener, final String[] buttonText) {
		AlertDialog alertDialog;
		if (isaService) {
			alertDialog = new AlertDialog.Builder(sContainer.$formService()).create();
		} else {
			alertDialog = new AlertDialog.Builder(container.$context()).create();
		}
		alertDialog.setTitle(title);
		// prevents the user from escaping the dialog by hitting the Back button
		alertDialog.setCancelable(false);
		alertDialog.setMessage(message);
		if (buttonText.length > 0) {
			alertDialog.setButton(buttonText[0], new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (listener == null) {
						AfterChoosing(buttonText[0]);
					} else {
						listener.onButtonClicked(buttonText[0]);
					}				
				}
			});
			if (buttonText.length > 1) {
				alertDialog.setButton2(buttonText[1], new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (listener == null) {
							AfterChoosing(buttonText[1]);
						} else {
							listener.onButtonClicked(buttonText[1]);
						}
					}
				});
			}
			if (buttonText.length > 2) {
				alertDialog.setButton3(buttonText[2], new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (listener == null) {
							AfterChoosing(buttonText[2]);
						} else {
							listener.onButtonClicked(buttonText[2]);
						}
					}
				});
			}
		}
		alertDialog.show();
	}
	
	private void showCustomAlert(String message, String title, final NotifierButtonListener listener, final String[] buttonText) {
		CustomDialog.Builder alertDialog;
		if (isaService) {
			alertDialog = new CustomDialog.Builder(sContainer.$formService());
		} else {
			alertDialog = new CustomDialog.Builder(container.$context());
		}
		alertDialog.setTitle(title);
		// prevents the user from escaping the dialog by hitting the Back button
		alertDialog.setCancelable(false);
		alertDialog.setMessage(message);
		if (buttonText.length > 0) {
			alertDialog.setPositiveButton(buttonText[0], new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (listener == null) {
						AfterChoosing(buttonText[0]);
					} else {
						listener.onButtonClicked(buttonText[0]);
					}
				}
			});
			if (buttonText.length > 1) {
				alertDialog.setNegativeButton(buttonText[1], new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (listener == null) {
							AfterChoosing(buttonText[1]);
						} else {
							listener.onButtonClicked(buttonText[1]);
						}
					}
				});
			}
			if (buttonText.length > 2) {
				alertDialog.setNeutralButton(buttonText[2], new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (listener == null) {
							AfterChoosing(buttonText[2]);
						} else {
							listener.onButtonClicked(buttonText[2]);
						}
					}
				});
			}
		}
		alertDialog.create().show();
	}

	/**
	 * Event after the user has made a selection for ShowChooseDialog.
	 * 
	 * @param choice
	 *            is the text on the button the user pressed
	 */
	public void AfterChoosing(String choice) {
		EventDispatcher.dispatchEvent(this, "AfterChoosing", choice);
	}

	public void AfterChoosing() {
		EventDispatcher.dispatchEvent(this, "AfterChoosing");
	}

	public void ShowTextDialogPreText(String message, String title, String enteredText, boolean cancelable) {
		textInputAlert(message, title, enteredText, cancelable);
	}

	public void ShowTextDialogPreText(String message, String title, String enteredText) {
		textInputAlert(message, title, enteredText, false);
	}

	public void ShowTextDialog(String message, String title, boolean cancelable) {
		textInputAlert(message, title, null, cancelable);
	}

	public void ShowTextDialog(String message, String title) {
		textInputAlert(message, title, null, false);
	}

	/**
	 * Display an alert with a text entry. Raises the AfterTextInput event when the text has been entered and the user
	 * presses "OK".
	 * 
	 * @param message
	 *            the text in the alert box
	 * @param title
	 *            the title for the alert box
	 */
	private void textInputAlert(String message, String title, String pretext, boolean cancelable) {
		Log.i(LOG_TAG, "Text input alert: " + message);
		if (customIds != null) {
			Log.i(LOG_TAG, "Using custom dialog layout");
			showCustomTextInputDialog(message, title, pretext, cancelable);
		} else {
			showStandardTextInputDialog(message, title, pretext, cancelable);
		}
	}

	@SuppressWarnings("deprecation")
	private void showStandardTextInputDialog(String message, String title, String pretext, boolean cancelable) {
		AlertDialog alertDialog;
		if (isaService) {
			alertDialog = new AlertDialog.Builder(sContainer.$formService()).create();
		} else {
			alertDialog = new AlertDialog.Builder(container.$context()).create();
		}
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		// Set an EditText view to get user input
		final EditText input;
		if (isaService) {
			input = new EditText(sContainer.$formService());
		} else {
			input = new EditText(container.$context());
		}
		if (pretext != null) {
			input.setText(pretext);
		}
		alertDialog.setView(input);
		// prevents the user from escaping the dialog by hitting the Back button
		alertDialog.setCancelable(false);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				AfterTextInput(input.getText().toString());
			}
		});
		if (cancelable) {
			alertDialog.setButton2("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		alertDialog.show();
	}

	private void showCustomTextInputDialog(String message, String title, String pretext, boolean cancelable) {
		CustomDialog.Builder alertDialog;
		if (isaService) {
			alertDialog = new CustomDialog.Builder(sContainer.$formService());
		} else {
			alertDialog = new CustomDialog.Builder(container.$context());
		}
		alertDialog.setDialogIds(customIds);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		// Set an EditText view to get user input
		final EditText input;
		if (isaService) {
			input = new EditText(sContainer.$formService());
		} else {
			input = new EditText(container.$context());
		}
		if (pretext != null) {
			input.setText(pretext);
		}
		alertDialog.setContentView(input);
		// prevents the user from escaping the dialog by hitting the Back button
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				AfterTextInput(input.getText().toString());
			}
		});
		if (cancelable) {
			alertDialog.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		alertDialog.create().show();
	}

	/**
	 * Event raised after the user has responded to ShowTextDialog.
	 * 
	 * @param response
	 *            is the text that was entered
	 */

	public void AfterTextInput(String response) {
		EventDispatcher.dispatchEvent(this, "AfterTextInput", response);
	}

	public void CustomAlertLayout(int layoutRes, int rootRes, int textViewRes) {
		this.layoutRes = layoutRes;
		this.textRes = textViewRes;
		this.rootRes = rootRes;
	}

	/**
	 * Display a temporary notification
	 * 
	 * @param notice
	 *            the text of the notification
	 */

	public void ShowAlert(final String notice) {
		handler.post(new Runnable() {
			public void run() {
				if (isaService) {
					Toast.makeText(getContext(), notice, Toast.LENGTH_LONG).show();
				} else {
					showToastAlert(container.getRegistrar(), notice);
				}
			}
		});
	}

	public static void ShowAlert(final Form form, final String notice) {
		form.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(form, notice, Toast.LENGTH_LONG).show();
			}
		});
	}

	private void showToastAlert(Registrar context, String notice) {
		if (layoutRes != 0 && textRes != 0 && rootRes != 0) {
			LayoutInflater inflater = context.getLayoutInflater();
			View layout = inflater.inflate(layoutRes, (ViewGroup) context.findViewById(rootRes));
			TextView text = (TextView) layout.findViewById(textRes);
			text.setText(notice);
			Toast toast = new Toast(container.$context());
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();
		} else {
			Toast.makeText(container.$context(), notice, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Display a message on the phone's notification bar. This will also play the user's default sound for
	 * notifications.
	 * 
	 * @param iconResourceId
	 *            The resourceId of the icon you want displayed along with the message.
	 * @param title
	 *            - The title of the message
	 * @param tickerText
	 *            - The ticker text that scrolls when the message is first delivered.
	 * @param message
	 *            - The message seen when the user opens their notification area.
	 */
	@SuppressWarnings("deprecation")
	public void ShowStatusBarNotification(int iconResourceId, String title, String tickerText, String message) {

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager manager;
		if (isaService) {
			manager = (NotificationManager) sContainer.$formService().getSystemService(ns);
		} else {
			manager = (NotificationManager) container.$context().getSystemService(ns);
		}
		long when = System.currentTimeMillis();
		notification = new Notification(iconResourceId, tickerText, when);
		if (notificationFlag != -1) {
			notification.flags |= notificationFlag;
		}
		notification.defaults |= Notification.DEFAULT_SOUND;
		if (noteColor != -1) {
			notification.ledARGB = noteColor;
			notification.ledOffMS = flashOff;
			notification.ledOnMS = flashOn;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		} else {
			notification.flags |= Notification.DEFAULT_LIGHTS;
		}

		Context context = getContext();
		Intent notificationIntent;
		if (classToSpawn != null) {
			notificationIntent = new Intent(context, classToSpawn);
		} else {
			notificationIntent = new Intent(Intent.ACTION_VIEW);
			notificationIntent.setData(dataUri);
		}
		if (intentFlag != -1) {
			notificationIntent.addFlags(intentFlag);
		}
		PendingIntent contentIntent;
		if (spawnService) {
			contentIntent = PendingIntent.getService(context, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, title, message, contentIntent);
		} else {
			contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, title, message, contentIntent);
		}
		manager.notify(NOTIFIER_ID, notification);

	}

	/**
	 * Display a message on the phone's notification bar. This will also play the user's default sound for
	 * notifications.
	 * 
	 * @param iconResourceId
	 *            The resourceId of the icon you want displayed along with the message.
	 * @param notifierId
	 *            A unique number representing this notification. Use this method if you need to display multiple
	 *            notifications in the status bar.
	 * @param title
	 *            - The title of the message
	 * @param tickerText
	 *            - The ticker text that scrolls when the message is first delivered.
	 * @param message
	 *            - The message seen when the user opens their notification area.
	 */
	@SuppressWarnings("deprecation")
	public void ShowStatusBarNotification(int iconResourceId, int notifierId, String title, String tickerText, String message) {

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager manager;
		if (isaService) {
			manager = (NotificationManager) sContainer.$formService().getSystemService(ns);
		} else {
			manager = (NotificationManager) container.$context().getSystemService(ns);
		}
		long when = System.currentTimeMillis();
		notification = new Notification(iconResourceId, tickerText, when);
		if (notificationFlag != -1) {
			notification.flags |= notificationFlag;
		}
		notification.defaults |= Notification.DEFAULT_SOUND;
		if (noteColor != -1) {
			notification.ledARGB = noteColor;
			notification.ledOffMS = flashOff;
			notification.ledOnMS = flashOn;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		} else {
			notification.flags |= Notification.DEFAULT_LIGHTS;
		}

		Context context = getContext();
		Intent notificationIntent;
		if (classToSpawn != null) {
			notificationIntent = new Intent(context, classToSpawn);
		} else {
			notificationIntent = new Intent(Intent.ACTION_VIEW);
			notificationIntent.setData(dataUri);
		}
		if (intentFlag != -1) {
			notificationIntent.addFlags(intentFlag);
		}
		PendingIntent contentIntent;
		if (spawnService) {
			contentIntent = PendingIntent.getService(context, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, title, message, contentIntent);
		} else {
			contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, title, message, contentIntent);
		}
		manager.notify(notifierId, notification);
	}

	/**
	 * A more configurable version of ShowStatusBarNotification. This method allows you to set the sound that is played
	 * when the notification is sent out, and also allows you to set it to vibrate.
	 * 
	 * @param iconResourceId
	 *            - The resourceId of the icon to use
	 * @param title
	 *            - The title of the Notification
	 * @param tickerText
	 *            - The ticker text that scrolls when the message is first delivered.
	 * @param message
	 *            - The message seen when the user opens their notification area.
	 * @param soundFile
	 *            - The resource Id of the sound file to use when the notification is first sent.
	 * @param vibrate
	 *            - Whether or not to vibrate as well as make a sound
	 */
	@SuppressWarnings("deprecation")
	public void ShowStatusBarNotification2(int iconResourceId, String title, String tickerText, String message, int soundFile, boolean vibrate) {

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager manager;
		Context context = getContext();

		manager = (NotificationManager) context.getSystemService(ns);

		long when = System.currentTimeMillis();
		notification = new Notification(iconResourceId, tickerText, when);
		if (notificationFlag != -1) {
			notification.flags |= notificationFlag;
		}
		if (noteColor != -1) {
			notification.ledARGB = noteColor;
			notification.ledOffMS = flashOff;
			notification.ledOnMS = flashOn;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		} else {
			notification.flags |= Notification.DEFAULT_LIGHTS;
		}
		if (vibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		Uri path = Uri.parse("android.resource://" + context.getPackageName() + "/" + soundFile);
		notification.sound = path;

		Intent notificationIntent;
		PendingIntent contentIntent;
		notificationIntent = new Intent(context, classToSpawn);
		if (intentFlag != -1) {
			notificationIntent.addFlags(intentFlag);
		}

		if (spawnService) {
			contentIntent = PendingIntent.getService(context, 0, notificationIntent, 0);
		} else {
			contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		}
		notification.setLatestEventInfo(context, title, message, contentIntent);

		manager.notify(NOTIFIER_ID, notification);

	}

	/**
	 * Cancels the current notification.
	 * 
	 */
	public void cancelNotification() {
		Context context = getContext();
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (notification != null) {
			manager.cancel(NOTIFIER_ID);
		}
	}

	/**
	 * Cancels the current notification.
	 * 
	 */
	public void cancelNotification(int notifierId) {
		Context context = getContext();
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (notification != null) {
			manager.cancel(notifierId);
		}
	}

	/**
	 * Set the LED color of the notification. Note that this will vary from device to device. The hardware will do it's
	 * best to match the color set here. You must also set the time in ms that the light will be on, and off (a common
	 * one is 300 on, 1000 off). This only applies when using the ShowStatusBarNotification methods.
	 * 
	 * @param color
	 * @param off
	 *            - The amount of time in ms the light is off
	 * @param on
	 *            - The amount of time in ms the light is on
	 */
	public void NotificationLEDColor(int color, int off, int on) {
		noteColor = color;
		flashOff = off;
		flashOn = on;
	}

	/**
	 * This allows you to set the flag of the intent when using the ShowStatusBarNotification (FLAG_ACTIVITY_CLEAR_TOP,
	 * FLAG_ACTIVITY_NEW_TASK, etc)
	 * 
	 * @param flag
	 */
	public void setIntentFlag(int flag) {
		intentFlag = flag;
	}

	/**
	 * This is when using the notification bar to show a message. This method sets the class that is opened when the
	 * notification is clicked. This will almost always be a Form (but it's possible you want a service to be started).
	 * 
	 * 
	 * @param classtoopen
	 * @param isaService
	 *            - Set to true if the class to open is a formservice
	 */
	public void setClassToOpen(Class<?> classtoopen, boolean isFormService) {
		classToSpawn = classtoopen;
		spawnService = isFormService;
		dataUri = null;
	}
	
	/**
	 * This is an alternate to using setClassToOpen. This will instead launch an intent with the VIEW action,
	 * with the uri specified in this method. If the uri is set, the class to open will be ignored, and vice
	 * versa.
	 * 
	 * @param uri
	 */
	public void setDataUri(Uri uri) {
		dataUri = uri;
		classToSpawn = null;
	}

	/**
	 * This sets what happens when the notification is touched. Notifier.AUTO_CANCEL, ONGOING, and INSISTENT are your
	 * options.
	 * 
	 * @param flag
	 */
	public void setNotificationFlag(int flag) {
		notificationFlag = flag;
	}

	/**
	 * Use this method to set the ids of your custom dialog layout. The ids contain the resource ids of the components
	 * in the dialog layout (like the positive, neutral, and negative buttons, the layout file, etc). When this is set,
	 * your custom dialog layout will be used for showing dialogs (this wont affect ShowAlert at all)
	 * 
	 * @param ids
	 */
	public void setCustomDialogIds(DialogIds ids) {
		customIds = ids;
	}

	/**
	 * Log an error message.
	 * 
	 * @param message
	 *            the error message
	 */

	public static void LogError(String message) {
		Log.e(LOG_TAG, message);
	}

	/**
	 * Log a warning message.
	 * 
	 * @param message
	 *            the warning message
	 */

	public static void LogWarning(String message) {
		Log.w(LOG_TAG, message);
	}

	/**
	 * Log an information message.
	 * 
	 * @param message
	 *            the information message
	 */

	public static void LogInfo(String message) {
		Log.i(LOG_TAG, message);
	}

	public interface NotifierButtonListener {
		public void onButtonClicked(String button);
	}

}
