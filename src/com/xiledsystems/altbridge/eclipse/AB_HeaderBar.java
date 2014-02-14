package com.xiledsystems.altbridge.eclipse;

import java.util.ArrayList;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ABColors;
import com.xiledsystems.altbridge.R;


public class AB_HeaderBar extends RelativeLayout {
	
	private Button leftIcon;
	private Button optionsBtn;
	private TextView title;
	private float textSize = 24;
	private PopupWindow optionsMenu;
	private LinearLayout buttonCont;
	private ArrayList<String> optButtons = new ArrayList<String>();

	public AB_HeaderBar(Context context, AttributeSet attrs) {
		super(context, attrs);		
		ViewGroup.LayoutParams lparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lparm);
		buildUI(context, attrs);
	}

	private void buildUI(Context context, AttributeSet attrs) {
		double den = context.getResources().getDisplayMetrics().density;
		int pad = (int) (den * 8);
		setPadding(pad, pad, pad, pad);
		setBackgroundColor(ABColors.LIGHT_GREY);
				
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HeaderBar, 0, 0 );
		int imgId = a.getResourceId(R.styleable.HeaderBar_leftIconImage, 0);
		int w,h;
		w = context.getResources().getDrawable(imgId).getIntrinsicWidth();
		h = context.getResources().getDrawable(imgId).getIntrinsicHeight();
		leftIcon = new Button(context);		
		if (imgId != 0) {
			leftIcon.setBackgroundResource(imgId);
		} else {
			leftIcon.setBackgroundColor(0);
		}
		leftIcon.setId(928349);
				
		super.addView(leftIcon);
		
		
		textSize = a.getDimension(R.styleable.HeaderBar_headerTextSize, 24f);
		
		String text = a.getString(R.styleable.HeaderBar_headerText);
		if (text == null || text.equals("")) {
			text = context.getResources().getString(R.string.app_name);
		}		
		title = new TextView(context);
		title.setTextSize(textSize);
		title.setTextColor(ABColors.BLACK);
		title.setText(text);
						
		super.addView(title);
		
		w = (int) ( 32 * den);
		h = w;
		LayoutParams parms = new LayoutParams(w, h);
		parms.rightMargin = pad;
		parms.addRule(ALIGN_PARENT_LEFT);
		leftIcon.setLayoutParams(parms);
		
		LayoutParams tparm = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tparm.addRule(RIGHT_OF, leftIcon.getId());
		tparm.addRule(CENTER_VERTICAL);
		title.setLayoutParams(tparm);
		
		boolean show = a.getBoolean(R.styleable.HeaderBar_showOptionsButton, false);
		int bimg = a.getResourceId(R.styleable.HeaderBar_optionsButtonImage, android.R.drawable.ic_menu_manage);
		optionsBtn = new Button(context);		
		optionsBtn.setBackgroundResource(bimg);
		optionsBtn.setOnClickListener(new OnClickListener() {          
          @Override
          public void onClick(View v) {
            optionsMenu.showAsDropDown(buildOptionsView(8), 0, getHeight());
          }
        });
		LayoutParams opt = new LayoutParams(w, h);
		opt.leftMargin = pad;
		opt.addRule(ALIGN_PARENT_RIGHT);
		optionsBtn.setLayoutParams(opt);
		showOptionsBtn(show);
		optionsBtn.setId(87243);
		super.addView(optionsBtn);
		
		buttonCont = new LinearLayout(context);
		LayoutParams bparm = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		bparm.addRule(LEFT_OF, optionsBtn.getId());
		
		optionsMenu = new PopupWindow(buildOptionsView(pad), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
		
		a.recycle();
		
	}
			
	public View buildOptionsView(int pad) {
		FrameLayout layout = new FrameLayout(getContext());
		android.view.ViewGroup.LayoutParams parms = new android.view.ViewGroup.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(parms);
		layout.setBackgroundColor(0x50000000);
		layout.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				optionsMenu.dismiss();
			}
		});
		
		buttonCont = new LinearLayout(getContext());
		android.widget.FrameLayout.LayoutParams fparm = new android.widget.FrameLayout.LayoutParams(
				android.widget.FrameLayout.LayoutParams.WRAP_CONTENT, 
				android.widget.FrameLayout.LayoutParams.WRAP_CONTENT);
		fparm.gravity = Gravity.RIGHT;
		buttonCont.setLayoutParams(fparm);
		buttonCont.setBackgroundColor(ABColors.DARK_GRAY);
		buttonCont.setPadding(pad, pad, pad, pad);
		
		layout.addView(buttonCont);
				
		buildOptionButtons();		
		
		return layout;
	}
	
	public void addOptionButton(String buttonText) {
		if (!optButtons.contains(buttonText)) {
			optButtons.add(buttonText);
			buildOptionButtons();
		}
	}
	
	public ArrayList<String> getOptionButtons() {
		return optButtons;
	}
	
	public LinearLayout getButtonContainer() {
		return buttonCont;
	}
	
	private void buildOptionButtons() {
		buttonCont.removeAllViews();
		for (int i = 0; i < optButtons.size(); i++) {
			final Button b = new Button(getContext());
			b.setBackgroundColor(ABColors.GRAY);
			b.setText(optButtons.get(i));
			b.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					optionsMenu.dismiss();
					optionsButtonClicked(b.getText().toString());
				}
			});
		}
	}
	
	public interface OptionsListener {
		public void buttonClicked(String buttonText);
	}
	
	private OptionsListener optListen;
	
	public void setOptionsListener(OptionsListener listener) {
		optListen = listener;
	}
	
	private void optionsButtonClicked(String string) {
		if (optListen != null) {
			optListen.buttonClicked(string);
		}
	}

	public void setOptionsImage(int resourceId) {
		optionsBtn.setBackgroundResource(resourceId);
	}
	
	public void showOptionsBtn(boolean show) {
		if (show) {
			optionsBtn.setVisibility(View.VISIBLE);
		} else {
			optionsBtn.setVisibility(View.GONE);
		}
	}
	
	public void setLeftIconClick(OnClickListener listener) {
		leftIcon.setOnClickListener(listener);
	}
	
	public void setHeaderText(String text) {
		title.setText(text);
	}
	
	public void setHeaderTextSize(float size) {
		title.setTextSize(size);
	}
	
	public void setLeftIconImage(Drawable drawable) {
		leftIcon.setBackgroundDrawable(drawable);		
	}

}