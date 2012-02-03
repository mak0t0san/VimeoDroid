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

package com.makotosan.vimeodroid;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpRequest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import com.makotosan.vimeodroid.common.VimeoUrls;
import com.makotosan.vimeodroid.vimeo.Methods;
import com.makotosan.vimeodroid.vimeo.OauthInfo;
import com.makotosan.vimeodroid.vimeo.User;

public class Authentication {

	public static final String CONSUMER_SECRET = "REPLACE ME";
	public static final String CONSUMER_KEY = "REPLACE ME";
	public static final Uri OAUTH_CALLBACK_URL = Uri.parse("vimeodroid://oauth.done");
	public static final String MY_PREFS = "VIMEO_DROID_PREFS";
	public static final String CONSUMER_TOKEN_PREF = "CONSUMER_TOKEN_PREF";
	public static final String CONSUMER_TOKEN_SECRET_PREF = "CONSUMER_TOKEN_SECRET_PREF";
	public static final String TAG = "Authentication";
	private static User user = null;

	private static final CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
	private static final CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider(VimeoUrls.REQUEST_TOKEN, VimeoUrls.ACCESS_TOKEN,
			VimeoUrls.AUTH);

	public Authentication() {
	}

	public static User getUser(Context context, Application app) {
		if (user == null) {
			final Methods methods = new Methods(context, app);
			OauthInfo info = methods.oauth_checkAccessToken();
			if (info != null) {
				user = info.getUser();
			}
		}

		return user;
	}

	public static boolean saveAuthentication(Context context, String verifier) {
		final ConsumerInfo info = new ConsumerInfo();

		try {
			provider.retrieveAccessToken(consumer, verifier);

			info.setConsumerToken(consumer.getToken());
			info.setConsumerTokenSecret(consumer.getTokenSecret());

			final SharedPreferences myPrefs = context.getSharedPreferences(Authentication.MY_PREFS, Context.MODE_PRIVATE);
			final SharedPreferences.Editor editor = myPrefs.edit();

			editor.putString(Authentication.CONSUMER_TOKEN_PREF, info.getConsumerToken());
			editor.putString(Authentication.CONSUMER_TOKEN_SECRET_PREF, info.getConsumerTokenSecret());
			editor.commit();
		} catch (Exception e) {
			// Log.e(TAG, "Unable to save authentication", e);
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	public static boolean isUserLoggedIn(Context context, Application app) {
		// Check if we have saved credentials
		final SharedPreferences myPrefs = context.getSharedPreferences(Authentication.MY_PREFS, Context.MODE_PRIVATE);
		final String authToken = myPrefs.getString(Authentication.CONSUMER_TOKEN_PREF, null);

		// If not, return false
		if (authToken == null || authToken.length() == 0) {
			return false;
		}

		if (getUser(context, app) == null) {
			return false;
		}

		return true;
	}

	public static void logout(Context context) {
		final SharedPreferences myPrefs = context.getSharedPreferences(Authentication.MY_PREFS, Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = myPrefs.edit();

		editor.remove(Authentication.CONSUMER_TOKEN_PREF);
		editor.remove(Authentication.CONSUMER_TOKEN_SECRET_PREF);
		editor.commit();
	}

	public static String authorizeUser() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException,
			OAuthCommunicationException {
		String url = null;
		url = provider.retrieveRequestToken(consumer, OAUTH_CALLBACK_URL.toString()); // OAuth.OUT_OF_BAND);

		if (url != null) {
			url += "&permission=delete";
		}

		return url;
	}

	public static void signRequest(ConsumerInfo info, HttpRequest request) throws OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException {
		consumer.setTokenWithSecret(info.getConsumerToken(), info.getConsumerTokenSecret());
		consumer.sign(request);
	}
}
