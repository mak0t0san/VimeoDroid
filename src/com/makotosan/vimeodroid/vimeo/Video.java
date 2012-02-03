package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class Video implements VimeoObject {
	// private String caption;
	public static String TAG = "video";

	private String description;

	private int duration;
	private int height;
	private String id;
	private Boolean isHd;

	private Boolean isLike;
	private Boolean isTranscoding;
	private int numberOfComments;
	private int numberOfLikes;
	private int numberOfPlays;
	private Owner owner;
	private String privacy;
	private List<Thumbnail> thumbnails;
	private String title;
	private Date uploadDate;
	private String urlVideo;
	private int width;
	private List<Url> urls;

	public Video() {
	}

	public String getDescription() {
		return description;
	}

	public int getDuration() {
		return duration;
	}

	public int getHeight() {
		return height;
	}

	public String getId() {
		return id;
	}

	public Boolean getIsHd() {
		return isHd;
	}

	public Boolean getIsLike() {
		return isLike;
	}

	public Boolean getIsTranscoding() {
		return isTranscoding;
	}

	public int getNumberOfComments() {
		return numberOfComments;
	}

	public int getNumberOfLikes() {
		return numberOfLikes;
	}

	public int getNumberOfPlays() {
		return numberOfPlays;
	}

	public Owner getOwner() {
		if (owner == null) {
			owner = new Owner();
		}

		return owner;
	}

	public String getPrivacy() {
		return privacy;
	}

	@Override
	public String getTag() {
		return Video.TAG;
	}

	public List<Thumbnail> getThumbnails() {
		return thumbnails;
	}

	public String getTitle() {
		return title;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public String getUrlVideo() {
		for(Url url: this.urls){
			if("video".equals(url.getType())){
				return url.getUrl();
			}
		}
		
		return "";
	}

	public int getWidth() {
		return width;
	}

	public List<Url> getUrls() {
		return this.urls;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException,
			IOException {
		int eventType = xpp.getEventType();
		String tagName = xpp.getName();

		// if (eventType != XmlPullParser.START_TAG ||
		// !tagName.equals(this.getTag())) {
		// Must be the starting tag <video>
		// return;
		// }

		// Initialize other classes
		this.thumbnails = new ArrayList<Thumbnail>();
		this.urls = new ArrayList<Url>();
		this.owner = new Owner();
		try {
			// Enumerate through the tags.
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tagName = xpp.getName();
					if (this.getTag().equals(tagName)) {
						this.id = xpp.getAttributeValue(
								XmlPullParser.NO_NAMESPACE, "id");
						this.isHd = "1".equals(xpp.getAttributeValue(
								XmlPullParser.NO_NAMESPACE, "is_hd"));
						this.isLike = "1".equals(xpp.getAttributeValue(
								XmlPullParser.NO_NAMESPACE, "is_like"));
						break;
					}

					if (Owner.TAG.equals(tagName)) {
						this.owner.initialize(xpp);
						break;
					}

					if (Thumbnail.TAG.equals(tagName)) {
						Thumbnail thumbnail = new Thumbnail();
						thumbnail.initialize(xpp);
						this.thumbnails.add(thumbnail);
						break;
					}

					if ("url".equals(tagName)) {
						Url url = new Url();
						url.initialize(xpp);
						this.urls.add(url);
						break;
					}

					break;

				case XmlPullParser.END_TAG:
					tagName = "";
					if (xpp.getName().equals(this.getTag())) {
						// All done, exit
						return;
					}
					break;
				case XmlPullParser.TEXT:
					if ("title".equals(tagName)) {
						this.title = xpp.getText().trim();
						break;
					}
					if ("description".equals(tagName)) {
						this.description = xpp.getText().trim();
						break;
					}
					if ("duration".equals(tagName)) {
						this.duration = Integer.parseInt(xpp.getText().trim());
						break;
					}
					if ("number_of_plays".equals(tagName)) {
						this.numberOfPlays = Integer.parseInt(xpp.getText()
								.trim());
						break;
					}
					if ("number_of_comments".equals(tagName)) {
						this.numberOfComments = Integer.parseInt(xpp.getText()
								.trim());
						break;
					}
					if ("number_of_likes".equals(tagName)) {
						this.numberOfLikes = Integer.parseInt(xpp.getText()
								.trim());
						break;
					}
					if ("upload_date".equals(tagName)) {
						SimpleDateFormat formatter = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						try {
							this.uploadDate = formatter.parse(xpp.getText()
									.trim());
						} catch (ParseException e) {
							this.uploadDate = new Date();
						}

						break;
					}
					//if ("url".equals(tagName)) {
					//	this.urlVideo = xpp.getText().trim();
					//	break;
					//}

					break;
				}
				eventType = xpp.next();
			}
		} catch (Exception ex) {
			Log.e(Video.TAG, ex.getMessage(), ex);
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIsLike(Boolean isLike) {
		this.isLike = isLike;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrlVideo(String urlVideo) {
		this.urlVideo = urlVideo;
	}
}
