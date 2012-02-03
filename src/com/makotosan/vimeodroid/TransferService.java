package com.makotosan.vimeodroid;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.AbortableHttpRequest;

import com.makotosan.vimeodroid.common.StaticInstances;
import com.makotosan.vimeodroid.common.Transfer;
import com.makotosan.vimeodroid.common.TransferType;
import com.makotosan.vimeodroid.common.CountingRequestEntity.ProgressListener;
import com.makotosan.vimeodroid.vimeo.Methods;
import com.makotosan.vimeodroid.vimeo.Quota;
import com.makotosan.vimeodroid.vimeo.UploadTicket;
import com.makotosan.vimeodroid.vimeo.Methods.OnTransferringHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class TransferService extends Service {
	private static final String TAG = "TransferService";
	private int counter = 0;
	private NotificationManager notificationManager;
	private java.util.HashMap<Integer, Notification> notifications = new HashMap<Integer, Notification>();
	private final DecimalFormat numericFormatter = new DecimalFormat("#,###.#");
	private final int refreshInterval = 5000;

	private Transfer transfer = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		String svcName = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(svcName);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		counter++;
		transfer = new Transfer();
		final String videoUri = intent.getStringExtra("videouri");
		final TransferType transferType = TransferType.valueOf(intent.getStringExtra("transferType"));
		final String fileName = intent.getStringExtra("fileName");
		Bitmap bitmapThumbnail = null;
		int transferIcon = android.R.drawable.stat_sys_download;

		if (transferType == TransferType.Upload) {
			// Get the resource ID for the video
			final long resourceId = ContentUris.parseId(Uri.parse(videoUri));

			bitmapThumbnail = android.provider.MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), resourceId,
					android.provider.MediaStore.Video.Thumbnails.MICRO_KIND, null);
			transfer.setIcon(bitmapThumbnail);
			transferIcon = android.R.drawable.stat_sys_upload;
		}

		// Initialize our notification
		final Notification notification = new Notification(transferIcon, "Transferring...", System.currentTimeMillis());
		notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(this.getPackageName(), R.layout.transferprogress);

		final Intent manageIntent = new Intent(this, ManageTransfersActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, manageIntent, 0);

		notification.contentIntent = pendingIntent;
		if (bitmapThumbnail != null) {
			notification.contentView.setImageViewBitmap(R.id.transferprogressIcon, bitmapThumbnail);
		} else {
			notification.contentView.setImageViewResource(R.id.transferprogressIcon, R.drawable.icon);
		}

		notification.contentView.setProgressBar(R.id.transferprogressBar, 100, 0, false);
		notification.contentView.setTextViewText(R.id.transferprogressText, "Transferring... ");
		notification.contentView.setOnClickPendingIntent(R.layout.transferprogress, pendingIntent);

		// Add it to the collection of notifications
		notifications.put(counter, notification);

		// Add our notification to the notification tray
		notificationManager.notify(counter, notification);

		// Initialize our asynchronous transfer task
		final TransferTask task = new TransferTask(counter, transferType);

		task.execute(videoUri, fileName);
		return Service.START_STICKY;
	}

	private class TransferTask extends AsyncTask<String, Long, Void> {
		private long amountTransferred;
		private long fileLength = 0;
		private Methods methods = null;
		private int notificationId;
		private long previousBytes = 0;
		private TransferType transferType;

		public TransferTask(int notificationid, TransferType transferType) {
			this.notificationId = notificationid;
			this.transferType = transferType;
		}

		private void download(String contentUri, final String fileName) throws ClientProtocolException, IOException {
			final Notification notification = notifications.get(notificationId);
			notificationManager.notify(notificationId, notification);

			fileLength = methods.getFileSize(contentUri);
			final OnTransferringHandler handler = new OnTransferringHandler() {
				@Override
				public void onTransferring(AbortableHttpRequest request) {
					transfer.setId(notificationId);
					transfer.setType(TransferType.Download);
					transfer.setAbortableRequest(request);
					transfer.setFileName(fileName);
					transfer.setBytesTotal(fileLength);
					StaticInstances.transfers.put(notificationId, transfer);
				}
			};

			methods.downloadFile(contentUri, fileName, new TransferProgressListener(), handler);

		}

		private void upload(String contentUri) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException,
				ClientProtocolException, IOException {
			final Quota quota = methods.videos_upload_getQuota();

			final String[] proj = { MediaStore.Images.Media.DATA };

			final Cursor cur = getContentResolver().query(Uri.parse(contentUri), proj, null, null, null);
			final int columnIndex = cur.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

			cur.moveToFirst();
			final String fileName = cur.getString(columnIndex);
			final File file = new File(fileName);

			fileLength = file.length();
			if (fileLength >= quota.getUploadSpaceFree()) {
				Toast.makeText(getApplicationContext(), "Video size exceeds quota", Toast.LENGTH_SHORT).show();
				// return null;
			}

			final UploadTicket ticket = methods.videos_upload_getTicket();

			final Notification notification = notifications.get(notificationId);
			notificationManager.notify(notificationId, notification);

			methods.uploadFile(ticket.getEndPoint(), ticket.getId(), file, file.getName(), new TransferProgressListener(),
					new OnTransferringHandler() {
						@Override
						public void onTransferring(AbortableHttpRequest request) {
							transfer.setId(notificationId);
							transfer.setType(TransferType.Upload);
							transfer.setAbortableRequest(request);
							transfer.setFileName(fileName);
							transfer.setBytesTotal(fileLength);
							StaticInstances.transfers.put(notificationId, transfer);
						}
					});

			methods.videos_upload_complete(ticket.getId(), fileName);
		}

		@Override
		protected Void doInBackground(String... params) {
			final String contentUri = params[0];

			methods = new Methods(getApplicationContext(), getApplication());
			// Our timer, to poll the status every second and update the
			// progress bar
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					publishProgress(amountTransferred);
				}
			};

			timer.schedule(task, 0, refreshInterval);

			try {
				if (transferType == TransferType.Upload) {
					this.upload(contentUri);
				} else if (transferType == TransferType.Download) {
					final String fileName = params[1];

					this.download(contentUri, fileName);
				}
			} catch (Exception e) {
				// Toast.makeText(getApplicationContext(), e.getMessage(),
				// Toast.LENGTH_SHORT).show();
				Log.e(TAG, "Error processing transfer in background", e);
				return null;
			} finally {
				task.cancel();
				timer.cancel();
				notificationManager.cancel(notificationId);
				notifications.remove(notificationId);
				StaticInstances.transfers.remove(notificationId);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			notificationManager.cancel(notificationId);
			notifications.remove(notificationId);
			StaticInstances.transfers.remove(notificationId);
			if (notifications.isEmpty()) {
				Toast.makeText(getApplicationContext(), "All transfers completed!", Toast.LENGTH_SHORT).show();
				stopSelf();
			}
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			super.onProgressUpdate(values);

			// Multiply by 8 to convert to bits, and 1024 to convert to kilo
			double rate = (amountTransferred - previousBytes) / 1024;

			// Offset for the interval
			rate /= (refreshInterval / 1000);
			final Notification notification = notifications.get(notificationId);
			notification.contentView.setProgressBar(R.id.transferprogressBar, (int) fileLength, (int) amountTransferred, false);
			notification.contentView.setTextViewText(R.id.transferprogressText, numericFormatter.format(amountTransferred / 1024) + " kB / "
					+ numericFormatter.format(fileLength / 1024) + " kB at " + numericFormatter.format(rate) + " kB/s");
			notificationManager.notify(notificationId, notification);
			previousBytes = amountTransferred;
			final Transfer transfer = StaticInstances.transfers.get(notificationId);
			if (transfer != null) {
				transfer.setBytesTransferred(amountTransferred);
			}
		}

		private class TransferProgressListener implements ProgressListener {
			@Override
			public void transferred(long num) {
				amountTransferred = num;
			}
		}
	}
}
