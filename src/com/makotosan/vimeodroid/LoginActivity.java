/**
 * VimeoDroid - Unofficial Vimeo app for Android
 * Copyright (C) 2012 Makoto Schoppert
 * This program is free software; 
 * you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package com.makotosan.vimeodroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public static final String TAG = "LoginActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.initializeButtons();
	}

	private final Handler handler = new Handler();
	private final Runnable checkUser = new Runnable() {
		// After we've received our data and parsed it, bind the list adapter
		@Override
		public void run() {
			final Uri uri = getIntent().getData();
			if (uri != null) {
				final String authorizationText = uri.getQueryParameter("oauth_verifier");
				if (authorizationText.length() > 0 && Authentication.saveAuthentication(getApplicationContext(), authorizationText)) {
					launchMainActivity();
				}
			}
			
			if (Authentication.isUserLoggedIn(getApplicationContext(), getApplication())) {
				launchMainActivity();
				return;
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		handler.post(checkUser);
	}

	private void launchMainActivity() {
		final Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private void initializeButtons() {
		// Initialize buttons
		final Button btnAuthorize = (Button) this.findViewById(R.id.ButtonAuthorize);

		btnAuthorize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final String url = Authentication.authorizeUser();
					launchAuthorizationActivity(url);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), 5).show();
				}
			}
		});
	}

	private void launchAuthorizationActivity(String url) {
		final Uri uri = Uri.parse(url);
		final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

}