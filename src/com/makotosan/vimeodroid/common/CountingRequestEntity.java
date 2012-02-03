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

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class CountingRequestEntity implements HttpEntity {
	private final HttpEntity entity;
	private final ProgressListener listener;
	private long startingAmount;
	private long totalSize;

	public CountingRequestEntity(final HttpEntity entity, final ProgressListener listener) {
		this(entity, listener, 0);
	}

	public CountingRequestEntity(final HttpEntity entity, final ProgressListener listener, long startingAmount) {
		super();
		this.entity = entity;
		this.listener = listener;
		this.startingAmount = startingAmount;
	}

	@Override
	public void consumeContent() throws IOException {
		this.entity.consumeContent();
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return new CountingInputStream(this.entity.getContent(), this.listener, this.startingAmount);
	}

	@Override
	public Header getContentEncoding() {
		return this.entity.getContentEncoding();
	}

	@Override
	public long getContentLength() {
		return this.entity.getContentLength();
	}

	@Override
	public Header getContentType() {
		return this.entity.getContentType();
	}

	public long getTotalSize() {
		return totalSize;
	}

	@Override
	public boolean isChunked() {
		return this.entity.isChunked();
	}

	@Override
	public boolean isRepeatable() {
		return this.entity.isRepeatable();
	}

	@Override
	public boolean isStreaming() {
		return this.entity.isStreaming();
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		this.entity.writeTo(new CountingOutputStream(outstream, this.listener, startingAmount));
	}

	public static class CountingInputStream extends FilterInputStream {
		private final ProgressListener listener;
		private long transferred = 0;

		protected CountingInputStream(InputStream in, final ProgressListener listener, long transferred) {
			super(in);
			this.listener = listener;
			this.transferred = transferred;
		}

		@Override
		public int read(byte[] buffer) throws IOException {
			int bytesRead = super.read(buffer);
			this.transferred += bytesRead;
			this.listener.transferred(this.transferred);
			return bytesRead;
		}
	}

	public static class CountingOutputStream extends FilterOutputStream {
		private final ProgressListener listener;
		private long transferred;

		public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
			this(out, listener, 0);
		}

		public CountingOutputStream(final OutputStream out, final ProgressListener listener, long transferred) {
			super(out);
			this.listener = listener;
			this.transferred = transferred;
		}

		/*
		 * public void write(byte[] b, int off, int len) throws IOException {
		 * super.write(b, off, len); this.transferred += len;
		 * this.listener.transferred(this.transferred); }
		 */

		@Override
		public void write(int b) throws IOException {
			super.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
	}

	public static interface ProgressListener {
		void transferred(long num);
	}
}
