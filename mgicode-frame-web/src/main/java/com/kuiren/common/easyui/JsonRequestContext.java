package com.kuiren.common.easyui;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.springframework.context.ApplicationContext;

import com.kuiren.common.spring.SpringInit;

@Deprecated
public class JsonRequestContext {

	private static ThreadLocal<HttpSession> sessionLocal = new ThreadLocal<HttpSession>();

	private static ThreadLocal<HttpServletRequest> requestLocal = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> responseLocal = new ThreadLocal<HttpServletResponse>();
	private static ThreadLocal<List<FileItem>> fileItemsLocal = new ThreadLocal<List<FileItem>>();
	private static ThreadLocal<JsonRequest> jsonRequestLocal = new ThreadLocal<JsonRequest>();

	public static JsonRequest getJsonRequest() {
		return jsonRequestLocal.get();
	}

	public static void setJsonRequest(JsonRequest jsonRequest) {
		jsonRequestLocal.set(jsonRequest);
	}

	public static void setFileItems(List<FileItem> list) {
		fileItemsLocal.set(list);
	}

	public static List<FileItem> getFileItems() {
		return fileItemsLocal.get();
	}

	@Deprecated
	public static void setResponse(HttpServletResponse res) {
		responseLocal.set(res);
	}

	// Use ApplicationContext.getResponse
	@Deprecated
	public static HttpServletResponse getResponse() {
		return responseLocal.get();
	}

	@Deprecated
	public static void setRequest(HttpServletRequest req) {
		requestLocal.set(req);
	}

	// Use ApplicationContext.getRequest
	@Deprecated
	public static HttpServletRequest getRequest() {
		return requestLocal.get();
	}

	@Deprecated
	public static void setSession(HttpSession req) {
		sessionLocal.set(req);
	}

	// Use ApplicationContext.getSession
	@Deprecated
	public static HttpSession getSession() {
		return sessionLocal.get();
	}
	@Deprecated
	public static void setReqestAndSession(HttpServletRequest req,
			HttpSession sess) {

		setRequest(req);
		setSession(sess);
	}

	public static void clearContext() {
		sessionLocal.set(null);
		requestLocal.set(null);
		responseLocal.set(null);
		fileItemsLocal.set(null);
		jsonRequestLocal.set(null);
	}

	public static ApplicationContext getContext() {

		return SpringInit.getApplicationContext();
	}

}
