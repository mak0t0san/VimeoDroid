package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Url implements VimeoObject {
	private String type;
	private String url;
	public static final String TAG = "url";

	public String getType() {
		return this.type;
	}

	public String getUrl() {
		return this.url;
	}

	@Override
	public String getTag() {
		return Url.TAG;
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
					// this.url = xpp.getText();
					this.type = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type");
				}
				break;
			case XmlPullParser.TEXT:
				if(this.getTag().equals(tagName)){
					this.url = xpp.getText();
				}
				break;
			case XmlPullParser.END_TAG:
				if (this.getTag().equals(xpp.getName())) {
					return;
				}
				tagName = "";
				break;
			}

			eventType = xpp.next();
		}

	}
}
