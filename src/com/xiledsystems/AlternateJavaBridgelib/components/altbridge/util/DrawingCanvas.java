package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;


public interface DrawingCanvas {

	public final static int TOP = 0;
	public final static int LEFT = 1;
	public final static int RIGHT = 2;
	public final static int BOTTOM = 3;

	public boolean atEdge(int edge);

	public int Width();

	public int Height();

	public Registrar getRegistrar();

	public int[] MoveCanvas(int xDifference, int yDifference);

	public void BackgroundColor(int color);
	
	public void canvasInitialized();
}
