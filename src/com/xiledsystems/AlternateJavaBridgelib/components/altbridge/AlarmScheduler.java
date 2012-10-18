package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.AlarmIntent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.DoubleList;


public class AlarmScheduler extends AndroidNonvisibleComponent implements OnStartListener, OnStartCommandListener {
	
	private final static String TAG = "AlarmScheduler";
	private final static String ALARM = "AlarmInfo.db";
	private int alarmId = 5844;
	private DoubleList wakeTimes;
	private TinyDB registeredAlarms;
	
	
	/**
	 * Constructor for AlarmScheduler in a Form. This component
	 * will register an android alarm. Basically, it will send
	 * the "Alarm" event through the Form/FormService that it is to wake
	 * upon the alarm going off.
	 * 
	 * @param container
	 */
	public AlarmScheduler(ComponentContainer container) {
		super(container);
		wakeTimes = new DoubleList();
		registeredAlarms = new TinyDB(container.$form());
		container.$form().registerForOnStart(this);
		
	}
	
	/**
	 * Constructor for AlarmScheduler in a FormService. This component
	 * will register an android alarm. Basically, it will send
	 * the "Alarm" event through the Form/FormService that it is to wake
	 * upon the alarm going off.
	 * 
	 * @param container
	 */
	public AlarmScheduler(SvcComponentContainer container) {
		super(container);
		wakeTimes = new DoubleList();
		registeredAlarms = new TinyDB(container.$formService());
		container.$formService().registerForOnStartCommand(this);
	}
	
	/**
	 * Use this to add an alarm event to go off at a specified time.
	 * 
	 * @param tag - A name to attach to this alarm for tracking purposes (if you want to cancel it)
	 * @param timeToWake - The time in ms to have this alarm go off
	 * @param classToOpen - The class to wake when this alarm goes off (can be form or formservice)
	 * @param isService - Set this to true if you want the alarm to wake a service
	 * 
	 * @return the alarmId used for this alarm (this is returned as args[0] in the thrown event)
	 */
	public int addAlarm(String tag, long timeToWake, Class<?> classToOpen, boolean isService) {
		Context context = getContext();
				
		AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Intent intent;
		intent = new Intent(context, classToOpen);
		
			
		PendingIntent pintent;
		boolean remove = false;
		if (wakeTimes.listOneContains(tag)) {
			int indx = wakeTimes.index(tag);
			alarmId = ((AlarmIntent) wakeTimes.get(indx)[1]).getId();
			remove = true;
		}
		intent.putExtra(Form.ALARM_EVENT, alarmId);
		if (isService) {
			pintent = PendingIntent.getService(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		} else {
			pintent = PendingIntent.getActivity(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		}
		int alId = alarmId;
		alarmId++;
		am.set(AlarmManager.RTC_WAKEUP, timeToWake, pintent);
		AlarmIntent aIntent = new AlarmIntent(alarmId, false, classToOpen);
		if (remove) {
			int index = wakeTimes.index(tag);
			wakeTimes.remove(index);
		}
		wakeTimes.add(tag, aIntent);		
		synchronized (registeredAlarms) {
			registeredAlarms.StoreValue(ALARM, wakeTimes);
		}
		return alId;
	}
	
	/**
	 * Use this to add an alarm event to go off at a specified time. This will
	 * wake the screen up if it has fallen asleep. It also will lock the screen
	 * on until the app loses focus (or you call LockScreen(false) ). This means
	 * you can only use this to open an Activity.
	 * 
	 * NOTE: This requires the WAKE_LOCK permission to be able to wake the screen.
	 * 
	 * @param tag - A name to attach to this alarm for tracking purposes (if you want to cancel it)
	 * @param timeToWake - The time in ms to have this alarm go off
	 * @param classToOpen - The class to wake when this alarm goes off (can be form or formservice)
	 * @param isService - Whether or not the class to open is a FormService
	 * @param flag - Manually set the flags for the form receiving the alarm. Default is to wake
	 * the screen up, and unlock it, and dismiss the keyguard. If you want the default, you can leave
	 * out this argument.
	 * 
	 */
	public void addWakeAlarm(String tag, long timeToWake, Class<?> classToOpen, boolean isService, int... flag) {
		Context context;
		if (container == null) {
			context = sContainer.$context();
		} else {
			context = container.$context();
		}
		AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(context, classToOpen);
		boolean remove = false;
		if (wakeTimes.listOneContains(tag)) {
			int indx = wakeTimes.index(tag);
			alarmId = ((AlarmIntent) wakeTimes.get(indx)[1]).getId();
			remove = true;
		}
		intent.putExtra(Form.ALARM_EVENT, alarmId);
		if (flag == null || flag.length < 1) {
			flag = new int[3];
			flag[0] = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
    		flag[1] = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
    		flag[2] = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
		}
		intent.putExtra(Form.ALARM_WAKE, flag);
		PendingIntent pintent;
		if (isService) {
			pintent = PendingIntent.getService(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		} else {
			pintent = PendingIntent.getActivity(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		}		
		alarmId++;
		am.set(AlarmManager.RTC_WAKEUP, timeToWake, pintent);
		AlarmIntent aIntent = new AlarmIntent(alarmId, true, classToOpen);
		if (remove) {
			int index = wakeTimes.index(tag);
			wakeTimes.remove(index);
		}
		wakeTimes.add(tag, aIntent);	
		synchronized (registeredAlarms) {
			registeredAlarms.StoreValue(ALARM, wakeTimes);
		}
	}
	
	/**
	 * Use this to cancel an alarm that you previously added.
	 * 
	 * @param tag - The name of the alarm you added in the addAlarm method
	 */
	public void cancelAlarm(String tag) {
		Context context = getContext();
		AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		if (wakeTimes.listOneContains(tag)) {		
			int index = wakeTimes.index(tag);
			Intent intent = new Intent(context, ((AlarmIntent)wakeTimes.get(index)[1]).getClassToOpen());
			if (((AlarmIntent)wakeTimes.get(index)[1]).isWakeAlarm()) {
				intent.putExtra(Form.ALARM_WAKE, true);
			}
			PendingIntent pIntent = PendingIntent.getActivity(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am.cancel(pIntent);
		} else {
			Log.e(TAG, "Tag not found in registered alarm database!");
		}
	}
		

	@SuppressWarnings("unchecked")
	@Override
	public void onStartCommand() {
		if (!registeredAlarms.GetValue(ALARM).equals("null")) {
			DoubleList tmp = new DoubleList();
			try {
				tmp = (DoubleList) registeredAlarms.GetValue(ALARM);
			} catch (ClassCastException e) {
				Log.e(TAG, "Past alarms were stored, but alarm scheduler alarm data appears corrupt.");				
			}
			if (!tmp.equals(wakeTimes)) {
				if (tmp.size() < 1) {
					wakeTimes = tmp;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onStart() {
		if (!registeredAlarms.GetValue(ALARM).equals("null")) {
			DoubleList tmp = new DoubleList();
			try {
				tmp = (DoubleList) registeredAlarms.GetValue(ALARM);
			} catch (ClassCastException e) {
				Log.e(TAG, "Past alarms were stored, but alarm scheduler alarm data appears corrupt.");				
			}
			if (!tmp.equals(wakeTimes)) {
				if (tmp.size() < 1) {
					wakeTimes = tmp;
				}
			}
		}
	}
	

}
