package com.makotosan.vimeodroid.common;

import com.makotosan.vimeodroid.R;

import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

public class PromptListener implements android.content.DialogInterface.OnClickListener {
	private String promptReply = null;

	View promptDialogView = null;

	public PromptListener(View inDialogView) {
		this.promptDialogView = inDialogView;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON1) {
			// OK button
			promptReply = getPromptText();
		} else {
			// cancel button
			promptReply = null;
		}
	}

	private String getPromptText() {
		EditText editText = (EditText) promptDialogView.findViewById(R.id.comment_text);
		return editText.getText().toString();
	}

	public String getPromptReply() {
		return this.promptReply;
	}
}
