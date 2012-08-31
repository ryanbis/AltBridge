package com.xiledsystems.AlternateJavaBridgelib.LiveWallpapers;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;


public abstract class AnimationWallpaper extends WallpaperService {

	protected abstract class AnimationEngine extends Engine {
		
		private Handler handler = new Handler();
		
		private int frameRate = 40;
		
		private Runnable mIteration = new Runnable() {			
			@Override
			public void run() {
				iteration();
				drawFrame();
			}
		};
		
		private boolean visible;
		
		protected void FrameRate(int frameRate) {
			this.frameRate = frameRate;
		}
		
		protected int FrameRate() {			
			return frameRate;			
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			// Stop the animation
			handler.removeCallbacks(mIteration);
		}
		
		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				iteration();
				drawFrame();
			} else {
				handler.removeCallbacks(mIteration);
			}
		}
		
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			iteration();
			drawFrame();
		}
		
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			visible = false;
			handler.removeCallbacks(mIteration);
		}
		
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep,
				float yOffsetStep, int xPixelOffest, int yPixelOffset) {
			iteration();
			drawFrame();
		}
		
		protected void drawFrame() {
			SurfaceHolder holder = getSurfaceHolder();			
			Canvas canvas = null;			
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					draw(canvas);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
		
		protected abstract void draw(Canvas canvas);
		
		protected void iteration() {			
			// Reschedule the next redraw. Default framerate is
			// 40ms (25 fps).
			handler.removeCallbacks(mIteration);
			if (visible) {
				handler.postDelayed(mIteration, frameRate);
			}
		}
		
	}		
}
