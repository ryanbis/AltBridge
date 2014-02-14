package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;

/**
 * Base object for any visible object to be placed in an OpenGLCanvas.
 * Triangles, Rectangles, Sprites, etc should extend this class.
 * 
 * @author Ryan Bis
 *
 */
public abstract class GLObject implements onSurfaceChangedListener,
		onGLSurfaceCreated, UpdateHandler, Component {
	
	private final static String TAG = "GLObject";
	protected final OpenGLView glCanvas;
	
	private float projMatrix[];
    private float mvMatrix[];
    
    private float rValue;
	private float gValue;
	private float bValue;
	
	private int vertexShader;
	private int fragmentShader;
	
	private FloatBuffer vertexBuffer;
	
	private boolean enabled;
	
	public float vertices[];	
	private boolean visible = true;
	protected boolean colorChanged;
	
	private int muMVPMatrixHandle;
	private float[] mMVPMatrix = new float[16];
	private float[] mMMatrix = new float[16];
	
	private int ticksBeforeDraw = 0;
	
	private int tickCount;
	
	// Boolean to denote that this component's onSurfaceCreated method has run
	protected boolean initialized;
	
	private String vertexShaderCode = GLConstants.COLOR_VERTEX_SHADER_CODE; 
			
	private String fragmentShaderCode = GLConstants.COLOR_FRAG_SHADER_CODE;
	
	protected int mProgram;
	protected int maPositionHandle;
	protected int mVerticesBufferHandle;
	
	
	
	public GLObject(OpenGLCanvas canvas) {
		canvas.$add(this);
		glCanvas = (OpenGLView) canvas.getView();
		mvMatrix = glCanvas.ViewMatrix();
		projMatrix = glCanvas.ProjectionMatrix();
	}
	
		
	// Public/protected/private methods
	
	/**
	 * Set the color of this object.
	 * 
	 * @param color
	 */
	public void Color(int color) {
		rValue = Color.red(color) / 255f;
		gValue = Color.green(color) / 255f;
		bValue = Color.blue(color) / 255f;
		buildShaderCode();
		colorChanged = true;		
		glCanvas.requestRender();
	}
	
	protected FloatBuffer getVertexBuffer() {
		return vertexBuffer;
	}
	
	protected void setmuMVPMatrixHandle(int handle) {
		muMVPMatrixHandle = handle;
	}
	
	protected void setVertexShaderInt(int vertexshader) {
		vertexShader = vertexshader;
	}
	
	protected void setFragmentShaderInt(int fragShader) {
		fragmentShader = fragShader;
	}
	
	protected float[] getMvMatrix() {
		return mvMatrix;
	}
	
	protected float[] getProjMatrix() {
		return projMatrix;
	}
	
	protected void setFragmentShader(String source) {
		fragmentShaderCode = source;
	}
	
	protected String FragmentShaderSource() {
		return fragmentShaderCode;
	}
	
	protected void setVertexShader(String source) {
		vertexShaderCode = source;
	}
	
	protected int VertexShaderHandle() {
		return vertexShader;
	}
	
	protected int FragmentShaderHandle() {
		return fragmentShader;
	}
	
	protected String VertexShaderSource() {
		return vertexShaderCode;
	}
	
	protected boolean SurfaceInitialized() {
		return initialized;
	}
	
	protected void ManualInitialize(boolean init) {
		initialized = init;
	}
	
	protected float RedValue() {
		return rValue;
	}
	
	protected float GreenValue() {
		return gValue;
	}
	
	protected float BlueValue() {
		return bValue;
	}
	
	/**
	 * Enable this object
	 * 
	 * @param enabled
	 */
	public void Enabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * 
	 * @return Whether or not this object is enabled
	 */
	public boolean Enabled() {
		return enabled;
	}
	
	/**
	 * This sets the ticks between draws. This will speed up, or
	 * slow down the drawing of this sprite. The lower the 
	 * number, the faster this sprite will be drawn, however
	 * this will put more stress on the GPU, so go lower on
	 * the more important sprites. If a sprite will be static,
	 * set a higher number here. Right now, 1 tick = 16ms. (~60fps)
	 * 
	 * @param ticks
	 */
	public void Ticks(int ticks) {
		this.ticksBeforeDraw = ticks;
	}
			
	protected void setVertexShaderHandle(int shader) {
		vertexShader = shader;
	}
	
	protected void setFragmentShaderHandle(int frag) {
		fragmentShader = frag;
	}
	
	protected void resetVertexBuffer() {
		// a float has 4 bytes so we allocate for each coordinate 4 bytes
		ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
				
		// allocates the memory from the byte buffer
		vertexBuffer = vertexByteBuffer.asFloatBuffer();
				
		// fill the vertexBuffer with the vertices
		vertexBuffer.put(vertices);
			
		// set the cursor position to the beginning of the buffer
		vertexBuffer.position(0);
	}
	
	/**
	 * Set this object's visibility
	 * @param visible
	 */
	public void Visible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * 
	 * @return Whether or not this object is visible
	 */
	public boolean Visible() {
		return visible;
	}
	
	
	
	// Abstract methods to be overriden in extending classes
	
	protected abstract void buildShaderCode();
	
	protected abstract void glDraw();
	
	//protected abstract void onUpdate(long now);	

	
	// Overriden methods
	
	@Override
	public boolean canDraw() {
		return (tickCount >= ticksBeforeDraw);
	}

	@Override
	public void addTick() {
		tickCount++;
	}

	@Override
	public void resetTickCount() {
		tickCount = 0;
	}
	
	protected void onDrawPreTransformations() {
		// Add program to OpenGL environment
		if (colorChanged) {
			GLES20.glDetachShader(mProgram, fragmentShader);
			//fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
			GLES20.glAttachShader(mProgram, fragmentShader);
			GLES20.glLinkProgram(mProgram);
		}
					
		GLES20.glUseProgram(mProgram);
			        
		// Prepare the shape data
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLRenderer.checkGLError("EnableVertexAttribArray");
	}
	
	protected void onDrawTransformations(float[] mvMatrix, float[] projMatrix, float[] mMMatrix, float[] mMVPMatrix) {
		Matrix.multiplyMM(mMVPMatrix, 0, projMatrix, 0, mvMatrix, 0);
	}
	
	protected void onDrawFrame() {
		if (visible) {
		
			onDrawPreTransformations();
	        
			onDrawTransformations(mvMatrix, projMatrix, mMMatrix, mMVPMatrix);    
	        
			// Apply a ModelView Projection transformation
	               
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLRenderer.checkGLError("UniformMatrix4v");
	        
			// The extending class only needs to call the actual GL draw function 
			// eg: GLES20.glDrawArrays, or GLES20.glDrawElements
			glDraw();
	    
			postDraw(mvMatrix, mMMatrix);
		}	    	    
	}
	
	protected void postDraw(float[] mvMatrix, float[] mMMatrix) {
		
	}

	@Override
	public void onSurfaceCreated() {
		resetVertexBuffer();
		
		vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode, TAG);
        fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode, TAG);
             
        mProgram = GLUtil.createAndLinkProgram(vertexShader, fragmentShader, new String[] { "uMVPMatrix", "vPosition" }, TAG);
                
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLRenderer.checkGLError("GetUniformLocation: uMVPMatrix");
        
        // get handle to the vertex shader's vPosition member
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLRenderer.checkGLError("UniformMatrix4v");
        
        initialized = true;
	}

	@Override
	public void onSurfaceChanged() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public HandlesEventDispatching getDispatchDelegate() {		
		return glCanvas.canvas.getDispatchDelegate();
	}

}
