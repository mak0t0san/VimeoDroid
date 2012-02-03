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

public class Quota implements VimeoObject {
	private int hdQuota;
	private String id;
	private Boolean isPlus;
	private int sdQuota;
	private int uploadSpaceFree;
	private int uploadSpaceMax;
	private int uploadSpaceResets;
	private int uploadSpaceUsed;

	public static final String TAG = "user";

	public int getHdQuota() {
		return hdQuota;
	}

	public String getId() {
		return id;
	}

	public Boolean getIsPlus() {
		return isPlus;
	}

	public int getSdQuota() {
		return sdQuota;
	}

	public int getUploadSpaceFree() {
		return uploadSpaceFree;
	}

	public int getUploadSpaceMax() {
		return uploadSpaceMax;
	}

	public int getUploadSpaceResets() {
		return uploadSpaceResets;
	}

	public int getUploadSpaceUsed() {
		return uploadSpaceUsed;
	}

	@Override
	public String getTag() {
		return Quota.TAG;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();

		if (eventType != XmlPullParser.START_TAG || !xpp.getName().equals(this.getTag())) {
			// Must be the starting tag <video>
			return;
		}
		String tagName = xpp.getName();
		// Enumerate through the tags.
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				tagName = xpp.getName();
				if (this.getTag().equals(tagName)) {
					this.id = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "id");
					this.isPlus = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "is_plus") == "1";
					break;
				}
				if ("upload_space".equals(tagName)) {
					this.uploadSpaceFree = Integer.parseInt(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "free"));
					this.uploadSpaceMax = Integer.parseInt(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "max"));
					this.uploadSpaceResets = Integer.parseInt(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "resets"));
					this.uploadSpaceUsed = Integer.parseInt(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "used"));
					break;
				}
				break;
			case XmlPullParser.TEXT:
				if ("hd_quota".equals(tagName)) {
					this.hdQuota = Integer.parseInt(xpp.getText());
					break;
				}
				if ("sd_quota".equals(tagName)) {
					this.sdQuota = Integer.parseInt(xpp.getText());
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

	}
}
