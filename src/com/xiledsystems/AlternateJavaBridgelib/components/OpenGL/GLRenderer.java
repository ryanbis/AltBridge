package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import java.util.ArrayList;
import java.util.Set;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Sprite;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Sets;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;


public class GLRenderer implements Renderer {
	
	private Set<GLObject> objects = Sets.newHashSet();
	private Set<GLTouchDragged> sprites = Sets.newHashSet();
	private Set<onGLSurfaceCreated> onCreateComponents = Sets.newHashSet();
	private Set<onSurfaceChangedListener> onChangedListeners = Sets.newHashSet();
	private final Set<GLTouchDragged> draggedSprites = Sets.newHashSet();
	protected float ratio;
	
	private float rValue = 0.5f;
	private float gValue = 0.5f;
	private float bValue = 0.5f;
	
	protected float xRatio;
	protected float yRatio;
	
	private float cameraNear = 2.99f;
	private float cameraFar = 7f;
	private float cameraEye[] = { 1f, 1f };		
	private ArrayList<Runnable> postedRunnables = new ArrayList<Runnable>();
	
	private float[] mVMatrix = new float[16];
	private float[] mProjMatrix = new float[16];
		
	private OpenGLView view;
	
	private boolean colorChanged;
	private boolean updateCamera;
	private final MotionEventParser parser;

	
	public GLRenderer(OpenGLView view) {
		parser = new MotionEventParser();
		this.view = view;
	}
	
	public void BackgroundColor(int color) {
		rValue = Color.red(color) / 255f;
		gValue = Color.green(color) / 255f;
		bValue = Color.blue(color) / 255f;
		colorChanged = true;		
				
	}
		
	protected float[] ViewMatrix() {
		return mVMatrix;
	}
	
	protected float[] ProjectionMatrix() {
		return mProjMatrix;
	}
	
	public boolean parseEvent(MotionEvent event, int width, int height) {
		parser.parse(event, width, height);
		return true;
	}
	
	public void post(Runnable runnable) {
		runnable.run();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(rValue, gValue, bValue, 1.0f);
		
		for (onGLSurfaceCreated component : onCreateComponents) {
			component.onSurfaceCreated();
		}
		
	}
	
	/**
	 * This sets the cameranear value. With a cameraFar value of 7 (default)
	 * 2.99f (default) is the highest you want to go with cameraNear. A smaller
	 * number will result in the camera pulling out (so objects will appear
	 * smaller)
	 * 
	 * @param cameraNear
	 */
	public void CameraNear(float cameraNear) {
		this.cameraNear = cameraNear;
		updateCamera = true;
	}
	
	public float CameraNear() {
		return cameraNear;
	}
	
	public void CameraFar(float cameraFar) {
		this.cameraFar = cameraFar;
		updateCamera = true;
	}
	
	public void postOnGLThread(Runnable action) {
		postedRunnables.add(action);
	}
	
	public float CameraFar() {
		return cameraFar;
	}
	
	public void CameraEye(float x, float y) {
		cameraEye[0] = x;
		cameraEye[1] = y;
	}
	
	public void CameraEye(float[] coords) {
		if (coords.length > 1) {
			cameraEye = coords;
		} else {
			throw new IllegalArgumentException("Float array too small! CameraEye requires an array of 2 floats.");
		}
	}
	
	public float[] CameraEye() {
		return cameraEye;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {		
		GLES20.glViewport(0, 0, width, height);		
		ratio = (float) width / height;
		
		float worldxRatio = width / view.xSize;
		float worldyRatio = height / view.ySize;
		
		xRatio = (ratio*2f) * worldxRatio;		
		yRatio = 2f * worldyRatio;
		
		// Convert Sprite's coordinates to real screen coordinates when
		// processing touch intersections
		view.canvasCoordXRatio = view.getWidth() / view.xSize;
		view.canvasCoordYRatio = view.getHeight() / view.ySize;
		
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, cameraNear, cameraFar);						
				
		for (onSurfaceChangedListener component : onChangedListeners) {
			component.onSurfaceChanged();
		}

		// Define camera view matrix
		
		Matrix.setLookAtM(mVMatrix, 0, cameraEye[0], cameraEye[1], 3f, cameraEye[0], cameraEye[1], 0f, 0f, 1.0f, 0f);
		
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (colorChanged) {
			GLES20.glClearColor(rValue, gValue, bValue, 1.0f);
		}
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		if (updateCamera) {
			Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, cameraNear, cameraFar);			
			updateCamera = false;
		}		
		// Now draw all objects
		for (GLObject obj : objects) {
			obj.onDrawFrame();
		}
		
		// Run any posted actions now
		if (postedRunnables.size() > 0) {
			for (Runnable run : postedRunnables) {
				run.run();
			}			
		}		
	}
	
	public void registerForOnSurfaceCreated(onGLSurfaceCreated component) {
		onCreateComponents.add(component);
	}
	
	public void registerForOnSurfaceChanged(onSurfaceChangedListener component) {
		onChangedListeners.add(component);
	}
	
	public void addObject(GLObject object) {
		objects.add(object);		
		if (object instanceof GLTouchDragged) {
			sprites.add((GLTouchDragged) object);
		}
	}
	
	/**
	   * Parser for Android {@link android.view.MotionEvent} sequences, which calls
	   * the appropriate event handlers.  Specifically:
	   * <ul>
	   * <li> If a {@link android.view.MotionEvent#ACTION_DOWN} is followed by one
	   * or more {@link android.view.MotionEvent#ACTION_MOVE} events, a sequence of
	   * {@link Sprite#Dragged(float, float, float, float, float, float)}
	   * calls are generated for sprites that were touched, and the final
	   * {@link android.view.MotionEvent#ACTION_UP} is ignored.
	   *
	   * <li> If a {@link android.view.MotionEvent#ACTION_DOWN} is followed by an
	   * {@link android.view.MotionEvent#ACTION_UP} event either immediately or
	   * after {@link android.view.MotionEvent#ACTION_MOVE} events that take it no
	   * further than {@link #TAP_THRESHOLD} pixels horizontally or vertically from
	   * the start point, it is interpreted as a touch, and a single call to
	   * {@link Sprite#Touched(float, float)} for each touched sprite is
	   * generated.
	   * </ul>
	   *
	   * After the {@code Dragged()} or {@code Touched()} methods are called for
	   * any applicable sprites, a call is made to
	   * {@link Canvas_backup#Dragged(float, float, float, float, float, float, boolean)}
	   * or {@link Canvas_backup#Touched(float, float, boolean)}, respectively.  The
	   * additional final argument indicates whether it was preceded by one or
	   * more calls to a sprite, i.e., whether the locations on the canvas had a
	   * sprite on them ({@code true}) or were empty of sprites {@code false}).
	   *
	   *
	   */
	  class MotionEventParser {
	    /**
	     * The number of pixels right, left, up, or down, a sequence of drags must
	     * move from the starting point to be considered a drag (instead of a
	     * touch).
	     */
	    public static final float TAP_THRESHOLD = 30;

	    /**
	     * The width of a finger.  This is used in determining whether a sprite is
	     * touched.  Specifically, this is used to determine the horizontal extent
	     * of a bounding box that is tested for collision with each sprite.  The
	     * vertical extent is determined by {@link #FINGER_HEIGHT}.
	     */
	    public static final float FINGER_WIDTH = 24;

	    /**
	     * The width of a finger.  This is used in determining whether a sprite is
	     * touched.  Specifically, this is used to determine the vertical extent
	     * of a bounding box that is tested for collision with each sprite.  The
	     * horizontal extent is determined by {@link #FINGER_WIDTH}.
	     */
	    public static final float FINGER_HEIGHT = 24;

	    private static final float HALF_FINGER_WIDTH = FINGER_WIDTH / 2;
	    private static final float HALF_FINGER_HEIGHT = FINGER_HEIGHT / 2;

	    /**
	     * The set of sprites encountered in a touch or drag sequence.  Checks are
	     * only made for sprites at the endpoints of each drag.
	     */
	    //private final List<Sprite> draggedSprites = new ArrayList<Sprite>();

	    // startX and startY hold the coordinates of where a touch/drag started
	    private static final int UNSET = -1;
	    private float startX = UNSET;
	    private float startY = UNSET;

	    // lastX and lastY hold the coordinates of the previous step of a drag
	    private float lastX = UNSET;
	    private float lastY = UNSET;

	    private boolean drag = false;

	    void parse(MotionEvent event, int width, int height) {
	    
	    	float x = event.getX();
	    	float y = event.getY();
	    	    	
	      GLBoundingBox rect = new GLBoundingBox(	    		 
	          Math.max(0f, x - HALF_FINGER_HEIGHT),
	          Math.max(0f, y - HALF_FINGER_WIDTH),
	          Math.min(width , x + HALF_FINGER_WIDTH),
	          Math.min(height , y + HALF_FINGER_HEIGHT));

	      switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	          draggedSprites.clear();
	          startX = x;
	          startY = y;
	          lastX = x;
	          lastY = y;
	          drag = false;
	          	          
	          for (GLTouchDragged sprite : sprites) {
	        	  if (sprite.Enabled() && sprite.Visible() && sprite.intersectsWith(rect)) {
	              draggedSprites.add(sprite);
	              sprite.DownState();	              
	            }
	          } 
	          
	          break;

	        case MotionEvent.ACTION_MOVE:
	          // Ensure that this was preceded by an ACTION_DOWN
	          if (startX == UNSET || startY == UNSET || lastX == UNSET || lastY == UNSET) {
	            Log.w("Canvas", "In Canvas.MotionEventParser.parse(), " +
	                "an ACTION_MOVE was passed without a preceding ACTION_DOWN: " + event);
	          }

	          // If the new point is near the start point, it may just be a tap
	          if (Math.abs(x - startX) < TAP_THRESHOLD && Math.abs(y - startY) < TAP_THRESHOLD) {
	            break;
	          }
	          // Otherwise, it's a drag.
	          drag = true;

	          // Update draggedSprites by adding any that are currently being
	          // touched.
	         
	          for (GLTouchDragged sprite : sprites) {
	            if (!draggedSprites.contains(sprite)
	                && sprite.Enabled() && sprite.Visible()
	                && sprite.intersectsWith(rect)) {
	              draggedSprites.add(sprite);
	            }
	          }

	          // Raise a Dragged event for any affected sprites
	          boolean handled = false;
	          for (GLTouchDragged sprite : draggedSprites) {
	            if (sprite.Enabled() && sprite.Visible()) {	              
	            	sprite.Dragged(startX, startY, lastX, lastY, x, y);
	            	handled = true;
	            }
	          }

	          // Last argument indicates whether a sprite handled the drag
	          Dragged(startX, startY, lastX, lastY, x, y, handled);
	          lastX = x;
	          lastY = y;
	          break;

	        case MotionEvent.ACTION_UP:
	          // If we never strayed far from the start point, it's a tap.  (If we
	          // did stray far, we've already handled the movements in the ACTION_MOVE
	          // case.)
	          if (!drag) {
	            // It's a tap
	            handled = false;
	            for (GLTouchDragged sprite : draggedSprites) {
	              if (sprite.Enabled() && sprite.Visible()) {	                
	            	  sprite.Touched(startX, startY);
	            	  handled = true;
	            	  sprite.UpState();	                
	              }
	            }
	            // Last argument indicates that one or more sprites handled the tap
	            Touched(startX, startY, handled);
	          } else {
	        	  // Throw the done dragging event
	        	  stoppedDragging();
	          }

	          // Prepare for next drag
	          drag = false;
	          startX = UNSET;
	          startY = UNSET;
	          lastX = UNSET;
	          lastY = UNSET;
	          break;
	      }
	    }
	  }

	public void Touched(float startX, float startY, boolean handled) {
		EventDispatcher.dispatchEvent(view.canvas, Events.TOUCHED, startX, startY, handled);
	}

	public void Dragged(float startX, float startY, float lastX, float lastY,
			float x, float y, boolean handled) {
		EventDispatcher.dispatchEvent(view.canvas, Events.DRAGGED, startX, startY, lastX, lastY, x, y, handled);
	}

	public void stoppedDragging() {
		EventDispatcher.dispatchEvent(view.canvas, Events.DONE_DRAGGING);
	}
	
	protected float convertXCoord(float coord) {
		return (coord * xRatio) - ratio;		
	}
	
	protected float convertYCoord(float coord) {
		return (coord * yRatio) - 1f;
	}
	
	public static void checkGLError(String operation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("GLES20Renderer", operation + ": glError " + error);
            throw new RuntimeException(operation + ": glError " + error);
		}
	}

}
