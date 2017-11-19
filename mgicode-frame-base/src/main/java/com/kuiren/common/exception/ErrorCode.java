package com.kuiren.common.exception;

public class ErrorCode {
	// 返回报文提示信息key
	public final static String COMM_RTN_MSG = "rtnMsg";
	// 返回报文信息码key
	public final static String COMM_RTN_CODE = "rtnCode";

	// // 返回报文信息码key
	// public final static String ERROR_RTN_CODE = "errorCode";
	// // 返回报文提示信息key
	// public final static String ERROR_RTN_MSG = "errorMsg";
	// 服务名为空
	public final static String SERVICE_NAME_IS_NULL = "9901";
	// 服务方法名为空
	public final static String SERVICE_METHOD_IS_NULL = "9902";
	// FTP上传不成功
	public final static String FTP_UPLOAD_FALL = "9903";

	// 交易成功
	public final static String SERVICE_CALL_SUCCESS = "2000";

}
