package com.kuiren.common.vo;

import java.io.Serializable;

public class AttrData   implements Serializable{

	private String name = "defautName";
	private Object data;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
