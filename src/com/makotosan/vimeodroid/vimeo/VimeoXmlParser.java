package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class VimeoXmlParser {
	public static <T extends VimeoObject> T parse(Class<T> c, XmlPullParser xpp) throws IllegalAccessException, InstantiationException, XmlPullParserException, IOException {
		T vimeoObject = c.newInstance();

		int eventType = xpp.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				tagName = xpp.getName();
				if (tagName.equals(vimeoObject.getTag())) {
					vimeoObject.initialize(xpp);
				}
				break;
			case XmlPullParser.END_TAG:
				if (xpp.getName().equals(vimeoObject.getTag())) {
					// If we hit the end tag, then we should be all done by this point.
					return vimeoObject;					
				}
				tagName = "";
				break;
			}
			eventType = xpp.next();
		}

		return vimeoObject;
	}
}
