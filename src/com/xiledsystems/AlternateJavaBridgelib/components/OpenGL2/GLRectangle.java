package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import android.opengl.GLES20;

public class GLRectangle extends GLObject {

	private ShortBuffer drawBuffer;

	private short drawOrder[] = { 0, 1, 2, 0, 2, 3 };

	public GLRectangle(GLCanvas canvas) {
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

	@Override
	public String setVertexShaderCode() {
		return ShaderCodes.Vertex.BASIC;
	}

	@Override
	public String setFragmentShaderCode() {
		return ShaderCodes.Fragment.BASIC;
	}

	@Override
	public void draw(float[] mMVPMatrix, float[] mRotationMatrix, float[] mvMatrix, float[] mProjMatrix) {
		if (visible && canvasSurfaceCreated()) {

			if (!initialized) {
				initObject();
			}

			// Add program to OpenGL environment
			GLES20.glUseProgram(programHandle);

			// get handle to vertex shader's vPosition member
			positionHandle = GLES20.glGetAttribLocation(programHandle, "vPosition");

			// Enable a handle to the triangle vertices
			GLES20.glEnableVertexAttribArray(positionHandle);

			// Prepare the triangle coordinate data
			GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vBuffer);

			// get handle to fragment shader's vColor member
			colorHandle = GLES20.glGetUniformLocation(programHandle, "vColor");

			// Set color for drawing the triangle
			GLES20.glUniform4fv(colorHandle, 1, color, 0);

			// get handle to shape's transformation matrix
			mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix");
			GLES20Renderer.checkGLError("glGetUniformLocation");

			// Apply the projection and view transformation
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20Renderer.checkGLError("glUniformMatrix4fv");

			// Draw the square
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawBuffer);

			// Disable vertex array
			GLES20.glDisableVertexAttribArray(positionHandle);
		}
	}

	@Override
	public void update(long now) {

	}

	@Override
	public boolean initObject() {
		if (coordinates != null) {
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

			// Prepare shaders and OpenGL program
			vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
			fragShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

			programHandle = GLES20.glCreateProgram(); // create empty OpenGL Program
			GLES20.glAttachShader(programHandle, vertexShaderHandle); // add the vertex shader to program
			GLES20.glAttachShader(programHandle, fragShaderHandle); // add the fragment shader to program
			GLES20.glLinkProgram(programHandle); // create OpenGL program executables
			initialized = true;
			return true;
		} else {
			return false;
		}
	}

}
