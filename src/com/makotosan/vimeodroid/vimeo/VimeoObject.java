package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public interface VimeoObject {
	public String getTag();

	public void initialize(XmlPullParser xpp) throws XmlPullParserException, IOException;

	// public void initialize(JSONObject jsonObject) throws JSONException, IOException;
}
