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

import com.makotosan.vimeodroid.common.GenericPromptDialog;
import com.makotosan.vimeodroid.common.ImageDownloader;
import com.makotosan.vimeodroid.common.ManagedActivityDialog;
import com.makotosan.vimeodroid.common.ManagedDialogsActivity;
import com.makotosan.vimeodroid.common.MoogalXml;
import com.makotosan.vimeodroid.common.StaticInstances;
import com.makotosan.vimeodroid.common.TransferType;
import com.makotosan.vimeodroid.vimeo.Methods;
import com.makotosan.vimeodroid.vimeo.Owner;
import com.makotosan.vimeodroid.vimeo.Url;
import com.makotosan.vimeodroid.vimeo.Video;
import com.makotosan.vimeodroid.vimeo.VideoQuality;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoInfoActivity extends ManagedDialogsActivity {
	private static final int COMMENT_DIALOG_ID = 1;
	private static final String TAG = "VideoInfoActivity";

	private Thread getDownloadThread(final VideoQuality quality) {
		return new Thread() {
			@Override
			public void run() {
				final Intent transferIntent = new Intent(getApplicationContext(), TransferService.class);
				String videoUrl = null;

				Methods methods1 = new Methods(getApplicationContext(), getApplication());
				try {
					MoogalXml moogalXml = methods1.getMoogalXml(video.getId(), video.getOwner().getId(), quality);
					videoUrl = methods1.getRedirectedUri(moogalXml.getUrl());
					transferIntent.putExtra("videouri", videoUrl);
					transferIntent.putExtra("fileName", video.getId() + " " + quality.name() + ".mp4");
					transferIntent.putExtra("transferType", TransferType.Download.toString());
					startService(transferIntent);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, e.getMessage());
				}
			}
		};
	}

	private MenuItem likeMenuItem = null;
	private Methods methods = null;

	private final GenericPromptDialog pd = new GenericPromptDialog(this, COMMENT_DIALOG_ID, "Enter your comment");

	private Video video = StaticInstances.video;

	@Override
	public void dialogFinished(ManagedActivityDialog dialog, int buttonId) {
		if (dialog.getDialogId() == pd.getDialogId()) {
			String comment = pd.getReplyText();
			if (comment != null && methods.videos_comments_addComment(comment, video.getId(), null)) {
				Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoinfo);

		final ImageView playButton = (ImageView) findViewById(R.id.videoinfo_playbutton);
		playButton.setAlpha(128);

		methods = new Methods(this, getApplication());

		// Check if they're coming in from the browser
		final Uri uri = this.getIntent().getData();
		if (uri != null) {
			Toast.makeText(this, "Uri = " + uri.toString(), Toast.LENGTH_SHORT);
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
				video = this.methods.videos_getInfo(lastPath);
				StaticInstances.video = video;
				// this.showVideoInfo(video);
			}
		}

		if (video == null) {
			Intent mainIntent = new Intent().setClass(this, MainActivity.class);
			this.startActivity(mainIntent);
			return;
		}

		final String videoId = video.getId();

		// Call on separate thread since .getMp4Url makes a couple of HTTP
		// requests.
		Thread playButtonSetupThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// Setup the play button
				final Intent videoPlayerIntent = new Intent(VideoInfoActivity.this, VideoPlayerActivity.class);
				final String ownerId = String.valueOf(video.getOwner().getId());
				VideoQuality quality = VideoQuality.sd;

				String qualitySettings = methods.getSettingVideoQuality();
				if (VideoQuality.hd.name().equals(qualitySettings)) {
					quality = video.getIsHd() ? VideoQuality.hd : quality;
				} else if (VideoQuality.mobile.name().equals(qualitySettings)) {
					// Wish Java supported LINQ queries
					for (Url url : video.getUrls()) {
						if (VideoQuality.mobile.name().equals(url.getType())) {
							quality = VideoQuality.mobile;
							break;
						}
					}
				}

				final MoogalXml moogal = methods.getMoogalXml(videoId, ownerId, quality);
				videoPlayerIntent.putExtra("videoUrl", moogal.getUrl());
				videoPlayerIntent.putExtra("embedCode", moogal.getEmbedCode());

				final FrameLayout imageFrame = (FrameLayout) findViewById(R.id.videoinfo_imageframe);
				imageFrame.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(videoPlayerIntent);
					}
				});

			}
		});

		playButtonSetupThread.start();

		// Title
		final TextView titleText = (TextView) findViewById(R.id.videoinfo_title);
		final String title = video.getTitle().trim();
		titleText.setText(title);
		setTitle(title);

		final Owner owner = video.getOwner();

		// Author
		final TextView addedBy = (TextView) findViewById(R.id.videoinfo_addedby);
		addedBy.setText("by " + owner.getDisplayName());
		addedBy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent userInfoIntent = new Intent(getApplicationContext(), UserInfoActivity.class);
				userInfoIntent.putExtra("userid", owner.getId());
				startActivity(userInfoIntent);
			}
		});

		final TextView addedOn = (TextView) findViewById(R.id.videoinfo_addedon);
		addedOn.setText(video.getUploadDate().toLocaleString());

		// Member type
		final ImageView memberType = (ImageView) findViewById(R.id.videoinfo_membertype);
		if (owner.getIsPlus()) {
			memberType.setVisibility(View.VISIBLE);
			memberType.setImageResource(R.drawable.plus_icon);
		} else if (owner.getIsStaff()) {
			memberType.setVisibility(View.VISIBLE);
			memberType.setImageResource(R.drawable.staff);
		}

		// Caption
		final TextView captionText = (TextView) findViewById(R.id.videoinfo_caption);
		captionText.setText(video.getDescription());

		// Comments button
		final Button commentBtn = (Button) findViewById(R.id.videoinfo_commentbutton);
		commentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent commentsIntent = new Intent(getBaseContext(), CommentsActivity.class);
				commentsIntent.putExtra("videoid", videoId);
				startActivity(commentsIntent);
			}
		});

		// Thumbnail
		final ImageView image = (ImageView) findViewById(R.id.videoinfo_thumb);
		final ImageDownloader downloader = new ImageDownloader((ApplicationEx) getApplication());
		final String thumbUrl = video.getThumbnails().get(2).getUrl();

		downloader.download(thumbUrl, image);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.videoinfomenu, menu);
		likeMenuItem = menu.findItem(R.id.videomenu_like);
		this.setLikeMenuItemLiked(video.getIsLike());
		menu.findItem(R.id.videomenu_download_hd).setVisible(video.getIsHd());
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.videomenu_like:
			// Toggle the like
			final Boolean isLiked = !video.getIsLike();

			// Send the value to vimeo, if it failed, it'll return false
			if (methods.videos_setLike(isLiked, video.getId())) {
				// Set it on our in memory video object
				video.setIsLike(isLiked);
				this.setLikeMenuItemLiked(isLiked);
				Toast.makeText(this, isLiked ? R.string.video_liked : R.string.video_unliked, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.videomenu_comment:
			pd.show();
			break;
		case R.id.videomenu_share:
			final Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			String userName = Authentication.getUser(this, getApplication()).getDisplayName();
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, userName + " shared the clip \"" + video.getTitle() + "\" with you");
			shareIntent.putExtra(Intent.EXTRA_TEXT, userName + " shared a clip with you on Vimeo \n" + video.getTitle() + "\n" + video.getUrlVideo());
			startActivity(Intent.createChooser(shareIntent, "Share this video"));
			break;
		case R.id.videomenu_download_sd:
			Thread downloadSdThread = getDownloadThread(VideoQuality.sd);
			downloadSdThread.start();
			break;
		case R.id.videomenu_download_hd:
			Thread downloadHdThread = getDownloadThread(VideoQuality.hd);
			downloadHdThread.start();
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void registerDialogs() {
		registerDialog(pd);
	}

	private void setLikeMenuItemLiked(Boolean isLiked) {
		likeMenuItem.setTitle(isLiked ? R.string.unlike : R.string.like);
	}
}
