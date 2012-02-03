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
