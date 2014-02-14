package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.TextViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Check box with the ability to detect initialization, focus change (mousing on
 * or off of it), and user clicks.
 * 
 */

public class CheckBox extends AndroidViewComponent implements OnCheckedChangeListener, OnFocusChangeListener {

	private final android.widget.CheckBox view;

	// Backing for background color
	private int backgroundColor;

	// Backing for font typeface
	private int fontTypeface;

	// Backing for font bold
	private boolean bold;

	// Backing for font italic
	private boolean italic;

	// Backing for text color
	private int textColor;

	private boolean manualCheck;

	/**
	 * Creates a new CheckBox component.
	 * 
	 * @param container
	 *            container, component will be placed in
	 */
	public CheckBox(ComponentContainer container) {
		super(container);
		view = new android.widget.CheckBox(container.$context());

		// Listen to focus changes
		view.setOnFocusChangeListener(this);
		view.setOnCheckedChangeListener(this);

		// Adds the component to its designated container
		container.$add(this);
		BackgroundColor(Component.COLOR_NONE);
		Enabled(true);
		fontTypeface = Component.TYPEFACE_DEFAULT;
		TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
		FontSize(Component.FONT_DEFAULT_SIZE);
		Text("");
		TextColor(Component.COLOR_BLACK);
		Checked(false);
	}

	public CheckBox(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = (android.widget.CheckBox) container.getRegistrar().findViewById(resourceId);
		// Listen to focus changes
		view.setOnFocusChangeListener(this);
		view.setOnCheckedChangeListener(this);
		getfontTypeFace();
	}

	private void getfontTypeFace() {
		Typeface tf;
		tf = view.getTypeface();
		if (tf == null) {
			fontTypeface = Component.TYPEFACE_DEFAULT;
		} else {
			if (tf.equals(Typeface.DEFAULT)) {
				fontTypeface = Component.TYPEFACE_DEFAULT;
			} else if (tf.equals(Typeface.SERIF)) {
				fontTypeface = Component.TYPEFACE_SERIF;
			} else if (tf.equals(Typeface.SANS_SERIF)) {
				fontTypeface = Component.TYPEFACE_SANSSERIF;
			} else if (tf.equals(Typeface.MONOSPACE)) {
				fontTypeface = Component.TYPEFACE_MONOSPACE;
			}
		}
	}

	@Override
	public View getView() {
		return view;
	}

	/**
	 * Default Changed event handler.
	 */

	public void Changed() {
		if (eventListener != null) {
			eventListener.eventDispatched(Events.CHANGED);
		} else {
			EventDispatcher.dispatchEvent(this, Events.CHANGED);
		}
	}

	/**
	 * Default GotFocus event handler.
	 */

	public void GotFocus() {
		if (eventListener != null) {
			eventListener.eventDispatched(Events.GOT_FOCUS);
		} else {
			EventDispatcher.dispatchEvent(this, Events.GOT_FOCUS);
		}
	}

	/**
	 * Default LostFocus event handler.
	 */

	public void LostFocus() {
		if (eventListener != null) {
			eventListener.eventDispatched(Events.LOST_FOCUS);
		} else {
			EventDispatcher.dispatchEvent(this, Events.LOST_FOCUS);
		}
	}

	/**
	 * Returns the checkbox's background color as an alpha-red-green-blue
	 * integer.
	 * 
	 * @return background RGB color with alpha
	 */

	public int BackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Specifies the checkbox's background color as an alpha-red-green-blue
	 * integer.
	 * 
	 * @param argb
	 *            background RGB color with alpha
	 */

	public void BackgroundColor(int argb) {
		backgroundColor = argb;
		if (argb != Component.COLOR_DEFAULT) {
			TextViewUtil.setBackgroundColor(view, argb);
		} else {
			TextViewUtil.setBackgroundColor(view, Component.COLOR_NONE);
		}
	}

	/**
	 * Returns true if the checkbox is active and clickable.
	 * 
	 * @return {@code true} indicates enabled, {@code false} disabled
	 */

	public boolean Enabled() {
		return view.isEnabled();
	}

	/**
	 * Specifies whether the checkbox should be active and clickable.
	 * 
	 * @param enabled
	 *            {@code true} for enabled, {@code false} disabled
	 */

	public void Enabled(boolean enabled) {
		TextViewUtil.setEnabled(view, enabled);
	}

	/**
	 * Returns true if the checkbox's text should be bold. If bold has been
	 * requested, this property will return true, even if the font does not
	 * support bold.
	 * 
	 * @return {@code true} indicates bold, {@code false} normal
	 */

	public boolean FontBold() {
		return bold;
	}

	/**
	 * Specifies whether the checkbox's text should be bold. Some fonts do not
	 * support bold.
	 * 
	 * @param bold
	 *            {@code true} indicates bold, {@code false} normal
	 */

	public void FontBold(boolean bold) {
		this.bold = bold;
		TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
	}

	/**
	 * Returns true if the checkbox's text should be italic. If italic has been
	 * requested, this property will return true, even if the font does not
	 * support italic.
	 * 
	 * @return {@code true} indicates italic, {@code false} normal
	 */

	public boolean FontItalic() {
		return italic;
	}

	/**
	 * Specifies whether the checkbox's text should be italic. Some fonts do not
	 * support italic.
	 * 
	 * @param italic
	 *            {@code true} indicates italic, {@code false} normal
	 */

	public void FontItalic(boolean italic) {
		this.italic = italic;
		TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
	}

	/**
	 * Returns the checkbox's text's font size, measured in pixels.
	 * 
	 * @return font size in pixel
	 */

	public float FontSize() {
		return TextViewUtil.getFontSize(view);
	}

	/**
	 * Specifies the checkbox's text's font size, measured in pixels.
	 * 
	 * @param size
	 *            font size in pixel
	 */

	public void FontSize(float size) {
		TextViewUtil.setFontSize(view, size);
	}

	/**
	 * Returns the checkbox's text's font face as default, serif, sans serif, or
	 * monospace.
	 * 
	 * @return one of {@link Component#TYPEFACE_DEFAULT},
	 *         {@link Component#TYPEFACE_SERIF},
	 *         {@link Component#TYPEFACE_SANSSERIF} or
	 *         {@link Component#TYPEFACE_MONOSPACE}
	 */

	public int FontTypeface() {
		return fontTypeface;
	}

	/**
	 * Specifies the checkbox's text's font face as default, serif, sans serif,
	 * or monospace.
	 * 
	 * @param typeface
	 *            one of {@link Component#TYPEFACE_DEFAULT},
	 *            {@link Component#TYPEFACE_SERIF},
	 *            {@link Component#TYPEFACE_SANSSERIF} or
	 *            {@link Component#TYPEFACE_MONOSPACE}
	 */

	public void FontTypeface(int typeface) {
		fontTypeface = typeface;
		TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
	}

	/**
	 * Returns the text displayed by the checkbox.
	 * 
	 * @return checkbox caption
	 */

	public String Text() {
		return TextViewUtil.getText(view);
	}

	/**
	 * Specifies the text displayed by the checkbox.
	 * 
	 * @param text
	 *            new caption for checkbox
	 */

	public void Text(String text) {
		TextViewUtil.setText(view, text);
	}

	/**
	 * Returns the checkbox's text color as an alpha-red-green-blue integer.
	 * 
	 * @return text RGB color with alpha
	 */

	public int TextColor() {
		return textColor;
	}

	/**
	 * Specifies the checkbox's text color as an alpha-red-green-blue integer.
	 * 
	 * @param argb
	 *            text RGB color with alpha
	 */

	public void TextColor(int argb) {
		textColor = argb;
		if (argb != Component.COLOR_DEFAULT) {
			TextViewUtil.setTextColor(view, argb);
		} else {
			TextViewUtil.setTextColor(view, Component.COLOR_BLACK);
		}
	}

	/**
	 * Returns true if the checkbox is checked.
	 * 
	 * @return {@code true} indicates checked, {@code false} unchecked
	 */

	public boolean Checked() {
		return view.isChecked();
	}

	/**
	 * Checked property setter method.
	 * 
	 * @param value
	 *            {@code true} indicates checked, {@code false} unchecked
	 */

	public void Checked(boolean value) {
		view.setChecked(value);
		view.invalidate();
	}

	/**
	 * Checked property setter method. This method will NOT throw the Changed
	 * event when used (unlike the Checked() method)
	 * 
	 * @param value
	 *            {@code true} indicates checked, {@code false} unchecked
	 */

	public void ManualChecked(boolean value) {
		manualCheck = true;
		view.setChecked(value);
		view.invalidate();
	}

	// OnCheckedChangeListener implementation

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (!manualCheck) {
			Changed();
		}
		manualCheck = false;		
	}

	// OnFocusChangeListener implementation

	public void onFocusChange(View previouslyFocused, boolean gainFocus) {
		if (gainFocus) {
			GotFocus();
		} else {
			LostFocus();
		}
	}
}