package com.makotosan.vimeodroid;

import java.util.List;

import com.makotosan.vimeodroid.common.IRefreshable;
import com.makotosan.vimeodroid.common.ImageDownloader;
import com.makotosan.vimeodroid.vimeo.*;
import com.ocpsoft.pretty.time.PrettyTime;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentsActivity extends ListActivity implements IRefreshable {
	// CommentListAdapter class
	private class CommentListAdapter extends ArrayAdapter<Comment> {
		private class ViewWrapper {
			View base;
			TextView commentAddedOn = null;
			TextView commentAuthor = null;
			ImageView commentAuthorImage = null;

			TextView commentText = null;

			public ViewWrapper(View base) {
				this.base = base;
			}

			public TextView getCommentAddedOn() {
				if (commentAddedOn == null) {
					commentAddedOn = (TextView) base.findViewById(R.id.comment_addedon);
				}

				return commentAddedOn;
			}

			public TextView getCommentAuthor() {
				if (commentAuthor == null) {
					commentAuthor = (TextView) base.findViewById(R.id.comment_author);
				}

				return commentAuthor;
			}

			public ImageView getCommentAuthorImage() {
				if (commentAuthorImage == null) {
					commentAuthorImage = (ImageView) base.findViewById(R.id.comment_authorimage);
				}

				return commentAuthorImage;
			}

			public TextView getCommentText() {
				if (commentText == null) {
					commentText = (TextView) base.findViewById(R.id.comment_text);
				}

				return commentText;
			}
		}

		private final ImageDownloader imageLoader = new ImageDownloader((ApplicationEx) getApplication());

		private final PrettyTime pt = new PrettyTime();

		public CommentListAdapter(Context context, int textViewResourceId, List<Comment> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ViewWrapper wrapper = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.commentrow, parent, false);

				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			} else {
				wrapper = (ViewWrapper) row.getTag();
			}

			final Comment comment = getModel(position);
			wrapper.getCommentText().setText(comment.getText());
			Author commentAuthor = comment.getAuthor();
			if (commentAuthor != null) {
				wrapper.getCommentAuthor().setText(comment.getAuthor().getDisplayName());
				imageLoader.download(commentAuthor.getPortraits().get(1).getUrl(), wrapper.getCommentAuthorImage());
			}

			if (comment.getId() != comment.getReplyToCommentId()) {
				wrapper.base.setBackgroundColor(getResources().getColor(R.color.commentReplyBackground));
			} else {
				wrapper.base.setBackgroundColor(0);
			}

			wrapper.getCommentAddedOn().setText(pt.format(comment.getDateCreate()) + " by ");

			return row;
		}
	}

	// Fields
	private CollectionItem<Comment> comments = null;
	private final Handler handler = new Handler();

	private Methods methods = null;

	final Runnable updateList = new Runnable() {
		// After we've received our data and parsed it, bind the list adapter
		@Override
		public void run() {
			setProgressBarIndeterminateVisibility(false);
			if (comments.getItems() != null) {
				setListAdapter(new CommentListAdapter(getApplicationContext(), R.layout.commentrow, comments.getItems()));
			}
		}
	};

	public Object onRetainNonConfigurationInstance() {
		// Return the cached list of comments so that we don't have to request
		// them for a configuration change (ie screen rotation)
		return comments;
	}

	@Override
	public void refresh() {
		try {
			final Intent intent = getIntent();
			final String videoId = intent.getExtras().getString("videoid");

			Thread t = new Thread() {
				public void run() {
					comments = methods.videos_comments_getList(videoId);
					handler.post(updateList);
				};
			};

			setProgressBarIndeterminateVisibility(true);
			t.start();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), 5).show();
		}
	}

	private Comment getModel(int position) {
		return ((CommentListAdapter) getListAdapter()).getItem(position);
	}

	@SuppressWarnings("unchecked")
	private void restoreMe() {
		comments = null;
		if (getLastNonConfigurationInstance() != null) {
			comments = (CollectionItem<Comment>) getLastNonConfigurationInstance();
			setListAdapter(new CommentListAdapter(getApplicationContext(), R.layout.commentrow, comments.getItems()));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.commentslayout);

		methods = new Methods(this, getApplication());
		restoreMe();
		if (comments == null) {
			refresh();
		}
	}
}
