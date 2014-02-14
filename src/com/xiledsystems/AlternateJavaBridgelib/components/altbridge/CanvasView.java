package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;

/**
 * Panel for drawing and manipulating sprites.
 * 
 */
@SuppressLint("ViewConstructor")
final class CanvasView extends View {
	/**
	 * 
	 */
	private final Canvas canvas2;
	private android.graphics.Canvas canvas;
	Bitmap bitmap;
	private boolean gotSize;
	
	// We have a separate bitmap/canvas for drawing. This gets sized in onLayout.
	// It seems in newer OSs (I believe JB), that onSizeChanged can be called after
	// the screen is initialized. Which means if you use any of the drawing methods
	// in define, or in screen init, it would result in the drawing bitmap getting resized,
	// affecting the drawing. Resizing in onLayout works better (it's called earlier in the
	// lifecycle so we know we can call any drawing methods whenever we want)
	private Bitmap drawBitmap;
	private android.graphics.Canvas drawCanvas;
	

	public CanvasView(Canvas canvas, Context context) {
		super(context);
		canvas2 = canvas;
		bitmap = Bitmap.createBitmap(ComponentConstants.CANVAS_PREFERRED_WIDTH, ComponentConstants.CANVAS_PREFERRED_HEIGHT,
				Bitmap.Config.ARGB_8888);
		this.canvas = new android.graphics.Canvas(bitmap);
		drawBitmap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
		drawCanvas = new android.graphics.Canvas(drawBitmap);
	}
	
	public void drawPaint(final Paint paint) {
		if (gotSize) {
			drawCanvas.drawPaint(paint);
			invalidate();
		} else {
			postDelayed(new Runnable() {				
				@Override
				public void run() {
					if (gotSize) {
						drawCanvas.drawPaint(paint);
						invalidate();
					} else {
						postDelayed(this, 50);
					}
				}
			}, 50);
		}
	}
	
	public void drawColor(final int color, final PorterDuff.Mode mode) {		
		if (gotSize) {
			drawCanvas.drawColor(color, mode);
			invalidate();
		} else {
			postDelayed(new Runnable() {				
				@Override
				public void run() {
					if (gotSize) {
						drawCanvas.drawColor(color, mode);
						invalidate();
					} else {
						postDelayed(this, 50);
					}
				}
			}, 50);
		}
	}
	
	public void drawPoint(final int x, final int y, final Paint paint) {	
		if (gotSize) {
			drawCanvas.drawPoint((float) x, (float) y, paint);
			invalidate();
		} else {
			postDelayed(new Runnable() {				
				@Override
				public void run() {
					if (gotSize) {
						drawCanvas.drawPoint((float) x, (float) y, paint);
						invalidate();
					} else {
						postDelayed(this, 50);
					}
				}
			}, 50);
		}
	}
	
	public void DrawCircle(final int x, final int y, final float r, final Paint paint) {
		if (gotSize) {
			drawCanvas.drawCircle(x, y, r, paint);		
			invalidate();
		} else {
			postDelayed(new Runnable() {				
				@Override
				public void run() {
					if (gotSize) {
						drawCanvas.drawCircle(x, y, r, paint);	
						invalidate();
					} else {
						postDelayed(this, 50);
					}
				}
			}, 50);
		}		
	}
	
	public void DrawLine(final int x1, final int y1, final int x2, final int y2, final Paint paint) {
		if (gotSize) {
			drawCanvas.drawLine(x1, y1, x2, y2, paint);
			invalidate();
		} else {
			postDelayed(new Runnable() {				
				@Override
				public void run() {
					if (gotSize) {
						drawCanvas.drawLine(x1, y1, x2, y2, paint);
						invalidate();
					} else {
						postDelayed(this, 50);
					}
				}
			}, 50);
		}		
	}
	
	public void DrawRectangle(final int x1, final int y1, final int x2, final int y2, final Paint paint) {
		if (gotSize) {
			drawCanvas.drawRect(x1, y1, x2, y2, paint);
			invalidate();
		} else {
			postDelayed(new Runnable() {				
				@Override
				public void run() {
					if (gotSize) {
						drawCanvas.drawRect(x1, y1, x2, y2, paint);
						invalidate();
					} else {
						postDelayed(this, 50);
					}
				}
			}, 50);
		}		
	}
	
	public void DrawRectangleBehind(final int x1, final int y1, final int x2, final int y2, final Paint paint) {
		if (gotSize) {
			canvas.drawRect(x1, y1, x2, y2, paint);
			invalidate();
		} else {
			postDelayed(new Runnable() {				
				@Override
				public void run() {
					if (gotSize) {
						canvas.drawRect(x1, y1, x2, y2, paint);
						invalidate();
					} else {
						postDelayed(this, 50);
					}
				}
			}, 50);
		}		
	}
	
	public void DrawText(final String text, final int x, final int y, final Paint paint) {
		if (gotSize) {
			drawCanvas.drawText(text, (float) x, (float) y, paint);
			invalidate();
		} else {
			postDelayed(new Runnable() {				
				@Override
				public void run() {
					if (gotSize) {
						drawCanvas.drawText(text, (float) x, (float) y, paint);
						invalidate();
					} else {
						postDelayed(this, 50);
					}
				}
			}, 50);
		}		
	}
	
	public void DrawTextAtAngle(final String text, final int x, final int y, final float angle, final Paint paint) {
		if (gotSize) {
			drawCanvas.save();
			drawCanvas.rotate(-angle, (float) x, (float) y);
			drawCanvas.drawText(text, (float) x, (float) y, paint);
			drawCanvas.restore();	
			invalidate();
		} else {
			postDelayed(new Runnable() {				
				@Override
				public void run() {
					if (gotSize) {
						drawCanvas.save();
						drawCanvas.rotate(-angle, (float) x, (float) y);
						drawCanvas.drawText(text, (float) x, (float) y, paint);
						drawCanvas.restore();		
						invalidate();
					} else {
						postDelayed(this, 50);
					}
				}
			}, 50);
		}		
	}
	
	public android.graphics.Canvas getAndroidCanvas() {
		return drawCanvas;
	}

	@SuppressLint("WrongCall")
	@Override
	public void onDraw(android.graphics.Canvas canvas0) {
		super.onDraw(canvas0); // Redraw the canvas itself		
		canvas0.drawBitmap(drawBitmap, 0, 0, null);
		for (Sprite sprite : canvas2.sprites) {
			sprite.onDraw(canvas0);
		}
		if (!canvas2.drawn) {
			canvas2.canvasInitialized();
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!gotSize) {
			processSizeChange(r - l, b - t);
			drawBitmap = scaleDrawBitmap(drawBitmap, r, l, b, t);
			drawCanvas = createCanvas(drawBitmap);
			gotSize = true;
		}
	}
	
	private static Bitmap scaleDrawBitmap(Bitmap bitmap, int r, int l, int b, int t) {
		return Bitmap.createScaledBitmap(bitmap, r - l, b - t, false);
	}
	
	private static android.graphics.Canvas createCanvas(Bitmap bitmap) {
		return new android.graphics.Canvas(bitmap);
	}
	
	private void processSizeChange(int w, int h) {
		int oldBitmapWidth = bitmap.getWidth();
		int oldBitmapHeight = bitmap.getHeight();
		if (w != oldBitmapWidth || h != oldBitmapHeight) {
			Bitmap oldBitmap = bitmap;

			// Create a new bitmap.
			// The documentation for Bitmap.createScaledBitmap doesn't
			// specify whether it creates a mutable or immutable bitmap. Looking at the source code shows
			// that it calls Bitmap.createBitmap(Bitmap, int, int, int, int, Matrix, boolean),
			// which is documented as returning an immutable bitmap. However, it actually returns a
			// mutable bitmap. It's possible that the behavior could change in the future if
			// they "fix" that bug. Try Bitmap.createScaledBitmap, but if it gives us an
			// immutable bitmap, we'll have to create a mutable bitmap and scale the old bitmap using
			// Canvas.drawBitmap.
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(oldBitmap, w, h, false);
			if (scaledBitmap.isMutable()) {
				// scaledBitmap is mutable; we can use it in a canvas.
				bitmap = scaledBitmap;
				// NOTE(user) - I tried just doing canvas.setBitmap(bitmap),
				// but after that the canvas.drawCircle() method did not work correctly. So, we
				// need to create a whole new canvas.
				canvas = new android.graphics.Canvas(bitmap);				

			} else {
				// scaledBitmap is immutable; we can't use it in a canvas.

				bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
				// NOTE(user) - I tried just doing canvas.setBitmap(bitmap),
				// but after that the canvas.drawCircle() method did not work correctly. So, we
				// need to create a whole new canvas.
				canvas = new android.graphics.Canvas(bitmap);

				// Draw the old bitmap into the new canvas, scaling as
				// necessary.
				Rect src = new Rect(0, 0, oldBitmapWidth, oldBitmapHeight);
				RectF dst = new RectF(0, 0, w, h);
				canvas.drawBitmap(oldBitmap, src, dst, null);
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		processSizeChange(w, h);		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {			
		int preferredWidth;
		int preferredHeight;
		Drawable backgroundDrawable = canvas2.backgroundDrawable;
		if (backgroundDrawable != null) {
			// Drawable.getIntrinsicWidth/Height gives weird values, but
			// Bitmap.getWidth/Height works.
			// If backgroundDrawable is a BitmapDrawable (it should be), we
			// can get
			// the Bitmap.
			if (backgroundDrawable instanceof BitmapDrawable) {
				Bitmap bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
				preferredWidth = bitmap.getWidth();
				preferredHeight = bitmap.getHeight();
			} else {
				preferredWidth = backgroundDrawable.getIntrinsicWidth();
				preferredHeight = backgroundDrawable.getIntrinsicHeight();
			}
		} else {
			preferredWidth = ComponentConstants.CANVAS_PREFERRED_WIDTH;
			preferredHeight = ComponentConstants.CANVAS_PREFERRED_HEIGHT;
		}
		setMeasuredDimension(getSize(widthMeasureSpec, preferredWidth), getSize(heightMeasureSpec, preferredHeight));			
	}

	private int getSize(int measureSpec, int preferredSize) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Use the preferred size.
			result = preferredSize;
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
	
	public void recycleBitmaps() {
		if (bitmap != null) {
			try {
				bitmap.recycle();
			} catch (Exception e) {				
			}
		}
		if (drawBitmap != null) {
			try {
				drawBitmap.recycle();
			} catch (Exception e) {				
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// The following call results in the Form not grabbing our events
		// and
		// handling dragging on its own, which it wants to do to handle
		// scrolling.
		// Its effect only lasts long as the current set of motion events
		// generated during this touch and drag sequence. Consequently, it
		// needs
		// to be called here, so that it happens for each touch-drag
		// sequence.

		canvas2.container.getRegistrar().dontGrabTouchEventsForComponent();
		canvas2.motionEventParser.parse(event);
		return true;
	}
}