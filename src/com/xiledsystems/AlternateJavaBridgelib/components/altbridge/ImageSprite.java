package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.DrawingCanvas;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.OnConfigurationListener;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

/**
 * Not-so simple image-based Sprite.
 * 
 * This has been heavily modified from the app inventor bridge source. It
 * includes a listener to automatically resize the component. It also gives you
 * the ability to better manage the memory hold. Use AutoToggle(true) to enable
 * this feature. It will automatically disable the sprite's timer when the
 * activity it's in is paused, then re-enables it upon resume. SpriteSheet
 * animation has also been built into this component. It runs in it's own thread
 * for as best performance as possible. In order to use the spritesheet
 * functionality, SetSheetAnimandFPS must be called with the filename (with, or
 * without the extension -- ex "buttonpic", or "buttonpic.png" ), the amount of
 * frames in the sheet, and the fps, or speed of the animation. When
 * startAnimation is called, the animation will run through the frames one after
 * another. You can also specify a custom animation list by using an array of
 * integers representing the frames (ex { 1, 2, 3, 2, 1, 3, 3, 2} ). You must
 * also call useCustomAnim(true) for the animation to use your custom frame list
 * when animating. You can also call upFrame, downFrame, or gotoFrame if you
 * want to manually adjust the frames.
 * 
 * 
 */

public class ImageSprite extends Sprite implements OnStopListener, OnResumeListener, OnConfigurationListener {

	private Drawable drawable;
	private int widthHint = LENGTH_PREFERRED;
	private int heightHint = LENGTH_PREFERRED;
	private String picturePath = ""; // Picture property
	private boolean rotates;
	private boolean sheetAnimation = false;
	private boolean autoToggle = true;
	private Matrix mat;
	public int currentFrame = 1;
	private Bitmap unrotatedBitmap;
	private Bitmap rotatedBitmap;
	private BitmapDrawable rotatedDrawable;
	private double cachedRotationHeading;
	private boolean rotationCached;
	private int spriteWidth;
	private int spriteHeight;
	private int frameCount;
	private Rect sourceRect;
	private Rect destRec;
	private double fps;
	private boolean animRunning = false;
	private boolean running = false;
	private double framePeriod;
	private boolean loop;
	private Thread animThread;
	private Handler androidUIhandler = new Handler();
	private boolean customAnim = false;
	private int customAnimFrames[];
	private boolean sheetHelper;
	private SpriteSheetHelper spriteHelper;
	// private boolean canvasControl;

	private int scrollLeft;
	private int scrollRight;
	private int scrollTop;
	private int scrollBottom;
	private double multipliers[];

	private boolean enabled = true;

	/**
	 * Constructor for ImageSprite.
	 * 
	 * @param container
	 */
	public ImageSprite(DrawingCanvas container) {
		super(container);

		mat = new Matrix();
		rotates = true;
		rotationCached = false;

		container.getRegistrar().registerForOnResume(this);
		container.getRegistrar().registerForOnStop(this);
		container.getRegistrar().registerForOnConfigChange(this);

		// In the anim canvas, sprite movement is handled in the
		// anim canvas' thread, so we shut off the internal
		// timer.
		if (container instanceof AnimCanvas) {
			super.Enabled(false);
		}

		frameCount = 1;
		sourceRect = new Rect(0, 0, 0, 0);
		destRec = new Rect(0, 0, 0, 0);
		fps = 10;

	}

	/**
	 * Constructor for ImageSprite placed in GLE.
	 * 
	 * @param container
	 *            - This is the canvas the sprite belongs to
	 * @param resourceId
	 *            - the resource Id of the imagesprite you added in the gle
	 */
	public ImageSprite(DrawingCanvas container, int resourceId) {
		super(container, resourceId);

		mat = new Matrix();
		rotates = true;
		rotationCached = false;

		container.getRegistrar().registerForOnResume(this);
		container.getRegistrar().registerForOnStop(this);

		// In the anim canvas, sprite movement is handled in the
		// anim canvas' thread, so we shut off the internal
		// timer.
		if (container instanceof AnimCanvas) {
			super.Enabled(false);
		}

		frameCount = 1;
		sourceRect = new Rect(0, 0, 0, 0);
		destRec = new Rect(0, 0, 0, 0);
		fps = 10;

		ImageView bkgrd = (ImageView) container.getRegistrar().findViewById(resourceId);
		if (bkgrd != null) {
			if (bkgrd.getDrawable() != null) {
				drawable = bkgrd.getDrawable();
				drawable.mutate();
				unrotatedBitmap = ((BitmapDrawable) bkgrd.getDrawable()).getBitmap();
				registerChange();
			}

			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bkgrd.getLayoutParams();
			Width(lp.width);
			Height(lp.height);
			X(lp.leftMargin);
			Y(lp.topMargin);

			RelativeLayout rl = (RelativeLayout) bkgrd.getParent();
			rl.removeView(bkgrd);
		}

	}

	/**
	 * Set this imagesprite as a canvacontrol sprite. This means when this
	 * sprite reaches a certain point, it will shift the background image over,
	 * if there's room left.
	 * 
	 * The double represents a percentage of the canvas' size. So, if you want
	 * the background image to move when this sprite gets to 10% of the
	 * left/right/top/bottom of the canvas, use CanvasControl(.1, .1);
	 * 
	 * This is a convenience method, you can also set the points manually.
	 * 
	 * @param widthMultiplier
	 * @param heightMultiplier
	 */
	public void CanvasControl(final double widthMultiplier, final double heightMultiplier) {
		final Handler handler;
		final DrawingCanvas dc = getCanvas();
		multipliers = new double[2];
		multipliers[0] = widthMultiplier;
		multipliers[1] = heightMultiplier;
		handler = dc.getRegistrar().getHandler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (dc.Width() != 0 && dc.Height() != 0 && Width() != 0 && Height() != 0) {
					setCanvasControls(widthMultiplier, heightMultiplier);
					canvasControl = true;
				} else {
					handler.post(this);
				}
			}
		});
	}

	private void setCanvasControls(double widthMultiplier, double heightMultiplier) {
		DrawingCanvas dc = getCanvas();
		int cwid = dc.Width();
		int chght = dc.Height();
		if (widthMultiplier == 1) {
			scrollLeft = 0;
			scrollRight = cwid;
		} else {
			double opp = 1 - widthMultiplier;
			scrollRight = (int) ((opp * cwid));
			scrollLeft = (int) (widthMultiplier * cwid);
		}
		if (heightMultiplier == 1) {
			scrollTop = 0;
			scrollBottom = chght;
		} else {
			double opp = 1 - heightMultiplier;
			scrollBottom = (int) ((opp * chght));
			scrollTop = (int) (heightMultiplier * chght);
		}
	}

	/**
	 * Set the actual pixel locations of where on the canvas you want this
	 * sprite to make the canvas' background move.
	 * 
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 */
	public void CanvasControl(int left, int right, int top, int bottom) {
		canvasControl = true;
		scrollLeft = left;
		scrollBottom = bottom;
		scrollRight = right;
		scrollTop = top;
	}

	/**
	 * 
	 * Method for checking if this sprite's spritesheet animation is running or
	 * not
	 * 
	 * @return running - Whether or not the animation is currently running.
	 */
	public boolean isAnimRunning() {
		return running;
	}

	/**
	 * 
	 * 
	 * 
	 * @return true if the animation is set to loop.
	 */
	public boolean isLoopedAnimation() {
		return this.loop;
	}

	/**
	 * Use this method to set the animation pattern.
	 * 
	 * @param frames
	 *            int array of the frames to animate
	 */
	public void setCustomAnimFrames(int[] frames) {
		if (frames.length > 1) {
			customAnimFrames = frames;
		} else {
			Toast.makeText(canvas.$context(), "Need more than one frame to animate!", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Use this method to tell the sprite to use the custom animation frameset.
	 * (Make sure you have run setCustomAnimFrames() first)
	 * 
	 * @param customanim
	 */
	public void useCustomAnim(boolean customanim) {
		this.customAnim = customanim;
	}

	/**
	 * 
	 * Set the image name for the sprite sheet animation, and the fps , or speed
	 * of the animation. Doing this negates any image that was set with the
	 * Picture() method.
	 * 
	 * @param picName
	 *            the name of the spritesheet file
	 * 
	 * @param framecount
	 *            The amount of frames in the sprite sheet
	 * 
	 * @param fps
	 *            frames per second; affects the speed of the animation
	 */

	public void SetSheetAnimandFPS(String picName, int framecount, double fps) {
		picturePath = picName;
		this.rotates = false;
		this.sheetAnimation = true;
		this.frameCount = framecount;
		this.fps = fps;
		setAnimBackground(picturePath);
	}

	public void SetSpriteSheetHelper(SpriteSheetHelper helper) {
		this.spriteHelper = helper;
		sheetHelper = true;
		sheetAnimation = true;
		this.rotates = false;
		frameCount = helper.getSpriteCount();
		setSheetBackground();
	}

	private void setAnimBackground(String picName) {

		if (picName.contains(".")) {
			picName = picName.split("\\.")[0];
		}
		try {
			drawable = MediaUtil.getDrawable(getCanvas().getRegistrar().$context(), picName);
			// we'll need the bitmap for the drawable in order to rotate it
			unrotatedBitmap = ((BitmapDrawable) drawable).getBitmap();
			spriteWidth = unrotatedBitmap.getWidth() / this.frameCount;
			spriteHeight = unrotatedBitmap.getHeight();
			sourceRect.right = spriteWidth;
			sourceRect.bottom = spriteHeight;
		} catch (IOException e) {
			Log.e("ImageSprite", "Unable to load " + picName);
			drawable = null;
			unrotatedBitmap = null;
		}
		registerChange();
	}

	private void setSheetBackground() {
		drawable = spriteHelper.getMap();
		unrotatedBitmap = ((BitmapDrawable) drawable).getBitmap();
		Rect r = spriteHelper.getSpriteInfo(0).getRect();
		sourceRect.top = r.top;
		sourceRect.left = r.left;
		sourceRect.right = r.right;
		sourceRect.bottom = r.bottom;
		registerChange();
	}

	/**
	 * 
	 * @return the current frame of the animation (starts at 1)
	 */
	public int getFrame() {
		return currentFrame + 1;
	}

	/**
	 * Set the frames per second of the animation
	 * 
	 * @param fps
	 */
	public void setFPS(double fps) {
		this.fps = fps;
	}

	public void setX(double x) {
		xLeft = x;
	}

	public void setY(double y) {
		yTop = y;
	}

	@Override
	protected void checkTempCoords(double x, double y) {
		// Check to see if the current position of the sprite
		// falls into the area set for moving the canvas.
		// If so, send the command to the canvas to move.
		if (canvasControl) {
			double[] d = processCanvasMove(x, y, Width(), Height(), new int[] { scrollLeft, scrollRight, scrollTop, scrollBottom }, 
					getCanvas());
			xLeft = d[0];
			yTop = d[1];
		} else {
			xLeft = x;
			yTop = y;
		}
	}
	
	// scroll int[] 0 = left, 1 = right, 2 = top, 3 = bottom
	private static double[] processCanvasMove(double x, double y, int width, int height, int scroll[], DrawingCanvas check) {
		double[] d = new double[2];
		int left = (int) Math.round(x);
		int top = (int) Math.round(y);
		int right = left + width;
		int bottom = top + height;
		int xDiff = 0;
		int yDiff = 0;
		boolean moveCanvas = false;		
		if (left < scroll[0]) {
			if (!check.atEdge(DrawingCanvas.LEFT)) {
				xDiff = scroll[0] - left;
			} else {
				xDiff = 0;
			}
			moveCanvas = true;
		}
		if (right > scroll[1]) {
			if (!check.atEdge(DrawingCanvas.RIGHT)) {
				xDiff = scroll[1] - right;
			} else {
				xDiff = 0;
			}
			moveCanvas = true;
		}
		if (top < scroll[2]) {
			if (!check.atEdge(DrawingCanvas.TOP)) {
				yDiff = scroll[2] - top;
			} else {
				yDiff = 0;
			}
			moveCanvas = true;
		}
		if (bottom > scroll[3]) {
			if (!check.atEdge(DrawingCanvas.BOTTOM)) {
				yDiff = scroll[3] - bottom;
			} else {
				yDiff = 0;
			}
			moveCanvas = true;
		}
		if (xDiff == 0 && yDiff == 0) {
			moveCanvas = false;
		}
		if (moveCanvas) {
			int[] rtn = check.MoveCanvas(-xDiff, -yDiff);
			if (rtn[0] == 0 && rtn[1] == 0) {
				d[0] = x;
				d[1] = y;
			}
		} else {
			d[0] = x;
			d[1] = y;
		}
		return d;
	}

	@Override
	protected void checkTempCoords() {
		// Check to see if the current position of the sprite
		// falls into the area set for moving the canvas.
		// If so, send the command to the canvas to move.
		double tmp[] = tempUpdateCoordinates();
		if (canvasControl) {
			double[] d = processCanvasMove(tmp[0], tmp[1], Width(), Height(), new int[] { scrollLeft, scrollRight, 
				scrollTop, scrollBottom}, getCanvas());
			xLeft = d[0];
			yTop = d[1];
		} else {
			xLeft = tmp[0];
			yTop = tmp[1];
		}
	}
	
	private static Rect setDestRec(double xLeft, double yTop, int width, int height) {
		Rect destRec = new Rect();
		int xinit = (int) Math.round(xLeft);
		int yinit = (int) Math.round(yTop);
		destRec.left = xinit;
		destRec.top = yinit;
		destRec.right = xinit + width;
		destRec.bottom = yinit + height;
		return destRec;
	}

	public void onDraw(android.graphics.Canvas canvas) {

		if (sheetAnimation) {
			if (unrotatedBitmap != null && visible) {				
				// The source Rect declares where in our image the currant frame
				// is. The dest Rect declares where on the canvas it should draw the
				// bitmap. We can use this to stretch the image however you want.
				destRec = setDestRec(xLeft, yTop, Width(), Height());
				canvas.drawBitmap(unrotatedBitmap, sourceRect, destRec, null);
			}
		} else {
			if (unrotatedBitmap != null && visible) {
				int xinit = (int) Math.round(xLeft);
				int yinit = (int) Math.round(yTop);
				int w = Width();
				int h = Height();
				// If the sprite doesn't rotate, use the original drawable
				// otherwise use the bitmapDrawable
				if (!rotates) {
					drawable.setBounds(xinit, yinit, xinit + w, yinit + h);
					drawable.draw(canvas);
				} else {
					// compute the new rotated image if the heading has changed
					if (!rotationCached || (cachedRotationHeading != Heading())) {
						// Set up the matrix for the rotation transformation. Rotate around the center of the sprite image (w/2,
						// h/2) TODO(user): Add a way for the user to specify the center of rotation.
						mat.setRotate((float) -Heading(), w / 2, h / 2);
						// Next create the rotated bitmap Careful: We use getWidth and getHeight of the
						// unrotated bitmap, rather than the Width and Height of the sprite. Doing the latter
						// produces an illegal argument exception in creating the bitmap, if the user sets
						// the Width or Height of the sprite to be larger than the image size.
						rotatedBitmap = Bitmap
								.createBitmap(unrotatedBitmap, 0, 0, unrotatedBitmap.getWidth(), unrotatedBitmap.getHeight(), mat, true);
						// make a drawable for the rotated image and cache the
						// heading
						rotatedDrawable = new BitmapDrawable(this.canvas.$context().getResources(), rotatedBitmap);
						cachedRotationHeading = Heading();
					}
					// Position the drawable:
					// We want the center of the image to remain fixed under the rotation.
					// To do this, we have to take account of the fact that, since the original
					// and the rotated bitmaps are rectangular, the offset of the center point from (0,0)
					// in the rotated bitmap will in general be different from the offset
					// in the unrotated bitmap. Namely, rather than being 1/2 the width and height of the
					// unrotated bitmap, the offset is 1/2 the width and height of the rotated bitmap.
					// So when we display on the canvas, we need to displace the upper left away
					// from (xinit, yinit) to take account of the difference in the offsets.
					rotatedDrawable.setBounds(xinit + w / 2 - rotatedBitmap.getWidth() / 2, yinit + h / 2 - rotatedBitmap.getHeight() / 2,
					// add in the width and height of the rotated bitmap
					// to get the other right and bottom edges
							xinit + w / 2 + rotatedBitmap.getWidth() / 2, yinit + h / 2 + rotatedBitmap.getHeight() / 2);
					rotatedDrawable.draw(canvas);
				}
			}
		}

	}

	/**
	 * Use this method to manually move the sprite's image to the specified
	 * frame in the sprite sheet.
	 * 
	 * @param frame
	 */
	public void gotoFrame(int frame) {
		if (sheetAnimation && !sheetHelper) {
			frame--;
			if (frame >= frameCount) {
				Toast.makeText(canvas.$context(), "Frame doesn't exist!", Toast.LENGTH_LONG).show();
			} else {
				currentFrame = frame;
				sourceRect.left = currentFrame * spriteWidth;
				sourceRect.right = sourceRect.left + spriteWidth;
				registerChange();
			}
		} else if (sheetAnimation && sheetHelper) {
			frame--;
			if (frame >= frameCount) {
				Log.e("ImageSprite", "Frame index given is larger than the total sprite size.");
			} else {
				currentFrame = frame;
				sourceRect.left = spriteHelper.getSpriteInfo(frame).getRect().left;
				sourceRect.right = spriteHelper.getSpriteInfo(frame).getRect().right;
				sourceRect.top = spriteHelper.getSpriteInfo(frame).getRect().top;
				sourceRect.bottom = spriteHelper.getSpriteInfo(frame).getRect().bottom;
				registerChange();
			}
		}
	}

	/**
	 * 
	 * @return the amount of frames in the sprite sheet
	 */
	public int getFrameCount() {
		return this.frameCount;
	}

	/**
	 * Use this to manually have the sprite move up one frame in the animation
	 * (sprite sheet animation)
	 */
	public void upFrame() {
		if (sheetAnimation && !sheetHelper && currentFrame < frameCount - 1) {
			currentFrame++;
			sourceRect.left = currentFrame * spriteWidth;
			sourceRect.right = sourceRect.left + spriteWidth;
			registerChange();
		} else if (sheetAnimation && sheetHelper && currentFrame < frameCount - 1) {
			currentFrame++;
			Rect r = spriteHelper.getSpriteInfo(currentFrame - 1).getRect();
			sourceRect.top = r.top;
			sourceRect.left = r.left;
			sourceRect.right = r.right;
			sourceRect.bottom = r.bottom;
			registerChange();
		}
	}

	/**
	 * Use this to manually have the sprite move down one frame in the animation
	 * (sprite sheet animation)
	 */
	public void downFrame() {
		if (sheetAnimation && !sheetHelper && currentFrame > 0) {
			currentFrame--;
			sourceRect.left = currentFrame * spriteWidth;
			sourceRect.right = sourceRect.left + spriteWidth;
			registerChange();
		} else if (sheetAnimation && sheetHelper && currentFrame > 0) {
			currentFrame--;
			Rect r = spriteHelper.getSpriteInfo(currentFrame - 1).getRect();
			sourceRect.top = r.top;
			sourceRect.left = r.left;
			sourceRect.right = r.right;
			sourceRect.bottom = r.bottom;
			registerChange();
		}
	}

	// Used internally by the thread. Changed to private to avoid confusion.
	// gotoFrame is the method to use.
	private void curFrame(int frame) {
		if (frame > frameCount) {
			this.currentFrame = 1;
		} else {
			this.currentFrame = frame;
		}
	}

	/**
	 * Use this to start the sprite sheet animation. Most of the animation is
	 * handled in a seperate thread in an effort to keep the animation as smooth
	 * as possible. When the sprite redraws, it is drawn using the UI thread
	 * (only the UI thread may touch the UI), so you can still experience
	 * slowness if the UI thread us processing a lot.
	 */
	public void startAnimation() {
		if (sheetAnimation && !animRunning) {
			framePeriod = 1000 / this.fps;
			running = true;
			animRunning = true;
			animThread = new Thread(new Runnable() {
				@Override
				public void run() {
					long beginTime;
					long timeDiff;
					int sleepTime;
					int curframe;
					int x = 0;
					while (running) {
						// we declare our internal frame counter here. We cannot
						// directly modify the
						// currentFrame variable, so we do it later by running
						// the curFrame() method
						if (!customAnim) {
							curframe = currentFrame;
							beginTime = System.currentTimeMillis();
							// Here we need to post to the UI's thread, as a
							// seperate thread cannot touch
							// any views. While this may seem to defeat the
							// purpose, the timing for the animation
							// is handled in a seperate thread, rather than the
							// UI thread (which has other
							// stuff to process).
							androidUIhandler.post(new Runnable() {
								@Override
								public void run() {
									gotoFrame(currentFrame);
								}
							});

							// Get the time spent changing the sprite's image
							timeDiff = System.currentTimeMillis() - beginTime;

							// If there is time to spare between frames, go to
							// sleep for this long
							sleepTime = (int) (framePeriod - timeDiff);

							if (sleepTime > 0) {
								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException e) {
								}
							}

							// to adjust if the system falls behind frames
							while (sleepTime < 0) {
								sleepTime += framePeriod;
							}

							// increase frame to go to next frame of animation
							curframe++;

							// If the current frame is higher than the
							// framecount, go back to the first frame
							// If the animation is not set to loop, then it
							// stops here.
							if (curframe > frameCount) {
								curframe = 1;
								if (!loop) {
									running = false;
									animRunning = false;
									endAnimationEvent();
								}
							}
							// This increases the currentframe which resides
							// outside of the thread.
							curFrame(curframe);
						} else {
							curframe = customAnimFrames[x];
							beginTime = System.currentTimeMillis();
							curFrame(curframe);
							androidUIhandler.post(new Runnable() {
								@Override
								public void run() {
									gotoFrame(currentFrame);
								}
							});
							timeDiff = System.currentTimeMillis() - beginTime;
							sleepTime = (int) (framePeriod - timeDiff);
							while (sleepTime < 0) {
								sleepTime += framePeriod;
								x++;
							}
							if (sleepTime > 0) {
								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException e) {
								}
							}
							x++;
							if (x >= customAnimFrames.length) {
								x = 0;
								if (!loop) {
									running = false;
									animRunning = false;
									endAnimationEvent();
								}
							}
						}
					}
				}
			});
			animThread.start();
		}
	}

	public int[] getCustomAnimFrames() {
		return customAnimFrames;
	}

	private void endAnimationEvent() {
		final ImageSprite component = this;
		androidUIhandler.post(new Runnable() {
			@Override
			public void run() {
				// This line is deprecated, but leaving it to avoid breaking
				// current apps.
				EventDispatcher.dispatchEvent(component, "AnimationStopped");
				// This is the correct event to throw
				EventDispatcher.dispatchEvent(component, Events.ANIM_END);
			}
		});
	}

	/**
	 * Returns the path of the sprite's picture
	 * 
	 * @return the path of the sprite's picture
	 */

	public String Picture() {
		return picturePath;
	}

	/**
	 * Specifies the path of the sprite's picture
	 * 
	 * <p/>
	 * See {@link MediaUtil#determineMediaSource} for information about what a
	 * path can be.
	 * 
	 * @param path
	 *            the path of the sprite's picture
	 */

	public void Picture(String path) {
		picturePath = (path == null) ? "" : path;
		if (path.contains(".")) {
			path = path.split("\\.")[0];
		}
		try {
			if (canvas != null) {
				drawable = MediaUtil.getDrawable(canvas.$context(), path);
			} else {
				drawable = MediaUtil.getDrawable(aCanvas.$context(), path);
			}
		} catch (IOException e) {
			Log.e("ImageSprite", "Resource not found: " + path);
			drawable = null;
		}
		if (drawable == null) {
			unrotatedBitmap = null;
		} else {
			unrotatedBitmap = ((BitmapDrawable) drawable).getBitmap();
		}
		registerChange();
	}
	
	/**
	 * Specifies the resource of the image to use.
	 * 
	 * 
	 * @param resourceId
	 *            the resource Id of the image to use
	 */

	public void Picture(int resourceId) {
		if (canvas != null) {
			drawable = canvas.$context().getResources().getDrawable(resourceId);
		} else {
			drawable = aCanvas.$context().getResources().getDrawable(resourceId);
		}
		if (drawable == null) {
			unrotatedBitmap = null;
		} else {
			unrotatedBitmap = ((BitmapDrawable) drawable).getBitmap();
		}
		registerChange();
	}
	
	/**
	 * Specifies the drawable the image will use.
	 * 
	 * 
	 * @param drawable
	 *            the drawable to use
	 */
	public void Picture(Drawable drawable) {
		this.drawable = drawable;
		if (drawable == null) {
			unrotatedBitmap = null;
		} else {
			unrotatedBitmap = ((BitmapDrawable) drawable).getBitmap();
		}
	}

	// The actual width/height of an ImageSprite whose Width/Height property is
	// set to Automatic or
	// Fill Parent will be the width/height of the image.

	@Override
	public int Height() {
		if (heightHint == LENGTH_PREFERRED || heightHint == LENGTH_FILL_PARENT) {
			// Drawable.getIntrinsicWidth/Height gives weird values, but
			// Bitmap.getWidth/Height works.
			// If drawable is a BitmapDrawable (it should be), we can get the
			// Bitmap.
			if (drawable instanceof BitmapDrawable) {
				return ((BitmapDrawable) drawable).getBitmap().getHeight();
			}
			return (drawable != null) ? drawable.getIntrinsicHeight() : 0;
		}
		return heightHint;
	}

	@Override
	public void Height(int height) {
		heightHint = height;
		registerChange();
	}

	@Override
	public int Width() {
		if ((widthHint == LENGTH_PREFERRED || widthHint == LENGTH_FILL_PARENT) && !sheetAnimation) {
			// Drawable.getIntrinsicWidth/Height gives weird values, but
			// Bitmap.getWidth/Height works.
			// If drawable is a BitmapDrawable (it should be), we can get the
			// Bitmap.
			if (drawable instanceof BitmapDrawable) {
				return ((BitmapDrawable) drawable).getBitmap().getWidth();
			}
			return (drawable != null) ? drawable.getIntrinsicWidth() : 0;
		}
		return widthHint;
	}

	@Override
	public void Width(int width) {
		widthHint = width;
		registerChange();
	}

	/**
	 * 
	 * @return {@code true} indicates that the image rotates to match the
	 *         sprite's heading {@code false} indicates that the sprite image
	 *         doesn't rotate.
	 */

	public boolean Rotates() {
		return rotates;
	}

	/**
	 * Rotates property setter method. If you want to resize the sprite to
	 * something other than the image's size, you can NOT have it rotate. This
	 * causes an Exception to happen normally (the exception doesn't happen with
	 * the bridge, as it simply won't resize the image if the sprite is set to
	 * rotate).
	 * 
	 * @param rotates
	 *            {@code true} indicates that the image rotates to match the
	 *            sprite's heading {@code false} indicates that the sprite image
	 *            doesn't rotate.
	 */
	public void Rotates(boolean rotates) {
		this.rotates = rotates;
		registerChange();
	}

	/*
	 * Set whether this animation will continually loop or not.
	 */
	public void LoopAnimation(boolean loop) {
		this.loop = loop;
	}

	/**
	 * Stop the current spritesheet animation on this sprite.
	 */
	public void stopAnimation() {
		this.running = false;
		this.animRunning = false;
		boolean retry = true;
		while (retry) {
			try {
				animThread.join();
				retry = false;
			} catch (InterruptedException e) {

			}
		}
	}

	/**
	 * This toggles whether or not to stop the sprite's internal timers when the
	 * application loses focus. When set to true, the timers will shut off when
	 * the app loses focus, and turned back on when the app regains focus.
	 * 
	 * @param autotoggle
	 */
	public void AutoToggle(boolean autotoggle) {
		this.autoToggle = autotoggle;
	}

	@Override
	public void onResume() {
		// We can now turn the timer back on
		if (autoToggle) {
			if (animRunning) {
				this.running = true;
			}
			Enabled(true);
		}
		if (destRec == null) {
			destRec = new Rect(0, 0, 0, 0);
		}
		if (sourceRect == null) {
			sourceRect = new Rect(0, 0, 0, 0);
		}
	}

	@Override
	public void onStop() {
		// Turn the sprite's timer off to avoid memory leaks (this can be shut
		// off by using AutoToggle(false);
		if (autoToggle) {
			if (animRunning || running) {
				this.running = false;
			}
			Enabled(false);
		}
	}

	@Override
	public void Enabled(boolean enabled) {
		if (canvas != null) {
			super.Enabled(enabled);
		}
		this.enabled = enabled;
	}

	@Override
	public boolean Enabled() {
		if (canvas != null) {
			return super.Enabled();
		} else {
			return enabled;
		}
	}

	public void requestDownEvent() {
		requestEvent(this, "DownState");
	}

	public void requestUpEvent() {
		requestEvent(this, "UpState");
	}

	@Override
	public void onConfigurationChanged() {
		if (canvasControl && multipliers != null) {
			setCanvasControls(multipliers[0], multipliers[1]);
		}
	}

}
