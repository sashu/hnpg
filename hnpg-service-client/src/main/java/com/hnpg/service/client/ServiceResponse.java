package com.hnpg.service.client;

import java.io.Serializable;

public class ServiceResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3750607897460675394L;
	private String message;
	private Object data;
	private int status;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
