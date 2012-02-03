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

public class Person implements VimeoObject {

	public static final String TAG = "person";
	private String bio;
	private String displayName;
	private String id;
	private boolean isContact;
	private boolean isPlus;
	private boolean isStaff;
	private boolean isSubscribedTo;
	private String location;
	private int numberOfContacts;
	private int numberOfLikes;
	private int numberOfVideos;
	private int numberOfVideosAppearsIn;
	private int numberOfUploads;
	private List<Portrait> portraits;
	private int profileUrl;
	private String url;
	private String username;
	private int videosUrl;

	public int getNumberOfUploads() {
		return numberOfUploads;
	}

	public String getBio() {
		return bio;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
		return id;
	}

	public boolean getIsContact() {
		return isContact;
	}

	public boolean getIsPlus() {
		return isPlus;
	}

	public boolean getIsStaff() {
		return isStaff;
	}

	public boolean getIsSubscribedTo() {
		return isSubscribedTo;
	}

	public String getLocation() {
		return location;
	}

	public int getNumberOfContacts() {
		return numberOfContacts;
	}

	public int getNumberOfLikes() {
		return numberOfLikes;
	}

	public int getNumberOfVideos() {
		return numberOfVideos;
	}

	public int getNumberOfVideosAppearsIn() {
		return numberOfVideosAppearsIn;
	}

	public List<Portrait> getPortraits() {
		return portraits;
	}

	public int getProfileUrl() {
		return profileUrl;
	}

	@Override
	public String getTag() {
		return Person.TAG;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public int getVideosUrl() {
		return videosUrl;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();
		String tagName = xpp.getName();

		this.portraits = new ArrayList<Portrait>();
		try {
			// Enumerate through the tags.
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tagName = xpp.getName();
					if (tagName.equals(this.getTag())) {
						this.id = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "id");
						this.isContact = "1".equals(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "is_contact"));
						this.isPlus = "1".equals(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "is_plus"));
						this.isStaff = "1".equals(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "is_staff"));
						this.isSubscribedTo = "1".equals(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "is_subscribed_to"));
						break;
					}

					if (Portrait.TAG.equals(tagName)) {
						Portrait portrait = new Portrait();
						portrait.initialize(xpp);
						this.portraits.add(portrait);
						break;
					}

					break;
				case XmlPullParser.TEXT:
					if ("username".equals(tagName)) {
						this.username = xpp.getText();
						break;
					}

					if ("display_name".equals(tagName)) {
						this.displayName = xpp.getText();
						break;
					}

					if ("number_of_videos".equals(tagName)) {
						this.numberOfVideos = Integer.parseInt(xpp.getText());
						break;
					}

					if ("number_of_contacts".equals(tagName)) {
						this.numberOfContacts = Integer.parseInt(xpp.getText());
						break;
					}

					if ("number_of_uploads".equals(tagName)) {
						this.numberOfUploads = Integer.parseInt(xpp.getText());
						break;
					}

					if ("number_of_likes".equals(tagName)) {
						this.numberOfLikes = Integer.parseInt(xpp.getText());
						break;
					}

					if ("bio".equals(tagName)) {
						this.bio = xpp.getText();
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
