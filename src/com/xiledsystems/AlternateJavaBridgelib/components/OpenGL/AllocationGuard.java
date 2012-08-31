package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;


/**
 * A good majority of the classes in this package come from replica island.
 * I liked the way things were implemented, and used a good amount from it.
 * Some things had to be changed along the way to fit it into the AltBridge's
 * event system.
 * 
 * AllocationGuard is a utility class for tracking down memory leaks.  It implements a 
 * "checkpoint" memory scheme.  After the static sGuardActive flag has been set, any further
 * allocation of AllocationGuard or its derivatives will cause an error log entry.  Note
 * that AllocationGuard requires all of its derivatives to call super() in their constructor. 
 *
 */
public class AllocationGuard {
	
	public static boolean sGuardAcive = false;
	
	public AllocationGuard() {
		if (sGuardAcive) {
			DebugLog.e("AllocGuard", "An allocation of type " + this.getClass().getName() 
                    + " occurred while the AllocGuard is active.");
		}
	}

}
