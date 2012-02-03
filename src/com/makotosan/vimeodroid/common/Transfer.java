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
