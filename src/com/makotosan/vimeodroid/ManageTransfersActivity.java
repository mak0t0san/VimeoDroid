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

package com.makotosan.vimeodroid;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.methods.AbortableHttpRequest;

import com.makotosan.vimeodroid.common.StaticInstances;
import com.makotosan.vimeodroid.common.Transfer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ManageTransfersActivity extends ListActivity {

	private final Handler handler = new Handler();
	private final DecimalFormat numericFormatter = new DecimalFormat("#,###.#");

	private final Runnable updateList = new Runnable() {
		// After we've received our data and parsed it, bind the list adapter
		@Override
		public void run() {
			final ArrayList<Transfer> transfers = Collections.list(StaticInstances.transfers.elements());
			if (transfers != null) {
				setListAdapter(new TransferListAdapter(getApplicationContext(), R.layout.transferprogress, transfers));
			}
		}
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if (item.getItemId() == R.id.contextmenu_cancel) {
			Transfer transfer = getModel(info.position);
			AbortableHttpRequest request = transfer.getAbortableRequest();
			if (request != null) {
				request.abort();
			}
		}

		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transfercontextmenu, menu);
	}

	private Transfer getModel(int position) {
		return ((TransferListAdapter) getListAdapter()).getItem(position);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.managetransferslayout);

		registerForContextMenu(getListView());
		// Initialize timer
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				handler.post(updateList);
				if (StaticInstances.transfers.isEmpty()) {
					this.cancel();
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(intent);
				} 
			}
		};

		timer.scheduleAtFixedRate(task, 0, 3000);
	}

	private class TransferListAdapter extends ArrayAdapter<Transfer> {
		public TransferListAdapter(Context context, int textViewResourceId, List<Transfer> items) {
			super(context, R.layout.transferprogress, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ViewWrapper wrapper = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.transferprogress, parent, false);

				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			} else {
				wrapper = (ViewWrapper) row.getTag();
			}

			final Transfer transfer = getModel(position);
			wrapper.getIconImage().setImageBitmap(transfer.getIcon());
			ProgressBar progressbar = wrapper.getTransferProgress();
			progressbar.setMax((int) transfer.getBytesTotal());
			progressbar.setProgress((int) transfer.getBytesTransferred());

			wrapper.getTransferredText().setText(
					transfer.getFileName() + " - " + numericFormatter.format(transfer.getBytesTransferred() / 1024) + " kB / "
							+ numericFormatter.format(transfer.getBytesTotal() / 1024) + " kB");
			return row;
		}
	}

	private class ViewWrapper {
		View base;
		ImageView iconImage = null;
		ProgressBar transferProgress = null;
		TextView transferredText = null;

		public ViewWrapper(View base) {
			this.base = base;
		}

		public ImageView getIconImage() {
			if (this.iconImage == null) {
				this.iconImage = (ImageView) base.findViewById(R.id.transferprogressIcon);
			}

			return this.iconImage;
		}

		public ProgressBar getTransferProgress() {
			if (this.transferProgress == null) {
				this.transferProgress = (ProgressBar) base.findViewById(R.id.transferprogressBar);
			}

			return this.transferProgress;
		}

		public TextView getTransferredText() {
			if (this.transferredText == null) {
				this.transferredText = (TextView) base.findViewById(R.id.transferprogressText);
			}

			return this.transferredText;
		}
	}

}
