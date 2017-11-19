package com.kuiren.common.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.kuiren.common.web.RandomValidateCode;

/**
 * 该文件在buz也定义了，如果使用了buz，那么该文件可以不要，这里只是临时使用的
 * 
 * @author prk
 * 
 */
public class CheckCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String location;

	public void init(ServletConfig config) throws ServletException {

	}

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RandomValidateCode rvc = new RandomValidateCode();
		rvc.getRandcode(request, response);

	}

}
