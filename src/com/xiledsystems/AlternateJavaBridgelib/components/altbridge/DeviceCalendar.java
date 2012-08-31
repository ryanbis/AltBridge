package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.Cal;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.CalendarEvent;

@TargetApi(14)
public class DeviceCalendar extends AndroidNonvisibleComponent {
	
	private ArrayList<Cal> calendars = new ArrayList<Cal>();
	private long calId;
	
	
	public DeviceCalendar(ComponentContainer container) {
		super(container);
	}
	
	/**
	 * This method is for pushing an event to the calendar
	 * through an intent. This way does not require any special
	 * permissions in your manifest.
	 * 
	 * @param event
	 */
	public void pushEvent(CalendarEvent event) {
		Intent intent = new Intent(Intent.ACTION_INSERT).
		    setData(CalendarContract.Events.CONTENT_URI).
		    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startTime()).
		    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endTime()).
		    putExtra(Events.TITLE, event.Title()).
		    putExtra(Events.DESCRIPTION, event.Description());
		container.$context().startActivity(intent);
	}
	
	/**
	 * This method will add the event to the user's calendar
	 * without the use of an intent. However, this requires
	 * you to set the READ_CALENDAR, and WRITE_CALENDAR
	 * permissions.
	 * 
	 * @param event
	 * 
	 * @return The ID for the new event that was added.
	 */
	public String addEvent(CalendarEvent event) {
		if (calId == 0) {
			getCalendars();
		}
		ContentResolver cr = container.$context().getContentResolver();
		ContentValues values = new ContentValues();
		values.put(CalendarContract.Events.DTSTART, event.startTime());
		values.put(CalendarContract.Events.DTEND, event.endTime());
		values.put(CalendarContract.Events.TITLE, event.Title());
		values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getDisplayName());
		values.put(CalendarContract.Events.DESCRIPTION, event.Description());
		values.put(CalendarContract.Events.CALENDAR_ID, calId);
		Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
		
		return uri.getLastPathSegment();
	}
	
	public void CalendarId(long id) {
	  calId = id;
	}
	
	public ArrayList<Cal> GetAvailableCalendars() {
	  if (calendars.size() < 1) {
	    getCalendars();
	  }
	  if (calendars.size() < 1) {
	    Log.e("DeviceCalendar", "Unable to locate any available calendars!");
	  }
	  return calendars;
	}

	private void getCalendars() {
		Uri uri = CalendarContract.Calendars.CONTENT_URI;
		String[] projection = new String[] { 
				CalendarContract.Calendars._ID,
				CalendarContract.Calendars.ACCOUNT_NAME,
				CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
				CalendarContract.Calendars.NAME,
				CalendarContract.Calendars.CALENDAR_COLOR
		};
		ContentResolver cr = container.$context().getContentResolver();
		Cursor c = cr.query(uri, projection, null, null, null);
		if (c.moveToFirst()) {
		  do {
		    Cal cal = new Cal();
		    cal.setId(c.getLong(0));
		    cal.setAccountName(c.getString(1));
		    cal.setDisplayName(c.getString(2));
		    cal.setName(c.getString(3));
		    cal.setColor(c.getInt(4));
		    calendars.add(cal);
		  } while (c.moveToNext());
		}		
	}
	

}
