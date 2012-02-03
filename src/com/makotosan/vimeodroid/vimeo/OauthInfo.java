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

public class OauthInfo implements VimeoObject {
	public static final String TAG = "oauth";
	private Permission permission;
	private String token;

	private User user;

	public Permission getPermission() {
		return permission;
	}

	public String getToken() {
		return token;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String getTag() {
		return OauthInfo.TAG;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();
		String tagName = "";
		this.user = new User();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				tagName = xpp.getName();
				if(User.TAG.equals(tagName)){
					this.user.initialize(xpp);
				}
				break;
			case XmlPullParser.END_TAG:
				if (this.getTag().equals(xpp.getName())) {
					// All done, exit
					return;
				}
				tagName = "";
				break;
			case XmlPullParser.TEXT:
				if ("token".equals(tagName)) {
					this.token=xpp.getText();
					break;
				}
				if ("permission".equals(tagName)) {
					this.permission=Permission.valueOf(xpp.getText());
					break;
				}
				break;
			}

			eventType = xpp.next();
		}
		
	}
}
