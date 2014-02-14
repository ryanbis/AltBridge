package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.EventListener;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Maps;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Sets;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.OnConfigurationListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.Registrar;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;
import com.xiledsystems.altbridge.BuildConfig;

public abstract class FormFragment extends Fragment implements ComponentContainer, Component, HandlesEventDispatching, Registrar, EventListener {

	private static final String LOG_TAG = "FormFragment";
	private ViewGroup container;

	private boolean screenInitialized;
	private View mainView;

	private final HashMap<Integer, ActivityResultListener> activityResultMap = Maps.newHashMap();
	private final Set<OnStopListener> onStopListeners = Sets.newHashSet();
	private final Set<OnDestroyListener> onDestroyListeners = Sets.newHashSet();
	private final Set<OnResumeListener> onResumeListeners = Sets.newHashSet();
	private final Set<OnPauseListener> onPauseListeners = Sets.newHashSet();
	private final Set<OnStartListener> onStartListeners = Sets.newHashSet();
	private final Set<OnConfigurationListener> onConfigListeners = Sets.newHashSet();

	private final Handler androidUIHandler = new Handler(Looper.getMainLooper());

	// AJB change - Set for the Initialize Listeners (mainly used for resizing
	// components)
	private final Set<OnInitializeListener> initializeListeners = Sets.newHashSet();
	private String formName;

	private static final int SWITCH_FORM_REQUEST_CODE = 10000;
	private static int nextRequestCode = SWITCH_FORM_REQUEST_CODE + 1;

	protected Events.Event eventListener;

	public abstract void $define();

	public abstract boolean dispatchEvent(Component component, String id, String eventName, Object[] args);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		this.container = container;

		// Figure out the name of this form.
		String className = getClass().getName();
		int lastDot = className.lastIndexOf('.');
		formName = className.substring(lastDot + 1);
		if (BuildConfig.DEBUG) {
			Log.d(LOG_TAG, "FormFragment " + formName + " got onCreateView");
		}

		$define();

		spawnScreenInitCheck();

		return mainView;
	}

	public void setEventListener(Events.Event event) {
		eventListener = event;
	}

	private void spawnScreenInitCheck() {
		androidUIHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mainView.getWidth() != 0 && mainView.getHeight() != 0) {
					screenInitialized = true;
					if (eventListener != null) {
						eventListener.eventDispatched(Events.SCREEN_INIT);
					} else {
						EventDispatcher.dispatchEvent(FormFragment.this, Events.SCREEN_INIT);
					}
				} else {
					androidUIHandler.postDelayed(this, 16);
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (BuildConfig.DEBUG) {
			Log.d(LOG_TAG, "FormFragment " + formName + " got onActivityResult, requestCode = " + requestCode + ", resultCode = " + resultCode);
		}
		if (requestCode == SWITCH_FORM_REQUEST_CODE) {
			// Dont do anything, this shouldnt fire.

		} else {
			// Another component (such as a ListPicker, ActivityStarter, etc) is
			// expecting this result.

			ActivityResultListener component = activityResultMap.get(requestCode);
			if (component != null) {
				component.resultReturned(requestCode, resultCode, data);
			} else {
				// Throw an event to catch any results we want from other forms.
				if (eventListener != null) {
					eventListener.eventDispatched(Events.FORM_RESULT, requestCode, resultCode, data);
				} else {
					if (!EventDispatcher.dispatchEvent(this, Events.FORM_RESULT, requestCode, resultCode, data)) {

					}
				}
			}
		}
	}

	@Override
	public HandlesEventDispatching getDispatchDelegate() {
		return this;
	}

	/**
	 * 
	 * @return the parent Form that this Fragment is living in.
	 * 
	 */
	public Form getParentForm() {
		return (Form) getActivity();
	}

	@Override
	public Activity $context() {
		return getActivity();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void $add(AndroidViewComponent component) {
		container.addView(component.getView());
	}

	@Override
	public void removeAllViews() {
		container.removeAllViews();
	}

	@Override
	public void setChildWidth(AndroidViewComponent component, int width) {
		ViewUtil.setChildWidthForVerticalLayout(component.getView(), width);
	}

	public Fragment getFragment() {
		return this;
	}

	@Override
	public void setChildHeight(AndroidViewComponent component, int height) {
		ViewUtil.setChildHeightForVerticalLayout(component.getView(), height);
	}

	@Override
	public boolean canDispatchEvent(Component component, String eventName) {
		boolean canDispatch = screenInitialized || (component == this && eventName.equals("Initialize"));
		return canDispatch;
	}

	@Override
	public void onStart() {
		super.onStart();
		for (OnStartListener startlistener : onStartListeners) {
			startlistener.onStart();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		for (OnResumeListener listener : onResumeListeners) {
			listener.onResume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		for (OnPauseListener listener : onPauseListeners) {
			listener.onPause();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		for (OnStopListener listener : onStopListeners) {
			listener.onStop();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		for (OnDestroyListener listener : onDestroyListeners) {
			listener.onDestroy();
		}
	}

	@Override
	public void dispatchErrorOccurredEvent(final Component component, final String functionName, final int errorNumber, final Object... messageArgs) {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				String message = ErrorMessages.formatMessage(errorNumber, messageArgs);
				ErrorOccurred(component, functionName, errorNumber, message);
			}
		});
	}

	/**
	 * ErrorOccurred event handler.
	 */

	public void ErrorOccurred(Component component, String functionName, int errorNumber, String message) {
		String componentType = component.getClass().getName();
		componentType = componentType.substring(componentType.lastIndexOf(".") + 1);
		Log.e(LOG_TAG, "FormFragment " + formName + " ErrorOccurred, errorNumber = " + errorNumber + ", componentType = " + componentType
				+ ", functionName = " + functionName + ", messages = " + message);
		if ((!(EventDispatcher.dispatchEvent(this, "ErrorOccurred", component, functionName, errorNumber, message))) && screenInitialized) {
			// If dispatchEvent returned false, then no user-supplied error handler was run.
			// If in addition, the screen initializer was run, then we assume that the
			// user did not provide an error handler. In this case, we run a default
			// error handler, namely, showing a notification to the end user of the app.
			// The app writer can override this by providing an error handler.
			new Notifier(this).ShowAlert("Error " + errorNumber + ": " + message);
		}
	}

	@Override
	public int registerForActivityResult(ActivityResultListener listener) {
		int requestCode = generateNewRequestCode();
		activityResultMap.put(requestCode, listener);
		return requestCode;
	}

	private static int generateNewRequestCode() {
		return nextRequestCode++;
	}

	@Override
	public void unregisterForActivityResult(ActivityResultListener listener) {
		// Do nothing, if a component is added that requires an activity result,
		// an exception will be thrown
	}

	@Override
	public void registerForOnInitialize(OnInitializeListener listener) {
		initializeListeners.add(listener);
	}

	@Override
	public void registerForOnStart(OnStartListener listener) {
		onStartListeners.add(listener);
	}

	@Override
	public void registerForOnResume(OnResumeListener listener) {
		onResumeListeners.add(listener);
	}

	@Override
	public void registerForOnPause(OnPauseListener listener) {
		onPauseListeners.add(listener);
	}

	@Override
	public void registerForOnStop(OnStopListener listener) {
		onStopListeners.add(listener);
	}

	@Override
	public void registerForOnDestroy(OnDestroyListener listener) {
		onDestroyListeners.add(listener);
	}

	@Override
	public void registerForOnConfigChange(OnConfigurationListener listener) {
		onConfigListeners.add(listener);
	}

	@Override
	public int getAvailWidth() {
		return container.getWidth();
	}

	@Override
	public int getAvailHeight() {
		return container.getHeight();
	}

	@Override
	public void dontGrabTouchEventsForComponent() {
		container.requestDisallowInterceptTouchEvent(true);
	}

	@Override
	public View findViewById(int resourceId) {
		return mainView.findViewById(resourceId);
	}

	@Override
	public Handler getHandler() {
		return androidUIHandler;
	}

	@Override
	public void post(Runnable run) {
		androidUIHandler.post(run);
	}

	@Override
	public void setOpenGL(boolean openGl, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentView(int view) {
		mainView = getActivity().getLayoutInflater().inflate(view, container, false);
	}

	@Override
	public void setContentView(View view) {
		mainView = view;
	}

	@Override
	public LayoutInflater getLayoutInflater() {
		return getActivity().getLayoutInflater();
	}

	@Override
	public View inflateLayout(int resourceId) {
		return getLayoutInflater().inflate(resourceId, null);
	}

	@Override
	public HandlesEventDispatching getDelegate() {
		return this;
	}

	@Override
	public void $remove(AndroidViewComponent component) {
		container.removeView(component.getView());
	}

	@Override
	public void registerForOnNewIntent(OnNewIntentListener listener) {
		// Fragments can't use this, it must be a Form only!.
	}

	@Override
	public Registrar getRegistrar() {
		return this;
	}

	@Override
	public Events.Event getEventListener() {
		return eventListener;
	}

}
