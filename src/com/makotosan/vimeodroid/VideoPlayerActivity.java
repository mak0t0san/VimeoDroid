package com.makotosan.vimeodroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;
import com.makotosan.vimeodroid.vimeo.Methods;

public class VideoPlayerActivity extends Activity {
	// private String videoUrl = "";
	private String uri = "";
	private String contentType = "";
	private boolean launchExternalPlayer = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ProgressDialog dialog = ProgressDialog.show(VideoPlayerActivity.this, "Preparing playback", "Preparing video...", true, true);

		final Intent intent = this.getIntent();
		uri = intent.getExtras().getString("videoUrl");
		final Methods methods = new Methods(getApplicationContext(), getApplication());

		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					uri = methods.getRedirectedUri(uri);
					contentType = methods.getContentType(uri);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (contentType.endsWith("mp4")) {
			if (methods.getSettingUseBuiltinPlayer()) {
				// Use built in player
				setContentView(R.layout.videoplayer);

				final Uri videoUri = Uri.parse(uri);
				final MediaController mc = new MediaController(this);
				final VideoView view = (VideoView) findViewById(R.id.videoView);
				view.setVideoURI(videoUri);
				view.setMediaController(mc);
				mc.setMediaPlayer(view);
				view.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer arg0) {
						dialog.dismiss();
						view.requestFocus();
						view.start();
					}
				});

				view.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						finish();
					}
				});

			} else {
				this.launchExternalPlayer = true;
			}
		} else {
			// Use Flash player to play older flv video
			setContentView(R.layout.videowebview);
			String embedCode = intent.getExtras().getString("embedCode");
			WebView webView = (WebView) findViewById(R.id.videowebview);
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);
			settings.setPluginsEnabled(true);
			webView.loadDataWithBaseURL("http://vimeo.com", embedCode, "text/html", "utf-8", "http://vimeo.com");
		}
	}

	@Override
	protected void onStart() {
		if (this.launchExternalPlayer) {
			// Prompt user to use external player
			final Intent openVideoIntent = new Intent(Intent.ACTION_VIEW);
			openVideoIntent.setDataAndType(Uri.parse(uri), contentType);
			startActivityForResult(openVideoIntent, 0);
		}
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		WebView webView = (WebView) findViewById(R.id.videowebview);
		if (webView != null) {
			webView.clearView();
			webView.destroy();
		}

		VideoView view = (VideoView) findViewById(R.id.videoView);
		if (view != null) {
			view.stopPlayback();
		}

		this.finish();
	}
}
