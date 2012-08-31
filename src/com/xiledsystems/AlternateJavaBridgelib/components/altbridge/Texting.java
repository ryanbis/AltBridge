package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.APNHelper;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.APNHelper.APN;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.PhoneEx;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.nokia.IMMConstants;
import com.xiledsystems.AlternateJavaBridgelib.nokia.MMContent;
import com.xiledsystems.AlternateJavaBridgelib.nokia.MMEncoder;
import com.xiledsystems.AlternateJavaBridgelib.nokia.MMMessage;
import com.xiledsystems.AlternateJavaBridgelib.nokia.MMResponse;
import com.xiledsystems.AlternateJavaBridgelib.nokia.MMSender;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * A component capable of sending and receiving text messages via SMS.

 *
 */

public class Texting extends AndroidNonvisibleComponent implements Component, OnDestroyListener, Deleteable, 
					OnDestroySvcListener, OnStartListener {

	private final static String SENT_NMBR = "Texting.SentNumber";
	private final static String SENT_MSG = "Texting.SentMessage";
	private final static String TXT_INTENT = "com.xiledsystems.AlternateJavaBridgelib.TextingIntent";	
	private MsgReceiver msgReceiver = new MsgReceiver();
	private final static String TAG = "Texting";
	
	// MMS fields
	
	private ConnectivityManager mConnMgr;
	private PowerManager.WakeLock mWakeLock;
	private ConnectivityBroadcastReceiver mReceiver;

	private NetworkInfo mNetworkInfo;
	private NetworkInfo mOtherNetworkInfo;
	public enum State {
		UNKNOWN,
		CONNECTED,
		NOT_CONNECTED
	}
	
	private State mState;
	private boolean mListening;
	private boolean mSending;
	
	
  /**
   * Handles the SMS reception
   */
  class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      SmsMessage[] messages = getMessagesFromIntent(intent);
      SmsMessage message = messages[0];
      if (message != null) {
        String from = message.getOriginatingAddress();
        String messageText = message.getMessageBody();
        MessageReceived(from, messageText);
      } else {
        Log.i("Simple", "Sms message suppposedly received but with no actual content.");
      }
    }
  }

  // Provides an event for SMS reception

  // Indicates whether the SMS receiver is running or not
  private boolean receivingEnabled;
  private SmsManager smsManager;

  // The phone number to send the text message to.
  private String phoneNumber;
  // The message to send
  private String message;
  private SmsReceiver thisreceiver;


  /**
   * Creates a new Texting component.
   *
   * @param container  Use "this"
   */
  public Texting(ComponentContainer container) {
    super(container);    
    IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    thisreceiver = new SmsReceiver();
    container.$context().registerReceiver(thisreceiver, intentFilter);    
    container.$context().registerReceiver(msgReceiver, new IntentFilter(TXT_INTENT));    
    container.$form().registerForOnDestroy(this);
    container.$form().registerForOnStart(this);
    Log.d("Simple", "Texting constructor");
    smsManager = SmsManager.getDefault();
    PhoneNumber("");
    receivingEnabled = true;
    mListening = true;
    mSending = false;
    //mConnMgr = (ConnectivityManager) container.$context().getSystemService(Context.CONNECTIVITY_SERVICE);
	//mReceiver = new ConnectivityBroadcastReceiver();
  }
  
  /**
   * Creates a new Texting component in a formservice.
   *
   * @param container  Use "this"
   */
  public Texting(SvcComponentContainer container) {
	  super(container);
	  IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
	  thisreceiver = new SmsReceiver();
	    container.$context().registerReceiver(thisreceiver, intentFilter);
	    container.$context().registerReceiver(msgReceiver, new IntentFilter(TXT_INTENT));
	    container.$formService().registerForOnDestroy(this);
	    Log.d("Simple", "Texting constructor");
	    smsManager = SmsManager.getDefault();
	    PhoneNumber("");
	    receivingEnabled = true;
	    mListening = true;
	    mSending = false;
	    //mConnMgr = (ConnectivityManager) container.$context().getSystemService(Context.CONNECTIVITY_SERVICE);
		//mReceiver = new ConnectivityBroadcastReceiver();
		//IntentFilter filter = new IntentFilter();
		//filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		//container.$context().registerReceiver(mReceiver, filter);
		//startMMSComm();
  }

  /**
   * Sets the phone number to send the text message to when the SendMessage function is called.
   *
   * @param phoneNumber a phone number to call
   */
  
  public void PhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
  
  /**
   * Get the phone number that the message will be sent to when the SendMessage function is called.
   */
  
  public String PhoneNumber() {
    return phoneNumber;
  }

  /**
   * Sets the text message to send when the SendMessage function is called.
   *
   * @param message the message to send when the SendMessage function is called.
   */
  
  public void Message(String message) {
    this.message = message;
  }

  /**
   * Get the message that will be sent when the SendMessage function is called.
   */
  
  public String Message() {
    return message;
  }

  /**
   * Send a text message
   */
  
  public void SendMessage() {
	  
	  Intent msgIntent = new Intent(TXT_INTENT);
	  msgIntent.putExtra(SENT_NMBR, phoneNumber);
	  msgIntent.putExtra(SENT_MSG, message);	  
	  PendingIntent pIntent = PendingIntent.getBroadcast(container.$context(), 0, msgIntent, PendingIntent.FLAG_NO_CREATE);	  
	  smsManager.sendTextMessage(phoneNumber, null, message, pIntent, null);	  
	  
  }
  
  public void SendMMSMessage() {
	  //startMMSComm();
  }

  /**
   * Event that's raised when a new text message is received by the phone.
   * @param number the phone number that the text message was sent from.
   * @param messageText the text of the message.
   */
  
  public void MessageReceived(String number, String messageText) {
    // TODO(user): maybe we should unregister and re-register the SmsReceiver based on the
    // receivingEnabled setting rather than just checking here.
    if (receivingEnabled) {
      Log.d("Simple", "MessageReceived");
      EventDispatcher.dispatchEvent(this, "MessageReceived", number, messageText);
    }
  }


  /**
   * Gets whether you want the {@link #MessageReceived) event to get run when a new text message is
   * received.
   *
   * @return 'true' or 'false' depending on whether you want the {@link #MessageReceived) event to
   *          get run when a new text message is received.
   */
  
  public boolean ReceivingEnabled() {
    return receivingEnabled;
  }

  /**
   * Sets whether you want the {@link #MessageReceived) event to get run when a new text message is
   * received.
   *
   * @param enabled  Set to 'true' or 'false' depending on whether you want the
   *                 {@link #MessageReceived) event to get run when a new text message is received.
   */
  
  public void ReceivingEnabled(boolean enabled) {
    this.receivingEnabled = enabled;
  }

  /**
   * Parse the messages out of the extra fields from the "android.permission.RECEIVE_SMS" broadcast
   * intent.
   *
   * Note: This code was copied from the Android android.provider.Telephony.Sms.Intents class.
   *
   * @param intent the intent to read from
   * @return an array of SmsMessages for the PDUs
   */
  public static SmsMessage[] getMessagesFromIntent(
          Intent intent) {
      Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
      byte[][] pduObjs = new byte[messages.length][];

      for (int i = 0; i < messages.length; i++) {
          pduObjs[i] = (byte[]) messages[i];
      }
      byte[][] pdus = new byte[pduObjs.length][];
      int pduCount = pdus.length;
      SmsMessage[] msgs = new SmsMessage[pduCount];
      for (int i = 0; i < pduCount; i++) {
          pdus[i] = pduObjs[i];
          msgs[i] = SmsMessage.createFromPdu(pdus[i]);
      }
      return msgs;
  }

@Override
public void onDestroy() {
	
	getContext().unregisterReceiver(thisreceiver);
	getContext().unregisterReceiver(msgReceiver);
	//getContext().unregisterReceiver(mReceiver);
	
	msgReceiver = null;
	thisreceiver = null;
	mReceiver = null;
	
}

@Override
public void onDelete() {	
	
	getContext().unregisterReceiver(thisreceiver);
	getContext().unregisterReceiver(msgReceiver);
	//getContext().unregisterReceiver(mReceiver);
	
	msgReceiver = null;
	thisreceiver = null;
	mReceiver = null;
	
}

	private void msgReceivedOk(Intent intent) {
		if (intent.hasExtra(SENT_NMBR) && intent.hasExtra(SENT_MSG)) {
			String n = intent.getStringExtra(SENT_NMBR);
			String m = intent.getStringExtra(SENT_MSG);
			EventDispatcher.dispatchEvent(this, "MessageSent", n, m);			
		}
	}

	private void msgNotSent(Intent intent, String errorMsg) {
		String n = "";
		String m = "";
		if (intent.hasExtra(SENT_NMBR) && intent.hasExtra(SENT_MSG)) {
			n = intent.getStringExtra(SENT_NMBR);
			m = intent.getStringExtra(SENT_MSG);
		}
		EventDispatcher.dispatchEvent(this, "MessageFailed", n, m, errorMsg);
	}

	private class MsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				msgReceivedOk(intent);
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				msgNotSent(intent, "Generic Error.");
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				msgNotSent(intent, "Error: No Service.");
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				msgNotSent(intent, "Error: Null PDU.");
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				msgNotSent(intent, "Error: Radio off.");
				break;			
			}
		}		
	}	
	
	private class ConnectivityBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || mListening == false) {
				Log.w(TAG, "onReceived() called with " + mState.toString() + " and " + intent);
				return;
			}

			boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			if (noConnectivity) {
				mState = State.NOT_CONNECTED;
			} else {
				mState = State.CONNECTED;
			}

			mNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			mOtherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

//			mReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//			mIsFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);


			// Check availability of the mobile network.
			if ((mNetworkInfo == null) || (mNetworkInfo.getType() != ConnectivityManager.TYPE_MOBILE_MMS)) {
				Log.v(TAG, "   type is not TYPE_MOBILE_MMS, bail");
				return;
			}

			if (!mNetworkInfo.isConnected()) {
				Log.v(TAG, "   TYPE_MOBILE_MMS not connected, bail");
				return;
			}
			else
			{ 
				Log.v(TAG, "connected..");

				if(mSending == false)
				{
					mSending = true;
					sendMMSUsingNokiaAPI();
				}
			}
		}
	}
	
	private synchronized void createWakeLock() {
		// Create a new wake lock if we haven't made one yet.
		if (mWakeLock == null) {
			PowerManager pm = (PowerManager)getContext().getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MMS Connectivity");
			mWakeLock.setReferenceCounted(false);
		}
	}

	private void sendMMSUsingNokiaAPI() {
		MMMessage mm = new MMMessage();
		SetMessage(mm);
		AddContents(mm);
		
		MMEncoder encoder = new MMEncoder();
		encoder.setMessage(mm);
		
		try {
			encoder.encodeMessage();
			byte[] out = encoder.getMessage();
			
			MMSender sender = new MMSender();
			APNHelper apnHelper = new APNHelper(getContext());
			List<APN> results = apnHelper.getMMSApns();
			
			if (results.size() > 0) {
				
				final String MMSCenterUrl = results.get(0).MMSCenterUrl;
		    	  final String MMSProxy = results.get(0).MMSProxy;
		    	  final int MMSPort = Integer.valueOf(results.get(0).MMSPort);
		    	  final Boolean  isProxySet =   (MMSProxy != null) && (MMSProxy.trim().length() != 0);			
		    	  
		    	  sender.setMMSCURL(MMSCenterUrl);
		    	  sender.addHeader("X-NOKIA-MMSC-Charging", "100");

			      MMResponse mmResponse = sender.send(out, isProxySet, MMSProxy, MMSPort);
			      Log.d(TAG, "Message sent to " + sender.getMMSCURL());
			      Log.d(TAG, "Response code: " + mmResponse.getResponseCode() + " " + mmResponse.getResponseMessage());

			      Enumeration keys = mmResponse.getHeadersList();
			      while (keys.hasMoreElements()){
			        String key = (String) keys.nextElement();
			        String value = (String) mmResponse.getHeaderValue(key);
			        Log.d(TAG, (key + ": " + value));
			      }
			      if(mmResponse.getResponseCode() == 200)
			      {
			    	  // 200 Successful, disconnect and reset.
			    	  endMmsConnectivity();
			    	  mSending = false;
			    	  mListening = false;
			      }
			      else
			      {
			    	  // kill dew :D hhaha -- what?!?
			      }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
						
	}

	private void AddContents(MMMessage mm) {
		// TODO add a way to add an image as well.
		MMContent part1 = new MMContent();
		byte[] buf = message.getBytes();
		part1.setContent(buf, 0, buf.length);
		part1.setContentId("<0>");
		part1.setType(IMMConstants.CT_TEXT_PLAIN);
		mm.addContent(part1);
	}

	private void SetMessage(MMMessage mm) {
		mm.setVersion(IMMConstants.MMS_VERSION_10);
		mm.setMessageType(IMMConstants.MESSAGE_TYPE_M_SEND_REQ);
		mm.setTransactionId("0000000066");
		mm.setDate(new Date(System.currentTimeMillis()));
		mm.addToAddress(phoneNumber+"/TYPE=PLMN");
		mm.setDeliveryReport(true);
		mm.setReadReply(false);
		mm.setContentType(IMMConstants.CT_APPLICATION_MULTIPART_MIXED);
		mm.setMessageClass(IMMConstants.MESSAGE_CLASS_PERSONAL);
	}

	private void acquireWakeLock() {
		// It's okay to double-acquire this because we are not using it
		// in reference-counted mode.
		mWakeLock.acquire();
	}

	private void releaseWakeLock() {
		// Don't release the wake lock if it hasn't been created and acquired.
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
		}
	}
	
	private void startMMSComm() {
		try {
			
			// Ask to start the connection to the APN. Pulled from Android source code.
			int result = beginMmsConnectivity();

			if (result != PhoneEx.APN_ALREADY_ACTIVE) {
				Log.v(TAG, "Extending MMS connectivity returned " + result + " instead of APN_ALREADY_ACTIVE");
				// Just wait for connectivity startup without
				// any new request of APN switch.
				return;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected int beginMmsConnectivity() throws IOException {
		// Take a wake lock so we don't fall asleep before the message is downloaded.
		createWakeLock();

		int result = mConnMgr.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, PhoneEx.FEATURE_ENABLE_MMS);

		Log.v(TAG, "beginMmsConnectivity: result=" + result);

		switch (result) {
		case PhoneEx.APN_ALREADY_ACTIVE:
		case PhoneEx.APN_REQUEST_STARTED:
			acquireWakeLock();
			return result;
		}

		throw new IOException("Cannot establish MMS connectivity");
	}
	
	protected void endMmsConnectivity() {
		// End the connectivity
		try {
			Log.v(TAG, "endMmsConnectivity");
			if (mConnMgr != null) {
				mConnMgr.stopUsingNetworkFeature(
						ConnectivityManager.TYPE_MOBILE,
						PhoneEx.FEATURE_ENABLE_MMS);
			}
		} finally {
			releaseWakeLock();
		}
	}

	
	@Override
	public void onStart() {
		
	}

}
