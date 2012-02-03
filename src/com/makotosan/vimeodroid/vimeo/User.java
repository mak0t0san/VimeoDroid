package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class User implements VimeoObject {
	private String displayName;
	private String id;
	private String username;
	public static final String TAG = "user";

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public String getTag() {
		return User.TAG;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				tagName = xpp.getName();
				if (this.getTag().equals(tagName)) {
					this.displayName = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "display_name");
					this.id = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "id");
					this.username = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "username");
				}
				break;
			case XmlPullParser.END_TAG:
				if (this.getTag().equals(xpp.getName())) {
					// All done, exit
					return;
				}
				tagName = "";
				break;
			}

			eventType = xpp.next();
		}

	}
}