package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

// Author is the same thing as owner, except vimeo uses two different tag names
public class Author extends Owner {
	public static final String TAG = "author";

	@Override
	public String getTag() {
		return Author.TAG;
	}

	@Override
	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		super.initialize(xpp);
	}
}
