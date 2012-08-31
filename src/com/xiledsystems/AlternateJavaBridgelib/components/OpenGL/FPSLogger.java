package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import java.util.ArrayList;


public class FPSLogger implements UpdateHandler {

	
	private final static int MS_IN_NANOS = 1000000;
	private final static int COUNT_LIMIT = 5;
	private long firstTick;
	private FPSListener listener;	
	private ArrayList<Float> fpsList;
	private boolean enabled;
	private int count;
	
	
	public FPSLogger(OpenGLCanvas canvas) {
		((OpenGLView)canvas.getView()).updateThread.addObjectToUpdateList(this);
		fpsList = new ArrayList<Float>(100);
		
	}
	
	public void Enabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean Enabled() {
		return enabled;
	}
	
	public void setFPSListener(FPSListener listener) {
		this.listener = listener;
	}
	
	public float averageFPS() {
		boolean reenable = false;
		if (enabled) {
			reenable = true;
		}
		enabled = false;
		int size = fpsList.size();
		float total = 0;
		for (Float f : fpsList) {
			total += f;
		}
		if (reenable) {
			enabled = true;
		}
		return total / size;		
	}
	
	private Float averageFPS(ArrayList<Float> list) {
		float f = 0;
		for (Float fl : list) {
			f += fl;
		}
		return f / list.size();
	}
	
	@Override
	public void onUpdate(long now) {		
		if (enabled) {			
			if (firstTick == 0) {
				firstTick = now;
			} else {
				final long dif = (now - firstTick) / MS_IN_NANOS;
				final float fps = 1000f / dif;
				fpsList.add(fps);
				count++;
				if (listener != null && count >= COUNT_LIMIT) {
										
					listener.FPSUpdate(averageFPS(fpsList));
					fpsList.clear();
					count = 0;
				}
				firstTick = now;
			}
		}
	}

	@Override
	public boolean canDraw() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void addTick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetTickCount() {
		// TODO Auto-generated method stub

	}

}
