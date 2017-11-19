package com.kuiren.common.exception;

import java.sql.SQLException;

/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class DAOException extends BaseException {
	private Throwable rootCause;

	public DAOException() {
	}

	public DAOException(String message) {
		super(message);
	}

	public DAOException(Throwable t) {
		super(getAllMessage(t));
		this.rootCause = t;
	}

	private static SQLException getInheritSQLException(Throwable t) {
		SQLException ex = null;

		while (t != null) {
			if (t instanceof SQLException) {
				ex = (SQLException) t;
				break;
			} else {
				t = t.getCause();
			}
		}
		return ex;
	}

	private static String getAllMessage(Throwable t) {
		String message = null;
		if (t != null) {
			if (!(t instanceof SQLException)) {
				message = t.getMessage();
			}
			SQLException ex = getInheritSQLException(t);
			if (ex != null) {
				if (message != null) {
					message += "\r\n" + ex.getMessage();
				} else {
					message = ex.getMessage();
				}
				String nextMessage = getAllMessage(ex.getNextException());
				if (nextMessage != null) {
					message += "\r\n" + nextMessage;
				}
			}
		}
		return message;
	}

	public DAOException(String code, String message, Throwable t) {
		super(message + "\r\n" + getAllMessage(t));
		this.rootCause = t;
	}

	public Throwable getRootCause() {
		return this.rootCause;
	}
}
