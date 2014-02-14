package com.xiledsystems.altbridge.eclipse;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public class AB_Spacer extends View {
  
  public AB_Spacer(Context context) {
    super(context);
    setVisibility(View.INVISIBLE);
  }
  
  public AB_Spacer(Context context, AttributeSet attrs) {
    super(context, attrs);
    setVisibility(View.INVISIBLE);
  }

  public AB_Spacer(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setVisibility(View.INVISIBLE);
  }

}
