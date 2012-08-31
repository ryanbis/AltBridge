package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;


public class GLCollision {
	
	private GLCollision() {		
	}
	
	/**
	   * Determines whether two sprites are in collision.  This uses a rectangle
	   * for collision detection.
	   *
	   * @param sprite1 one sprite
	   * @param sprite2 another sprite
	   * @return {@code true} if they are in collision, {@code false} otherwise
	   */
	  public static boolean colliding(GLSprite sprite1, GLSprite sprite2) {
	    // If the bounding boxes don't intersect, there can be no collision.
	    GLBoundingBox rect1 = sprite1.getBoundingBox(1);
	    GLBoundingBox rect2 = sprite2.getBoundingBox(1);
	      
	    return rect1.left <= rect2.right && rect1.right >= rect2.left 
	    && rect1.top <= rect2.bottom && rect1.bottom >= rect2.top;
	   
	  }
	  
	  /**
	   * Determines whether two GL BoundingBoxes are in collision.  This uses a rectangle
	   * for collision detection.
	   *
	   * @param sprite1 one sprite
	   * @param sprite2 another sprite
	   * @return {@code true} if they are in collision, {@code false} otherwise
	   */
	  public static boolean colliding(GLBoundingBox rect1, GLBoundingBox rect2) {
	    // If the bounding boxes don't intersect, there can be no collision.
	   	      
	    return rect1.left <= rect2.right && rect1.right >= rect2.left 
	    && rect1.top <= rect2.bottom && rect1.bottom >= rect2.top;
	   
	  }
	  
	    
	  /**
	   * Determines whether two sprites are in collision.  This uses
	   * a circle to determine collisions.
	   *
	   * @param sprite1 one sprite
	   * @param sprite2 another sprite
	   * @return {@code true} if they are in collision, {@code false} otherwise
	   */
	  public static boolean circlecolliding(GLSprite sprite1, GLSprite sprite2) {
		  
		  int newx = (int) (sprite2.vertices[0] - sprite1.vertices[0]);
		  if (newx < 0) {
			  newx = -newx;
		  }
		  int newy = (int) (sprite2.vertices[1] - sprite1.vertices[1]);
		  if (newy < 0) {
			  newy = -newy;
		  }
		  double squaredDistance = (sprite1.getCollisionRadius()+sprite2.getCollisionRadius())+(sprite1.getCollisionRadius()+sprite2.getCollisionRadius());
		  return (newx*newx)+(newy*newy) <= squaredDistance;
		  
	  }

}
