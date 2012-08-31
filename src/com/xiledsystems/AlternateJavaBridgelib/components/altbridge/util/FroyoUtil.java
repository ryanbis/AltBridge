package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.view.Display;

public class FroyoUtil {
  
  private FroyoUtil() {    
  }
  
  /**
   * Calls {@link Display#getRotation()}
   *
   * @return one of {@link android.view.Surface#ROTATION_0},
   *         {@link android.view.Surface#ROTATION_90},
   *         {@link android.view.Surface#ROTATION_180},
   *         or {@link android.view.Surface#ROTATION_180}.
   */
  public static int getRotation(Display display) {
    return display.getRotation();
  }

}
