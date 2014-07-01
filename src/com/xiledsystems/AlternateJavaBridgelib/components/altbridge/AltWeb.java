package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import com.xiledsystems.AlternateJavaBridgelib.components.util.AltSSLSocketFactory;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;

/**
 * This is an alternate class to using Web. It doesn't yet have a Post method. This class uses apache's HTTP classes,
 * rather than the standard java ones used by Web. Android uses the apache methods more, so this class was introduced.
 * 
 * @author Ryan Bis
 * 
 */
public class AltWeb extends AndroidNonvisibleComponent {

	private String url;
	private Map<String, String> headers = new HashMap<String, String>();
	private HttpParams params = null;
	private boolean ssl;
	private GotTextListener gotListener;
	private JSONObject jsonObject;

	public AltWeb(ComponentContainer Container) {
		super(Container);
	}

	public AltWeb(SvcComponentContainer sContainer) {
		super(sContainer);
	}

	public AltWeb SSL(boolean useSSL) {
		ssl = useSSL;
		return this;
	}

	public JSONObject getJSONObject() {
		return jsonObject;
	}

	public boolean SSL() {
		return ssl;
	}

	public void setGotTextListener(GotTextListener listener) {
		gotListener = listener;
	}

	/**
	 * Adds a header to this component
	 * 
	 * @param headerName
	 * @param value
	 * @return - an instance of AltWeb, for chaining methods
	 */
	public AltWeb addHeader(String headerName, String value) {
		headers.put(headerName, value);
		return this;
	}

	/**
	 * 
	 * @return - The headers assigned to this component.
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * Clears the headers for this component
	 */
	public void clearHeaders() {
		headers.clear();
	}

	/**
	 * Adds parameters to this component.
	 * 
	 * @param paramName
	 * @param paramValue
	 * @return - an instance of AltWeb for chaining methods
	 */
	public AltWeb addParam(String paramName, String paramValue) {
		if (params == null) {
			params = new BasicHttpParams();
		}
		params.setParameter(paramName, paramValue);
		return this;
	}

	/**
	 * Set the url for this component.
	 * 
	 * @param url
	 * @return - an instance of this component for chaining methods
	 */
	public AltWeb Url(String url) {
		this.url = url;
		return this;
	}

	/**
	 * 
	 * @return - The url this component is set to
	 */
	public String Url() {
		return url;
	}

	/**
	 * 
	 * @return - The parameters assigned to this component.
	 */
	public HttpParams getParams() {
		return params;
	}

	/**
	 * Clear all parameters associated with this component.
	 */
	public void clearParams() {
		params = null;
	}

	/**
	 * This is a simple get method to run, which will return a String result using the GOT_TEXT event. To use get to get
	 * a file, use Get(pathnametonewfile).
	 * 
	 * The request is sent in a separate thread, but the event is posted on the UI thread.
	 * 
	 */
	public void Get() {
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				final HttpClient client = new DefaultHttpClient();
				ResponseHandler<String> response = new BasicResponseHandler();
				HttpGet request = null;
				try {
					request = new HttpGet(url);
				} catch (IllegalArgumentException e) {
					// Something is wrong with the url. Throw an error.
					e.printStackTrace();
					dispatchError("Get()", ErrorMessages.ERROR_ALTWEB_URL_EXCEPTION);
					return;
				}
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						request.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					request.setParams(params);
				}
				try {
					final String respMsg = client.execute(request, response);
					if (gotListener == null) {
						post(new Runnable() {
							@Override
							public void run() {
								GotText(respMsg);
							}
						});
					} else {
						gotListener.GotText(respMsg);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					dispatchError("Get()", ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION);
				} catch (IOException e) {
					e.printStackTrace();
					dispatchError("Get()", ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION);
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}

	public void GotText(String responseText) {
		if (eventListener != null) {
			eventListener.eventDispatched(Events.GOT_TEXT, responseText);
		} else {
			EventDispatcher.dispatchEvent(AltWeb.this, Events.GOT_TEXT, responseText);
		}
	}

	private HttpClient getNewHttpClient() {
		if (ssl) {
			try {
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);

				SSLSocketFactory sf = new AltSSLSocketFactory(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

				HttpParams params = new BasicHttpParams();
				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				registry.register(new Scheme("https", sf, 443));

				ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
				return new DefaultHttpClient(ccm, params);
			} catch (Exception e) {
				return new DefaultHttpClient();
			}
		} else {
			return new DefaultHttpClient();
		}
	}

	/**
	 * Method to POST a file to a web server. If it sends successfully, the Events.RESPONSE event is thrown, with the
	 * response in String format as args[0].
	 * 
	 * @param filePath
	 */
	public void PostFile(final String filePath) {
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				final HttpClient client = getNewHttpClient();
				HttpPost post = null;
				try {
					post = new HttpPost(url);
				} catch (IllegalArgumentException e) {
					// Something is wrong with the url. Throw an error.
					e.printStackTrace();
					dispatchError("PostFile()", ErrorMessages.ERROR_ALTWEB_URL_EXCEPTION);
					return;
				}
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						post.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					post.setParams(params);
				}
				InputStreamEntity entity;
				try {
					entity = new InputStreamEntity(MediaUtil.openMedia(getContext(), filePath), -1);
					entity.setContentType("binary/octet-stream");
					entity.setChunked(true);
					post.setEntity(entity);
					ResponseHandler<String> response = new BasicResponseHandler();
					final String rspMsg = client.execute(post, response);
					post(new Runnable() {
						public void run() {
							if (eventListener != null) {
								eventListener.eventDispatched(Events.RESPONSE, rspMsg);
							} else {
								EventDispatcher.dispatchEvent(AltWeb.this, Events.RESPONSE, rspMsg);
							}
						}
					});
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					dispatchError("PostFile()", ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION);
				} catch (IOException e) {
					e.printStackTrace();
					dispatchError("PostFile()", ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION);
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}
	
	/**
	 * POST a byte array to a URL. This is useful if you are transferring encrypted data.
	 * 
	 * @param bytes
	 * @param listener
	 * @param postOnUi
	 */
	public void PostBytes(final byte[] bytes, final ResponseListener listener, final boolean postOnUi) {
		ThreadTimer.runOneTimeThread(new Runnable() {			
			@Override
			public void run() {
				final HttpClient client = getNewHttpClient();
				HttpPost post = null;
				post = new HttpPost(url);
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						post.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					post.setParams(params);
				}
				InputStreamEntity entity;
				try {
					entity = new InputStreamEntity(new ByteArrayInputStream(bytes), -1);
					entity.setContentType("binary/octet-stream");
					entity.setChunked(true);
					post.setEntity(entity);
					ResponseHandler<String> response = new BasicResponseHandler();
					final String rspMsg = client.execute(post, response);
					if (postOnUi) {
						post(new Runnable() {
							public void run() {
								listener.Response(rspMsg);
							}
						});
					} else {
						listener.Response(rspMsg);
					}
				} catch (final ClientProtocolException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null),
										e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null), e.getMessage());
					}
				} catch (final IOException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
					}
				} catch (final Exception e) {
					e.printStackTrace();
					listener.GotError("General exception caught.", e.getMessage());
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError("General exception caught.", e.getMessage());
							}
						});
					} else {
						listener.GotError("General exception caught.", e.getMessage());
					}
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}
	
	/**
	 * POST an HttpEntity to a URL.
	 * 
	 * @param bytes
	 * @param listener
	 * @param postOnUi
	 */
	public void PostEntity(final HttpEntity entity, final ResponseListener listener, final boolean postOnUi) {
		ThreadTimer.runOneTimeThread(new Runnable() {			
			@Override
			public void run() {
				final HttpClient client = getNewHttpClient();
				HttpPost post = null;
				post = new HttpPost(url);
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						post.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					post.setParams(params);
				}				
				try {					
					post.setEntity(entity);
					ResponseHandler<String> response = new BasicResponseHandler();
					final String rspMsg = client.execute(post, response);
					if (postOnUi) {
						post(new Runnable() {
							public void run() {
								listener.Response(rspMsg);
							}
						});
					} else {
						listener.Response(rspMsg);
					}
				} catch (final ClientProtocolException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null),
										e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null), e.getMessage());
					}
				} catch (final IOException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
					}
				} catch (final Exception e) {
					e.printStackTrace();
					listener.GotError("General exception caught.", e.getMessage());
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError("General exception caught.", e.getMessage());
							}
						});
					} else {
						listener.GotError("General exception caught.", e.getMessage());
					}
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}
	
	/**
	 * POST an inputstream to a URL. 
	 * 
	 * @param bytes
	 * @param listener
	 * @param postOnUi
	 */
	public void PostStream(final InputStream stream, final ResponseListener listener, final boolean postOnUi) {
		ThreadTimer.runOneTimeThread(new Runnable() {			
			@Override
			public void run() {
				final HttpClient client = getNewHttpClient();
				HttpPost post = null;
				post = new HttpPost(url);
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						post.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					post.setParams(params);
				}
				InputStreamEntity entity;
				try {
					entity = new InputStreamEntity(stream, -1);
					entity.setContentType("binary/octet-stream");
					entity.setChunked(true);
					post.setEntity(entity);
					ResponseHandler<String> response = new BasicResponseHandler();
					final String rspMsg = client.execute(post, response);
					try {
						stream.close();
					} catch (IOException e) {
						// Must already be closed.
						e.printStackTrace();
					}
					if (postOnUi) {
						post(new Runnable() {
							public void run() {
								listener.Response(rspMsg);
							}
						});
					} else {
						listener.Response(rspMsg);
					}
				} catch (final ClientProtocolException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null),
										e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null), e.getMessage());
					}
				} catch (final IOException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
					}
				} catch (final Exception e) {
					e.printStackTrace();
					listener.GotError("General exception caught.", e.getMessage());
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError("General exception caught.", e.getMessage());
							}
						});
					} else {
						listener.GotError("General exception caught.", e.getMessage());
					}
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}

	/**
	 * Method to POST a file to a web server. If it sends successfully, the Events.RESPONSE event is thrown, with the
	 * response in String format as args[0].
	 * 
	 * @param filePath
	 */
	public void PostFile(final String filePath, final ResponseListener listener, final boolean postOnUi) {
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				final HttpClient client = getNewHttpClient();
				HttpPost post = null;
				post = new HttpPost(url);
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						post.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					post.setParams(params);
				}
				InputStreamEntity entity;
				try {
					File file = new File(filePath);
					entity = new InputStreamEntity(new BufferedInputStream(new FileInputStream(file)), -1);
					entity.setContentType("binary/octet-stream");
					entity.setChunked(true);
					post.setEntity(entity);
					ResponseHandler<String> response = new BasicResponseHandler();
					final String rspMsg = client.execute(post, response);
					if (postOnUi) {
						post(new Runnable() {
							public void run() {
								listener.Response(rspMsg);
							}
						});
					} else {
						listener.Response(rspMsg);
					}
				} catch (final ClientProtocolException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null),
										e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null), e.getMessage());
					}
				} catch (final IOException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
					}
				} catch (final Exception e) {
					e.printStackTrace();
					listener.GotError("General exception caught.", e.getMessage());
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError("General exception caught.", e.getMessage());
							}
						});
					} else {
						listener.GotError("General exception caught.", e.getMessage());
					}
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}

	public void Post(final JSONObject json) {
		jsonObject = json;
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				final HttpClient client = getNewHttpClient();
				HttpPost post = null;
				try {
					post = new HttpPost(url);
				} catch (IllegalArgumentException e) {
					// Something is wrong with the url. Throw an error.
					e.printStackTrace();
					dispatchError("PostFile(json)", ErrorMessages.ERROR_ALTWEB_URL_EXCEPTION);
					return;
				}
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						post.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					post.setParams(params);
				}
				try {
					StringEntity entity = new StringEntity(json.toString());
					entity.setContentType("application/json");
					post.setEntity(entity);
					ResponseHandler<String> rhandl = new BasicResponseHandler();
					final String resp = client.execute(post, rhandl);
					post(new Runnable() {
						@Override
						public void run() {
							if (eventListener != null) {
								eventListener.eventDispatched(Events.RESPONSE, resp);
							} else {
								EventDispatcher.dispatchEvent(AltWeb.this, Events.RESPONSE, resp);
							}
						}
					});
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					dispatchError("Post(json)", ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION);
				} catch (IOException e) {
					e.printStackTrace();
					dispatchError("Post(json)", ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION);
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}

	/**
	 * This will POST what's in this component's headers as name value pairs to the url set in this component (so no
	 * headers will be set)
	 * 
	 * @param listener
	 * @param postOnUi
	 */
	public void Post(final ResponseListener listener, final boolean postOnUi) {
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				final HttpClient client = getNewHttpClient();
				HttpPost post = null;
				post = new HttpPost(url);
				// Convert the headers to nameValue Pairs
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				for (String name : headers.keySet()) {
					pairs.add(new BasicNameValuePair(name, headers.get(name)));
				}
				try {
					post.setEntity(new UrlEncodedFormEntity(pairs));
				} catch (UnsupportedEncodingException e) {
					// TODO Handle this error
				}
				try {
					ResponseHandler<String> respH = new BasicResponseHandler();
					final String resp = client.execute(post, respH);
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.Response(resp);
							}
						});
					} else {
						listener.Response(resp);
					}
				} catch (final ClientProtocolException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null),
										e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null), e.getMessage());
					}
				} catch (final IOException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
					}
				} catch (final Exception e) {
					e.printStackTrace();
					listener.GotError("General exception caught.", e.getMessage());
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError("General exception caught.", e.getMessage());
							}
						});
					} else {
						listener.GotError("General exception caught.", e.getMessage());
					}
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}

	public void Post(final JSONObject json, final ResponseListener listener, final boolean postOnUi) throws IllegalArgumentException {
		jsonObject = json;
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				final HttpClient client = getNewHttpClient();
				HttpPost post = null;
				post = new HttpPost(url);
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						post.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					post.setParams(params);
				}
				try {
					StringEntity entity = new StringEntity(json.toString());
					entity.setContentType("application/json");
					post.setEntity(entity);
					ResponseHandler<String> rhandl = new BasicResponseHandler();
					final String resp = client.execute(post, rhandl);
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.Response(resp);
							}
						});
					} else {
						listener.Response(resp);
					}
				} catch (final ClientProtocolException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null),
										e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null), e.getMessage());
					}
				} catch (final IOException e) {
					e.printStackTrace();
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
							}
						});
					} else {
						listener.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
					}
				} catch (final Exception e) {
					e.printStackTrace();
					listener.GotError("General exception caught.", e.getMessage());
					if (postOnUi) {
						post(new Runnable() {
							@Override
							public void run() {
								listener.GotError("General exception caught.", e.getMessage());
							}
						});
					} else {
						listener.GotError("General exception caught.", e.getMessage());
					}
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}

	/**
	 * This is a simple get method to run, which will return a String result. The GotTextListener has the GotText(String
	 * response) method. This is where the result is returned. This is meant for more advanced users who are familiar
	 * with working with interfaces.
	 * 
	 * The request is sent in a separate thread. If postOnUI is true, then the event is posted on the UI thread.
	 * 
	 */
	public void Get(final GotTextListener afterDoneAction, final boolean postOnUI) {
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				final HttpClient client = getNewHttpClient();
				ResponseHandler<String> response = new BasicResponseHandler();
				HttpGet request = null;
				request = new HttpGet(url);
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						request.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					request.setParams(params);
				}
				try {
					final String respMsg = client.execute(request, response);
					if (postOnUI) {
						post(new Runnable() {
							@Override
							public void run() {
								afterDoneAction.GotText(respMsg);
							}
						});
					} else {
						afterDoneAction.GotText(respMsg);
					}
				} catch (final ClientProtocolException e) {
					e.printStackTrace();
					if (postOnUI) {
						post(new Runnable() {
							@Override
							public void run() {
								afterDoneAction.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null),
										e.getMessage());
							}
						});
					} else {
						afterDoneAction.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION, null),
								e.getMessage());
					}
				} catch (final IOException e) {
					e.printStackTrace();
					if (postOnUI) {
						post(new Runnable() {
							@Override
							public void run() {
								afterDoneAction.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
							}
						});
					} else {
						afterDoneAction.GotError(ErrorMessages.formatMessage(ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION, null), e.getMessage());
					}
				} catch (final Exception e) {
					e.printStackTrace();
					afterDoneAction.GotError("General exception caught.", e.getMessage());
					if (postOnUI) {
						post(new Runnable() {
							@Override
							public void run() {
								afterDoneAction.GotError("General exception caught.", e.getMessage());
							}
						});
					} else {
						afterDoneAction.GotError("General exception caught.", e.getMessage());
					}
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}

	public interface ResponseListener {
		public void Response(String response);

		public void GotError(String errorMsg, String stackTrace);
	}

	/**
	 * Interface to use with Get(GotTextListener). The GotText(String response) method is called from this interface
	 * when a response has been delivered. The GotError method is called when an error has occurred. When using this
	 * interface, the normal error occurred event is NOT thrown, as it is being handled in this interface.
	 * 
	 */
	public interface GotTextListener {
		public void GotText(String response);

		public void GotError(String errorMsg, String stackTrace);
	}

	/**
	 * Use this method to get a file using an HttpGet request. You submit the path you'd like the file to download to.
	 * This request is run in it's own thread, and the GOT_FILE event is posted in the UI thread when complete, and
	 * successful. If not, check your logcat to see the issue (errors are dumped into logcat).
	 * 
	 * @param path
	 */
	public void Get(final String path) {
		ThreadTimer.runOneTimeThread(new Runnable() {
			@Override
			public void run() {
				final HttpClient client = new DefaultHttpClient();
				HttpGet request = null;
				try {
					request = new HttpGet(url);
				} catch (IllegalArgumentException e) {
					// Something is wrong with the url. Throw an error.
					e.printStackTrace();
					dispatchError("Get(path)", ErrorMessages.ERROR_ALTWEB_URL_EXCEPTION);
					return;
				}
				if (headers.size() > 0) {
					for (String s : headers.keySet()) {
						request.addHeader(s, headers.get(s));
					}
				}
				if (params != null) {
					request.setParams(params);
				}
				try {
					HttpResponse response = client.execute(request);
					InputStream in = response.getEntity().getContent();
					FileOutputStream out = new FileOutputStream(new File(path));
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					out.close();
					post(new Runnable() {
						public void run() {
							if (eventListener != null) {
								eventListener.eventDispatched(Events.GOT_FILE, path);
							} else {
								EventDispatcher.dispatchEvent(AltWeb.this, Events.GOT_FILE, path);
							}
						}
					});
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					dispatchError("Get(path)", ErrorMessages.ERROR_ALTWEB_CLIENT_PROTOCOL_EXCEPTION);
				} catch (IOException e) {
					e.printStackTrace();
					dispatchError("Get(path)", ErrorMessages.ERROR_ALTWEB_IO_EXCEPTION);
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
		});
	}

	private void dispatchError(String functionName, int errorNumber) {
		if (container == null) {
			sContainer.$formService().dispatchErrorOccurredEvent(this, functionName, errorNumber);
		} else {
			container.getRegistrar().dispatchErrorOccurredEvent(this, functionName, errorNumber);
		}
	}

}
