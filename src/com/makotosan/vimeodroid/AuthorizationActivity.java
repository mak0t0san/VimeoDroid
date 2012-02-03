package com.makotosan.vimeodroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AuthorizationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.authorization);

		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		String url = extras.getString(getPackageName() + ".url");
		WebView webView = (WebView) this.findViewById(R.id.webAuth);
		WebSettings settings = webView.getSettings();
		settings.setAppCacheEnabled(true);

		//settings
		//		.setUserAgentString("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.4) Gecko/20100611 Firefox/3.6.4 GTB7.1");
		settings.setJavaScriptEnabled(true);
		webView.loadUrl(url);
	}
}
