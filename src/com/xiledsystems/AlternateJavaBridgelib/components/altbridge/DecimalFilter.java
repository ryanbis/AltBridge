package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.TextBox;

/** Class to help set the decimal limits in TextBox
 * 
 * @author bc
 *
 */
public class DecimalFilter implements TextWatcher {

	int count = -1 ;
	EditText et;
	int placesCount;
	//Activity activity;
	Form form;
	boolean firstTime;
	String message = "";

	//Constructor for DecimalFilter
	
	/**This is the default constructor for DecimalFilter.  It will allow you to set a limit on 
	 * the number of decimal places one can type in to a TextBox
	 * 
	 * @param edittext - The EditText view you are setting this item for
	 * @param numberOfPlaces - How many places you can limit the text to the right of the decimal
	 *                         valid values are 0 - 100
	 */
	public DecimalFilter(EditText edittext, Integer numberOfPlaces) {
		et = edittext;
		
		
		if (numberOfPlaces == null) 
			placesCount = 2;
		else
			placesCount = numberOfPlaces;
	}
	
	/** This is an additional constructor for DecimalFilter.  It displays a ShowAlert message of your choosing
	 *  when the user tries to type past the decimal limit you set
	 * 
	 * @param edittext - The EditText view you are setting this item for
	 * @param numberOfPlaces - How many places you can limit the text to the right of the decimal
	 *                         valid values are 0 - 100
	 * @param afterTypingMessage - Message to be displayed in a ShowAlert notifier when user tries to type past limit
	 */
	public DecimalFilter(EditText edittext, Integer numberOfPlaces, Form form, String afterTypingMessage) {
		et = edittext;
		this.form = form;
		
		if (numberOfPlaces == null) 
			placesCount = 2;
		else
			placesCount = numberOfPlaces;
		
		message = afterTypingMessage;
	}

	public void afterTextChanged(Editable s) {

		if (s.length() > 0) {
			String str = et.getText().toString();
			et.setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_DEL) {
						count--;
						InputFilter[] fArray = new InputFilter[1];
						fArray[0] = new InputFilter.LengthFilter(100);//Re sets the maxLength of edittext to 100.
						et.setFilters(fArray);  
					}
					
					//Now we will set a toast to go off if you try to type past the limit
					if (count > placesCount ) {
						if (!message.equals(""))
	//					   Toast.makeText(activity, "Sorry! You cant enter more than two digits after decimal point!", Toast.LENGTH_SHORT).show();
						   Notifier.ShowAlert(form, message);
					}
					
					return false;
				}
			});

			char t = str.charAt(s.length() - 1);

			if (t == '.') {
				count = 0;
				firstTime = true;
			}

			if (count >= 0) {
				if (count == placesCount) {
					InputFilter[] fArray = new InputFilter[1];
					fArray[0] = new InputFilter.LengthFilter(s.length());
					et.setFilters(fArray); // sets edittext's maxLength to number of digits now entered.
		//			Toast.makeText(activity,"Sorry! You cant enter more than two digits after decimal point!",
	//						Toast.LENGTH_SHORT).show();
				//	Notifier.ShowAlert((Form) activity, "balh");
			//		notifier.ShowAlert("blah");

				}
			//	if(firstTime)
			//		firstTime = false;
		//		else
					count++;
			}
		}

	}

	//The below methods are required for this class but we do not use them
	//Don't try to do anything with them
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
	}

}