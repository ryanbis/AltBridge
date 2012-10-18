package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewer extends AndroidViewComponent {
		
	private final WebView view;
	
	private String url;

	private boolean followLinks;
	
	public WebViewer(ComponentContainer container) {
		super(container);
		view = new WebView(container.$context());
		view.setWebViewClient(new WebViewerClient());
		view.getSettings().setJavaScriptEnabled(true);
		view.getSettings().setBuiltInZoomControls(true);
		view.setFocusable(true);
		//container.$context().getWindow().requestFeature(Window.FEATURE_PROGRESS);
		container.$add(this);
		
		
	}
	
	public WebViewer(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = (WebView) container.$context().findViewById(resourceId);
		view.getSettings().setJavaScriptEnabled(true);
		view.getSettings().setBuiltInZoomControls(true);
		view.setFocusable(true);
		//container.$context().getWindow().requestFeature(Window.FEATURE_PROGRESS);
		view.setWebViewClient(new WebViewerClient());
	}
	
	public void Url(String url) {
		this.url = url;
	}
	
	public String Url() {
		return url;
	}
	
	public void FollowLinks(boolean follow) {
	  followLinks = follow;
	}
	
	public boolean FollowLinks() {
	  return followLinks;
	}
	
	public void Go() {
		view.loadUrl(url);
	}
	
	public void JavaScriptEnabled(boolean enabled) {
		view.getSettings().setJavaScriptEnabled(enabled);
	}
	
	public boolean JavaScriptEnabled() {
		return view.getSettings().getJavaScriptEnabled();
	}
	
	@Override
	public View getView() {		
		return view;
	}

	@Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, Events.ANIM_MIDDLE);
	}
	
	private class WebViewerClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
		  return !followLinks;
		}
	}

}
