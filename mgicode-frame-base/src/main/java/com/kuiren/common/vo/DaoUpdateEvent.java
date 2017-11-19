package com.kuiren.common.vo;

import java.util.ArrayList;
import java.util.List;

public class DaoUpdateEvent {

	private String serverName;
	private String methodName;
	private List parmas = new ArrayList();

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<?> getParmas() {
		return parmas;
	}

	public void setParmas(List<?> parmas) {
		this.parmas = parmas;
	}

	public void addParm(Object param) {
		this.parmas.add(param);
	}

}
