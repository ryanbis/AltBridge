package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import com.xiledsystems.AlternateJavaBridgelib.components.util.BoundingBox;
import com.xiledsystems.altbridge.BuildConfig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.util.Log;


/**
 * OpenGL Sprite. This is probably the most used class for
 * openGL. The sprite can rotate, and move by itself,
 * and supports images with transparency.
 * 
 * @author Ryan Bis
 *
 */
public class GLSprite extends GLRectangle implements GLTouchDragged, GLCollisionHandler {
	
	private static final String TAG = "GLSprite";
	
	private FloatBuffer textureCoordBuffer;
    private float[] texCoords = { 
    		0, 0,
			0, 1,
			1, 1,
			1, 0 };
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;
    private int[] textureHandles;
    
    private int imageId;
    
    private boolean image;
    
    private boolean circleCollision=false;
    protected double cRadius=0;
	
    
	public GLSprite(OpenGLCanvas canvas) {		
		super(canvas);		
	}
	
	/**
	 * Sets the image of the Sprite.
	 * 
	 * @param resourceId
	 */
	public void Image(int resourceId) {
		imageId = resourceId;
		if (resourceId == 0) {
			image = false;
		} else {
			image = true;
			buildShaderCode();
			textureHandles = new int[3];
			getSize(resourceId);			
		}				
				
	}
	
	private void getSize(int resourceId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		Bitmap tbit = BitmapFactory.decodeResource(glCanvas.getResources(), resourceId, options);
		
		if (tbit == null) {
			tbit = ((BitmapDrawable) glCanvas.getResources().getDrawable(resourceId)).getBitmap();
		}
		
		final Bitmap bitmap = tbit;
					
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		setVertices();
	}
	
	/**
	* Provides the bounding box for this sprite.  Modifying the returned value
	* does not affect the sprite.
	*
	* @param border the number of pixels outside the sprite to include in the
	*        bounding box
	* @return the bounding box for this sprite
	*/
	public GLBoundingBox getBoundingBox(int border) {
		//scaleVertices(false);
		float xratio = glCanvas.canvasCoordXRatio;
		float yratio = glCanvas.canvasCoordYRatio;
		return new GLBoundingBox((vertices[0]*xratio) - border, (vertices[1]*yratio) - border,
				(vertices[0]*xratio) + width + border, vertices[4]*yratio + border);
	}
	
	/**
	   * Determines whether this sprite intersects with the given rectangle.
	   *
	   * @param rect the rectangle
	   * @return {@code true} if they intersect, {@code false} otherwise
	   */
	  public boolean intersectsWith(GLBoundingBox rect) {
		  /* ************ Old intersectsWith method...try using better
		   * collision detection here
		   
	    // If the bounding boxes don't intersect, there can be no intersection.
	    GLBoundingBox rect1 = getBoundingBox(0);
	    if (!rect1.intersectDestructively(rect)) {
	      return false;
	    }

	    // If we get here, rect1 has been mutated to hold the intersection of the
	    // two bounding boxes.  Now check every point in the intersection to see if
	    // the sprite contains it.
	    for (float x = rect1.left; x < rect1.right; x++) {
	      for (float y = rect1.top; y < rect1.bottom; y++) {
	        if (containsPoint(x, y)) {
	            return true;
	        }
	      }
	    }
	    return false;
	    */
		  GLBoundingBox rect1 = getBoundingBox(0);
		  return GLCollision.colliding(rect1, rect);
	  }
	  
	  /**
	   * Indicates whether the specified point is contained by this sprite.
	   * Subclasses of Sprite that are not rectangular should override this method.
	   *
	   * @param qx the x-coordinate
	   * @param qy the y-coordinate
	   * @return whether (qx, qy) falls within this sprite
	   */
	  private boolean containsPoint(float qx, float qy) {
	    return qx >= xLeft && qx < xLeft + width &&
	        qy >= yTop && qy < yTop + height;
	  }
	
	  public void useCircleCollision(boolean circlecollision) {
		  this.circleCollision = circlecollision;
	  }
	  
	  public boolean isCircleCollision() {
		  return this.circleCollision;
	  }
	  
	  public void setCollisionRadius(double radius) {
		  this.cRadius = radius;
	  }
	  
	  public double getCollisionRadius() {
		  return this.cRadius;
	  }
	
	  
	@Override
	public void buildShaderCode() {
		if (!image) {
			setVertexShader(GLConstants.COLOR_VERTEX_SHADER_CODE);
			setFragmentShader(GLConstants.ColorFragShaderCode(RedValue(), GreenValue(), BlueValue()));
		} else {
			setVertexShader(GLConstants.TEXTURE_VERTEX_SHADER_CODE);					
					
			setFragmentShader(GLConstants.TEXTURE_FRAG_SHADER_CODE);
		}
	}
	
	@Override
	public void onSurfaceCreated() {
		if (image) {
			
			resetVertexBuffer();
			
			textureHandles = GLUtil.loadTexture(glCanvas, imageId, TAG);
			mTextureDataHandle = textureHandles[0];
			width = textureHandles[1];
			height = textureHandles[2];
			setVertices();
			
			GLES20.glEnable(GLES20.GL_BLEND);
			
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);			
															
			setVertexShaderInt(GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, VertexShaderSource(), TAG));
	        setFragmentShaderInt(GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, FragmentShaderSource(), TAG));
			
			mProgram = GLUtil.createAndLinkProgram(VertexShaderHandle(), FragmentShaderHandle(), new String[] { "uMVPMatrix", "vPosition", "u_Texture", "a_TexCoordinate" }, TAG);
			
			setmuMVPMatrixHandle(GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"));	
			
			maPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");	
			mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
			mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
			
		}
		        
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		textureCoordBuffer = tbb.asFloatBuffer();
		textureCoordBuffer.put(texCoords).position(0);
		ManualInitialize(true);
	}
	
	@Override
	protected void glDraw() {
		if (Visible()) {						
						
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 8, textureCoordBuffer);
									
			GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
						
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
						
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
						
			GLES20.glUniform1i(mTextureUniformHandle, 0);		
						
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
						
		}
	}

	@Override
	public void Touched(float x, float y) {
		EventDispatcher.dispatchEvent(this, Events.TOUCHED, x, y);
	}

	@Override
	public void Dragged(float startX, float startY, float prevX, float prevY,
			float currentX, float currentY) {
		EventDispatcher.dispatchEvent(this, Events.DRAGGED, startX, startY, prevX, prevY, currentX, currentY);
	}

	@Override
	public void UpState() {
		EventDispatcher.dispatchEvent(this, Events.UP_STATE);
	}

	@Override
	public void DownState() {
		EventDispatcher.dispatchEvent(this, Events.DOWN_STATE);
	}

	@Override
	public void CollidedWith(GLSprite other) {
		if (registeredCollisions.contains(other)) {
			if (BuildConfig.DEBUG) {
				Log.e("Sprite", "Collision between sprites " + this + " and "
						+ other + " re-registered");
			}
		      return;
		}
		registeredCollisions.add(other);
		EventDispatcher.dispatchEvent(this, Events.COLLIDED_WITH, other);    
	}

	@Override
	public void NoLongerCollidingWith(GLSprite other) {
		if (!registeredCollisions.contains(other)) {
			if (BuildConfig.DEBUG) {
				Log.e("Sprite", "Collision between sprites " + this + " and "
		          + other + " removed but not present");
			}
		}
		registeredCollisions.remove(other);
	}

	@Override
	public boolean CollidingWith(GLSprite other) {		
		return registeredCollisions.contains(other);
	}	
	
}
