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

import org.apache.http.client.methods.AbortableHttpRequest;

import android.graphics.Bitmap;

public class Transfer {
	private AbortableHttpRequest abortableRequest;
	private long bytesTotal;
	private long bytesTransferred;
	private String fileName;
	private Bitmap icon;
	private int id;
	private TransferType type;
	
	public AbortableHttpRequest getAbortableRequest() {
		return abortableRequest;
	}
	public long getBytesTotal() {
		return bytesTotal;
	}
	public long getBytesTransferred() {
		return bytesTransferred;
	}
	public String getFileName() {
		return fileName;
	}
	public Bitmap getIcon() {
		return icon;
	}
	public int getId() {
		return id;
	}
	public TransferType getType() {
		return type;
	}
	public void setAbortableRequest(AbortableHttpRequest abortableRequest) {
		this.abortableRequest = abortableRequest;
	}
	public void setBytesTotal(long bytesTotal) {
		this.bytesTotal = bytesTotal;
	}
	public void setBytesTransferred(long bytesTransferred) {
		this.bytesTransferred = bytesTransferred;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setType(TransferType type) {
		this.type = type;
	}
}
