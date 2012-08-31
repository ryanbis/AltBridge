package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.xiledsystems.altbridge.BuildConfig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class GLUtil {
	
	private GLUtil() {		
	}
	
	public static int initFloatBuffer(float[] data, String debugTag) {
		int buffer[] = new int[1];
		GLES20.glGenBuffers(1, buffer, 0);
		int pointer = buffer[0];
		if (pointer == -1) {
			Log.e(debugTag, "Unable to create float buffer!");
		}
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, pointer);
		ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer floatbuffer = bb.asFloatBuffer();
		floatbuffer.put(data);
		floatbuffer.position(0);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, data.length * 4, floatbuffer, GLES20.GL_STATIC_DRAW);
		return pointer;
	}
	
	public static int loadShader(int type, String shaderCode, String debugTag){
	    
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type); 
        
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        
        if (compileStatus[0] == 0) {
        	Log.e(debugTag, "Error compiling shader: "+GLES20.glGetShaderInfoLog(shader));
        	GLES20.glDeleteShader(shader);
        	shader = 0;
        }
        
        if (shader == 0) {
        	throw new RuntimeException("Error creating shader.");
        }
        
        return shader;
    }	
	
	public static int createAndLinkProgram(int vertexShaderHandle, int fragmentShaderHandle, String[] attributes, String debugTag) {
		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0) {
			
			GLES20.glAttachShader(programHandle, vertexShaderHandle);
			
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			if (attributes != null) {
				final int size = attributes.length;
				for (int i = 0; i < size; i++) {
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}
			}
			
			GLES20.glLinkProgram(programHandle);
			
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
			
			if (linkStatus[0] == 0) {
				Log.e(debugTag, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
									
		}
		
		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}
		
		return programHandle;
	}
	
	public static void checkGLError(String debugTag) {
		if (BuildConfig.DEBUG) {
			int ec = GLES20.glGetError();
			if (ec != GLES20.GL_NO_ERROR) {
				Log.e(debugTag, "GL Error Code: "+ec + " Line: 196");
			}
		}
	}
	
	public static int[] loadTexture(OpenGLView canvas, int resourceId, String debugTag) {
		int[] textureHandle  = new int[3];
		
		GLES20.glGenTextures(1, textureHandle, 0);
		checkGLError(debugTag);
		
		if (textureHandle[0] != 0) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;
			
			Bitmap tbit = BitmapFactory.decodeResource(canvas.getContext().getResources(), resourceId, options);
			
			if (tbit == null) {
				tbit = ((BitmapDrawable) canvas.getResources().getDrawable(resourceId)).getBitmap();
			}
			
			final Bitmap bitmap = tbit;
			
			textureHandle[1] =  bitmap.getWidth();
			textureHandle[2] = bitmap.getHeight();
													
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);	
			checkGLError(debugTag);
								
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			checkGLError(debugTag);
			
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			checkGLError(debugTag);
			
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			checkGLError(debugTag);
			
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			checkGLError(debugTag);
			
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			checkGLError(debugTag);
			
			bitmap.recycle();
			
		}
		
		if (textureHandle[0] == 0) {
			throw new RuntimeException("Unable to load texture!");
		}
		return textureHandle;
	}
	
	public static float[] convertRectVertices(float[] vertices, float ratio, float xRatio, float yRatio) {
		float[] newVerts = new float[vertices.length];
		newVerts[0] = (vertices[0] * xRatio) - ratio;
		newVerts[1] = 1f - (vertices[1] * yRatio);
		
		newVerts[3] = (vertices[3] * xRatio) - ratio;
		newVerts[4] = 1f - (vertices[4] * yRatio);
		
		newVerts[6] = (vertices[6] * xRatio) - ratio;
		newVerts[7] = 1f - (vertices[7] * yRatio);
		
		newVerts[9] = (vertices[9] * xRatio) - ratio;
		newVerts[10] = 1f - (vertices[10] * yRatio);		
		
		return newVerts;
		
	}
	
	
}
