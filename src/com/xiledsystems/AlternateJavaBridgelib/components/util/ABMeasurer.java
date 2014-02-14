package com.xiledsystems.AlternateJavaBridgelib.components.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;

public class ABMeasurer {

	private float widthMult;
	private float heightMult;
	private int width;
	private int height;
	private boolean useMults = false;

	public float getWidthMult() {
		return widthMult;
	}

	public void setWidthMult(float width) {
		this.widthMult = width;
	}

	public float getHeightMult() {
		return heightMult;
	}

	public void setHeightMult(float height) {
		this.heightMult = height;
	}

	public int getWidth() {
		return width;
	}

	public int getWidthSpec() {
		return MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
	}

	public int getHeight() {
		return height;
	}

	public int getHeightSpec() {
		return MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
	}

	public boolean useMults() {
		return useMults;
	}

	// Grab the multiplier values from xml
	public void getMultipliers(Context context, AttributeSet attrs, int defStyle, int[] style, int width, int height) {
		TypedArray a = context.obtainStyledAttributes(attrs, style, 0, defStyle);
		setWidthMult(a.getFloat(width, -1));
		setHeightMult(a.getFloat(height, -1));
		a.recycle();
	}

	public boolean processOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (widthMult > 0 || heightMult > 0) {
			width = MeasureSpec.getSize(widthMeasureSpec);
			height = MeasureSpec.getSize(heightMeasureSpec);
			if (widthMult > 0) {
				width *= widthMult;
			}
			if (heightMult > 0) {
				height *= heightMult;
			}
			useMults = true;
			return true;
		} else {
			useMults = false;
			return false;
		}
	}

	public boolean processOnMeasure(Context context) {
		if (widthMult > 0 || heightMult > 0) {
			width = context.getResources().getDisplayMetrics().widthPixels;
			width *= widthMult;
			height = context.getResources().getDisplayMetrics().heightPixels;
			height *= heightMult;
			useMults = true;
			return true;
		} else {
			useMults = false;
			return false;
		}
	}

	public boolean processOnMeasure(Context context, int widthMeasureSpec, int heightMeasureSpec) {
		if (widthMult > 0 || heightMult > 0) {
			if (widthMult > 0) {
				width = context.getResources().getDisplayMetrics().widthPixels;
				width *= widthMult;
			} else {
				width = MeasureSpec.getSize(widthMeasureSpec);
			}
			if (heightMult > 0) {
				height = context.getResources().getDisplayMetrics().heightPixels;
				height *= heightMult;
			} else {
				height = MeasureSpec.getSize(heightMeasureSpec);
			}
			useMults = true;
			return true;
		} else {
			useMults = false;
			return false;
		}
	}
}
