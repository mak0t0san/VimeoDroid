package com.makotosan.vimeodroid.common;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public abstract class ManagedActivityDialog implements IDialogProtocol, OnClickListener {
	private ManagedDialogsActivity mActivity;
	private int mDialogId;

	public ManagedActivityDialog(ManagedDialogsActivity a, int dialogId) {
		this.mActivity = a;
		this.mDialogId = dialogId;
	}

	public int getDialogId() {
		return mDialogId;
	}

	public void show() {
		this.mActivity.showDialog(mDialogId);
	}

	public void onClick(DialogInterface v, int buttonId) {
		onClickHook(buttonId);
		this.mActivity.dialogFinished(this, buttonId);
	}
}
