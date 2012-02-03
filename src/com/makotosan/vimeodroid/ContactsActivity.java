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

import java.util.List;

import com.makotosan.vimeodroid.common.IRefreshable;
import com.makotosan.vimeodroid.common.ImageDownloader;
import com.makotosan.vimeodroid.vimeo.CollectionItem;
import com.makotosan.vimeodroid.vimeo.Contact;
import com.makotosan.vimeodroid.vimeo.Methods;
import com.makotosan.vimeodroid.vimeo.Portrait;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsActivity extends ListActivity implements IRefreshable {
	// Fields
	private CollectionItem<Contact> contacts = null;
	private final Handler handler = new Handler();
	private Methods methods = null;

	final Runnable updateList = new Runnable() {
		// After we've received our data and parsed it, bind the list adapter
		@Override
		public void run() {
			setProgressBarIndeterminateVisibility(false);
			if (contacts.getItems() != null) {
				setListAdapter(new ContactListAdapter(getApplicationContext(), R.layout.contactrow, contacts.getItems()));
			}
		}
	};

	@Override
	public Object onRetainNonConfigurationInstance() {
		// Return the cached list of comments so that we don't have to request
		// them for a configuration change (ie screen rotation)
		return contacts;
	}

	@Override
	public void refresh() {
		try {
			Thread t = new Thread() {
				@Override
				public void run() {
					contacts = methods.contacts_getAll();
					handler.post(updateList);
				};
			};

			setProgressBarIndeterminateVisibility(true);
			t.start();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private Contact getModel(int position) {
		return ((ContactListAdapter) getListAdapter()).getItem(position);
	}

	@SuppressWarnings("unchecked")
	private void restoreMe() {
		contacts = null;
		if (getLastNonConfigurationInstance() != null) {
			contacts = (CollectionItem<Contact>) getLastNonConfigurationInstance();
			setListAdapter(new ContactListAdapter(getApplicationContext(), R.layout.contactrow, contacts.getItems()));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.contacts);

		methods = new Methods(this, getApplication());
		restoreMe();
		if (contacts == null) {
			refresh();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Contact contact = getModel(position);
		Intent intent = new Intent(this, VideosActivity.class);
		intent.putExtra("userId", contact.getId());
		intent.putExtra("displayName", contact.getDisplayName());
		intent.putExtra("method", "getAll");

		startActivity(intent);
	}

	// ContactListAdapter class
	private class ContactListAdapter extends ArrayAdapter<Contact> {
		private final ImageDownloader imageLoader = new ImageDownloader((ApplicationEx) getApplication());

		public ContactListAdapter(Context context, int textViewResourceId, List<Contact> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ViewWrapper wrapper = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.contactrow, parent, false);

				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			} else {
				wrapper = (ViewWrapper) row.getTag();
			}

			final Contact contact = getModel(position);
			wrapper.getContactName().setText(contact.getDisplayName());
			List<Portrait> portraits = contact.getPortraits();
			if (portraits != null) {
				imageLoader.download(portraits.get(1).getUrl(), wrapper.getContactImage());
			}

			return row;
		}

		private class ViewWrapper {
			View base;
			ImageView contactImage = null;
			TextView contactName = null;

			public ViewWrapper(View base) {
				this.base = base;
			}

			public ImageView getContactImage() {
				if (contactImage == null) {
					contactImage = (ImageView) base.findViewById(R.id.contact_image);
				}

				return contactImage;
			}

			public TextView getContactName() {
				if (contactName == null) {
					contactName = (TextView) base.findViewById(R.id.contact_name);
				}

				return contactName;
			}
		}
	}

}
