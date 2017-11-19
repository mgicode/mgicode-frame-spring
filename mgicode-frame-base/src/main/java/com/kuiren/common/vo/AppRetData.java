package com.kuiren.common.vo;

import java.io.Serializable;

public class AppRetData  implements Serializable {

	// {"code": 0, "data": xxx}

	private int code = 0;
	private Object data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
