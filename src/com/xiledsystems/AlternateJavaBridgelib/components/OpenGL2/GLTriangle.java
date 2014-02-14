package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.opengl.GLES20;

public class GLTriangle extends GLObject {

	public GLTriangle(GLCanvas canvas) {
		super(canvas);
		coordinates = new float[] { 0.0f, 0.622f, 0.0f, -0.5f, -0.311f, 0.0f, 0.5f, -0.311f, 0.0f };
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
				if (!initObject()) {
					return;
				}
			}

			// Add program to GL environment
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

			// Draw the triangle
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

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

			vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
			fragShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

			programHandle = GLES20.glCreateProgram();
			GLES20.glAttachShader(programHandle, vertexShaderHandle);
			GLES20.glAttachShader(programHandle, fragShaderHandle);
			GLES20.glLinkProgram(programHandle);
			initialized = true;
			return true;
		} else {
			return false;
		}
	}

}
