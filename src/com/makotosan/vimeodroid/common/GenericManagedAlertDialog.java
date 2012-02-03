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
