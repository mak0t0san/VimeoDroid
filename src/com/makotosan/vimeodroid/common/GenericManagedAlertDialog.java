package com.makotosan.vimeodroid.common;

import com.makotosan.vimeodroid.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;

public class GenericManagedAlertDialog extends ManagedActivityDialog {
	private String alertMessage = null;
	private Context context = null;

	public GenericManagedAlertDialog(ManagedDialogsActivity activity, int dialogId, String initialMessage) {
		super(activity, dialogId);
		this.alertMessage = initialMessage;
		context = activity;
	}

	@Override
	public Dialog create() {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("Alert");
		builder.setMessage(alertMessage);
		builder.setPositiveButton(R.string.ok, this);
		AlertDialog ad = builder.create();
		return ad;
	}

	@Override
	public void onClickHook(int buttonId) {
		// nothing to do
	}

	@Override
	public void prepare(Dialog dialog) {
		AlertDialog ad = (AlertDialog) dialog;
		ad.setMessage(alertMessage);
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

}
