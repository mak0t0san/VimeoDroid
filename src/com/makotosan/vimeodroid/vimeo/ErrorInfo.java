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
