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

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public abstract class ManagedActivityDialog implements IDialogProtocol, OnClickListener {
	private ManagedDialogsActivity mActivity;
	private int mDialogId;

	public ManagedActivityDialog(ManagedDialogsActivity a, int dialogId) {
		this.mActivity = a;
		this.mDialogId = dialogId;
	}

	@Override
	public int getDialogId() {
		return mDialogId;
	}

	@Override
	public void show() {
		this.mActivity.showDialog(mDialogId);
	}

	@Override
	public void onClick(DialogInterface v, int buttonId) {
		onClickHook(buttonId);
		this.mActivity.dialogFinished(this, buttonId);
	}
}
