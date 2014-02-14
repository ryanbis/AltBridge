package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2;

import android.view.View;
import android.widget.RelativeLayout;

import com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2.GLES20Renderer.OnGLSurfaceCreatedListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.AndroidViewComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.ComponentContainer;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnDestroyListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnInitializeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnPauseListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnResumeListener;


public class GLCanvas extends AndroidViewComponent implements OnPauseListener, OnResumeListener, OnInitializeListener, OnDestroyListener {
	
	private final GLSView view;
	private final GLES20Renderer renderer;
	
	private final int baseWidth;
	private final int baseHeight;
	
	private float baseWidthRatio;
	private float baseHeightRatio;
	
	// Outer container. The GL View will take up the entire container's space, but
	// this allows us to place other views on top of the GL View (such as a HUD)
	private RelativeLayout outerLayout;
	
	
	public GLCanvas(ComponentContainer container, int basewidth, int baseheight) {
		super(container);	
		
		baseHeight = baseheight;
		baseWidth = basewidth;
		view = new GLSView(container.$context());
		renderer = view.getRenderer();
		renderer.setOnSurfaceCreateListener(new OnGLSurfaceCreatedListener() {			
			@Override
			public void surfaceCreated(int width, int height) {
				baseWidthRatio = (float) width / baseWidth;
				baseHeightRatio = (float) height / baseHeight;
			}
		});
		outerLayout = new RelativeLayout(container.$context());
		outerLayout.addView(view);
		container.getRegistrar().setOpenGL(true, view);
		container.getRegistrar().setContentView(outerLayout);
		container.getRegistrar().registerForOnDestroy(this);
		container.getRegistrar().registerForOnInitialize(this);
		container.getRegistrar().registerForOnPause(this);
		container.getRegistrar().registerForOnResume(this);
	}
	
	protected void addGLObject(GLObject glObject) {
		renderer.addObject(glObject);
	}
	
	public GLES20Renderer getRenderer() {
		return view.getRenderer();
	}
	
	public float getWidthRatio() {
		return baseWidthRatio;
	}
	
	public float getHeightRatio() {
		return baseHeightRatio;
	}
	
	/**
	 * 
	 * @return The ratio of the current canvas (width / height)
	 */
	public float Ratio() {
		return renderer.getRatio();
	}
	
	@Override
	public View getView() {
		return view;
	}
	
	@Override
	public void onResume() {
		view.onResume();
	}

	@Override
	public void onPause() {
		view.onPause();
	}
	
}
