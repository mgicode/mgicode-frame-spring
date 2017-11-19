package com.kuiren.common.vo;

import java.io.Serializable;

//import com.kuiren.common.treefilter.IPropQuery;

/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class ObjQuery   implements Serializable{

	private String name;
	private Object value;
	private String relative = "=";// = > < <> like
	

	private String merge = "and"; // and or
	
	//private IPropQuery propQuery;

	public ObjQuery(String name, String relative, Object value, String merge) {
		this.name = name;
		this.value = value;
		this.relative = relative;
		this.merge = merge;
	}

	public ObjQuery(String name, String relative, Object value) {
		this.name = name;
		this.value = value;
		this.relative = relative;

	}

	public ObjQuery(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
//	public ObjQuery(IPropQuery propQuery) {
//		this.setPropQuery(propQuery);
//	//	this.relative = "autod";
//	}

	public ObjQuery() {

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

	public String getRelative() {
		return relative;
	}

	public void setRelative(String relative) {
		this.relative = relative;
	}

	public String getMerge() {
		return merge;
	}

	public void setMerge(String merge) {
		this.merge = merge;
	}

//	public IPropQuery getPropQuery() {
//		return propQuery;
//	}
//
//	public void setPropQuery(IPropQuery propQuery) {
//		this.propQuery = propQuery;
//	}
//
	

}
