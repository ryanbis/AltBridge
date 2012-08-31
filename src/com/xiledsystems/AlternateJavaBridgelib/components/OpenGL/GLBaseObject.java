package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

public abstract class GLBaseObject extends AllocationGuard {

	//static ObjectRegistry sSystemRegistry = new ObjectRegistry();

    public GLBaseObject() {
        super();
    }
    
    /**
     * Update this object.
     * @param timeDelta  The duration since the last update (in seconds).
     * @param parent  The parent of this object (may be NULL).
     */
    public void update(float timeDelta, GLBaseObject parent) {
        // Base class does nothing.
    }
    
    
    public abstract void reset();

}
