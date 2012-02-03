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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class VideoInfoEditActivity extends Activity {
	private EditText descriptionText = null;
	private EditText titleText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoinfoedit);

		final Video video = StaticInstances.video;

		descriptionText = (EditText) findViewById(R.id.videoinfoedit_description);
		descriptionText.setText(video.getDescription());

		titleText = (EditText) findViewById(R.id.videoinfoedit_title);
		titleText.setText(video.getTitle());

		final Button saveButton = (Button) findViewById(R.id.videoinfoedit_savebutton);
		saveButton.setOnClickListener(new SaveButtonOnClickListener());
	}

	private class SaveButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// Save
			final Methods methods = new Methods(VideoInfoEditActivity.this, getApplication());
			final String title = titleText.getText().toString();
			final String description = descriptionText.getText().toString();
			final Video video = StaticInstances.video;
			final String videoId = video.getId();

			if (title != null) {
				if (methods.videos_setTitle(title, videoId)) {
					video.setTitle(title);
				}
			}

			if (description != null) {
				if (methods.videos_setDescription(description, videoId)) {
					video.setDescription(description);
				}
			}

			Toast.makeText(VideoInfoEditActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
			VideoInfoEditActivity.this.finish();
		}
	}
}
