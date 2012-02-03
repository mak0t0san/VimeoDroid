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

import com.makotosan.vimeodroid.common.StaticInstances;
import com.makotosan.vimeodroid.vimeo.Methods;
import com.makotosan.vimeodroid.vimeo.Video;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

	private Boolean isInitialized = false;
	private Methods methods = null;

	private void initialize() {
		setTitle(Authentication.getUser(this, getApplication()).getDisplayName());
		initializeTabs();
		isInitialized = true;
	}

	private void initializeTabs() {
		final TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, VideosActivity.class);
		intent.putExtra("method", "getSubscriptions");
		intent.putExtra("title", "Subscriptions");
		spec = tabHost.newTabSpec("inbox").setIndicator("Inbox").setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, VideosActivity.class);
		intent.putExtra("method", "getAll");
		intent.putExtra("title", "My Videos");
		spec = tabHost.newTabSpec("videos").setIndicator("Videos").setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, VideosActivity.class);
		intent.putExtra("method", "getWatchLater");
		intent.putExtra("title", "My Watch Later");
		spec = tabHost.newTabSpec("watchlater").setIndicator("Watch Later").setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, VideosActivity.class);
		intent.putExtra("method", "getLikes");
		intent.putExtra("title", "My Likes");
		spec = tabHost.newTabSpec("likes").setIndicator("Likes").setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ContactsActivity.class);
		spec = tabHost.newTabSpec("contacts").setIndicator("Contacts").setContent(intent);
		tabHost.addTab(spec);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.home);

		// If not logged in, take them to the login page
		if (!Authentication.isUserLoggedIn(this, getApplication())) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		} else {
			initialize();
		}
		
		methods = new Methods(this, getApplication());
		
		// Check if they're coming in from the browser
		final Uri uri = this.getIntent().getData();
		if (uri != null) {
			boolean isVideoUrl = false;
			String lastPath = null;
			try {
				lastPath = uri.getLastPathSegment();

				// Don't care about the integer value, just want to make sure
				// that it is.
				Integer.parseInt(lastPath);
				if (uri.getHost().contains("vimeo.com")) {
					isVideoUrl = true;
				}
			} catch (NumberFormatException ex) {
				isVideoUrl = false;
			}

			if (isVideoUrl && null != lastPath) {
				Video video = this.methods.videos_getInfo(lastPath);
				this.showVideoInfo(video);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isInitialized && Authentication.isUserLoggedIn(this, getApplication())) {
			initialize();
		}
	}
	
	private void showVideoInfo(Video video) {
		StaticInstances.video = video;
		final Intent videoInfoIntent = new Intent(this, VideoInfoActivity.class);
		startActivity(videoInfoIntent);
	}
}
