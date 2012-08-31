package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;


import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnInitializeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import com.xiledsystems.altbridge.BuildConfig;

/**
 * A rectangular object which has no built in movement (as opposed to the Sprite).
 * 
 * This is the base object for things like a HUD, or background.
 * 
 * @author Ryan Bis
 *
 */
public abstract class GLImmobileObject extends GLObject implements OnInitializeListener, GLTouchDragged {
	
	private final String TAG = "GLImmobileObject";
	
	private float width = 1.0f;
    private float height = 1.0f;
    private float xLeft = -1.0f;
    private float yTop = 1.0f;
    
    private ShortBuffer indexBuffer;
    
    private FloatBuffer textureCoordBuffer;
    private float[] texCoords;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;
    private int imageId;
    private int[] textureHandles;
    
    private boolean image;
    
    private short indices[] = { 0, 1, 2, 0, 2, 3 };
    
	
	public GLImmobileObject(OpenGLCanvas canvas) {
		super(canvas);
		vertices = new float[12];
		texCoords = new float[12];
		buildShaderCode();
		
				
				
		// Register this rectangle for the GL Canvas renderer's onSurfaceCreated
		// method, as well as adding it to the updatethread for any rotation or movement
		// Initialize is needed to convert coordinate system to opengl coordinates
		glCanvas.renderer.registerForOnSurfaceCreated(this);		
		glCanvas.updateThread.addObjectToUpdateList(this);
		((OpenGLCanvas)glCanvas.canvas).$form().registerForOnInitialize(this);
	}
	
	public void StartPosition(float x, float y) {
		xLeft = x;
		yTop = y;
		setVertices();		
	}
	
	private void setVertices() {
		vertices[0] = xLeft;
		vertices[1] = yTop;
		vertices[3] = xLeft;
		vertices[4] = yTop - height;
		vertices[6] = xLeft + width;
		vertices[7] = yTop - height;
		vertices[9] = xLeft + width;
		vertices[10] = yTop;
		if (image) {
			texCoords = new float[] { 
				0, 0,
				0, 1,
				1, 1,
				1, 0
			};			
		}
	}
	
	public void Size(float width, float height) {
		this.width = width;
		this.height = height;
		setVertices();
		
	}
	
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
		final Bitmap b = BitmapFactory.decodeResource(glCanvas.getResources(), resourceId, options);
		width = b.getWidth();
		height = b.getHeight();
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
		return new GLBoundingBox(vertices[0] - border, vertices[1] - border,
				vertices[0] + width - 1 + border, vertices[1] + height - 1 + border);
	}
	
	/**
	   * Determines whether this sprite intersects with the given rectangle.
	   *
	   * @param rect the rectangle
	   * @return {@code true} if they intersect, {@code false} otherwise
	   */
	  public boolean intersectsWith(GLBoundingBox rect) {
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
	  }
	  
	  /**
	   * Indicates whether the specified point is contained by this sprite.
	   * Subclasses of Sprite that are not rectangular should override this method.
	   *
	   * @param qx the x-coordinate
	   * @param qy the y-coordinate
	   * @return whether (qx, qy) falls within this sprite
	   */
	  public boolean containsPoint(double qx, double qy) {
	    return qx >= xLeft && qx < xLeft + width &&
	        qy >= yTop && qy < yTop + height;
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
			
			GLUtil.loadTexture(glCanvas, imageId, TAG);
			
			width = textureHandles[1];
			height = textureHandles[2];
			setVertices();
			
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			setVertexShaderInt(GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, VertexShaderSource(), TAG));
	        setFragmentShaderInt(GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, FragmentShaderSource(), TAG));
						
			mProgram = GLUtil.createAndLinkProgram(VertexShaderHandle(), FragmentShaderHandle(), new String[] { "uMVPMatrix", "vPosition", "a_TexCoordinate" }, TAG);
			
			setmuMVPMatrixHandle(GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"));
			
			maPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
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
	public void glDraw() {
		if (Visible()) {			
			
			mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
			mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
			
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 8, textureCoordBuffer);
			int ec;
			if (BuildConfig.DEBUG) {
				ec = GLES20.glGetError();
				if (ec != GLES20.GL_NO_ERROR) {
					Log.e(TAG, "GL Error Code: "+ec);
				}
			}
			
			GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
			if (BuildConfig.DEBUG) {
				ec = GLES20.glGetError();
				if (ec != GLES20.GL_NO_ERROR) {
					Log.e(TAG, "GL Error Code: "+ec);
				}
			}
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			if (BuildConfig.DEBUG) {
				ec = GLES20.glGetError();
				if (ec != GLES20.GL_NO_ERROR) {
					Log.e(TAG, "GL Error Code: "+ec);
				}
			}
			
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
			if (BuildConfig.DEBUG) {
				ec = GLES20.glGetError();
				if (ec != GLES20.GL_NO_ERROR) {
					Log.e(TAG, "GL Error Code: "+ec);
				}
			}
			
			GLES20.glUniform1i(mTextureUniformHandle, 0);
			if (BuildConfig.DEBUG) {
				ec = GLES20.glGetError();
				if (ec != GLES20.GL_NO_ERROR) {
					Log.e(TAG, "GL Error Code: "+ec);
				}
			}
			
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
			if (BuildConfig.DEBUG) {
				ec = GLES20.glGetError();
				if (ec != GLES20.GL_NO_ERROR) {
					Log.e(TAG, "GL Error Code: "+ec);
				}
			}						
		}
	}
	
	@Override
	public void onInitialize() {
		int width = glCanvas.canvas.getRealScreenSize()[0];
		int height = glCanvas.canvas.getRealScreenSize()[1];
		float ratio = (float) width / height;
		float xRatio = (ratio*2f) / glCanvas.xSize;
		float yRatio = 2f / glCanvas.ySize;
		
		vertices[0] = (vertices[0] * xRatio) - ratio;
		vertices[1] = (vertices[1] * yRatio) - 1f;
		vertices[3] = (vertices[3] * xRatio) - ratio;
		vertices[4] = (vertices[4] * yRatio) - 1f;
		vertices[6] = (vertices[6] * xRatio) - ratio;
		vertices[7] = (vertices[7] * yRatio) - 1f;
		vertices[9] = (vertices[9] * xRatio) - ratio;
		vertices[10] = (vertices[10] * yRatio) - 1f;				
	}	
	
	@Override
	public void onUpdate(long now) {
		// As we don't move or rotate, do nothing here
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

}
