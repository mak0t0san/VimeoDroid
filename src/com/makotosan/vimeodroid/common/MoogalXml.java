package com.makotosan.vimeodroid.common;

public class MoogalXml {
	private String embedCode;
	private String request_signature;
	private String request_signature_expires;
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmbedCode() {
		return embedCode;
	}

	public void setEmbedCode(String embedCode) {
		this.embedCode = embedCode;
	}

	public String getRequestSignature() {
		return request_signature;
	}

	public void setRequestSignature(String requestSignature) {
		request_signature = requestSignature;
	}

	public String getRequestSignatureExpires() {
		return request_signature_expires;
	}

	public void setRequestSignatureExpires(String requestSignatureExpires) {
		request_signature_expires = requestSignatureExpires;
	}

}
