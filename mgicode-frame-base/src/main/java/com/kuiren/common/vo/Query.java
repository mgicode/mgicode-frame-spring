package com.kuiren.common.vo;

public class Query extends PropertyQuery {

	public Query(String name, String relative, Object value) {
		super(name, relative, value);
	}

	public Query(String name, String relative, Object value, String type) {
		super(name, relative, value, type);
	}
}
