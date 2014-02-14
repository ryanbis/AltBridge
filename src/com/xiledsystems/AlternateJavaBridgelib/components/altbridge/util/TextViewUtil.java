package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.Hashtable;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;

/**
 * Helper methods for manipulating {@link TextView} objects.
 *
 */
public class TextViewUtil {
  
  private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

  private TextViewUtil() {
  }

  /**
   * TextView alignment setter.
   *
   * @param alignment  one of {@link Component#ALIGNMENT_NORMAL},
   *                   {@link Component#ALIGNMENT_CENTER} or
   *                   {@link Component#ALIGNMENT_OPPOSITE}
   * @param centerVertically whether the text should be centered vertically
   */
  public static void setAlignment(TextView textview, int alignment, boolean centerVertically) {
    int horizontalGravity;
    switch (alignment) {
      default:
        throw new IllegalArgumentException();

      case Component.ALIGNMENT_NORMAL:
        horizontalGravity = Gravity.LEFT;
        break;

      case Component.ALIGNMENT_CENTER:
        horizontalGravity = Gravity.CENTER_HORIZONTAL;
        break;

      case Component.ALIGNMENT_OPPOSITE:
        horizontalGravity = Gravity.RIGHT;
        break;
    }
    int verticalGravity = centerVertically ? Gravity.CENTER_VERTICAL : Gravity.TOP;
    textview.setGravity(horizontalGravity | verticalGravity);
    textview.invalidate();
  }

  /**
   * {@link TextView} background color setter.
   *
   * @param textview   text view instance
   * @param argb  background RGB color with alpha
   */
  public static void setBackgroundColor(TextView textview, int argb) {
    if (argb != Component.COLOR_NONE) {
      textview.setBackgroundColor(argb);
      textview.invalidate();
    }
  }

  /**
   * Returns the enabled state a {@link TextView}.
   *
   * @param textview   text view instance
   * @return  {@code true} for enabled, {@code false} disabled
   */
  public static boolean isEnabled(TextView textview) {
    return textview.isEnabled();
  }

  /**
   * Enables a {@link TextView}.
   *
   * @param textview   text view instance
   * @param enabled  {@code true} for enabled, {@code false} disabled
   */
  public static void setEnabled(TextView textview, boolean enabled) {
    textview.setEnabled(enabled);
    textview.invalidate();
  }

  /**
   * Returns the font size for a {@link TextView}.
   *
   * @param textview   text view instance
   * @return  font size in pixel
   */
  public static float getFontSize(TextView textview) {
    return textview.getTextSize();
  }

  /**
   * Sets the font size for a {@link TextView}.
   *
   * @param textview   text view instance
   * @param size  font size in pixel
   */
  public static void setFontSize(TextView textview, float size) {
    textview.setTextSize(size);
    textview.requestLayout();
  }

  /**
   * Sets the font typeface for a {@link TextView}.
   *
   * @param textview   text view instance
   * @param typeface  one of @link Component#TYPEFACE_DEFAULT},
   *                  {@link Component#TYPEFACE_SERIF},
   *                  {@link Component#TYPEFACE_SANSSERIF} or
   *                  {@link Component#TYPEFACE_MONOSPACE}
   * @param bold true for bold, false for not bold
   * @param italic true for italic, false for not italic
   */
  public static void setFontTypeface(TextView textview, int typeface,
      boolean bold, boolean italic) {
    Typeface tf;
    switch (typeface) {
      default:
        throw new IllegalArgumentException();

      case Component.TYPEFACE_DEFAULT:
        tf = Typeface.DEFAULT;
        break;

      case Component.TYPEFACE_SERIF:
        tf = Typeface.SERIF;
        break;

      case Component.TYPEFACE_SANSSERIF:
        tf = Typeface.SANS_SERIF;
        break;

      case Component.TYPEFACE_MONOSPACE:
        tf = Typeface.MONOSPACE;
        break;
    }

    int style = 0;
    if (bold) {
      style |= Typeface.BOLD;
    }
    if (italic) {
      style |= Typeface.ITALIC;
    }
    textview.setTypeface(Typeface.create(tf, style));
    textview.requestLayout();
  }
  
  public static void setCustomTypeface(TextView textview, Typeface typeface, boolean bold, boolean italic) {
    int style = 0;
    if (bold) {
      style |= Typeface.BOLD;
    }
    if (italic) {
      style |= Typeface.ITALIC;
    }
    textview.setTypeface(typeface, style);
    textview.requestLayout();
  }

  /**
   * Returns the text for a {@link TextView}.
   *
   * @param textview   text view instance
   * @return  text shown in text view
   */
  public static String getText(TextView textview) {
    return textview.getText().toString();
  }

  /**
   * Sets the text for a {@link TextView}.
   *
   * @param textview   text view instance
   * @param text  new text to be shown
   */
  public static void setText(TextView textview, String text) {
    textview.setText(text);
    textview.requestLayout();
  }

  /**
   * Sets the text color for a {@link TextView}.
   *
   * @param textview   text view instance
   * @param argb  text RGB color with alpha
   */
  public static void setTextColor(TextView textview, int argb) {
    textview.setTextColor(argb);
    textview.invalidate();
  }

  public static void setTextColors(TextView textview, ColorStateList colorStateList) {
    textview.setTextColor(colorStateList);
  }
  
  /**
   * This will get a font from the assets directory. The typeface will be
   * loaded in memory only once to prevent memory leaks. 
   * 
   * @param context
   * @param assetPath
   * @return
   */
  public static Typeface getTypeface(Context context, String assetPath) {
    synchronized (fontCache) {
      if (!fontCache.containsKey(assetPath)) {
        try {
          Typeface t = Typeface.createFromAsset(context.getAssets(), assetPath);          
          fontCache.put(assetPath, t);
        } catch (Exception e) {
          return null;
        }
      }
      return fontCache.get(assetPath);
    }
  }
  
}
