package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class AlarmIntent implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7055915969755167861L;
	private boolean wakeAlarm;
	private Class<?> classToOpen;
	private int alarmId;
	
	public AlarmIntent() {
		
	}
	
	public AlarmIntent(int alarmId, boolean wakeAlarm, Class<?> classToOpen) {
		this.alarmId = alarmId;
		this.wakeAlarm = wakeAlarm;
		this.classToOpen = classToOpen;
	}
	
	public Class<?> getClassToOpen() {
		return classToOpen;
	}
 	
	public boolean isWakeAlarm() {
		return wakeAlarm;
	}
	
	public int getId() {
		return alarmId;
	}
	
	// Serializable implementation
		private void writeObject(ObjectOutputStream out) throws IOException {
			
			out.writeInt(alarmId);
			out.writeBoolean(wakeAlarm);
			out.writeObject(classToOpen);
			
		}
		
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			
			alarmId = in.readInt();
			wakeAlarm = in.readBoolean();
			classToOpen = (Class<?>) in.readObject();
			
		}

}
