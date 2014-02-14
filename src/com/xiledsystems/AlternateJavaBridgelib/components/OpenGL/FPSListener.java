package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

public interface FPSListener {

	/**
	 * This method is called when the FPSLogger it is attached to is set
	 * to FinePrecision (this is the default setting).
	 * @param fps
	 */
	public void FPSUpdate(float fps);
	
	/**
	 * This method is called when the FPSLogger it is attached to is NOT
	 * set to FinePrecision.
	 * @param fps
	 */
	public void FPSUpdate(int fps);
	
}
