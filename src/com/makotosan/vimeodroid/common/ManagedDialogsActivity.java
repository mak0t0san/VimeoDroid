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
