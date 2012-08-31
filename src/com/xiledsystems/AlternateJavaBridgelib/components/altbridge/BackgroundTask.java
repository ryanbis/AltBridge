package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

import android.os.AsyncTask;

public class BackgroundTask extends AndroidNonvisibleComponent {

	private BTask task;
	private boolean success;
	
	public BackgroundTask(ComponentContainer container) {
		super(container);
		task = new BTask();				
	}
	
	public BackgroundTask(SvcComponentContainer container) {
		super(container);
		task = new BTask();
	}
	
	public synchronized void setSuccess(boolean success) {
		this.success = success; 
	}	
	
	public void startTask(String... data) {
		task.execute(data);
	}
	
	private class BTask extends AsyncTask<String, Void, Boolean> {
	
		@Override
		protected void onPreExecute() {
			EventDispatcher.dispatchEvent(BackgroundTask.this, Events.BEFORE_EXECUTE);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			Object[] data = params;
			EventDispatcher.dispatchEvent(BackgroundTask.this, Events.TASK_EXECUTE, data);
			return success;
		}
					
		@Override
		protected void onPostExecute(final Boolean success) {
			EventDispatcher.dispatchEvent(BackgroundTask.this, Events.TASK_COMPLETE, success);
		}

	}

}
