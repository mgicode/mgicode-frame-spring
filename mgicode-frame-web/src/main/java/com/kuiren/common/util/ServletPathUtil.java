package com.kuiren.common.util;

import javax.servlet.http.HttpServletRequest;

public class ServletPathUtil {

	// <%
	// String path = request.getContextPath();
	// String basePath =
	// request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	// %>

	public static String getBasePath(HttpServletRequest request) {
		String path = request.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + path + "/";
		return basePath;
	}

	public static String getActionName(HttpServletRequest req,
			String servletPath) {
		if (StringUtil.IsNullOrEmpty(servletPath)) {
			servletPath = req.getServletPath();

		}
		String path = "";

		if (servletPath.indexOf('?') == -1) {
			path = servletPath;
		} else {

			path = servletPath.substring(0, servletPath.indexOf('?'));
		}
		if (path.charAt(0) == '/') {
			path = path.substring(1);
		}

		return path;
	}

	/**
	 * 取/mgicode/frame/login中的login
	 * 
	 * @param servletPath
	 * @return
	 */
	public static String getLastActionName(String servletPath) {

		String path = "";

		if (servletPath.indexOf('?') == -1) {
			path = servletPath;
		} else {
			path = servletPath.substring(0, servletPath.indexOf('?'));
		}
		if (path.contains("/")) {
			int index = path.lastIndexOf("/");
			path = path.substring(index);
		}
		if (path.charAt(0) == '/') {
			path = path.substring(1);
		}

		return path;
	}

//  public static String normalizePath(String path) {
//  if (path == null) {
//      return null;
//  }
//
//  String normalized = path;
//
//  // Normalize the slashes
//  if (normalized.indexOf('\\') >= 0)
//      normalized = normalized.replace('\\', '/');
//
//  // Resolve occurrences of "/../" in the normalized path
//  while (true) {
//      int index = normalized.indexOf("/../");
//      if (index < 0)
//          break;
//      if (index == 0)
//          return (null);  // Trying to go outside our context
//      int index2 = normalized.lastIndexOf('/', index - 1);
//      normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
//  }
//
//  // Return the normalized path that we have completed
//  return normalized;
//}
}
