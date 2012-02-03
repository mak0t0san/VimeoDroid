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
