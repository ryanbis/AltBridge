package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public abstract class GLRenderer implements Renderer {
	
	private boolean firstDraw;
	private boolean surfaceCreated;
	private boolean trackFPS;
	private int width;
	private int height;
	private long lastTime;
	private int fps;
	
	public GLRenderer() {
		firstDraw = true;
		surfaceCreated = false;
		width = -1;
		height = -1;
		lastTime = System.currentTimeMillis();
		fps = 0;
	}
	
	public void trackFPS(boolean track) {
		trackFPS = track;
	}
	
	public boolean trackingFPS() {
		return trackFPS;
	}
	
	public int getFPS() {
		return fps;
	}
	
	public abstract void onCreate(int width, int height, boolean contextLost);
	
	public abstract void onDrawFrame(boolean firstDraw);

	@Override
	public void onDrawFrame(GL10 arg0) {
		onDrawFrame(firstDraw);
		
		if (trackFPS) {
			fps++;
			long curTime = System.currentTimeMillis();
			if (curTime - lastTime >= 1000) {
				fps = 0;
				lastTime = curTime;
			}
		}
		
		if (firstDraw) {
			firstDraw = false;
		}
	}	

	@Override
	public void onSurfaceChanged(GL10 arg0, int w, int h) {
		if (!surfaceCreated && w == width && h == height) {
			// Surface changed, but already handled
			return;
		}
		width = w;
		height = h;
		
		onCreate(width, height, surfaceCreated);
		surfaceCreated = false;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		surfaceCreated = true;
		width = -1;
		height = -1;
	}

}
