package com.kuiren.common.vo;

import java.io.Serializable;

public class AjaxHtmlVo implements Serializable {

	private String name;
	private boolean success;
	private String contents;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

}
