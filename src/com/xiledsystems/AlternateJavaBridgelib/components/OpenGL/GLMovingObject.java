package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import android.opengl.Matrix;

public abstract class GLMovingObject extends GLObject {

	
	private double headingRadians;  // heading in radians
	private double headingCos;      // cosine(heading)
	private double headingSin;      // sine(heading)
	
	protected float centerX = 0;
	protected float centerY = 0;
	
	private float mAngle;	
	private boolean rotate;
	private float speed;
	private float rotationSpeed;
	
	
	public GLMovingObject(OpenGLCanvas canvas) {
		super(canvas);		
	}
	
	/**
	 * This sets the center of rotation on the object
	 * 
	 * @param x
	 * @param y
	 */
	public void CenterOfRotation(float x, float y) {
		centerX = x;
		centerY = y;
	}
	
	/**
	 * Returns a float[] containing the x and y
	 * values of the center of rotation.
	 * idx 0 = x, idx 1 = y
	 * 
	 * @return 
	 */
	public float[] getCenterOfRotation() {
		return new float[] { centerX, centerY } ;
	}
	
	/**
	 * Set whether this object should rotate or not
	 * @param rotate
	 */
	public void Rotate(boolean rotate) {
		this.rotate = rotate;		
	}
	
	/**
	 * 
	 * @return Whether or not this object is set to rotate
	 */
	public boolean Rotate() {
		return rotate;
	}
	
	/**
	 * Set the speed of this object (this affects both movement
	 * and rotation). This is the amount of pixels to move each
	 * time the object is drawn to screen.
	 * @param speed
	 */
	public void Speed(float speed) {
		this.speed = speed;
	}
	
	/**
	 * 
	 * @return The speed of this object
	 */
	public float Speed() {
		return speed;
	}
	
	/**
	 * 
	 * @return The speed at which this object rotates each tick
	 */
	public float RotationSpeed() {
		return rotationSpeed;
	}
	
	/**
	 * Set the rotation speed for this object.
	 * 
	 * @param speed
	 */
	public void RotationSpeed(float speed) {
		rotationSpeed = speed;
	}
	
	/**
	 * Set the angle this object is facing
	 * 
	 * @param angle
	 */
	public void Angle(float angle) {
		mAngle = angle;		
	}
	
	/**
	 * 
	 * @return the angle this object is facing
	 */
	public float Angle() {
		return mAngle;
	}
	
	/**
	 * Used when rotating, this will center the rotation
	 * on the object itself, making it rotate around
	 * itself.
	 */
	public abstract void CenterOnSelf();
	
	@Override
	protected void onDrawTransformations(float[] mvMatrix, float[] projMatrix, float[] mMMatrix, float[] mMVPMatrix) {
		if (rotate) {       	
        	
        	Matrix.setRotateM(mMMatrix, 0, mAngle, 0, 0, 1);      
        	
        	if ( centerX != 0 || centerY != 0) {
        		Matrix.translateM(mvMatrix, 0, centerX, centerY, 0);	
        		Matrix.translateM(mMMatrix, 0, -centerX, -centerY, 0);
        	}
        	Matrix.multiplyMM(mMVPMatrix, 0, mvMatrix, 0, mMMatrix, 0);
        	Matrix.multiplyMM(mMVPMatrix, 0, projMatrix, 0, mMVPMatrix, 0);
        } else {
        
        	Matrix.multiplyMM(mMVPMatrix, 0, projMatrix, 0, mvMatrix, 0);
        }
	}
	
	@Override
	protected void postDraw(float[] mvMatrix, float[] mMMatrix) {
		if (rotate) {			
        	if (centerX != 0 || centerY != 0) {
        		Matrix.translateM(mvMatrix, 0, -centerX, -centerY, 0);
        		Matrix.translateM(mMMatrix, 0, centerX, centerY, 0);
        	}
		}
	}
	
	@Override
	public void onUpdate(long now) {
		if (Visible()) {
			if (Enabled() && rotate && rotationSpeed > 0) {
				Angle(mAngle + rotationSpeed);
				if (mAngle > 360) {
					mAngle = 0;
				}			
			}		
		}
	}
	
}
