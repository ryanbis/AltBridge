package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.ActivityResultListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnDestroyListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnInitializeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnNewIntentListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnPauseListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnResumeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnStartListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnStopListener;

public interface Registrar {
  
  public int registerForActivityResult(ActivityResultListener listener);
  
  public void unregisterForActivityResult(ActivityResultListener listener);
  
  public void registerForOnInitialize(OnInitializeListener listener);
  
  public void registerForOnStart(OnStartListener listener);
  
  public void registerForOnResume(OnResumeListener listener);
  
  public void registerForOnPause(OnPauseListener listener);
  
  public void registerForOnStop(OnStopListener listener);
  
  public void registerForOnDestroy(OnDestroyListener listener);
  
  public void registerForOnConfigChange(OnConfigurationListener listener);
  
  public void dispatchErrorOccurredEvent(Component component, String functionName, int errorNumber, Object... messageArgs);

  public int getAvailWidth();
  
  public int getAvailHeight();
  
  public void dontGrabTouchEventsForComponent();
  
  public View findViewById(int resourceId);
  
  public Handler getHandler();
  
  public void post(Runnable run);
  
  public void setOpenGL(boolean openGl, View view);
  
  public void setContentView(int view);
  
  public void setContentView(View view);
  
  public LayoutInflater getLayoutInflater();
  
  public View inflateLayout(int layoutResourceId);
  
  public Context $context();
  
  public void startActivityForResult(Intent intent, int requestCode);
  
  public void registerForOnNewIntent(OnNewIntentListener listener);
  
}
