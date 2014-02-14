package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL2;

import java.util.ArrayList;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ABColors;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


public class GLES20Renderer extends GLRenderer {
	
	private ArrayList<GLObject> objects = new ArrayList<GLObject>();
	
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private float ratio;
    
    private OnGLSurfaceCreatedListener onCreateListener;
    
    private boolean surfaceCreated;
    
    private float eyeZ = -3f;
    
    private int bColor = ABColors.GRAY;
   

    public void addObject(GLObject glObject) {
    	objects.add(glObject);
    }
    
    public void removeObject(GLObject glObject) {
    	objects.remove(glObject);
    }
    
    public float getRatio() {
    	return ratio;
    }
    
    public void setOnSurfaceCreateListener(OnGLSurfaceCreatedListener listener) {
    	onCreateListener = listener;
    }
    
    public void setBackgroundColor(int color) {
    	bColor = color;
    	float r = Color.red(bColor) / 255f;
		float g = Color.green(bColor) / 255f;
		float b = Color.blue(bColor) / 255f;
		float a = Color.alpha(bColor) / 255f;
		GLES20.glClearColor(r, g, b, a);
    }
    
	@Override
	public void onCreate(int width, int height, boolean contextLost) {
		float r = Color.red(bColor) / 255f;
		float g = Color.green(bColor) / 255f;
		float b = Color.blue(bColor) / 255f;
		float a = Color.alpha(bColor) / 255f;
		GLES20.glClearColor(r, g, b, a);
		GLES20.glViewport(0, 0, width, height);
		
		ratio = (float) width / height;
		
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
		surfaceCreated = true;
		int size = objects.size();
		for (int i = 0; i < size; i++) {
			objects.get(i).initObject();
		}
		if (onCreateListener != null) {
			onCreateListener.surfaceCreated(width, height);
		}
	}
	
	public boolean isSurfaceCreated() {
		return surfaceCreated;
	}

	@Override
	public void onDrawFrame(boolean firstDraw) {
		// Draw Background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		// Set the camera position (View matrix)
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, eyeZ, 0f, 0f, -.5f, 0f, 1f, 0f);
		
		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		
		int size = objects.size();
		
		// Update all objects
		long now = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			objects.get(i).update(now);
		}
		
		// Draw all objects		
		for (int i = 0; i < size; i++) {
			objects.get(i).draw(mMVPMatrix, mRotationMatrix, mVMatrix, mProjMatrix);
		}
		
	}
	
	public static void checkGLError(String operation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("GLES20Renderer", operation + ": glError " + error);
            throw new RuntimeException(operation + ": glError " + error);
		}
	}
	
	public interface OnGLSurfaceCreatedListener {
		public void surfaceCreated(int width, int height);
	}

}
