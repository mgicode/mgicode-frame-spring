package com.kuiren.common.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class BizRuntimeException extends BaseException {
    private static final Log log = LogFactory.getLog(BizRuntimeException.class);

    public BizRuntimeException() {
        super();
    }

    public BizRuntimeException(String message, Throwable cause) {
        super(message, cause);
        log.error(getFullErrorMessage(cause));
    }

    public BizRuntimeException(String message) {
        super(message);
    }

    public BizRuntimeException(Throwable cause) {
        super(cause);
        log.error(getFullErrorMessage(cause));
    }


    /**
     * 获取异常的全部信息
     *
     * @param e
     * @return
     */
    public static String getFullErrorMessage(Throwable e) {
        StringBuffer buffer = new StringBuffer();
        StackTraceElement[] stacktrace = e.getStackTrace();
        buffer.append("Caused by: " + e + "\n");
        for (StackTraceElement tmp : stacktrace) {
            buffer.append("\tat " + tmp.toString() + "\n");
        }
        return buffer.toString();
    }
}


