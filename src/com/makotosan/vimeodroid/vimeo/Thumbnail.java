package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class Thumbnail implements VimeoObject {
	public static final String TAG = "thumbnail";
	private int height;
	private String url;
	private int width;

	public int getHeight() {
		return height;
	}

	@Override
	public String getTag() {
		return Thumbnail.TAG;
	}

	public String getUrl() {
		return url;
	}

	public int getWidth() {
		return width;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();
		String tagName = xpp.getName();

		if (eventType != XmlPullParser.START_TAG || !tagName.equals(this.getTag())) {
			// Must be the starting tag
			return;
		}

		try {
			// Enumerate through the tags.
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tagName = xpp.getName();
					if (tagName.equals(this.getTag())) {
						this.height = Integer.parseInt(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "height"));
						this.width = Integer.parseInt(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "width"));
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
					this.url = xpp.getText();
					break;
				}
				eventType = xpp.next();
			}
		} catch (Exception ex) {
			Log.e(Video.TAG, ex.getMessage(), ex);
		}
	}

}
