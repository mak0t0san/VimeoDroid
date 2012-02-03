package com.makotosan.vimeodroid;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.makotosan.vimeodroid.common.IRefreshable;
import com.makotosan.vimeodroid.common.ImageDownloader;
import com.makotosan.vimeodroid.common.MoogalXml;
import com.makotosan.vimeodroid.common.StaticInstances;
import com.makotosan.vimeodroid.common.TransferType;
import com.makotosan.vimeodroid.vimeo.CollectionItem;
import com.makotosan.vimeodroid.vimeo.Methods;
import com.makotosan.vimeodroid.vimeo.Video;
import com.makotosan.vimeodroid.vimeo.VideoQuality;
import com.ocpsoft.pretty.time.PrettyTime;

public class VideosActivity extends ListActivity implements IRefreshable {
	private static final int PICK_VIDEO_REQUEST = 1;
	private final Handler handler = new Handler();
	private ImageDownloader imageDownloader = null;
	private Methods methods = null;
	private String sortBy = null;
	private CollectionItem<Video> videos = null;
	private static final String TAG = "VideosActivity";

	final Runnable updateList = new Runnable() {
		// After we've received our data and parsed it, bind the list adapter
		@Override
		public void run() {
			setProgressBarIndeterminateVisibility(false);
			if (videos.getItems() != null) {

				setListAdapter(new VideoListAdapter(getApplicationContext(), R.layout.videorow, videos.getItems()));
			}
		}
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Video video = getModel(info.position);
		switch (item.getItemId()) {
		case R.id.menu_video_play:
			this.playVideo(video);
			break;
		case R.id.menu_video_details:
			this.showVideoInfo(video);
			break;
		case R.id.menu_video_delete:
			new AlertDialog.Builder(this).setTitle("Really delete?").setMessage("Do you really want to delete this video?").setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					methods.videos_delete(video.getId());
					refresh();
				}
			}).setNegativeButton(R.string.no, null).show();
			break;
		case R.id.menu_video_edit:
			StaticInstances.video = video;
			Intent editIntent = new Intent(this, VideoInfoEditActivity.class);
			startActivity(editIntent);
		}

		return true;
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		final Video video = getModel(info.position);

		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.videolistcontextmenu, menu);
		// final Intent intent = getIntent();
		// final String methodName = intent.getExtras().getString("method") !=
		// null ? intent.getExtras().getString("method") : "";

		// if ("getAll".equals(methodName)) {
		if (video.getOwner().getId().equals(Authentication.getUser(this, getApplication()).getId())) {
			// If we're looking at our list of videos, then allow deleting
			menu.findItem(R.id.menu_video_delete).setVisible(true);
			menu.findItem(R.id.menu_video_edit).setVisible(true);
		}
	}

	// Inflate our menu from XML
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Upload buttonIntent intent
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_upload:
			intent = new Intent(Intent.ACTION_PICK);
			intent.setType("video/*");
			startActivityForResult(Intent.createChooser(intent, "Select video"), PICK_VIDEO_REQUEST);
			break;
		case R.id.menu_Record:
			intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			startActivityForResult(intent, PICK_VIDEO_REQUEST);
			break;
		case R.id.menu_refresh:
			this.refresh();
			break;
		case R.id.menu_search:
			onSearchRequested();
			break;
		case R.id.menu_Logout:
			Authentication.logout(this);
			intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_Settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;

		// All of our sorts
		case R.id.menu_sort_mostcommented:
			sortBy = "most_commented";
			this.refresh();
			break;
		case R.id.menu_sort_mostliked:
			sortBy = "most_liked";
			this.refresh();
			break;
		case R.id.menu_sort_mostplayed:
			sortBy = "most_played";
			this.refresh();
			break;
		case R.id.menu_sort_newest:
			sortBy = "newest";
			this.refresh();
			break;
		case R.id.menu_sort_oldest:
			sortBy = "oldest";
			this.refresh();
			break;
		}
		return true;
	}

	public Object onRetainNonConfigurationInstance() {
		// Return the cached list of videos so that we don't have to request
		// them for a configuration change (ie screen rotation)
		return videos;
	}

	@Override
	public void refresh() {
		try {
			final Intent intent = getIntent();
			final String methodName = intent.getStringExtra("method") != null ? intent.getStringExtra("method") : "";
			final String userId = intent.getStringExtra("userId");
			if (userId != null && userId.length() > 0) {
				final String displayName = intent.getStringExtra("displayName");
				if (displayName != null && displayName.length() > 0) {
					setTitle("Videos for " + displayName);
				}
			}

			final Thread t = new Thread() {
				public void run() {
					if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
						final String query = intent.getStringExtra(SearchManager.QUERY);
						videos = methods.videos_search(query, sortBy);
					}
					if ("getAll".equals(methodName)) {
						videos = methods.videos_getAll(sortBy, userId);
					}
					if ("getSubscriptions".equals(methodName)) {
						videos = methods.videos_getSubscriptions(sortBy);
					}
					if ("getWatchLater".equals(methodName)) {
						videos = methods.albums_getWatchLater();
					}
					if ("getLikes".equals(methodName)) {
						videos = methods.videos_getLikes(sortBy);
					}
					handler.post(updateList);
				};
			};

			setProgressBarIndeterminateVisibility(true);
			t.start();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), 5).show();
		}
	}

	private Video getModel(int position) {
		return ((VideoListAdapter) getListAdapter()).getItem(position);
	}

	private void playVideo(Video video) {
		final String videoId = video.getId();

		final MoogalXml moogal = methods.getMoogalXml(videoId, String.valueOf(video.getOwner().getId()), VideoQuality.sd);

		Thread t = new Thread() {
			@Override
			public void run() {
				Methods methods1 = new Methods(getApplicationContext(), getApplication());
				try {
					final Intent intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
					intent.putExtra("videoUrl", methods1.getRedirectedUri(moogal.getUrl()));
					intent.putExtra("embedCode", moogal.getEmbedCode());
					startActivity(intent);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		};

		t.start();
	}

	@SuppressWarnings("unchecked")
	private void restoreMe() {
		videos = null;
		if (getLastNonConfigurationInstance() != null) {
			videos = (CollectionItem<Video>) getLastNonConfigurationInstance();
			setListAdapter(new VideoListAdapter(getApplicationContext(), R.layout.videorow, videos.getItems()));
		}
	}

	private void showVideoInfo(Video video) {
		StaticInstances.video = video;
		final Intent videoInfoIntent = new Intent(this, VideoInfoActivity.class);
		startActivity(videoInfoIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == PICK_VIDEO_REQUEST) {
				final String selectedVideoUri = data.getDataString();
				final Intent intent = new Intent(this, TransferService.class);
				intent.putExtra("videouri", selectedVideoUri);
				intent.putExtra("transferType", TransferType.Upload.toString());
				startService(intent);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.videos);

		registerForContextMenu(getListView());

		setTitle("Videos");
		imageDownloader = new ImageDownloader((ApplicationEx) getApplication());

		methods = new Methods(this, getApplication());

		restoreMe();
		if (videos == null) {
			refresh();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		final Video video = getModel(position);
		showVideoInfo(video);
		// this.playVideo(video);
	}

	private class VideoListAdapter extends ArrayAdapter<Video> {
		final NumberFormat formatter = new DecimalFormat("00");
		final DecimalFormat numericFormatter = new DecimalFormat("#,###");
		final PrettyTime pt = new PrettyTime();

		public VideoListAdapter(Context context, int textViewResourceId, List<Video> items) {
			super(context, R.layout.videorow, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ViewWrapper wrapper = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.videorow, parent, false);

				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			} else {
				wrapper = (ViewWrapper) row.getTag();
			}

			final Video video = getModel(position);
			wrapper.getLabel().setText(video.getTitle());

			wrapper.getCaption().setText(video.getDescription());
			final int minutes = video.getDuration() / 60;
			final int seconds = video.getDuration() % 60;
			wrapper.getDuration().setText(minutes + ":" + formatter.format(seconds));

			wrapper.getNumberOfPlays().setText(numericFormatter.format(video.getNumberOfPlays()));
			wrapper.getNumberOfLikes().setText(numericFormatter.format(video.getNumberOfLikes()));
			wrapper.getNumberOfComments().setText(numericFormatter.format(video.getNumberOfComments()));

			wrapper.getVideoInfoButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showVideoInfo(video);
				}
			});

			final String dateSpan = pt.format(video.getUploadDate());
			wrapper.getDateAdded().setText(dateSpan + " by " + video.getOwner().getDisplayName());
			if (video.getThumbnails() != null && !video.getThumbnails().isEmpty()) {
				imageDownloader.download(video.getThumbnails().get(0).getUrl(), wrapper.getIcon());
			}

			return row;
		}
	}

	private class ViewWrapper {
		View base;
		TextView caption = null;
		TextView dateAdded = null;
		TextView duration = null;
		ImageView icon = null;
		TextView label = null;
		TextView numberOfComments = null;
		TextView numberOfLikes = null;
		TextView numberOfPlays = null;
		Button videoInfoButton = null;

		public ViewWrapper(View base) {
			this.base = base;
		}

		public TextView getCaption() {
			if (caption == null) {
				caption = (TextView) base.findViewById(R.id.videorowCaption);
			}

			return caption;
		}

		public TextView getDateAdded() {
			if (dateAdded == null) {
				dateAdded = (TextView) base.findViewById(R.id.videorowDateAdded);
			}

			return dateAdded;
		}

		public TextView getDuration() {
			if (duration == null) {
				duration = (TextView) base.findViewById(R.id.videorowDuration);
			}

			return duration;
		}

		public ImageView getIcon() {
			if (icon == null) {
				icon = (ImageView) base.findViewById(R.id.videorowImage);
			}

			return icon;
		}

		public TextView getLabel() {
			if (label == null) {
				label = (TextView) base.findViewById(R.id.videorowLabel);
			}

			return label;
		}

		public TextView getNumberOfComments() {
			if (numberOfComments == null) {
				numberOfComments = (TextView) base.findViewById(R.id.videorowNumberOfComments);
			}
			return numberOfComments;
		}

		public TextView getNumberOfLikes() {
			if (numberOfLikes == null) {
				numberOfLikes = (TextView) base.findViewById(R.id.videorowNumberOfLikes);
			}

			return numberOfLikes;
		}

		public TextView getNumberOfPlays() {
			if (numberOfPlays == null) {
				numberOfPlays = (TextView) base.findViewById(R.id.videorowNumberOfPlays);
			}

			return numberOfPlays;
		}

		public Button getVideoInfoButton() {
			if (videoInfoButton == null) {
				videoInfoButton = (Button) base.findViewById(R.id.videorowInfoButton);
			}

			return videoInfoButton;
		}
	}
}
