package com.kuiren.common.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * 压缩过滤器
 * 1、基于com.planetj.servlet.filter.compression.CompressingFilter，但修正其对Etag的修改后造成Web容器无法识别原ETAG从而导致浏览器缓存失效的问题。
 * @see com.planetj.servlet.filter.compression.CompressingFilter
 */
public class CompressingFilter implements Filter {
	private static final String ETAG_HEADER = "ETag";
	private String useWrapper;
	
	com.planetj.servlet.filter.compression.CompressingFilter delegate = null;

	public String isUseWrapper() {
		return useWrapper;
	}

	public void setUseWrapper(String useWrapper) {
		this.useWrapper = useWrapper;
	}

	public void init(FilterConfig config) throws ServletException {
		delegate = new com.planetj.servlet.filter.compression.CompressingFilter();
		delegate.init(config);
	}

	public void destroy() {
		delegate.destroy();
	}

	public  void  doFilter(ServletRequest request,
	                     ServletResponse response,
	                     FilterChain chain) throws IOException, ServletException {
		//将ETAG中的"-压缩方式"后缀去掉，以使容器能认识ETAG，避免缓存失效
		if(response instanceof HttpServletResponse){
				response = new HttpServletResponseWrapper((HttpServletResponse) response){
					@Override
					public void setHeader(String name, String value) {
						if(ETAG_HEADER.equalsIgnoreCase(name)){
							if((value != null) && value.endsWith("-gzip")) {
								value = value.substring(0, value.length()-"-gzip".length());
							}
							else if((value != null) && value.endsWith("-x-gzip")) {
								value = value.substring(0, value.length()-"-x-gzip".length());
							}
							else if((value != null) && value.endsWith("-compress")) {
								value = value.substring(0, value.length()-"-compress".length());
							}
							else if((value != null) && value.endsWith("-x-compress")) {
								value = value.substring(0, value.length()-"-x-compress".length());
							}
							else if((value != null) && value.endsWith("-deflate")) {
								value = value.substring(0, value.length()-"-deflate".length());
							}
						}
						super.setHeader(name, value);
					}
					// net.sf.ehcache.constructs.web.filter.GzipFilter
					//TODO 解决socket write error 问题
					/*
					public ServletOutputStream getOutputStream() throws java.io.IOException{
						
						if(valBooleanExpress(useWrapper)){							
							return new ServletOutputStreamWrapper(super.getOutputStream());
						}						
						return super.getOutputStream();
						
					}*/
				};
		}
		
		delegate.doFilter(request, response, chain);
	}
	/*TODO 解决socket write error 问题
	private boolean valBooleanExpress(String express){
		boolean expressValue = false;
		
		if(express != null) {
			try {
				Object value = Scriptlet.eval(express);
				if(value instanceof Boolean){
					expressValue = ((Boolean) value).booleanValue();
				}
				else{
					System.err.println("表达式[" + express + "]的返回值不是布尔值！表达式值当作false。");
				}
			} catch (Throwable e) {
				System.err.println("计算布尔表达式[" + express + "]的异常！表达式值当作false。"+ e.getMessage());
			}
		}
		
		return expressValue;
	}*/
}