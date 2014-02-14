package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GLSView extends GLSurfaceView {
	
	private final GLES20Renderer renderer;

	public GLSView(Context context) {
		super(context);
		
		// Create an Open GL ES 2.0 context
		setEGLContextClientVersion(2);
		
		// Create our renderer, which will draw on the surfaceview
		renderer = new GLES20Renderer();
		setRenderer(renderer);
		
		// We'll start out in dirty rendering mode
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	public GLES20Renderer getRenderer() {
		return renderer;
	}

}
