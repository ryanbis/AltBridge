package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import java.util.HashSet;
import java.util.Set;

public class Updater extends Thread {
	
	private final static int MS_IN_NANOS = 1000000;
	private int TICK_SIZE = MS_IN_NANOS * 16;
	private boolean enabled;
	private final OpenGLView canvas;
	private int threadPriority = android.os.Process.THREAD_PRIORITY_DEFAULT;
	private long lastTick;
	private long nextTick;
	private Set<UpdateHandler> objects = new HashSet<UpdateHandler>();
	private boolean requestRefresh;
	private long sleepTime;
	private long now;
	
	
	public Updater(OpenGLView canvas) {
		super(canvas.getClass().getSimpleName());
		this.canvas = canvas;
		lastTick = System.nanoTime();
		nextTick = lastTick + TICK_SIZE;
	}		
	
	@Override
	public void run() {		
		android.os.Process.setThreadPriority(threadPriority);
		try {			
			while (enabled) {
				now = System.nanoTime();
				if (now >= nextTick) {
					synchronized (objects) {
						for (UpdateHandler obj : objects) {							
							if (obj.canDraw()) {	
								requestRefresh = true;
								obj.onUpdate(now);
								obj.resetTickCount();
							} else {
								obj.addTick();
							}
						}
						if (requestRefresh) {
							canvas.requestRender();
							requestRefresh = false;
						}					
					}
					lastTick = now;
					nextTick = now + TICK_SIZE;
					sleepTime = (System.nanoTime() - nextTick) / MS_IN_NANOS;
					if (sleepTime > 0) {
						Thread.sleep(sleepTime);
					}
				}
			}
		} catch (final InterruptedException e) {
			// This is most likely wanted, just interrupt.
			this.interrupt();
		}
	}	
		
	public void setTickSize(int sizeInMs) {
		TICK_SIZE = sizeInMs * MS_IN_NANOS;
	}
	
	public void setThreadPriority(int priority) {
		threadPriority = priority;
	}
	
	public synchronized void Enabled(boolean enabled) {
		this.enabled = enabled;		
	}
	
	public boolean Enabled() {
		return enabled;
	}
	
	public void addObjectToUpdateList(UpdateHandler component) {
		objects.add(component);
	}
	
	public void removeObjectFromUpdateList(UpdateHandler component) {
		if (objects.contains(component)) {
			objects.remove(component);
		}
	}	
	
	public void clearAllObjects() {
		objects.clear();
	}

}
