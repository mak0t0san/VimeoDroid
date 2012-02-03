package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ErrorInfo implements VimeoObject {
	public static final String TAG = "err";
	
	private int code;
	private String explanation;
	private String message;

	public int getCode() {
		return code;
	}

	public String getExplanation() {
		return explanation;
	}

	public String getMessage(){
		return message;
	}
	
	@Override
	public String getTag() {
		return ErrorInfo.TAG;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (xpp.getName().equals(this.getTag())) {
					this.code = Integer.parseInt(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "code"));
					this.explanation = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "expl");
					this.message = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "msg");
					break;
				}
				break;
			case XmlPullParser.END_TAG:
				if (xpp.getName().equals(this.getTag())) {
					// All done, exit
					return;
				}
				break;
			}

			eventType = xpp.next();
		}

	}

}
