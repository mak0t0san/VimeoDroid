package com.makotosan.vimeodroid.vimeo;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class VimeoJsonParser {
	public static <T extends VimeoObject> T parse(Class<T> c, String json) throws IllegalAccessException, InstantiationException, IOException, JSONException {
		T vimeoObject = c.newInstance();
		JSONObject jsonObject = (JSONObject) new JSONTokener(json).nextValue();

		
		return vimeoObject;
	}
}
