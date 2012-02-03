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
