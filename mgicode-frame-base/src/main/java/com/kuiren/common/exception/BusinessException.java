package com.kuiren.common.exception;


public class BusinessException extends BaseException {
	public BusinessException() {
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(Throwable t) {
		super(t);
	}

	public BusinessException(String message, Throwable t) {
		super(message, t);
	}
}
