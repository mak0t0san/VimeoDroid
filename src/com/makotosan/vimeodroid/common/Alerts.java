package com.makotosan.vimeodroid.common;

import com.makotosan.vimeodroid.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class Alerts {
	public static String prompt(String message, Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.comment_enter_layout, null);

		Builder builder = new Builder(context);
		builder.setTitle("Prompt");
		builder.setView(view);

		// add buttons and listener
		PromptListener listener = new PromptListener(view);
		builder.setPositiveButton(R.string.ok, listener);
		builder.setNegativeButton(R.string.cancel, listener);
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
		return listener.getPromptReply();
	}
}
