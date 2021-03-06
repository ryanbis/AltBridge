package com.xiledsystems.AlternateJavaBridgelib.components.events;

public class Events {
	
	private Events() {		
	}
	
	public final static String ALARM = "Alarm";
	public final static String CLICK = "Click";
	public final static String UP_STATE = "UpState";
	public final static String DOWN_STATE = "DownState";
	public final static String GOT_FOCUS = "GotFocus";
	public final static String LOST_FOCUS = "LostFocus";
	public final static String SCREEN_INIT = "Initialize";
	public final static String CANVAS_INIT = "CanvasInitialized";
	public final static String CHANGED = "Changed";
	public final static String MSG_RECEIVED = "MessageReceived";
	public final static String AFTER_ACTIVIY = "AfterActivity";
	public final static String AFTER_PICKING = "AfterPicking";
	public final static String AFTER_CHOOSING = "AfterChoosing";
	public final static String AFTER_SELECTION = "AfterSelection";
	public final static String ACCELERATION_CHANGED = "AccelerationChanged";
	public final static String AFTER_GETTING_TEXT = "AfterGettingText";
	public final static String AFTER_ACTIVITY = "AfterActivity";
	public final static String AFTER_SCAN = "AfterScan";
	public final static String AFTER_SND_RECORDED = "AfterSoundRecorded";
	public final static String AFTER_TXT_INPUT = "AfterTextInput";
	public final static String AFTER_PICTURE = "AfterPicture";
	public final static String ANIM_START = "AnimStart";
	public final static String ANIM_MIDDLE = "AnimationMiddle";
	public final static String ANIM_END = "AnimEnd";
	public final static String ANIM_REPEAT = "AnimRepeat";
	public final static String BEFORE_GETTING_TXT = "BeforeGettingText";
	public final static String BEFORE_PICKING = "BeforePicking";
	public final static String COLLIDED_WITH = "CollidedWith";
	public final static String COMPLETED = "Completed";
	
	public final static String DRAWER_OPENED = "DrawerOpened";
	public final static String DRAWER_CLOSED = "DrawerClosed";
	
	public final static String OAUTH1_AUTHORIZED = "OAuth1Authorized";
	public final static String OAUTH_API_RETURN = "OAuth1APIReturn";
	
	public final static String LEFT_ICON_CLICK = "LeftIconClicked";
	
	/**
	 * args[0] = startX   ---   args[1] = startY   ---   args[2] = prevX   ---   
	 * 
	 * args[3] = prevY   ---   args[4] = currentX   ---   args[5] = currentY   ---   
	 * 
	 * args[6] = draggedSprite (only valid is Canvas')
	 */
	public final static String DRAGGED = "Dragged";
	public final static String DONE_DRAGGING = "DoneDragging";
	public final static String EDGE_REACHED = "EdgeReached";
	public final static String ERROR_OCCURRED = "ErrorOccurred";
	public final static String FORM_SVC_MSG = "FormServiceMessage";
	
	/**
	 * Event thrown when a Form gets a result from another form (when using
	 * startActivityForResult() ).
	 * args[0] = (int) requestCode
	 * args[1] = (int) resultCode
	 * args[2] = (Intent) data
	 * 
	 */
	public final static String FORM_RESULT = "FormResult";
	public final static String GOT_VALUE = "GotValue";
	public final static String GOT_TEXT = "GotText";
	public final static String GOT_FILE = "GotFile";
	public final static String LOCATION_CHANGED = "LocationChanged";
	public final static String ON_START_CMD = "onStartCommand";
	public final static String ORIENTATION_CHANGED = "OrientationChanged";
	public final static String POSITION_CHANGED = "PositionChanged";
	public final static String RESPONSE = "Response";
	public final static String SCREEN_ORIENTATION_CHANGED = "ScreenOrientationChanged";
	public final static String SHAKING = "Shaking";
	public final static String STARTED_REC = "StartedRecording";
	public final static String STATUS_CHANGED = "StatusChanged";
	public final static String STOPPED_REC = "StoppedRecording";
	public final static String THREAD_RUNNING = "ThreadRunning";
	public final static String TIMER = "Timer";
	public final static String SOAP_RESPONSE = "SoapResponse";
	public final static String ON_RESUME = "onResume";
	public final static String ON_STOP = "onStop";
	public final static String ON_START = "onStart";
	public final static String ON_PAUSE = "onPause";
	
	/**This event is fired after a date is chosen from the "DateSelector"
	 * 
	 * It returns the date in the format 
	 *            
	 * args[0] = int year
	 *            
	 * args[1] = int month
	 *             
	 * args[2] = int day
	 * 
	 * You can also retrieve the date via the getDate() method
	 */
	public final static String DATE_SET = "DateSet";
	
	/**This event is fired after a time is chosen from the "TimeSelector"
	 *         
	 * It returns the time in the format
	 *           
	 * args[0] = int hour (24 hour time style)
	 *        
	 * args[1] = int min 
	 * 
	 * You can also retrieve the time via the getTime() method
	 */
	public final static String TIME_SET = "TimeSet";
	
	/**
	 *  
	 * args[0] = x coordinate of where the touch happened  ---  
	 * 
	 * args[1] = y coordinate of where the touch happened  ---  
	 * 
	 * args[2] = true/false if a sprite was touched (this only applies for
	 * canvas'
	 * 
	 */
	public final static String TOUCHED = "Touched";
	public final static String VALUE_STORED = "ValueStored";
	public final static String WEB_SVC_ERROR = "WebServiceError";
	public final static String MENU_CLICKED = "MenuClicked";	
	public final static String SERVICE_BOUND = "ServiceBound";
	public final static String SERVICE_UNBOUND = "ServiceUnBound";
	public final static String BEFORE_EXECUTE = "BeforeExecuting";
	public final static String TASK_EXECUTE = "BackgroundTaskExecute";
	public final static String TASK_COMPLETE = "TaskCompleted";
	public final static String LONG_CLICK = "LongClick";
	
	/**
	 * Returns a string with the error message in args[0]
	 */
	public final static String BILLING_ERROR = "GoogleBillingError";
	
	/**
	 * This event is thrown when querying the billing inventory for
	 * purchases. 
	 * 
	 * args[0] = (IabResult) - the result of the query
	 * args[1] = (Inventory) - The inventory of purchases, use getPurchase to check for a purchase
	 * 
	 */
	public final static String INVENTORY_RESULT = "BillingInventoryResult";
	
	/**
	 * This event is thrown when a purchase request has been sent. If there are
	 * errors, the BILLING_ERROR event will be thrown.
	 * 
	 * args[0] = (IabResult) - The result of the purchase request
	 * args[1] = (Purchase) - The purchase information of this request.
	 * 
	 */
	public final static String PURCHASE_FINISHED = "BillingPurchaseFinished";
	
	public interface Click {
		public void Clicked();
	}
	
	public interface Event {
		public void eventDispatched(String eventName, Object... args);
	}

}
