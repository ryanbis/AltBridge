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

	public WebViewer(ComponentContainer container) {
		super(container);
		view = new WebView(container.$context());
		view.getSettings().setJavaScriptEnabled(true);
		container.$context().getWindow().requestFeature(Window.FEATURE_PROGRESS);
		view.setWebChromeClient(new WebViewerClient());
		
	}
	
	public WebViewer(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = (WebView) container.$context().findViewById(resourceId);
		view.getSettings().setJavaScriptEnabled(true);
		container.$context().getWindow().requestFeature(Window.FEATURE_PROGRESS);
		view.setWebChromeClient(new WebViewerClient());
	}
	
	public void Url(String url) {
		this.url = url;
	}
	
	public String Url() {
		return url;
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
	
	private class WebViewerClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int progress) {
			container.$context().setProgress(progress * 1000);
		}
	}

}
