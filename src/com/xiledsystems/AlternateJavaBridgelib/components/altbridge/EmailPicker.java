package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SdkLevel;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;

import android.widget.AutoCompleteTextView;

/**
 * Text box using auto-completion to pick out an email address from contacts.
 *
 */


public class EmailPicker extends TextBoxBase {

  private final EmailAddressAdapter addressAdapter;

  /**
   * Create a new EmailPicker component.
   *
   * @param container the parent container.
   */
  public EmailPicker(ComponentContainer container) {
    super(container, new AutoCompleteTextView(container.$context()));
    addressAdapter = new EmailAddressAdapter(container.$context());
    ((AutoCompleteTextView) super.view).setAdapter(addressAdapter);
  }

  /**
   * Event raised when this component is selected for input, such as by
   * the user touching it.
   */
  
  @Override
  public void GotFocus() {
    if (SdkLevel.getLevel() > SdkLevel.LEVEL_DONUT) {
      container.getRegistrar().dispatchErrorOccurredEvent(this, "GotFocus",
          ErrorMessages.ERROR_FUNCTIONALITY_NOT_SUPPORTED_EMAIL_PICKER);
    }
    if (eventListener != null) {
		eventListener.eventDispatched(Events.GOT_FOCUS);
	} else {
		EventDispatcher.dispatchEvent(this, Events.GOT_FOCUS);
	}
  }

}
