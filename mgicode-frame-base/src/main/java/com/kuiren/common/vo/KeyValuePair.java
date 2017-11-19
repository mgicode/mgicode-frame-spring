package com.kuiren.common.vo;

import java.io.Serializable;

/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class KeyValuePair  implements Serializable {
	private String name;
	private Object value;

	private String relative;

	public KeyValuePair(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}

	public KeyValuePair(String name, Object value, String relative) {
		super();
		this.name = name;
		this.value = value;
		this.relative = relative;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setRelative(String relative) {
		this.relative = relative;
	}

	public String getRelative() {
		return relative;
	}

}
