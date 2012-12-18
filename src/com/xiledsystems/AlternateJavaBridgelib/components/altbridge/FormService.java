package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.SvcComponentContainer;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Sets;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.altbridge.BuildConfig;

import android.os.HandlerThread;
import android.os.Process;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * This is the service class for the AltBridge. Writing for a service is similar
 * to a Form in that you still have a $define method, and the event system.
 * However, a service cannot directly touch the UI, so no visible components may
 * be used in a service. Communication can be achieved between a Form and
 * FormService using the SendMessageToForm method (you must make sure the
 * service is bound to the Form first). If you need to use a thread, then use a
 * ThreadTimer.
 * 
 * To make sure the service doesn't get arbitrarily killed by the android
 * system, use setStickyVal(START_STICKY). When using this, make sure you leave
 * some way for the app user to shut it off (using the stopService method). They
 * shouldn't have to navigate through their system settings just to turn your
 * service off, if they wanted to.
 * 
 * The Initialize event has been taken out, seeing as a service can't touch the
 * UI.
 * 
 * Ryan Bis - www.xiledsystems.com
 * 
 */

public class FormService extends Service implements Component, SvcComponentContainer,
    HandlesEventDispatching {

  private static FormService activeFormService;
  private int stickyVal = START_NOT_STICKY;
  private static final String LOG_TAG = "FormService";
  public boolean isRunning = false;
  private String formServiceName;
  private final Handler serviceHandler = new Handler();
  private final Set<OnStartCommandListener> onStartCommandListeners = Sets.newHashSet();
  private final Set<OnDestroySvcListener> onDestroyListeners = Sets.newHashSet();
  private final IBinder mBinder = new FSBinder();
  private Looper mServiceLooper;
  private boolean useThread;

  protected String getFormName() {
    return formServiceName;
  }

  public Handler getHandler() {
    return new Handler();
  }

  @Override
  public void onCreate() {
    super.onCreate();

    // Figure out the name of this FormService
    String className = getClass().getName();
    int lastDot = className.lastIndexOf(".");
    formServiceName = className.substring(lastDot + 1);
    if (BuildConfig.DEBUG) {
      Log.d(LOG_TAG, "FormService " + formServiceName + " got onCreate");
    }

    activeFormService = this;
    if (BuildConfig.DEBUG) {
      Log.i(LOG_TAG, "active FormService is now " + activeFormService.formServiceName);
    }
    isRunning = true;

    $define();

  }

  void $define() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (BuildConfig.DEBUG) {
      Log.d(LOG_TAG, "FormService " + formServiceName + " got onStartCommand");
    }
    activeFormService = this;

    EventDispatcher.dispatchEvent(this, "onStartCommand", intent, startId);
    if (intent != null) {
      if (intent.hasExtra(Form.ALARM_EVENT)) {
        int alarmId = intent.getIntExtra(Form.ALARM_EVENT, 0);
        EventDispatcher.dispatchEvent(FormService.this, "Alarm", alarmId);
      }
    }

    for (OnStartCommandListener onStartCommandListener : onStartCommandListeners) {
      onStartCommandListener.onStartCommand();
    }

    return stickyVal;
  }

  public void setStickyVal(int i) {
    stickyVal = i;
  }

  public void registerForOnStartCommand(OnStartCommandListener component) {
    onStartCommandListeners.add(component);
  }

  public void UseThread(boolean bool) {
    this.useThread = bool;
  }

  public boolean isUsingThread() {
    return useThread;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (BuildConfig.DEBUG) {
      Log.d(LOG_TAG, "FormService " + formServiceName + " got onDestroy");
    }
    EventDispatcher.removeDispatchDelegate(this);
    for (OnDestroySvcListener onDestroyListener : onDestroyListeners) {
      onDestroyListener.onDestroy();
    }
    if (stickyVal == START_STICKY) {
      mServiceLooper = null;
    }
    isRunning = false;
  }

  public void registerForOnDestroy(OnDestroySvcListener component) {
    onDestroyListeners.add(component);
  }

  @Override
  public IBinder onBind(Intent intent) {
    // Needed for binding to the service from a Form.
    return mBinder;
  }

  @Override
  public boolean canDispatchEvent(Component arg0, String arg1) {
    boolean canDisptach = isRunning;
    if (canDisptach) {
      activeFormService = this;
    }

    return canDisptach;
  }

  @Override
  public boolean dispatchEvent(Component component, String componentName, String eventName,
      Object[] args) {
    throw new UnsupportedOperationException();

  }

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
    // TODO Auto-generated method stub
    return this;
  }

  // Component Container implementation. We need this so we can initialize the
  // app inventor components. Remember, as this
  // is a service, only use NON visible components!

  @Override
  public FormService $formService() {

    return this;
  }

  @Override
  public String $formSvcName() {
    return formServiceName;
  }

  public static FormService getActiveFormService() {
    return activeFormService;
  }

  public static void destroyService() {
    if (activeFormService != null) {
      activeFormService.stopSelf();
    } else {
      throw new IllegalStateException("activeFormService is null.");

    }
  }

  public void deleteComponent(Object component) {
    if (component instanceof Deleteable) {
      ((Deleteable) component).onDelete();
    }
  }

  public void callInitialize(Object component) throws Throwable {
    Method method;
    try {
      method = component.getClass().getMethod("Initialize", (Class<?>[]) null);
    } catch (SecurityException e) {
      Log.d(LOG_TAG, "Security exception " + e.getMessage());
      return;
    } catch (NoSuchMethodException e) {
      return;
    }
    try {
      if (BuildConfig.DEBUG) {
        Log.d(LOG_TAG, "calling Initialize method for Object " + component.toString());
      }
      method.invoke(component, (Object[]) null);
    } catch (InvocationTargetException e) {
      Log.e(LOG_TAG, "invoke exception: " + e.getMessage());
      throw e.getTargetException();
    }
  }

  @Override
  public Service $context() {

    return this;
  }

  public void ErrorOccurred(Component component, String functionName, final int errorNumber,
      final String message) {
    String componentType = component.getClass().getName();
    componentType = componentType.substring(componentType.lastIndexOf(".") + 1);
    Log.e(LOG_TAG, "FormService " + formServiceName + " ErrorOccurred, errorNumber = "
        + errorNumber + ", componentType = " + componentType + ", functionName = " + functionName
        + ", messages = " + message);
    if ((!(EventDispatcher.dispatchEvent(this, "ErrorOccurred", component, functionName,
        errorNumber, message)))) {
      // If dispatchEvent returned false, then no user-supplied error handler
      // was run.
      // If in addition, the screen initializer was run, then we assume that the
      // user did not provide an error handler. In this case, we run a default
      // error handler, namely, showing a notification to the end user of the
      // app.
      // The app writer can override this by providing an error handler.
      // This also gets posted using the UI thread, so the notifier doesn't
      // throw an exception.

      serviceHandler.post(new Runnable() {
        @Override
        public void run() {
          new Notifier(FormService.this).ShowAlert("Error " + errorNumber + ": " + message);
        }
      });

    }
  }

  public void dispatchErrorOccurredEvent(final Component component, final String functionName,
      final int errorNumber, final Object... messageArgs) {

    String message = ErrorMessages.formatMessage(errorNumber, messageArgs);
    ErrorOccurred(component, functionName, errorNumber, message);

  }

  public class FSBinder extends Binder {
    public FormService getService() {
      return FormService.this;
    }
  }

  public void post(Runnable action) {
    serviceHandler.post(action);
  }

  /**
   * Use this method to send a message to the currently active (visible) Form.
   * This will spawn the Form's "FormServiceMessage" event.
   * 
   * @param message
   *          The message you'd like to send. Can be anything in string format.
   */
  public void SendMessageToForm(String message) {
    Intent intent = new Intent(FormServiceReceiver.FORMSVC_MSG);
    intent.putExtra(FormServiceReceiver.FORMSVC_MSG, message);
    sendBroadcast(intent);
  }

  public final void runOnSvcThread(Runnable action) {
    post(action);    
  }
}
