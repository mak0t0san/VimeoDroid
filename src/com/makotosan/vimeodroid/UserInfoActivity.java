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

import java.util.List;

import com.makotosan.vimeodroid.common.ImageDownloader;
import com.makotosan.vimeodroid.vimeo.Methods;
import com.makotosan.vimeodroid.vimeo.Person;
import com.makotosan.vimeodroid.vimeo.Portrait;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class UserInfoActivity extends Activity {
	private Methods methods = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo);

		methods = new Methods(this, getApplication());

		final String userId = getIntent().getStringExtra("userid");
		Person person = methods.people_getInfo(userId);

		// Thumbnail
		final ImageView image = (ImageView) findViewById(R.id.user_image);
		final ImageDownloader downloader = new ImageDownloader((ApplicationEx) getApplication());
		List<Portrait> portraits = person.getPortraits();
		final String thumbUrl = portraits.get(portraits.size() - 1).getUrl();

		downloader.download(thumbUrl, image);
	}
}
