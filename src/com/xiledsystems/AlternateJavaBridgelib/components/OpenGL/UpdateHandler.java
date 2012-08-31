package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

public interface UpdateHandler {
	
	
	public void onUpdate(long now);
			
	public boolean canDraw();
	
	public void addTick();
	
	public void resetTickCount();
	
}
