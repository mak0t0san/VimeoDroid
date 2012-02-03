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

package com.makotosan.vimeodroid.vimeo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.makotosan.vimeodroid.ApplicationEx;
import com.makotosan.vimeodroid.Authentication;
import com.makotosan.vimeodroid.ConsumerInfo;
import com.makotosan.vimeodroid.R;
import com.makotosan.vimeodroid.common.CountingRequestEntity;
import com.makotosan.vimeodroid.common.MoogalXml;
import com.makotosan.vimeodroid.common.VimeoUrls;
import com.makotosan.vimeodroid.common.CountingRequestEntity.ProgressListener;

public class Methods {

	public static interface OnTransferringHandler {
		public void onTransferring(AbortableHttpRequest request);
	}

	private static final String MY_PREFS = "VIMEO_DROID_PREFS";
	private static final String TAG = "Methods";

	public static boolean isExternalStorageWritable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		return mExternalStorageAvailable && mExternalStorageWriteable;
	}

	private ApplicationEx app;

	private Context context;

	public Methods(Context context, Application app) {
		this.context = context;
		this.app = (ApplicationEx) app;
	}

	public CollectionItem<Video> albums_getWatchLater() {
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("full_response", "1");

		return parseVideosXml(makeRequest("albums.getWatchLater", params));
	}

	public CollectionItem<Contact> contacts_getAll() {
		final CollectionItem<Contact> contacts = new CollectionItem<Contact>(Contact.class, "contacts");
		try {
			final XmlPullParser xpp = makeRequest("contacts.getAll", null);
			contacts.initialize(xpp);
		} catch (Exception e) {
			Log.e(TAG, "Unable to get contacts", e);
			Toast.makeText(context, "Unable to get contacts", Toast.LENGTH_SHORT).show();
		}

		return contacts;
	}

	public void downloadFile(String uri, String fileName, ProgressListener listener, OnTransferringHandler handler) throws ClientProtocolException, IOException {
		final HttpGet request = new HttpGet(uri);
		final HttpResponse response = this.getHttpResponse(request, uri, false);

		handler.onTransferring(request);

		HttpEntity entity = new CountingRequestEntity(response.getEntity(), listener);

		// long fileSize = entity.getContentLength();
		// entity.getContent();

		this.saveVideo(entity, uri, fileName);
	}

	private ConsumerInfo getConsumerInfo() {
		final SharedPreferences prefs = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
		final ConsumerInfo info = new ConsumerInfo();

		final String consumerToken = prefs.getString(Authentication.CONSUMER_TOKEN_PREF, null);
		final String consumerTokenSecret = prefs.getString(Authentication.CONSUMER_TOKEN_SECRET_PREF, null);

		info.setConsumerToken(consumerToken);
		info.setConsumerTokenSecret(consumerTokenSecret);
		return info;
	}

	public String getContentType(String uri) throws ClientProtocolException, IOException {
		final HttpResponse response = this.getHttpResponse(uri);
		return response.getFirstHeader("Content-Type").getValue();
	}

	public long getFileSize(String uri) throws ClientProtocolException, IOException {
		final HttpResponse response = this.getHttpResponse(uri);
		return response.getEntity().getContentLength();
	}

	public HttpResponse getHttpResponse(HttpGet request, String uri, Boolean closeConnection) throws ClientProtocolException, IOException {
		boolean useAltHttpClient = this.getUseAltHttpClient();
		final HttpClient client = useAltHttpClient ? app.getHttpClient() : AndroidHttpClient.newInstance("Vimeo Droid");
		HttpResponse response;
		try {
			response = client.execute(request);
		} finally {
			if (client != null && client instanceof AndroidHttpClient) {
				if (closeConnection) {
					((AndroidHttpClient) client).close();
				}
			}
		}

		return response;
	}

	public HttpResponse getHttpResponse(String uri) throws ClientProtocolException, IOException {
		final HttpGet request = new HttpGet(uri);
		return this.getHttpResponse(request, uri, true);
	}

	public MoogalXml getMoogalXml(String clipId, String userId, VideoQuality quality) {
		MoogalXml moogal = new MoogalXml();

		Display display = ((WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		String baseUrl = "http://vimeo.com/moogaloop/load/clip:"
				+ clipId
				+ "/local/?moog_width="
				+ display.getWidth()
				+ "&moog_height="
				+ display.getHeight()
				+ "&embed_location=&param_server=vimeo.com&param_force_embed=0&param_multimoog=&param_autoplay=1&param_fullscreen=1&param_md5=0&param_force_info=undefined&param_show_portrait=0&param_show_title=0&param_ver=39487&param_show_byline=0&param_context=user:"
				+ userId + "&param_clip_id=" + clipId + "&param_color=00ADEF&param_context_id=&context=user:" + userId;

		// Call it, and then we'll get XML
		final HttpGet request = new HttpGet(baseUrl);
		final HttpClient client = app.getHttpClient();
		final ResponseHandler<String> handler = new BasicResponseHandler();
		String finalUrl = "";

		try {
			final String response = client.execute(request, handler);
			String requestSignature = "";
			String requestSignatureExpires = "";
			String embedCode = "";

			final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

			final XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(response));
			int eventType = xpp.getEventType();
			String tagName = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tagName = xpp.getName();
					break;
				case XmlPullParser.END_TAG:
					tagName = "";
					break;
				case XmlPullParser.TEXT:
					if ("request_signature".equals(tagName)) {
						requestSignature = xpp.getText();
						break;
					}
					if ("request_signature_expires".equals(tagName)) {
						requestSignatureExpires = xpp.getText();
						break;
					}
					if ("embed_code".equals(tagName)) {
						embedCode = xpp.getText();
					}
					break;
				}

				eventType = xpp.next();

			}

			finalUrl = String.format("http://vimeo.com/moogaloop/play/clip:%s/%s/%s/?q=%s&type=local&embed_location=", clipId, requestSignature, requestSignatureExpires, quality);

			moogal.setUrl(finalUrl);
			moogal.setEmbedCode(embedCode);
			moogal.setRequestSignature(requestSignature);
			moogal.setRequestSignatureExpires(requestSignatureExpires);
			// Log.d("GetMp4Url", "Playing video at " + finalUrl);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return moogal;
		// return finalUrl;
	}

	public String getRedirectedUri(String uri) throws ClientProtocolException, IOException {
		boolean useAltHttpClient = this.getUseAltHttpClient();
		final HttpClient client = useAltHttpClient ? app.getHttpClient() : AndroidHttpClient.newInstance("Vimeo Droid");
		final HttpGet request = new HttpGet(uri);
		try {
			final HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 302) { // Redirect
				// Need to get the redirected URL value and do another request
				uri = response.getHeaders("Location")[0].getValue();
			}
		} finally {
			if (client != null && client instanceof AndroidHttpClient) {
				((AndroidHttpClient) client).close();
			}
		}

		return uri;
	}

	public boolean getSettingUseBuiltinPlayer() {
		SharedPreferences prefs = this.context.getSharedPreferences("com.makotosan.vimeodroid_preferences", 0);
		String settingKey = this.context.getResources().getString(R.string.SETTING_USE_BUILTIN_PLAYER);
		boolean useBuiltinPlayer = prefs.getBoolean(settingKey, true);
		return useBuiltinPlayer;
	}

	public String getSettingVideoQuality() {
		SharedPreferences prefs = this.context.getSharedPreferences("com.makotosan.vimeodroid_preferences", 0);
		String settingKey = this.context.getResources().getString(R.string.SETTING_VIDEO_QUALITY);
		String quality = prefs.getString(settingKey, "sd");
		return quality;
	}

	private boolean getUseAltHttpClient() {
		SharedPreferences prefs = this.context.getSharedPreferences("com.makotosan.vimeodroid_preferences", 0);
		String settingKey = this.context.getResources().getString(R.string.SETTING_HTTP_CLIENT_PREF);
		boolean useAltHttpClient = prefs.getBoolean(settingKey, false);
		return useAltHttpClient;
	}

	private XmlPullParser makeRequest(String method, HashMap<String, String> parameters) {
		final StringBuilder urlStringBuilder = new StringBuilder();
		try {
			urlStringBuilder.append(VimeoUrls.STANDARD_API + "?method=vimeo." + method);
			final ConsumerInfo info = getConsumerInfo();

			if (parameters != null && !parameters.isEmpty()) {
				for (String key : parameters.keySet()) {
					urlStringBuilder.append("&" + key + "=" + parameters.get(key));
				}
				if (!parameters.containsKey("user_id")) {
					urlStringBuilder.append("&user_id=" + info.getConsumerToken());
				}
			}

			final HttpGet request = new HttpGet(urlStringBuilder.toString());
			request.addHeader("Accept-Encoding", "gzip");

			final HttpClient client = this.app.getHttpClient();

			Authentication.signRequest(info, request);

			final org.apache.http.HttpResponse response = client.execute(request);
			final int statusCode = response.getStatusLine().getStatusCode();
			// Log.d("HTTP method : " + method, "return statusCode : " +
			// statusCode);

			if (statusCode != 200) {
				throw new HttpResponseException(statusCode, "HTTP Error");
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Header contentEncoding = response.getFirstHeader("Content-Encoding");
					if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
						inputStream = new GZIPInputStream(inputStream);
					}

					final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					final XmlPullParser xpp = factory.newPullParser();
					final String rawXml = IOUtils.toString(inputStream);
					xpp.setInput(new StringReader(rawXml));
					int eventType = xpp.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						switch (eventType) {
						case XmlPullParser.START_TAG:
							if ("rsp".equals(xpp.getName())) {
								String status = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "stat");
								if ("fail".equals(status)) {
									ErrorInfo error = VimeoXmlParser.parse(ErrorInfo.class, xpp);
									Log.e(TAG, error.getExplanation());
									Toast.makeText(context, error.getExplanation(), Toast.LENGTH_LONG).show();
									return null;
									// throw new
									// Exception(error.getExplanation());
								}
								return xpp;
							}
							break;
						}

						eventType = xpp.next();
					}
					return xpp;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			// Toast.makeText(context, e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}

		return null;
	}

	public OauthInfo oauth_checkAccessToken() {
		// Log.d(TAG, "oauth_checkAccessToken()");
		final ConsumerInfo consumerInfo = getConsumerInfo();
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("oauth_token", consumerInfo.getConsumerToken());

		try {
			final OauthInfo oauth = VimeoXmlParser.parse(OauthInfo.class, makeRequest("oauth.checkAccessToken", params));
			return oauth;

		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		return null;
	}

	private CollectionItem<Video> parseVideosXml(XmlPullParser xpp) {
		final CollectionItem<Video> videos = new CollectionItem<Video>(Video.class, "videos");
		try {
			videos.initialize(xpp);
		} catch (Exception e) {
			Log.e(TAG, "Unable to parse videos xml", e);
			// Toast.makeText(context, e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}
		return videos;
	}

	private void saveVideo(HttpEntity entity, String url, String fileName) {
		if (isExternalStorageWritable()) {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath();
			path += "/Videos/";
			OutputStream out = null;
			byte[] buffer = new byte[8 * 1024];
			try {
				InputStream input = entity.getContent();
				File file = new File(path);
				file.mkdirs();
				file = new File(path + fileName);
				out = new FileOutputStream(file);
				int bytesRead;
				while ((bytesRead = input.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}

			} catch (Exception e) {
				// What to do here?
			} finally {
				try {
					entity.consumeContent();
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					// Swallow exception
				}
			}
		}
	}

	public void uploadFile(String endpoint, String ticketId, File file, String filename, ProgressListener listener, OnTransferringHandler handler) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException, IOException {
		// final int chunkSize = 512 * 1024; // 512 kB
		// final long pieces = file.length() / chunkSize;
		int chunkId = 0;

		final HttpPost request = new HttpPost(endpoint);

		// BufferedInputStream stream = new BufferedInputStream(new
		// FileInputStream(file));

		// for (chunkId = 0; chunkId < pieces; chunkId++) {
		// byte[] buffer = new byte[chunkSize];

		// stream.skip(chunkId * chunkSize);

		// stream.read(buffer);

		final MultipartEntity entity = new MultipartEntity();
		entity.addPart("chunk_id", new StringBody(String.valueOf(chunkId)));
		entity.addPart("ticket_id", new StringBody(ticketId));
		request.setEntity(new CountingRequestEntity(entity, listener)); // ,
		// chunkId
		// *
		// chunkSize));
		// ByteArrayInputStream arrayStream = new ByteArrayInputStream(buffer);

		Authentication.signRequest(getConsumerInfo(), request);

		entity.addPart("file_data", new FileBody(file));
		// entity.addPart("file_data", new InputStreamBody(arrayStream,
		// filename));

		final HttpClient client = app.getHttpClient();

		handler.onTransferring(request);
		final HttpResponse response = client.execute(request);
		final HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
			responseEntity.consumeContent();
		}
		// }
	}

	public Boolean videos_comments_addComment(String commentText, String videoId, String replyToCommentId) {
		final HashMap<String, String> params = new HashMap<String, String>();
		final ConsumerInfo info = this.getConsumerInfo();
		params.put("oauth_token", info.getConsumerToken());
		try {
			params.put("comment_text", URLEncoder.encode(commentText, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Unable to URL Encode comment", e);
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			return false;
		}

		params.put("video_id", videoId);

		if (replyToCommentId != null && replyToCommentId.length() > 0) {
			params.put("reply_to_comment_id", replyToCommentId);
		}

		return makeRequest("videos.comments.addComment", params) != null;
	}

	public CollectionItem<Comment> videos_comments_getList(String videoId) {
		final HashMap<String, String> params = new HashMap<String, String>();
		final CollectionItem<Comment> comments = new CollectionItem<Comment>(Comment.class, "comments");
		params.put("video_id", videoId);
		try {
			final XmlPullParser xpp = makeRequest("videos.comments.getList", params);
			comments.initialize(xpp);
			// comments = VimeoXmlParser.parse(Comments.class, xpp);
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		return comments;
	}

	public void videos_delete(String videoId) {
		final HashMap<String, String> params = new HashMap<String, String>();
		final ConsumerInfo info = this.getConsumerInfo();
		params.put("oauth_token", info.getConsumerToken());
		params.put("video_id", videoId);
		makeRequest("videos.delete", params);
	}

	public Person people_getInfo(String userId) {
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", userId);
		final Person person = new Person();
		final XmlPullParser xpp = makeRequest("people.getInfo", params);
		try {
			person.initialize(xpp);
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		return person;
	}

	public Video videos_getInfo(String videoId) {
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("video_id", videoId);
		final Video video = new Video();
		final XmlPullParser xpp = makeRequest("videos.getInfo", params);
		try {
			video.initialize(xpp);
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		return video;

	}

	public CollectionItem<Video> videos_getAll(String sortBy, String userId) {
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("full_response", "1");
		if (userId != null && userId.length() > 0) {
			params.put("user_id", userId);
		}

		if (sortBy != null && sortBy.length() > 0) {
			params.put("sort", sortBy);
		}

		return parseVideosXml(makeRequest("videos.getAll", params));
	}

	public CollectionItem<Video> videos_getSubscriptions(String sortBy) {
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("full_response", "1");
		if (sortBy != null) {
			params.put("sort", sortBy);
		}

		return parseVideosXml(makeRequest("videos.getSubscriptions", params));
	}

	public CollectionItem<Video> videos_search(String query, String sortBy) {
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("full_response", "1");
		try {
			params.put("query", URLEncoder.encode(query, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return null;
		}

		if (sortBy != null) {
			params.put("sort", sortBy);
		}

		return parseVideosXml(makeRequest("videos.search", params));
	}

	public Boolean videos_setDescription(String description, String videoId) {
		final HashMap<String, String> params = new HashMap<String, String>();
		final ConsumerInfo info = this.getConsumerInfo();
		params.put("oauth_token", info.getConsumerToken());
		try {
			params.put("description", URLEncoder.encode(description, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return false;
		}
		params.put("video_id", videoId);
		return makeRequest("videos.setDescription", params) != null;
	}

	public Boolean videos_setLike(boolean like, String videoId) {
		final HashMap<String, String> params = new HashMap<String, String>();
		final ConsumerInfo info = this.getConsumerInfo();
		params.put("oauth_token", info.getConsumerToken());
		params.put("like", String.valueOf(like));
		params.put("video_id", videoId);
		return makeRequest("videos.setLike", params) != null;
	}

	public Boolean videos_setTitle(String title, String videoId) {
		final HashMap<String, String> params = new HashMap<String, String>();
		final ConsumerInfo info = this.getConsumerInfo();
		params.put("oauth_token", info.getConsumerToken());
		try {
			params.put("title", URLEncoder.encode(title, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return false;
		}
		params.put("video_id", videoId);
		return makeRequest("videos.setTitle", params) != null;
	}

	public void videos_upload_complete(String ticketId, String fileName) {
		final HashMap<String, String> params = new HashMap<String, String>();
		final ConsumerInfo info = this.getConsumerInfo();
		params.put("oauth_token", info.getConsumerToken());
		params.put("ticket_id", ticketId);
		params.put("filename", fileName);
		makeRequest("videos.upload.complete", params);
	}

	public Quota videos_upload_getQuota() {
		final HashMap<String, String> params = new HashMap<String, String>();
		final ConsumerInfo info = this.getConsumerInfo();
		params.put("oauth_token", info.getConsumerToken());
		Quota quota = null;
		try {
			final XmlPullParser xpp = makeRequest("videos.upload.getQuota", params);
			quota = VimeoXmlParser.parse(Quota.class, xpp);
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		return quota;
	}

	public UploadTicket videos_upload_getTicket() {
		final HashMap<String, String> params = new HashMap<String, String>();
		final ConsumerInfo info = this.getConsumerInfo();
		params.put("oauth_token", info.getConsumerToken());
		UploadTicket ticket = null;
		try {
			ticket = VimeoXmlParser.parse(UploadTicket.class, makeRequest("videos.upload.getTicket", params));
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		return ticket;
	}

	public CollectionItem<Video> videos_getLikes(String sortBy) {
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("full_response", "1");
		if (sortBy != null && sortBy.length() > 0) {
			params.put("sort", sortBy);
		}

		return parseVideosXml(makeRequest("videos.getLikes", params));
	}
}
