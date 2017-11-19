package com.kuiren.common.context;

public class DataObjectRes extends CommDataObject {
	public DataObjectRes() {
		super();
	}

	/**
	 * 返回代码
	 */
	public String rtnCode = null;
	/**
	 * 返回消息
	 */
	public String rtnMsg = null;

	public DataObjectRes(Object data) {
		super(data);
	}
}
