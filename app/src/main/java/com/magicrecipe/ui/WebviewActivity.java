package com.magicrecipe.ui;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.puppy.magicrecipe.R;

public class WebviewActivity extends Activity {
	
	private WebView webView;
	private ProgressBar progressBar;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		String url = (String)getIntent().getExtras().get("URL");
		webView = (WebView) findViewById(R.id.termsprivacy_webview);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);


		webView.setWebViewClient(new MyWebViewClient());
		webView.loadUrl(url);
		
	}
	
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			// This is my web site, so do not override; let my WebView load
			// the page
			return false;

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			progressBar.setVisibility(View.GONE);

			super.onPageFinished(view, url);
//		    view.loadUrl("javascript:document.body.innerHTML = document.body.innerHTML.replace(/<a.*href=/gi,'<a href=\"#\" _url=');"); 
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub

			progressBar.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
