package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.AndroidViewComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Canvas;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.ComponentContainer;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnDestroyListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnInitializeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnPauseListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnResumeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnStartListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnStopListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.DrawingCanvas;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.Registrar;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import android.view.View;
import android.widget.RelativeLayout;


public class OpenGLCanvas extends AndroidViewComponent implements DrawingCanvas, OnStopListener, OnResumeListener, OnPauseListener, 
																	OnInitializeListener, OnDestroyListener, OnStartListener {
	
	private OpenGLView view;
	private final RelativeLayout outerLayout;
	
	
	public OpenGLCanvas(ComponentContainer container) {
		super(container);		
		view = new OpenGLView(this);
		outerLayout = new RelativeLayout(container.$context());
		outerLayout.addView(view);
		container.getRegistrar().setOpenGL(true, outerLayout);
		container.getRegistrar().setContentView(outerLayout);
		container.getRegistrar().registerForOnStart(this);
		container.getRegistrar().registerForOnStop(this);
		container.getRegistrar().registerForOnResume(this);
		container.getRegistrar().registerForOnPause(this);
		container.getRegistrar().registerForOnInitialize(this);
	}
	
	public void addViewToParent(View newView) {
		outerLayout.addView(newView);
	}
	
	public void removeViewFromParent(View oldView) {
		outerLayout.removeView(oldView);
	}
	
	public void CanvasDrawingSpeed(int speedInMs) {
		view.CanvasDrawingSpeed(speedInMs);
	}
	
	public void CameraNear(float cameraNear) {
		view.renderer.CameraNear(cameraNear);
	}
	
	public void CanvasCoordinateSize(int width, int height) {
		view.xSize = width;
		view.ySize = height;
	}
	
	public int[] getRealScreenSize() {
		return new int[] { container.getRegistrar().getAvailWidth(), container.getRegistrar().getAvailHeight() };
	}
	
	public float CameraNear() {
		return view.renderer.CameraNear();
	}
	
	public void CameraFar(float cameraFar) {
		view.renderer.CameraFar(cameraFar);
	}
	
	public float CameraFar() {
		return view.renderer.CameraFar();
	}
	
	public void CameraEye(float x, float y) {
		view.renderer.CameraEye(x, y);
	}
	
	public float[] CameraEye() {
		return view.renderer.CameraEye();
	}
	
	public void $add(GLObject object) {
		view.addObject(object);
	}		
		
	@Override
	public View getView() {
		return view;
	}

	@Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, Events.ANIM_MIDDLE);		
	}
	
	@Override
	public boolean atEdge(int edge) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public void BackgroundColor(int color) {
		view.BackgroundColor(color);
	}
	
	@Override
	public int[] MoveCanvas(int xDifference, int yDifference) {
		// TODO Auto-generated method stub
		return null;
	}
		
	public void requestEvent(String eventName, Object... args) {
		EventDispatcher.dispatchEvent(this, eventName, args);
	}
	
	@Override
	public void onInitialize() {
		view.Initialize();
	}

	@Override
	public void onPause() {
		view.pauseUpdateThread();
		view.onPause();		
	}
	
	@Override
	public void onResume() {
		view.onResume();
		view.resumeUpdateThread();
	}
	
	@Override
	public void onStart() {
		//view.resumeUpdateThread();
	}

	@Override
	public void onStop() {		
		//view.pauseUpdateThread();		
	}
	
	@Override
	public void onDestroy() {
		view.destroyUpdateThread();
		view.onPause();				
	}

  @Override
  public Registrar getRegistrar() {
    return container.getRegistrar();
  }
  
  	@Override
	public void canvasInitialized() {		
		EventDispatcher.dispatchEvent(OpenGLCanvas.this, Events.CANVAS_INIT);
	}

}
