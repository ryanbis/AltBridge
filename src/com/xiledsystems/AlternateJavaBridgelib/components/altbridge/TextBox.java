package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * A box in which the user can enter text.
 * 
 */

public class TextBox extends TextBoxBase {
	/*
	 * TODO(user): this code requires Android SDK M5 or newer - we are currently
	 * on M3 enables this when we upgrade
	 * 
	 * // Backing for text during validation private String text;
	 * 
	 * private class ValidationTransformationMethod extends TransformationMethod
	 * {
	 * 
	 * @Override public CharSequence getTransformation(CharSequence source) {
	 * BooleanReferenceParameter accept = new BooleanReferenceParameter(false);
	 * Validate(source.toString, accept);
	 * 
	 * if (accept.get()) { text = source.toString(); }
	 * 
	 * return text; } }
	 */

	// If true, then accept numeric keyboard input only
	private boolean acceptsNumbersOnly;
	private boolean multiLine;

	/**
	 * Creates a new TextBox component.
	 * 
	 * @param container
	 *            container, component will be placed in
	 */
	public TextBox(ComponentContainer container) {
		super(container, new EditText(container.$context()));
		NumbersOnly(false);
		MultiLine(false);
	}

	public TextBox(ComponentContainer container, int resourceId) {
		super(container, (EditText) container.getRegistrar().findViewById(resourceId), resourceId);
	}

	/**
	 * NumbersOnly property getter method.
	 * 
	 * @return {@code true} indicates that the textbox accepts numbers only,
	 *         {@code false} indicates that it accepts any text
	 */

	public boolean NumbersOnly() {
		return acceptsNumbersOnly;
	}

	public void HideKeyboard() {
		InputMethodManager imm = (InputMethodManager) container.$context().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public boolean MultiLine() {
		return multiLine;
	}

	public void MultiLine(boolean multiLine) {
		this.multiLine = multiLine;
		view.setSingleLine(!multiLine);
	}

	/**
	 * NumersOnly property setter method.
	 * 
	 * @param acceptsNumbersOnly
	 *            {@code true} restricts input to numeric, {@code false} allows
	 *            any text
	 */

	public void NumbersOnly(boolean acceptsNumbersOnly) {
		if (acceptsNumbersOnly) {
			view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		} else {
			view.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		}
		this.acceptsNumbersOnly = acceptsNumbersOnly;
	}

	/**Use this method to limit the number of decimal places a user can type in to 
	 * 
	 * NOTE: ONLY use when "NumbersOnly" is set to true
	 * 
	 * @param decimalPlaces
	 */
	public void SetDecimalPlaceLimit(int decimalPlaces) {
		//view.addTextChangedListener(new DecimalFilter(view, decimalPlaces));
	}
	
	public void SetDecimalPlaceLimit(int decimalPlaces, String message) {
		view.addTextChangedListener(new DecimalFilter(view, decimalPlaces, (Form) container, message));
	}
}
