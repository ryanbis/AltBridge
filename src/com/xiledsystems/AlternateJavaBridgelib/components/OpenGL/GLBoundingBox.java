package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;


public final class GLBoundingBox {
	
	public float left;
	public float right;
	public float top;
	public float bottom;
	
	/**
	   * Constructor for a bounding box.  All coordinates are inclusive.
	   *
	   * @param l leftmost x-coordinate
	   * @param t topmost y-coordinate
	   * @param r rightmost x-coordinate
	   * @param b bottommost y-coordinate
	   */
	  public GLBoundingBox(float l, float t, float r, float b) {
	    left = l;
	    top = t;
	    right = r;
	    bottom = b;
	  }
	  
	  /**
	   * Determines whether this bounding box intersects with the passed bounding
	   * box and, if so, mutates the bounding box to be the intersection.  This was
	   * designed to behave the same as
	   * {@link android.graphics.Rect#intersect(android.graphics.Rect)}.
	   *
	   * @param bb bounding box to intersect with this bounding box
	   * @return {@code true} if they intersect, {@code false} otherwise
	   */
	  public boolean intersectDestructively(GLBoundingBox bb) {
	    // Determine intersection.
		  float xmin = Math.max(left, bb.left);
		  float xmax = Math.min(right, bb.right);
		  float ymin = Math.max(top, bb.top);
		  float ymax = Math.min(bottom, bb.bottom);
		 // float ymin = Math.min(bottom, bb.bottom);
		  //float ymax = Math.max(top, bb.top);
	    // If there is no intersection, return false.
	    if (xmin > xmax || ymin > ymax) {
	      return false;
	    }

	    // Mutate this bounding box to be the intersection before returning true.
	    left = xmin;
	    right = xmax;
	    top = ymin;
	    bottom = ymax;
	    return true;

	  }
	  
	  public String toString() {
		    return "<BoundingBox (left = " + left + ", top = " + top +
		        ", right = " + right + ", bottom = " + bottom + ">";
	  }

}
