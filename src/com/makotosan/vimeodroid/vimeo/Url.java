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
