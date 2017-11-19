package com.kuiren.common.exception;

/**
 * 基础应用异常，所有的不检查异常应继承此异常
 * @author CHANGYL
 *
 */
public class BaseException extends RuntimeException {

	private static final long serialVersionUID = -6021077900819863433L;

	private String errorUUid;
	private String errorCode;

	public String getErrorUUid() {
		return errorUUid;
	}

	public void setErrorUUid(String errorUUid) {
		this.errorUUid = errorUUid;
	}

	public BaseException() {
		super();
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param errorCode 异常代码
	 * @param message 异常消息
	 */
	public BaseException(String errorCode, String message) {
		super(message);
		this.setErrorCode(errorCode);
	}

	/**
	 * 
	 * @param errorCode 异常代码
	 * @param message 异常消息
	 * @param cause 嵌套异常
	 */
	public BaseException(String errorCode, String message,
			Throwable cause) {
		super(message, cause);
		this.setErrorCode(errorCode);
	}

	@Override
	public String toString() {
		String errorCode = getErrorUUid();
		String s = (errorCode != null) ? errorCode + "--"
				+ getClass().getName() : getClass().getName();
		String message = getLocalizedMessage();
		return (message != null) ? (s + ": " + message) : s;
	}

	public String getErrorCode() {
		return errorCode;
	}

	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
