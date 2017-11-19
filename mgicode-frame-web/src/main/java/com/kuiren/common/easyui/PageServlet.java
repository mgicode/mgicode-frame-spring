package com.kuiren.common.easyui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.kuiren.common.context.Executor;
import com.kuiren.common.exception.BizRuntimeException;
//import com.kuiren.common.json.JsonHelper;
import com.kuiren.common.util.AppconfigUtil;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.AttrData;
import com.kuiren.common.vo.KeyValue;
//import com.kuiren.spring.util.ConfigUtil;

public class PageServlet extends HttpServlet {

	public static String defaultAttriName = "_attName";
	public static Map<String, Object[]> clzMethodMap = new HashMap<String, Object[]>();
	protected final Log logger = LogFactory.getLog(getClass());
	// default .page
	public static String PAGE_EXT;// AppconfigUtil.val("");

	public PageServlet() {
	}

	public void init() throws ServletException {

		PAGE_EXT = AppconfigUtil.val("mgicode.page.ext");
		if (StringUtil.IsNullOrEmpty(PAGE_EXT)) {

			throw new RuntimeException(
					"config.properties配置文件中没有配置mgicode.page.ext属性或其属性值为空");
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * 20160403 pengrk add the multi service合并功能
	 */
	public void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		// List<String> sm = ServletUtil.getServiceAndMethodName(request,
		// PAGE_EXT, null);
		// String serviceName = sm.get(0);
		// String serviceMethod = sm.get(1);
		// if (log.isDebugEnabled()) {
		// log.debug("\n请求的服务名和方法名：" + serviceName + "-->" + serviceMethod);
		// }
		List<KeyValue> sms = ServletUtil.getSMNames(request, PAGE_EXT, null);
		if (sms == null || sms.size() < 1) {
			throw new RuntimeException("没有找到请求的方法");
		}

		final String jsName = JsonRequestUtil.getParams(request, "_js,_jsName");
		final String _text = JsonRequestUtil.getParams(request, "_t,_text");
		final String _attrName = JsonRequestUtil.getParams(request,
				"_attr,_attrName");

		final JsonRequest jsonRequest = createJsonRequest(request);
		JsonRequestContext.setJsonRequest(jsonRequest);

		JsonRequestContext.setReqestAndSession(request, request.getSession());
		JsonRequestContext.setResponse(response);
		// JsonRequestContext.setFileItems(upload(request));

		try {
			List<Object[]> resps = new ArrayList<Object[]>();
			for (KeyValue kv : sms) {
				resps.add(excute(kv.getKey(), kv.getValue(), request, response,
						jsonRequest));
			}
			Object[] respArr = resps.get(0);
			Object resp = respArr[0];
			int count = ((Integer) respArr[1]).intValue();
			if (resp instanceof String) {
				if (count == 1) {// 兼容于easyUIServlet
					outputJson(resps, resp, jsName, _text, response);
				} else { // count==0,2,
					pageDirect(resps, resp, _attrName, request, response);
					// String page = (String) resp;
					// if (log.isDebugEnabled()) {
					// log.debug("\n返回的路径：" + page);
					// }
					// if (page.contains("@")) {
					// response.sendRedirect(page.split("[@]")[0]);
					// } else {
					// request.getRequestDispatcher(page).forward(request,
					// response);
					// }
				}
			} else if (resp instanceof File) {
				downFile((File) resp);
			}

		} catch (Throwable e) {
			e.printStackTrace();		
			log.error(StringUtil.getFullErrorMessage(e));
			Map error = new HashMap();
			String result = "";
			RetData retData = new RetData();
			retData.setSuccess(false);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				if (ite.getTargetException() instanceof BizRuntimeException) {
					retData.setSuccess(true);
					retData.setMsg(StringUtil.getFullErrorMessage(e));
					result =  new Gson().toJson(retData);//jackson.toJson(retData);
				} else if (ite.getTargetException() instanceof DataIntegrityViolationException) {
					retData.setSuccess(true);
					retData.setMsg("该项不能删除，该项已经被使用");
					result = new Gson().toJson(retData);//jackson.toJson(retData);
				}
				else {
					retData.setMsg(e.getMessage());
					result = new Gson().toJson(retData); //jackson.toJson(retData);
				}
			}
			

			//# Access-Control-Allow-Origin: *
			String v="1";//ConfigUtil.val("cros", "");
			if(StringUtil.isNotNullOrEmpty(v)){
		     	response.addHeader("Access-Control-Allow-Origin", v);
			}
			
			response.setContentType("text/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
			
		} finally {

			JsonRequestContext.clearContext();
		}

	}

	public Object[] excute(String serviceName, String serviceMethod,
			final HttpServletRequest request,
			final HttpServletResponse response, final JsonRequest jsonRequest) {
		jsonRequest.setServiceName(serviceName);
		jsonRequest.setMethodName(serviceMethod);

		try {
			Object[] clzMethod = JsonRequestUtil.dyncCalClzMethod(serviceName,
					serviceMethod, JsonRequestUtil.TYPE_EASYUI_PAGE);
			final Object service = clzMethod[0];
			final Method method = (Method) clzMethod[2];
			final int count = BeanUtil.hasParamCount(method);
			Executor e = new Executor() {
				public Object execute() throws Throwable {
					if (count == 0) {
						return JsonRequestUtil.callMethod(service, method,
								new Object[] {});
					} else if (count == 1) {

						return JsonRequestUtil.callMethod(service, method,
								new Object[] { jsonRequest });
					} else if (count == 2) {
						return JsonRequestUtil.callMethod(service, method,
								new Object[] { request, response });
					} else {
						log.error("\n找到的函数有" + count + "个参数，目前只支持0，1，2个参数的三种情况");
						throw new RuntimeException("\n找到的函数有" + count
								+ "个参数，目前只支持0，1，2个参数的三种情况");
						// return null;
					}
				}
			};

			Object resp = JsonRequestUtil.transactionExcute(e);
			return new Object[] { resp, count };

		} catch (Throwable e1) {
			e1.printStackTrace();

		}
		return null;

	}

	private void outputJson(Object resp, String jsName, String _text,
			HttpServletResponse response) throws IOException {
		String result = (String) resp;
		if (StringUtil.IsNotNullOrEmpty(jsName)) {
			result = "window." + jsName + "=" + result + ";";
		}
		if (log.isDebugEnabled()) {
			log.debug("\n返回的JSON：" + result);
		}
		if (StringUtil.IsNotNullOrEmpty(_text)) {
			response.setContentType("text/plain");
		} else {
			response.setContentType("text/json");
		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		out.print(result);
		out.flush();
		out.close();

	}

	private void outputJson(List<Object[]> resps, Object resp, String jsName,
			String _text, HttpServletResponse response) throws IOException {

		String result = null;
		List<String> jsNameArr = StringUtil.split(jsName, ",");

		if (resps != null && resps.size() > 1) {
			if (jsNameArr != null && jsNameArr.size() == (resps.size() + 1)) {
				result = "{";
				for (int i = 0; i < resps.size(); i++) {
					Object[] respArr = resps.get(i);
					Object resp1 = respArr[0];
					if (i != 0) {
						result = result + ",";
					}
					if (!(resp1 instanceof String)) {
						throw new RuntimeException("目前返回的值只支持String类型");
					}
					result = result + jsNameArr.get(i + 1) + ":" + resp1;

				}
				result = result + "}";

			} else {
				result = "[";
				for (int i = 0; i < resps.size(); i++) {
					Object[] respArr = resps.get(i);
					Object resp1 = respArr[0];
					if (i != 0) {
						result = result + ",";
					}
					if (!(resp1 instanceof String)) {
						throw new RuntimeException("目前返回的值只支持String类型");
					}
					result = result + "" + resp1;

				}
				result = result + "]";
			}
		}

		else {
			// 只有一种情况下，不转换为数组
			result = (String) resp;
		}

		if (StringUtil.IsNotNullOrEmpty(jsName)) {
			String jn = jsNameArr.get(0);
			result = "window." + jn + "=" + result + ";";

			// result = "window." + jsName + "=" + result + ";";
		}
		if (log.isDebugEnabled()) {
			log.debug("\n返回的JSON：" + result);
		}
		

		//# Access-Control-Allow-Origin: *
		String v="1";//ConfigUtil.val("cros", "");
		if(StringUtil.isNotNullOrEmpty(v)){
	     	response.addHeader("Access-Control-Allow-Origin", v);
		}
		
		if (StringUtil.IsNotNullOrEmpty(_text)) {
			response.setContentType("text/plain");
		} else {
			response.setContentType("text/json");
		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		out.print(result);
		out.flush();
		out.close();

	}

	public void pageDirect(List<Object[]> resps, Object resp, String attrName,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// count==0,2,
		String page = (String) resp;
		if (log.isDebugEnabled()) {
			log.debug("\n返回的路径：" + page);
		}

		if (page.contains("@")) {
			response.sendRedirect(page.split("[@]")[0]);
		} else {
			// 跳转页面使用
			List<Object> list = new ArrayList<Object>();
			for (int i = 1; i < resps.size(); i++) {
				Object[] respArr = resps.get(i);
				Object resp1 = respArr[0];
				list.add(resp1);
				if (resp1 instanceof AttrData) {
					AttrData ad = (AttrData) resp1;
					request.setAttribute(ad.getName(), ad.getData());
				}
			}
			if (StringUtil.IsNullOrEmpty(attrName)) {
				attrName = defaultAttriName;
			}
			request.setAttribute(attrName, list);

			request.getRequestDispatcher(page).forward(request, response);
		}

	}

	// list = new ArrayList<FileItem>();
	// // 构造一个文件上传处理对象
	// FileItemFactory factory = new DiskFileItemFactory();
	// ServletFileUpload upload = new ServletFileUpload(factory);
	// Iterator items; // 解析表单中提交的所有文件内容
	// try {
	// items = upload.parseRequest(request).iterator();
	// while (items.hasNext()) {
	// FileItem item = (FileItem) items.next();
	// if (!item.isFormField()) {
	// list.add(item);
	// } else {
	// // Map param = new HashMap();
	// // param.put(item.getFieldName(),
	// // item.getString("utf-8"));
	// // 如果你页面编码是utf-8的
	// // request.set
	// }
	// }
	// } catch (FileUploadException e) {
	// e.printStackTrace();
	// log.error(e);
	// }

	public JsonRequest createJsonRequest(HttpServletRequest request) {
		try {
			Map map = new HashMap();
			List<FileItem> list = new ArrayList<FileItem>();
			if (isMultipart(request)) {
				// 构造一个文件上传处理对象
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				Iterator items; // 解析表单中提交的所有文件内容
				try {
					items = upload.parseRequest(request).iterator();
					while (items.hasNext()) {
						FileItem item = (FileItem) items.next();
						if (item.isFormField()) {
							String name = item.getFieldName();
							String val = item.getString("utf-8");
							map.put(name, val);
							if (log.isDebugEnabled()) {
								log.debug("\n" + name + ":" + val);
							}
						} else {
							list.add(item);
							if (log.isDebugEnabled()) {
								log.debug("\n找到上传文件:" + item.getName());
							}
						}
					}
				} catch (FileUploadException e) {
					e.printStackTrace();
					log.error(e);
				}

				JsonRequestContext.setFileItems(list);

			} else {
				String postStr = String.valueOf(ServletUtil.getPostData(
						request, "params"));
				// 把查询参数转换为Json
				if (StringUtil.IsNullOrEmpty(postStr)) {
					postStr = requestParamstoJson(request);
				}
				map = JsonRequestUtil.getMapFromJson(postStr, true);
				// save,search
				String _type = JsonRequestUtil.getParams(request, "_type");
				if ("save".equals(_type)) {
					map = JsonRequestUtil.removeEmptyObj(map);
				}
			}

			JsonRequest req = new JsonRequest(map);

			return req;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}

		return null;
	}

	// public JsonRequest createJsonRequest(HttpServletRequest request) {
	// try {
	// String postStr = String.valueOf(ServletUtil.getPostData(request,
	// "params"));
	// // 把查询参数转换为Json
	// if (StringUtil.IsNullOrEmpty(postStr)) {
	// postStr = requestParamstoJson(request);
	// }
	// Map map = JsonRequestUtil.getMapFromJson(postStr, true);
	// // save,search
	// String _type = JsonRequestUtil.getParams(request, "_type");
	// if ("save".equals(_type)) {
	// map = JsonRequestUtil.removeEmptyObj(map);
	// }
	// JsonRequest req = new JsonRequest(map);
	//
	// return req;
	// } catch (Exception e) {
	// log.error(e);
	// }
	//
	// return null;
	// }

	public String requestParamstoJson(HttpServletRequest request) {
		String postStr = "{}";
		try {
			// 把查询参数转换为Json,支持点串的方式
			Map map = MapUtil.fetchFromRequestParameterMap(
					request.getParameterMap(), true, true);
			MapUtil.remove("service,s,serviceMethod,m", map);
			postStr = new Gson().toJson(map);//jackson.toJson(map);
			return postStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return postStr;

	}

	public boolean isMultipart(HttpServletRequest request) {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			return true;
		} else {

			return false;
		}
	}

	public List<FileItem> upload(HttpServletRequest request)
			throws UnsupportedEncodingException {

		List<FileItem> list = null;
		// 判断提交过来的表单是否为文件上传菜单
		// boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart(request)) {
			list = new ArrayList<FileItem>();
			// 构造一个文件上传处理对象
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			Iterator items; // 解析表单中提交的所有文件内容
			try {
				items = upload.parseRequest(request).iterator();
				while (items.hasNext()) {
					FileItem item = (FileItem) items.next();
					if (!item.isFormField()) {
						list.add(item);
					} else {
						// Map param = new HashMap();
						// param.put(item.getFieldName(),
						// item.getString("utf-8"));
						// 如果你页面编码是utf-8的
						// request.set
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
				log.error(e);
			}
		}
		return list;

	}

	public void downFile(File file) throws FileNotFoundException {

		HttpServletResponse response = JsonRequestContext.getResponse();
		InputStream fileInputStream = new FileInputStream(file);

		String fileName = null;
		try {
			fileName = URLDecoder.decode(file.getName(), "UTF-8");
		} catch (UnsupportedEncodingException e4) {
			e4.printStackTrace();
		}
		try {
			fileName = new String(fileName.getBytes(), "iso8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		response.setContentType("application/octet-stream");
		response.setHeader("Content-disposition", "attachment;filename="
				+ fileName);
		BufferedInputStream bis = new BufferedInputStream(fileInputStream);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			bos.flush();

		} catch (Exception e1) {
			if ("ClientAbortException".equals(e1.getClass().getSimpleName())) {
				System.out.println("----->Socket异常，可能原因是客户端中断了附件下载。");
			} else {

			}
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e2) {
				log.error(e2.getMessage(), e2);
			}

			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e3) {
				e3.printStackTrace();
			}

		}

	}

	//private static JsonHelper jackson = JsonHelper.buildNonDefaultBinder();
	private static final Log log = LogFactory.getLog(PageServlet.class);
	private static final long serialVersionUID = 1L;

}
