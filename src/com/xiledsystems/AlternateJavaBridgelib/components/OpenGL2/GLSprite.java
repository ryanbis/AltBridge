package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class GLSprite extends GLObject {

	private ShortBuffer drawBuffer;
	private FloatBuffer textureBuffer;

	private short drawOrder[] = { 0, 1, 2, 0, 2, 3 };

	private int textureUniformHandle;
	private int textureDataHandle;
	private float[] textureCoordinates = new float[] 
			{ 	0f, 0f,
				0f, 1f,
				1f, 1f,
				1f, 0f,				
			};

	private int textureCoordinateHandle;
	private int mMVPMatrixHandle;
	private int mModelMatrixHandle;

	private int imageResId;

	public GLSprite(GLCanvas canvas) {
		super(canvas);
		coordinates = new float[] { -0.5f, 0.5f, 0.0f, // Top left
				-0.5f, -0.5f, 0.0f, // Bottom Left
				0.5f, -0.5f, 0.0f, // Bottom right
				0.5f, 0.5f, 0.0f // Top right
		};
	}

	public void setCoordinates(float[] coords) {
		coordinates = coords;
	}

	public void Image(int resourceId) {
		imageResId = resourceId;
	}

	@Override
	public String setVertexShaderCode() {
		return ShaderCodes.Vertex.SPRITE;
	}

	@Override
	public String setFragmentShaderCode() {
		return ShaderCodes.Fragment.SPRITE;
	}

	@Override
	public void draw(float[] mMVPMatrix, float[] mRotationMatrix, float[] mvMatrix, float[] mProjMatrix) {
		if (visible && canvasSurfaceCreated()) {

			if (!initialized) {				
				if (!initObject()) {
					return;
				}
			}

			// Add program to OpenGL environment
			GLES20.glUseProgram(programHandle);

			
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
			
			GLES20.glUniform1i(textureUniformHandle, 0);

			// Enable a handle to the triangle vertices			
			// Prepare the triangle coordinate data
			vBuffer.position(0);
			GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vBuffer);
			GLES20.glEnableVertexAttribArray(positionHandle);

			// Pass in the texture coordinate information
			textureBuffer.position(0);
			GLES20.glVertexAttribPointer(textureCoordinateHandle, COORDS_PER_TEXTURE, GLES20.GL_FLOAT, false, (COORDS_PER_TEXTURE * 4), textureBuffer);
			GLES20.glEnableVertexAttribArray(textureCoordinateHandle);			

			Matrix.multiplyMM(mMVPMatrix, 0, mvMatrix, 0, mModelMatrix, 0);

			// Apply the projection and view transformation
			GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20Renderer.checkGLError("glUniformMatrix4fv");

			Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20Renderer.checkGLError("glUniformMatrix4fv");

			// Draw the square
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawBuffer);

			// Disable vertex array
			//GLES20.glDisableVertexAttribArray(positionHandle);
		}
	}

	@Override
	public void update(long now) {

	}

	@Override
	public boolean initObject() {
		if (imageResId != 0) {
			vertexCount = coordinates.length / COORDS_PER_VERTEX;
			ByteBuffer buff = ByteBuffer.allocateDirect(coordinates.length * 4);
			buff.order(ByteOrder.nativeOrder());
			vBuffer = buff.asFloatBuffer();
			vBuffer.put(coordinates);
			vBuffer.position(0);

			ByteBuffer drawBuff = ByteBuffer.allocateDirect(drawOrder.length * 2);
			drawBuff.order(ByteOrder.nativeOrder());
			drawBuffer = drawBuff.asShortBuffer();
			drawBuffer.put(drawOrder);
			drawBuffer.position(0);

			ByteBuffer textBuff = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
			textBuff.order(ByteOrder.nativeOrder());
			textureBuffer = textBuff.asFloatBuffer();
			textureBuffer.put(textureCoordinates);
			textureBuffer.position(0);

			// Prepare shaders and OpenGL program
			vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
			fragShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

			programHandle = GLES20.glCreateProgram(); // create empty OpenGL Program
			GLES20.glAttachShader(programHandle, vertexShaderHandle); // add the vertex shader to program
			GLES20.glAttachShader(programHandle, fragShaderHandle); // add the fragment shader to program

			GLES20.glBindAttribLocation(programHandle, 0, "aPosition");
			GLES20.glBindAttribLocation(programHandle, 1, "aTexCoordinate");			

			GLES20.glLinkProgram(programHandle); // create OpenGL program executables
			
			final int[] status = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, status, 0);
			
			if (status[0] == 0) {
				Log.e("GLSprite", "Error compiling OpenGL program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
				throw new RuntimeException("Error creating OpenGL program.");
			}

			textureDataHandle = GLUtil.loadTexture(mCanvas.getView().getContext(), imageResId);
			
			// get handle to vertex shader's aPosition member
			positionHandle = GLES20.glGetAttribLocation(programHandle, "aPosition");
			textureCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "aTexCoordinate");
			mModelMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVMatrix");
			GLES20Renderer.checkGLError("glUniformLocation uMVMatrix");
			mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix");
			GLES20Renderer.checkGLError("glUniformLocation uMVPMatrix");
			textureUniformHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture");
			GLES20Renderer.checkGLError("glUniformLocation u_Texture");
			
			GLES20.glEnable(GLES20.GL_BLEND);
			
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			initialized = true;
			return true;
		} else {
			return false;
		}
	}

}
