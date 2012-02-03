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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Comment implements VimeoObject {
	public static final String TAG = "comment";

	private Author author;
	private Date dateCreate;
	private int id;
	private String permalink;
	private int replyToCommentId;
	private String text;

	public Author getAuthor() {
		return author;
	}

	public Date getDateCreate() {
		return dateCreate;
	}

	public int getId() {
		return id;
	}

	public String getPermalink() {
		return permalink;
	}

	public int getReplyToCommentId() {
		return replyToCommentId;
	}

	@Override
	public String getTag() {
		return Comment.TAG;
	}

	public String getText() {
		return text;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();
		String tagName = xpp.getName();

		if (eventType != XmlPullParser.START_TAG || !tagName.equals(this.getTag())) {
			// Must be the starting tag <videos>
			return;
		}

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				tagName = xpp.getName();
				if (tagName.equals(this.getTag())) {
					this.id = Integer.parseInt(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "id"));
					this.replyToCommentId = Integer.parseInt(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "reply_to_comment_id"));
					this.permalink = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "permalink");
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						this.dateCreate = formatter.parse(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "datecreate"));
					} catch (ParseException e) {
						this.dateCreate = new Date();
					}
					break;
				}
				if (tagName.equals(Author.TAG)) {
					this.author = new Author();
					this.author.initialize(xpp);
				}
				break;
			case XmlPullParser.TEXT:
				if ("text".equals(tagName)) {
					this.text = xpp.getText();
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
	}
}
