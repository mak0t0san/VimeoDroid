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
