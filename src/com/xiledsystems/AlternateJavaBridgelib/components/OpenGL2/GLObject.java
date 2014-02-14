package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2;

import java.nio.FloatBuffer;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;


public abstract class GLObject {
	
	protected FloatBuffer vBuffer;
	
	protected static final int COORDS_PER_VERTEX = 3;
	protected static final int COORDS_PER_TEXTURE = 2;
	
	protected String vertexShaderCode;
	protected String fragmentShaderCode;
	
	protected float[] color = { .5f, 0f, 0f, 1.0f };
	
	protected final float[] mModelMatrix = new float[16];
	
	protected float coordinates[];		
			
	protected int vertexShaderHandle;
	protected int fragShaderHandle;
	
	protected int programHandle;
	
	protected int positionHandle;
	protected int colorHandle;
	protected int mMVPMatrixHandle;
	
		
	protected int vertexCount;
	protected final int vertexStride = COORDS_PER_VERTEX * 4;
	
	protected static GLCanvas mCanvas;
	
	protected boolean visible = true;
	protected boolean initialized;
	
	
	public GLObject(GLCanvas canvas) {
		mCanvas = canvas;
		mCanvas.addGLObject(this);
		vertexShaderCode = setVertexShaderCode();
		fragmentShaderCode = setFragmentShaderCode();		
	}
		
	public static int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);
		
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		
		final int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		
		if (compileStatus[0] == 0) {
			Log.e("GLObject", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
			GLES20.glDeleteShader(shader);
			throw new RuntimeException("Error compiling shader.");
		}
		
		return shader;
	}
	
	public boolean canvasSurfaceCreated() {
		return mCanvas.getRenderer().isSurfaceCreated();
	}	
	
	public void Visible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean Visible() {
		return visible;
	}
	
	public void Color(int color) {
		setColor(color, this.color);
	}
	
	public int Color() {
		return getColor(color);
	}
	
	public static int createAndLinkGLProgram(int vertexShaderHandle, int fragmentShaderHandle, String[] attributes) {
		int programHandle = GLES20.glCreateProgram();
		if (programHandle != 0) {
			GLES20.glAttachShader(programHandle, vertexShaderHandle);
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			if (attributes != null) {
				int size = attributes.length;
				for (int i = 0; i < size; i++) {
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}
				GLES20.glLinkProgram(programHandle);
				int[] status = new int[1];
				GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, status, 0);
				if (status[0] == 0) {
					Log.e("GLObject", "Error compiling OpenGL Program: " + GLES20.glGetProgramInfoLog(programHandle));
					GLES20.glDeleteProgram(programHandle);
					programHandle = 0;
				}
			}
		}
		if (programHandle == 0) {
			throw new RuntimeException("Error creating OpenGL program.");
		}
		return programHandle;
	}
	
	private static void setColor(int color, float[] fColor) {
		float r = Color.red(color) / 255f;
		float g = Color.green(color) / 255f;
		float b = Color.blue(color) / 255f;
		float a = Color.alpha(color) / 255f;
		fColor[0] = r;
		fColor[1] = g;
		fColor[2] = b;
		fColor[3] = a;
	}
	
	private static int getColor(float[] color) {
		int r = (int) (color[0] * 255);
		int g = (int) (color[1] * 255);
		int b = (int) (color[2] * 255);
		int a = (int) (color[3] * 255);
		return Color.argb(a, r, g, b);
	}
	
		
	public abstract String setVertexShaderCode();
	public abstract String setFragmentShaderCode();
	public abstract void draw(float[] mMVPMatrix, float[] mRotationMatrix, float[] mvMatrix, float[] mProjMatrix);
	public abstract void update(long now);
	public abstract boolean initObject();
	
}
