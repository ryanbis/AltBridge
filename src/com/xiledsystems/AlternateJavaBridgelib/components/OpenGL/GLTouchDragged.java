package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

public interface GLTouchDragged {
	
	/**
	* Handler for Touched events.
	*
	* @param x  x-coordinate of touched point
	* @param y  y-coordinate of touched point
	*/
	public void Touched(float x, float y);
	
	/**
	   * Handler for Dragged events.  On all calls, the starting coordinates
	   * are where the screen was first touched, and the "current" coordinates
	   * describe the endpoint of the current line segment.  On the first call
	   * within a given drag, the "previous" coordinates are the same as the
	   * starting coordinates; subsequently, they are the "current" coordinates
	   * from the prior call.  Note that the Sprite won't actually move
	   * anywhere in response to the Dragged event unless MoveTo is
	   * specifically called.
	   *
	   * @param startX the starting x-coordinate
	   * @param startY the starting y-coordinate
	   * @param prevX the previous x-coordinate (possibly equal to startX)
	   * @param prevY the previous y-coordinate (possibly equal to startY)
	   * @param currentX the current x-coordinate
	   * @param currentY the current y-coordinate
	   */
	public void Dragged(float startX, float startY,
            float prevX, float prevY,
            float currentX, float currentY);
	
		
	public void UpState();
	
	public void DownState();
	
	public boolean Enabled();
	
	public boolean Visible();
	
	public boolean intersectsWith(GLBoundingBox bb);

}
