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
