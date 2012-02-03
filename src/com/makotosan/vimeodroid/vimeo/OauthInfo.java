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
