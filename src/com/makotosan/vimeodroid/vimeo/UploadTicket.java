package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class UploadTicket implements VimeoObject {
	private String id;
	private String endPoint;

	public static final String TAG = "ticket";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public String getTag() {
		return UploadTicket.TAG;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();

		if (eventType != XmlPullParser.START_TAG || !xpp.getName().equals(UploadTicket.TAG)) {
			// Must be the starting tag <video>
			return;
		}

		// Enumerate through the tags.
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (xpp.getName().equals(UploadTicket.TAG)) {
					this.id = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "id");
					this.endPoint = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "endpoint");
					break;
				}
				break;

			case XmlPullParser.END_TAG:
				if (xpp.getName().equals(UploadTicket.TAG)) {
					// All done, exit
					return;
				}
				break;
			}

			eventType = xpp.next();
		}
	}

}
