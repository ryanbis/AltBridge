package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import android.opengl.GLES20;

import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Form;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnInitializeListener;


public class GLTriangle extends GLMovingObject implements OnInitializeListener {
	
	
	public GLTriangle(OpenGLCanvas canvas, float vertex1X, float vertex1Y, float vertex2X, float vertex2Y, float vertex3X, float vertex3Y) {
		super(canvas);
		vertices = new float[9];
		vertices[0] = vertex1X;
		vertices[1] = vertex1Y;
		vertices[3] = vertex2X;
		vertices[4] = vertex2Y;
		vertices[6] = vertex3X;
		vertices[7] = vertex3Y;
		
		CenterOnSelf();
		
		Color(Form.COLOR_GREEN);
		
		buildShaderCode();
		
		// Register this triangle for the GL Canvas's renderer's onSurfaceCreated method
		glCanvas.renderer.registerForOnSurfaceCreated(this);		
		glCanvas.updateThread.addObjectToUpdateList(this);
		((OpenGLCanvas)glCanvas.canvas).$form().registerForOnInitialize(this);
	}
	
	

	@Override
	public void buildShaderCode() {
		setFragmentShader("precision mediump float;  \n" +
		        "void main(){              \n" +
		        " gl_FragColor = vec4 ("+RedValue()+", "+GreenValue()+", "+BlueValue()+", 1.0); \n" +
		        "}                         \n");
	}

	@Override
	public void glDraw() {
		if (Visible()) {
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
		}
	}

	
	@Override
	public void CenterOnSelf() {
		centerX = ( vertices[0] + vertices[3] + vertices[6] ) / 3f;
    	centerY = ( vertices[1] + vertices[4] + vertices[7] ) / 3f;
	}

	@Override
	public void onInitialize() {
		int width = glCanvas.canvas.getRealScreenSize()[0];
		int height = glCanvas.canvas.getRealScreenSize()[1];
		float ratio = (float) width / height;
		float xRatio = (ratio*2f) / glCanvas.xSize;
		float yRatio = 2f / glCanvas.ySize;
		
		vertices[0] = (vertices[0] * xRatio) - ratio;
		vertices[1] = 1f - (vertices[1] * yRatio);
		vertices[3] = (vertices[3] * xRatio) - ratio;
		vertices[4] = 1f - (vertices[4] * yRatio);
		vertices[6] = (vertices[6] * xRatio) - ratio;
		vertices[7] = 1f - (vertices[7] * yRatio);
		
		if (centerX != 0 || centerY != 0) {
			centerX = (centerX * xRatio) - ratio;
			centerY = 1f - (centerY * yRatio);
		}
	}
}
