package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import java.util.ArrayList;


public class FPSLogger implements UpdateHandler {

	
	private final static int MS_IN_NANOS = 1000000;
	private final static int COUNT_LIMIT = 5;
	private long firstTick;
	private FPSListener listener;	
	private ArrayList<Float> fpsList;	
	private boolean enabled;
	private boolean finePrecision = true;
	private int count;
	
	
	public FPSLogger(OpenGLCanvas canvas) {
		((OpenGLView)canvas.getView()).updateThread.addObjectToUpdateList(this);
		fpsList = new ArrayList<Float>(100);		
	}
	
	/**
	 * Enable FPS logging.
	 * @param enabled
	 */
	public void Enabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * 
	 * @return whether this logger is enabled or not.
	 */
	public boolean Enabled() {
		return enabled;
	}
	
	/**
	 * This sets fine precision. The output will be averaged
	 * floats. Other wise, it will return an integer. In FPSListener
	 * is where these events are handled 
	 * @param finePrecision
	 */
	public void FinePrecision(boolean finePrecision) {
		this.finePrecision = finePrecision;
	}
	
	/**
	 * 
	 * @return whether this logger is set to output fine precision floats
	 */
	public boolean FinePrecision() {
		return finePrecision;
	}
	
	/**
	 * Set the listener for the FPS logger updates. The FPS is what is returned.
	 * There are two FPSUpdate() methods. One is for fine precision using a float,
	 * and the other uses an integer.
	 * 
	 * @param listener
	 */
	public void setFPSListener(FPSListener listener) {
		this.listener = listener;
	}
	
	public float averageFPS() {
		boolean reenable = false;
		if (enabled) {
			reenable = true;
		}
		enabled = false;
		Float avg = processAverageFPS(fpsList);
		if (reenable) {
			enabled = true;
		}
		return avg;
	}
			
	private Float averageFPS(ArrayList<Float> list) {
		return processAverageFPS(fpsList);
	}
	
	private int averageFPSInt(ArrayList<Float> list) {
		return (int) processAverageFPS(list);
	}
			
	@Override
	public void onUpdate(long now) {		
		if (enabled) {			
			if (firstTick == 0) {
				firstTick = now;
			} else {
				final long dif = getDiff(now, firstTick);
				final float fps = 1000f / dif;
				fpsList.add(fps);
				count++;
				if (listener != null && count >= COUNT_LIMIT) {
					if (finePrecision) {
						listener.FPSUpdate(averageFPS(fpsList));
					} else {
						listener.FPSUpdate(averageFPSInt(fpsList));						
					}
					fpsList.clear();
					count = 0;
				}
				firstTick = now;
			}
		}
	}
	
	private static float processAverageFPS(ArrayList<Float> list) {
		float f = 0;
		for (Float fl : list) {
			f += fl;
		}
		return f / list.size();
	}
		
	private static long getDiff(long now, long firstTick) {
		return ((now - firstTick) / MS_IN_NANOS);
	}

	@Override
	public boolean canDraw() {
		return true;
	}

	@Override
	public void addTick() {
	}

	@Override
	public void resetTickCount() {
	}

}
