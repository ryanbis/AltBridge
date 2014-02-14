package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;


@SuppressLint("ViewConstructor")
public class OpenGLView extends GLSurfaceView  {
	
	protected GLRenderer renderer;
	protected final OpenGLCanvas canvas;
	protected final Updater updateThread;
	
	protected int xSize;
	protected int ySize;
	
	protected float canvasCoordXRatio;
	protected float canvasCoordYRatio;
	
	public OpenGLView(OpenGLCanvas context) {
		super(context.getRegistrar().$context());		
		updateThread = new Updater(this);
				
		canvas = context;
		setEGLContextClientVersion(2);
		
		renderer = new GLRenderer(this);
		setRenderer(renderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	public void Initialize() {
		updateThread.Enabled(true);
		updateThread.start();		
	}
	
	public void postOnGLThread(Runnable run) {
		renderer.postOnGLThread(run);
	}
	
	public void addObject(GLObject object) {
		renderer.addObject(object);		
	}
	
	public void CanvasDrawingSpeed(int speedInMs) {
		updateThread.setTickSize(speedInMs);
	}
	
	public void addObjectToUpdateThread(UpdateHandler component) {
		updateThread.addObjectToUpdateList(component);
	}
	
	public void removeObjectFromUpdateThread(UpdateHandler component) {
		updateThread.removeObjectFromUpdateList(component);
	}
	
	public void pauseUpdateThread() {
		updateThread.Enabled(false);
	}
	
	public void resumeUpdateThread() {
		updateThread.Enabled(true);
	}
	
	public void destroyUpdateThread() {
		updateThread.Enabled(false);
		updateThread.clearAllObjects();
		boolean retry = true;
		while (retry) {
			try {
				updateThread.join();
				updateThread.interrupt();
										
				retry=false;
				
			} catch (InterruptedException e) {						
				Thread.State state = updateThread.getState();						
				if (state.equals(Thread.State.RUNNABLE)) {
					updateThread.interrupt();
					retry = false;
				}
			}
		}
	}
	
	protected float[] ViewMatrix() {
		return renderer.ViewMatrix();
	}

	protected float[] ProjectionMatrix() {
		return renderer.ProjectionMatrix();
	}
	 
	public void BackgroundColor(int color) {
		renderer.BackgroundColor(color);
		requestRender();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		return renderer.parseEvent(event, getWidth(), getHeight());
	}
	
			
}
