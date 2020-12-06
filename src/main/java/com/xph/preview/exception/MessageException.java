package com.xph.preview.exception;

import com.xph.preview.model.ResultCode;

public class MessageException extends RuntimeException {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	private String message;

	private int status = ResultCode.ERROR.getCode();

	public MessageException(String message) {
		this.message = message;
	}

	public MessageException(String message, Throwable e) {
		super(message, e);
		this.message = message;
	}

	public MessageException(String message, int status) {
		super(message);
		this.message = message;
		this.status = status;
	}

	public MessageException(String message, int status, Throwable e) {
		super(message, e);
		this.message = message;
		this.status = status;
	}

	public MessageException(ResultCode result) {

		this.message = result.getMessage();
		this.status = result.getCode();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
