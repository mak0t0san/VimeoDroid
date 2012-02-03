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
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class Owner implements VimeoObject {
	public static final String TAG = "owner";
	private String displayName;
	private String id;
	private Boolean isPlus;
	private Boolean isStaff;
	private List<Portrait> portraits;
	private String profileUrl;
	private String realName;
	private String userName;

	private String videosUrl;

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
		return id;
	}

	public Boolean getIsPlus() {
		return isPlus;
	}

	public Boolean getIsStaff() {
		return isStaff;
	}

	public List<Portrait> getPortraits() {
		return portraits;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public String getRealName() {
		return realName;
	}

	@Override
	public String getTag() {
		return Owner.TAG;
	}

	public String getUserName() {
		return userName;
	}

	public String getVideosUrl() {
		return videosUrl;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		this.portraits = new ArrayList<Portrait>();

		int eventType = xpp.getEventType();
		String tagName = xpp.getName();

		if (eventType != XmlPullParser.START_TAG || !tagName.equals(this.getTag())) {
			// Must be the starting tag
			return;
		}

		try {
			// Enumerate through the tags.
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tagName = xpp.getName();
					if (tagName.equals(this.getTag())) {
						this.id = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "id");
						this.displayName = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "display_name");
						this.isPlus = "1".equals(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "is_plus"));
						this.isStaff = "1".equals(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "is_staff"));
						this.realName = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "realname");
						this.userName = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "username");
						this.profileUrl = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "profileurl");
						this.videosUrl = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "videosurl");
						break;
					}
					if (Portrait.TAG.equals(tagName)) {
						Portrait portrait = new Portrait();
						portrait.initialize(xpp);
						this.portraits.add(portrait);
						break;
					}
					break;

				case XmlPullParser.END_TAG:
					tagName = "";
					if (xpp.getName().equals(this.getTag())) {
						// All done, exit
						return;
					}
					break;
				}
				eventType = xpp.next();
			}
		} catch (Exception ex) {
			Log.e(Video.TAG, ex.getMessage(), ex);
		}

	}
}
