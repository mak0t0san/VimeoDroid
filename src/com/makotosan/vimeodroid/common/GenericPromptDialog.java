package com.makotosan.vimeodroid.common;

import com.makotosan.vimeodroid.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class GenericPromptDialog extends ManagedActivityDialog {
	private String mPromptMessage = null;
	private View promptView = null;
	String promptValue = null;
	private Context context = null;

	public GenericPromptDialog(ManagedDialogsActivity activity, int dialogId, String promptMessage) {
		super(activity, dialogId);
		this.mPromptMessage = promptMessage;
		this.context = activity;
	}

	@Override
	public Dialog create() {
		final LayoutInflater inflater = LayoutInflater.from(this.context);
		this.promptView = inflater.inflate(R.layout.comment_enter_layout, null);
		final AlertDialog.Builder builder = new Builder(this.context);
		builder.setTitle(this.mPromptMessage);
		builder.setView(this.promptView);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, this);
		final AlertDialog ad = builder.create();
		return ad;
	}

	@Override
	public void onClickHook(int buttonId) {
		if (buttonId == DialogInterface.BUTTON1) {
			// OK Button
			this.promptValue = getEnteredText();
		}
	}

	public String getReplyText() {
		return this.promptValue;
	}

	private String getEnteredText() {
		final EditText et = (EditText) this.promptView.findViewById(R.id.comment_text);
		final String enteredText = et.getText().toString();
		return enteredText;
	}

	@Override
	public void prepare(Dialog dialog) {
		// Nothing here
	}

}
