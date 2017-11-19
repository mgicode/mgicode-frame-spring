package com.kuiren.common.easyui;

public class ExcuteInterceptVo {

	private String interceptService;
	private String interceptMethod;
	private String excuteService;
	private String excuteMethod;

	public ExcuteInterceptVo(String interceptService, String interceptMethod,
			String excuteService, String excuteMethod) {
		super();
		this.interceptService = interceptService;
		this.interceptMethod = interceptMethod;
		this.excuteService = excuteService;
		this.excuteMethod = excuteMethod;
	}

	public String getInterceptService() {
		return interceptService;
	}

	public void setInterceptService(String interceptService) {
		this.interceptService = interceptService;
	}

	public String getInterceptMethod() {
		return interceptMethod;
	}

	public void setInterceptMethod(String interceptMethod) {
		this.interceptMethod = interceptMethod;
	}

	public String getExcuteService() {
		return excuteService;
	}

	public void setExcuteService(String excuteService) {
		this.excuteService = excuteService;
	}

	public String getExcuteMethod() {
		return excuteMethod;
	}

	public void setExcuteMethod(String excuteMethod) {
		this.excuteMethod = excuteMethod;
	}

}
