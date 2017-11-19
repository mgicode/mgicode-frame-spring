package com.kuiren.common.easyui;

import java.util.Date;

import com.kuiren.common.context.DataObjectReq;
import com.kuiren.common.util.StringUtil;

public class JsonRequest extends DataObjectReq {

	private String serviceName;
	private String methodName;

	public JsonRequest() {
		super();

	}

	public JsonRequest(Object data) {
		super(data);

	}

	public Parameter param(String name) {
		return new Parameter(this, name);
	}

	public int paramAsInt(String name) {
		return new Parameter(this, name).toInt();
	}

	public int paramAsInt(String name, int defaultValue) {
		return new Parameter(this, name).toInt(defaultValue);
	}

	public String paramAsStr(String name) {
		return new Parameter(this, name).toStr();
	}

	public String paramAsStr(String name, String defaultValue) {
		return new Parameter(this, name).toStr(defaultValue);
	}

	public String[] paramAsStrArr(String name, String defaultValue, String split) {
		String str = new Parameter(this, name).toStr(defaultValue);
		if (str == null)
			return new String[] {};
		return str.split(split);
	}

	public String[] paramAsStrArr(String name, String defaultValue) {
		return paramAsStrArr(name, defaultValue, "[，,]");
	}

	public String paramAsStr(String name, String notInArr[], String defaultValue) {
		Parameter p = new Parameter(this, name);
		if (notInArr == null || notInArr.length == 0) {
			return p.toStr(defaultValue);

		} else {
			String v = p.toStr();
			if (StringUtil.contain(notInArr, v)) {
				return v;
			} else
				return defaultValue;

		}
	}

	public float paramAsFloat(String name) {
		return new Parameter(this, name).toFloat();
	}

	public float paramAsFloat(String name, float defalutValue) {
		return new Parameter(this, name).toFloat(defalutValue);
	}

	public Date paramAsDate(String name) {
		return new Parameter(this, name).toDate();
	}

	public Date paramAsDate(String name, String fmt) {
		return new Parameter(this, name).toDate(fmt);
	}

	public Date paramAsDate(String name, String fmt, Date defaultValue) {
		return new Parameter(this, name).toDate(defaultValue, fmt);
	}

	public void eachParam(String name, IParaListConvert convert) {
		new Parameter(this, name).each(convert);
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

}
