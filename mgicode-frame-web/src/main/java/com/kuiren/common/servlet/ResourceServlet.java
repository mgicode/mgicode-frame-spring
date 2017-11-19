package com.kuiren.common.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kuiren.common.util.AppconfigUtil;
import com.kuiren.common.util.StringUtil;

/**
 * 该文件在buz也定义了，如果使用了buz，那么该文件可以不要，这里只是临时使用的
 * 
 * @author prk
 * 
 */
public class ResourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String location;
	protected final Log logger = LogFactory.getLog(getClass());

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		location = this.getInitParameter("location");
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String url = request.getParameter("url");
		if (url != null) {
			// ThemeColor themeColor = ThemeConfig.getThemeColor(request,
			// response);

			// url = request.getContextPath() + "/" + this.location + "/"
			// + AppconfigUtil.val("mgicode.theme") + "/" + url;

			url = request.getContextPath() + "/" + getThemesPath() + "/" + url;
			if (logger.isDebugEnabled()) {
				logger.debug(url);
			}
			response.sendRedirect(url);
			// url = "/" + this.location + "/" + themeColor.getThemeId() + "/" +
			// themeColor.getColorId() + "/" + url;
			// RequestDispatcher dispatcher = request.getRequestDispatcher(url);
			// if (dispatcher != null) {
			// dispatcher.forward(request, response);
			// }

		}
	}

	/**
	 * 主题相对于上下文的路径
	 * 
	 * @return
	 */
	public static String getThemesPath() {

		return location + "/" + AppconfigUtil.val("mgicode.theme") + "";
	}

	public static String getAdminThemesPath() {

		return getThemesPath() + "/" + "admin";
	}

	public static String getFrontThemesPath() {

		return getThemesPath() + "/" + "front";
	}

	public static String getRootAddr() {

		return AppconfigUtil.val("mgicode.web.server.path");
	}

	public static String getRootContextAddr(String path) {

		return StringUtil.buildPath(getRootAddr() + path);
	}
}
