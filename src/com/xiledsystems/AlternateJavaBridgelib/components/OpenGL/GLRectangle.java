package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnInitializeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;


public class GLRectangle extends GLMovingObject implements OnInitializeListener {
		
    protected float width = 1.0f;
    protected float height = 1.0f;
    protected float xLeft = -1.0f;
    protected float yTop = 1.0f;
    
    protected ShortBuffer indexBuffer;
    
    private Events.Event eventListener;
    
    //protected short indices[] = { 0, 1, 2, 0, 2, 3 };
    protected short indices[] = { 3, 2, 0, 2, 1, 0 };
    
    private boolean vertsConverted;
    
	
	public GLRectangle(OpenGLCanvas canvas) {
		super(canvas);
		vertices = new float[12];		
		buildShaderCode();			
				
		// Register this rectangle for the GL Canvas renderer's onSurfaceCreated
		// method, as well as adding it to the updatethread for any rotation or movement
		// Initialize is needed to convert coordinate system to opengl coordinates
		glCanvas.renderer.registerForOnSurfaceCreated(this);		
		glCanvas.updateThread.addObjectToUpdateList(this);
		((OpenGLCanvas)glCanvas.canvas).getRegistrar().registerForOnInitialize(this);
	}
	
	/**
	 * Set the starting position of this object.
	 * 
	 * @param x
	 * @param y
	 */
	public void StartPosition(float x, float y) {
		xLeft = x;
		yTop = y;
		setVertices();		
	}
	
	/**
	 * Set the size in width/height of this object
	 *  
	 * @param width
	 * @param height
	 */
	public void Size(float width, float height) {
		this.width = width;
		this.height = height;
		setVertices();
		
	}
	
	protected void setVertices() {
		vertices[0] = xLeft;
		vertices[1] = yTop;
		vertices[3] = xLeft;
		vertices[4] = yTop + height;
		vertices[6] = xLeft + width;
		vertices[7] = yTop + height;
		vertices[9] = xLeft + width;
		vertices[10] = yTop;		
	}

	@Override
	public void CenterOnSelf() {
		centerX = xLeft + (width / 2f);
		centerY = yTop + (height / 2f);
	}
	
	@Override
	public void onSurfaceCreated() {
		super.onSurfaceCreated();
		
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
	}

	@Override
	public void buildShaderCode() {
		setVertexShader(GLConstants.COLOR_VERTEX_SHADER_CODE);
		setFragmentShader(GLConstants.ColorFragShaderCode(RedValue(), GreenValue(), BlueValue()));
	}

	@Override
	protected void glDraw() {
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
	}
	
	protected void scaleVertices(boolean initializing) {
		if (vertices[0] == xLeft) { 
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
			
			vertices[9] = (vertices[9] * xRatio) - ratio;
			vertices[10] = 1f - (vertices[10] * yRatio);
		
			if (initializing) {
				if (centerX != 0 || centerY != 0) {
					centerX = (centerX * xRatio) - ratio;
					centerY = 1f - (centerY * yRatio);
				}
			}
			vertsConverted = true;
		}
	}
	
	@Override
	  public Events.Event getEventListener() {
		  return eventListener;
	  }

	@Override
	public void onInitialize() {
		scaleVertices(true);		
		
	}
	
	
}
