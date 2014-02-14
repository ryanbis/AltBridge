package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Intent;
import android.net.Uri;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

/**
 * Helper class for receiving OAuth 1.0/1.0a authorizations.
 * 
 * IMPORTANT NOTE:
 * 
 * If you didn't mark the Form with OAuth compatibility when you created it, you
 * must make sure to add the following to your manifest inside the Form's
 * activity container (Just change the android:host value to what you set the
 * serviceTag to for this component ex. "BitBucket":
 * 
 * <intent-filter > <action android:name="android.intent.action.VIEW" />
 * <category android:name="android.intent.category.DEFAULT"></category>
 * <category android:name="android.intent.category.BROWSABLE"></category> <data
 * android:scheme="x-oauth1" android:host="serviceTag"></data> </intent-filter>
 * 
 * You also have to make sure that the Form's launchMode is set to "singleTask".
 * 
 * 
 * @author Ryan Bis
 * 
 */
public class OAuth1APIHelper extends AndroidNonvisibleComponent implements OnNewIntentListener {

	private String SCHEME = "x-oauth1";

	public final static String BITBUCKET = "BitBucket";
	public final static String TWITTER = "Twitter";
	public final static String GOOGLE = "Google";
	public final static String NETFLIX = "NetFlix";
	public final static String FLICKR = "Flickr";
	
	public final static String FL_REQ_TOKEN_URL = "http://www.flickr.com/services/oauth/request_token";
	public final static String FL_ACCESS_TOKEN_URL = "http://www.flickr.com/services/oauth/access_token";
	public final static String FL_AUTH_URL = "http://www/flickr.com/services/oauth/authorize";

	public final static String BB_REQ_TOKEN_URL = "https://bitbucket.org/!api/1.0/oauth/request_token";
	public final static String BB_ACCESS_TOKEN_URL = "https://bitbucket.org/!api/1.0/oauth/access_token";
	public final static String BB_AUTH_URL = "https://bitbucket.org/!api/1.0/oauth/authenticate";

	public final static String TWIT_REQ_URL = "http://twitter.com/oauth/request_token";
	public final static String TWIT_ACCESS_URL = "http://twitter.com/oauth/access_token";
	public final static String TWIT_AUTH_URL = "http://twitter.com/oauth/authorize";

	public final static String GOOGLE_REQ_URL = "https://www.google.com/accounts/OAuthGetRequestToken";
	public final static String GOOGLE_ACCESS_URL = "https://www.google.com/accounts/OAuthGetAccessToken";
	public final static String GOOGLE_AUTH_URL = "https://www.google.com/accounts/OAuthAuthorizeToken?hd=default";

	public final static String NF_REQ_URL = "http://api.netflix.com/oauth/request_token";
	public final static String NF_ACCESS_URL = "http://api.netflix.com/oauth/access_token";
	public final static String NF_AUTH_URL = "https://api-user.netflix.com/oauth/login";

	private final static String UNSAVED = "UnsavedTokenOrSecret";

	public final static String ENCODING = "UTF-8";

	// A string tag to save this oauth's token/tokensecrets in prefs
	private String service;

	private String conKey;
	private String conSecret;

	private String callbackUrl;

	private String authToken = UNSAVED;
	private String authTokenSecret = UNSAVED;

	private final Prefs prefs;

	private CommonsHttpOAuthConsumer consumer;
	private CommonsHttpOAuthProvider provider;

	/**
	 * This component is only meant to be used from a Form.
	 * 
	 * IMPORTANT NOTE:
	 * 
	 * If you didn't check OAuth 1.0/1.0a when using the New Form wizard, you
	 * must also make sure to add the following to your manifest inside the
	 * Form's activity container (Just change the android:host value to what you
	 * set the serviceTag to for this component ex. "BitBucket":
	 * 
	 * <intent-filter > <action android:name="android.intent.action.VIEW" />
	 * <category android:name="android.intent.category.DEFAULT"></category>
	 * <category android:name="android.intent.category.BROWSABLE"></category>
	 * <data android:scheme="x-oauth1" android:host="serviceTag"></data>
	 * </intent-filter>
	 * 
	 * You also have to make sure that the Form's launchMode is set to
	 * "singleTask".
	 * 
	 * @param container
	 */
	public OAuth1APIHelper(ComponentContainer container) {
		super(container);
		prefs = new Prefs(container);
		container.getRegistrar().registerForOnNewIntent(this);
	}

	/**
	 * The service tag is an identifier tag to use for this instance of
	 * Oauth1Helper. This should match what you set for the android:host
	 * parameter in your AndroidManifest file. This must be called AFTER
	 * ConsumerKey is called.
	 * 
	 * Use this method if you are not using one of the preconfigured options in
	 * this class.
	 * 
	 * @param requestUrl
	 *            - The url for requesting permissions
	 * @param accessUrl
	 *            - The url for requesting access
	 * @param authUrl
	 *            - The url for authorizing an account
	 */
	public void ServiceInfo(String requestUrl, String accessUrl, String authUrl) {
		if (conKey != null && conSecret != null) {
			consumer = new CommonsHttpOAuthConsumer(conKey, conSecret);
			provider = new CommonsHttpOAuthProvider(requestUrl, accessUrl, authUrl);
			checkForTokens();
			callbackUrl = SCHEME + "://" + service;
		}
	}

	/**
	 * Call this method to request an OAuth 1.0/1.0a authorization. When access
	 * is granted, the Events.OAUTH1_AUTHORIZED event will be thrown.
	 */
	public void RequestAuthorization() {
		checkForTokens();
		if (authToken.equals(UNSAVED) || authTokenSecret.equals(UNSAVED)) {
			requestAuth();
		} else {
			consumer.setTokenWithSecret(authToken, authTokenSecret);
			postAuthorizedEvent();
		}
	}

	/**
	 * 
	 * @return - The service name (host) this helper is using.
	 */
	public String ServiceTag() {
		return service;
	}

	/**
	 * The service tag is an identifier tag to use for this instance of
	 * Oauth1Helper. This should match what you set for the android:host
	 * parameter in your AndroidManifest file. The scheme is for the data
	 * return. If this is null, or "", the default will be "x-oauth1".
	 * 
	 * The consumerKey and consumerSecret, you obtain from the site you are
	 * trying to access with OAuth 1.0/1.0a.
	 * 
	 * @param tag
	 */
	public void ConsumerKey(String serviceTag, String scheme, String consumerKey, String consumerSecret) {
		if (scheme != null && !scheme.equals("")) {
			SCHEME = scheme;
		}
		service = serviceTag;
		conKey = consumerKey;
		conSecret = consumerSecret;
		checkForTemplates();
		checkForTokens();
		callbackUrl = SCHEME + "://" + service;
	}

	public String getConsumerKey() {
		return conKey;
	}

	public String getConsumerSecret() {
		return conSecret;
	}

	/**
	 * Basic Http GET command. This will return the results in a string format.
	 * 
	 * The Events.OAUTH_API_RETURN event is thrown when a response is received.
	 * The first argument is the Statusline, and the second (args[1]) is the
	 * content.
	 * 
	 * The network transmition is handled in a seperate thread, so you don't
	 * have to worry about it. The return event is posted on the UI thread.
	 * 
	 * @param url
	 */
	public void Get(final String url) {
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				try {
					consumer.sign(request);
				} catch (OAuthCommunicationException e) {
					e.printStackTrace();
					return;
				} catch (OAuthMessageSignerException e) {
					e.printStackTrace();
					return;
				} catch (OAuthExpectationFailedException e) {
					e.printStackTrace();
					return;
				}
				HttpResponse response;
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				final String statusLine = response.getStatusLine().toString();
				InputStream in;
				try {
					in = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line;
					StringBuilder builder = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					final String content = builder.toString();
					postReturnEvent(statusLine, content);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		});
	}

	/**
	 * Basic Http POST command. This will return the results in a string format.
	 * 
	 * The Events.OAUTH_API_RETURN event is thrown when a response is received.
	 * The first argument is the Statusline, and the second (args[1]) is the
	 * content.
	 * 
	 * 
	 * The network transmition is handled in a seperate thread, so you don't
	 * have to worry about it. The return event is posted on the UI thread.
	 * 
	 * @param url
	 * @param data
	 *            - Arraylist of NameValuePairs to add as parameters in your
	 *            post.
	 */
	public void Post(final String url, final ArrayList<NameValuePair> data) {
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);
				try {
					request.setEntity(new UrlEncodedFormEntity(data));
					consumer.sign(request);
				} catch (OAuthCommunicationException e) {
					e.printStackTrace();
					return;
				} catch (OAuthMessageSignerException e) {
					e.printStackTrace();
					return;
				} catch (OAuthExpectationFailedException e) {
					e.printStackTrace();
					return;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return;
				}
				HttpResponse response;
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				final String statusLine = response.getStatusLine().toString();
				InputStream in;
				try {
					in = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line;
					StringBuilder builder = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					final String content = builder.toString();
					postReturnEvent(statusLine, content);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		});
	}

	/**
	 * Basic Http POST command. This will return the results in a string format.
	 * 
	 * The Events.OAUTH_API_RETURN event is thrown when a response is received.
	 * The first argument is the Statusline, and the second (args[1]) is the
	 * content.
	 * 
	 * The network transmition is handled in a seperate thread, so you don't
	 * have to worry about it. The return event is posted on the UI thread.
	 * 
	 * @param url
	 * @param filePath
	 *            - The absolute file path to the file to send.
	 */
	public void PostFile(final String url, final String filePath) {
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);
				try {
					File file = new File(filePath);
					InputStreamEntity fileEntity = new InputStreamEntity(new FileInputStream(file), -1);
					fileEntity.setContentType("binary/octet-stream");
					fileEntity.setChunked(true);
					request.setEntity(fileEntity);
					consumer.sign(request);
				} catch (OAuthCommunicationException e) {
					e.printStackTrace();
					return;
				} catch (OAuthMessageSignerException e) {
					e.printStackTrace();
					return;
				} catch (OAuthExpectationFailedException e) {
					e.printStackTrace();
					return;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return;
				}
				HttpResponse response;
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				final String statusLine = response.getStatusLine().toString();
				InputStream in;
				try {
					in = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line;
					StringBuilder builder = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					final String content = builder.toString();
					postReturnEvent(statusLine, content);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		});
	}

	private void checkForTokens() {
		authToken = prefs.GetString(service + "_" + OAuth.OAUTH_TOKEN, UNSAVED);
		authTokenSecret = prefs.GetString(service + "_" + OAuth.OAUTH_TOKEN_SECRET, UNSAVED);

	}

	private void checkForTemplates() {
		if (service.equals(BITBUCKET)) {
			consumer = new CommonsHttpOAuthConsumer(conKey, conSecret);
			provider = new CommonsHttpOAuthProvider(BB_REQ_TOKEN_URL, BB_ACCESS_TOKEN_URL, BB_AUTH_URL);
		} else if (service.equals(TWITTER)) {
			consumer = new CommonsHttpOAuthConsumer(conKey, conSecret);
			provider = new CommonsHttpOAuthProvider(TWIT_REQ_URL, TWIT_ACCESS_URL, TWIT_AUTH_URL);
		} else if (service.equals(GOOGLE)) {
			consumer = new CommonsHttpOAuthConsumer(conKey, conSecret);
			provider = new CommonsHttpOAuthProvider(GOOGLE_REQ_URL, GOOGLE_ACCESS_URL, GOOGLE_AUTH_URL);
		} else if (service.equals(NETFLIX)) {
			consumer = new CommonsHttpOAuthConsumer(conKey, conSecret);
			provider = new CommonsHttpOAuthProvider(NF_REQ_URL, NF_ACCESS_URL, NF_AUTH_URL);
		} else if (service.equals(FLICKR)) {
			consumer = new CommonsHttpOAuthConsumer(conKey, conSecret);
			provider = new CommonsHttpOAuthProvider(FL_REQ_TOKEN_URL, FL_ACCESS_TOKEN_URL, FL_AUTH_URL);
		}
	}

	private void requestAuth() {
		// Request authorization in a separate thread. When the authorization
		// is received, the Form will resume, and onNewIntent will be called.
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				String authUrl;
				try {
					authUrl = provider.retrieveRequestToken(consumer, callbackUrl);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
					container.$context().startActivity(intent);
				} catch (OAuthCommunicationException e) {
					e.printStackTrace();
				} catch (OAuthMessageSignerException e) {
					e.printStackTrace();
				} catch (OAuthNotAuthorizedException e) {
					e.printStackTrace();
				} catch (OAuthExpectationFailedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void postAuthorizedEvent() {
		container.getRegistrar().post(new Runnable() {
			@Override
			public void run() {
				EventDispatcher.dispatchEvent(OAuth1APIHelper.this, Events.OAUTH1_AUTHORIZED);
			}
		});
	}

	private void postReturnEvent(final String statusLine, final Object content) {
		container.getRegistrar().post(new Runnable() {
			@Override
			public void run() {
				EventDispatcher.dispatchEvent(OAuth1APIHelper.this, Events.OAUTH_API_RETURN, statusLine, content);
			}
		});
	}

	@Override
	public void onNewIntent(Intent intent) {
		final Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(SCHEME)) {
			ThreadTimer.runOneTimeThread(new Runnable() {
				@Override
				public void run() {
					final String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
					try {
						provider.retrieveAccessToken(consumer, verifier);
						prefs.StoreString(service + "_" + OAuth.OAUTH_TOKEN, consumer.getToken());
						prefs.StoreString(service + "_" + OAuth.OAUTH_TOKEN_SECRET, consumer.getTokenSecret());
						postAuthorizedEvent();
					} catch (OAuthMessageSignerException e) {
						e.printStackTrace();
					} catch (OAuthNotAuthorizedException e) {
						e.printStackTrace();
					} catch (OAuthExpectationFailedException e) {
						e.printStackTrace();
					} catch (OAuthCommunicationException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

}
