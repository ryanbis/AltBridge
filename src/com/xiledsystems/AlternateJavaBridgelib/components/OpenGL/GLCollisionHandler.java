package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

import java.util.HashSet;
import java.util.Set;


public interface GLCollisionHandler {
	
	public Set<GLSprite> registeredCollisions = new HashSet<GLSprite>();
	
	public void CollidedWith(GLSprite other);
	
	public void NoLongerCollidingWith(GLSprite other);
	
	public boolean CollidingWith(GLSprite other);

}
