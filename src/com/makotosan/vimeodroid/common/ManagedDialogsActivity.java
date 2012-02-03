package com.makotosan.vimeodroid.common;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

public class ManagedDialogsActivity extends Activity implements IDialogFinishedCallBack {
	private DialogRegistry dr = new DialogRegistry();

	@Override
	public void dialogFinished(ManagedActivityDialog dialog, int buttonId) {
		// derived classes should implement this
	}

	public void registerDialog(IDialogProtocol dialog) {
		this.dr.registerDialog(dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.registerDialogs();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return this.dr.create(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		this.dr.prepare(dialog, id);
	}

	protected void registerDialogs() {
		// derived classes should implement this
	}

}
