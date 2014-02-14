package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.TextViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;


public class RadioCheckBox extends AndroidViewComponent implements android.widget.CompoundButton.OnCheckedChangeListener, 
		OnFocusChangeListener {

	private RadioButton view;

	private int backgroundColor;

	// Backing for font typeface
	private int fontTypeface;

	// Backing for font bold
	private boolean bold;

	// Backing for font italic
	private boolean italic;

	// Backing for text color
	private int textColor;

	/**
	 * Creates a new RadioCheckBox component.
	 * 
	 * @param container
	 *            container, component will be placed in
	 */
	public RadioCheckBox(ComponentContainer container) {
		super(container);

		view = new android.widget.RadioButton(container.$context());

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

	public RadioCheckBox(ComponentContainer container, int resourceId) {
		super(container, resourceId);

		view = (android.widget.RadioButton) container.getRegistrar().findViewById(resourceId);
		// Listen to focus changes
		view.setOnFocusChangeListener(this);
		view.setOnCheckedChangeListener(this);
		getfontTypeFace();
	}

	private void getfontTypeFace() {
		Typeface tf;
		if (resourceId > 0) {
			android.widget.Button tmpview = (android.widget.Button) container.getRegistrar().findViewById(resourceId);
			tf = tmpview.getTypeface();
		} else {
			tf = view.getTypeface();
		}
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
		EventDispatcher.dispatchEvent(this, "Changed");
	}

	/**
	 * Default GotFocus event handler.
	 */

	public void GotFocus() {
		EventDispatcher.dispatchEvent(this, "GotFocus");
	}

	/**
	 * Default LostFocus event handler.
	 */

	public void LostFocus() {
		EventDispatcher.dispatchEvent(this, "LostFocus");
	}

	/**
	 * Returns the Radio CheckBox's background color as an alpha-red-green-blue
	 * integer.
	 * 
	 * @return background RGB color with alpha
	 */

	public int BackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Specifies the radio checkbox's background color as an
	 * alpha-red-green-blue integer.
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
	 * Returns true if the radio checkbox is active and clickable.
	 * 
	 * @return {@code true} indicates enabled, {@code false} disabled
	 */

	public boolean Enabled() {
		return view.isEnabled();
	}

	/**
	 * Specifies whether the radio checkbox should be active and clickable.
	 * 
	 * @param enabled
	 *            {@code true} for enabled, {@code false} disabled
	 */

	public void Enabled(boolean enabled) {
		TextViewUtil.setEnabled(view, enabled);
	}

	/**
	 * Returns true if the radio checkbox's text should be bold. If bold has
	 * been requested, this property will return true, even if the font does not
	 * support bold.
	 * 
	 * @return {@code true} indicates bold, {@code false} normal
	 */

	public boolean FontBold() {
		return bold;
	}

	/**
	 * Specifies whether the radio checkbox's text should be bold. Some fonts do
	 * not support bold.
	 * 
	 * @param bold
	 *            {@code true} indicates bold, {@code false} normal
	 */

	public void FontBold(boolean bold) {
		this.bold = bold;
		TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
	}

	/**
	 * Returns true if the radio checkbox's text should be italic. If italic has
	 * been requested, this property will return true, even if the font does not
	 * support italic.
	 * 
	 * @return {@code true} indicates italic, {@code false} normal
	 */

	public boolean FontItalic() {
		return italic;
	}

	/**
	 * Specifies whether the radio checkbox's text should be italic. Some fonts
	 * do not support italic.
	 * 
	 * @param italic
	 *            {@code true} indicates italic, {@code false} normal
	 */

	public void FontItalic(boolean italic) {
		this.italic = italic;
		TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
	}

	/**
	 * Returns the radio checkbox's text's font size, measured in pixels.
	 * 
	 * @return font size in pixel
	 */

	public float FontSize() {
		return TextViewUtil.getFontSize(view);
	}

	/**
	 * Specifies the radio checkbox's text's font size, measured in pixels.
	 * 
	 * @param size
	 *            font size in pixel
	 */

	public void FontSize(float size) {
		TextViewUtil.setFontSize(view, size);
	}

	/**
	 * Returns the radio checkbox's text's font face as default, serif, sans
	 * serif, or monospace.
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
	 * Specifies the radio checkbox's text's font face as default, serif, sans
	 * serif, or monospace.
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
	 * Returns the text displayed by the radio checkbox.
	 * 
	 * @return radio checkbox caption
	 */

	public String Text() {
		return TextViewUtil.getText(view);
	}

	/**
	 * Specifies the text displayed by the radio checkbox.
	 * 
	 * @param text
	 *            new caption for radio checkbox
	 */

	public void Text(String text) {
		TextViewUtil.setText(view, text);
	}

	/**
	 * Returns the radio checkbox's text color as an alpha-red-green-blue
	 * integer.
	 * 
	 * @return text RGB color with alpha
	 */
	public int TextColor() {
		return textColor;
	}

	/**
	 * Specifies the radio checkbox's text color as an alpha-red-green-blue
	 * integer.
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
	 * Returns true if the radio checkbox is checked.
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

	// OnCheckedChangeListener implementation

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Changed();
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