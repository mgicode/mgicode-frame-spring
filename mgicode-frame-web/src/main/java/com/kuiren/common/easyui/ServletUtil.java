package com.kuiren.common.easyui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.KeyValue;

public class ServletUtil {
	protected static final Log logger = LogFactory.getLog(ServletUtil.class);

	/**
	 * 取得请求的路径，如http://localhost:8888/mgicode/ucDict-manager.do?
	 * 会取得其？前面，最后一个/后面的内容
	 * 
	 * @param req
	 * @param servletPath
	 * @return
	 */
	public static String getActionName(HttpServletRequest req,
			String servletPath) {
		if (StringUtil.isNullOrEmpty(servletPath)) {
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

		if (logger.isDebugEnabled()) {
			logger.debug("\nactionName:" + path);
		}
		return path;
	}

	/**
	 * 在请求时，很多时候希望把页面需要请求的一些数据一同请求，<br>
	 * 而不是单个单个地请求,同时请求会请求请求到性能， 这里取得请求的
	 * 
	 * @param req
	 * @param ext
	 * @param defaultMethodName
	 * @return 彭仁夔 于 2016年4月3日 下午6:24:20 创建
	 */
	public static List<KeyValue> getSMNames(HttpServletRequest req, String ext,
			String dfaultName) {

		List<KeyValue> ret = new ArrayList<KeyValue>();
		ret.addAll(getSMNamesFromUrl(req, ext, dfaultName));
		ret.addAll(getSMNameFormParams(req));

		return ret;
	}

	public static List<KeyValue> getSMNames(HttpServletRequest req, String ext,
			String dfaultName, String sname, String mname) {

		List<KeyValue> ret = new ArrayList<KeyValue>();
		ret.addAll(getSMNamesFromSelf(sname, mname));
		ret.addAll(getSMNamesFromUrl(req, ext, dfaultName));
		ret.addAll(getSMNameFormParams(req));

		return ret;
	}

	public static List<KeyValue> getSMNamesFromSelf(String sname, String mname) {
		List<KeyValue> ret = new ArrayList<KeyValue>();
		KeyValue kv = new KeyValue(sname, mname);
		ret.add(kv);
		return ret;
	}

	public static List<KeyValue> getSMNameFormParams(HttpServletRequest req) {
		List<KeyValue> ret = new ArrayList<KeyValue>();
		String serviceName = JsonRequestUtil.getParams(req, "service,s");
		String serviceMethod = JsonRequestUtil
				.getParams(req, "serviceMethod,m");
		if (StringUtil.IsNotNullOrEmpty(serviceName)
				&& StringUtil.IsNotNullOrEmpty(serviceMethod)) {
			List<String> sm = StringUtil.split(serviceName, "[，,]");
			List<String> mm = StringUtil.split(serviceMethod, "[，,]");

			if (sm.size() != mm.size()) {
				if (logger.isDebugEnabled()) {
					logger.debug("\n" + serviceName + "," + serviceMethod
							+ "中采用，拆分的个数不对，该参数不作为servername和methodname");
				}

			} else {
				for (int i = 0; i < sm.size(); i++) {
					KeyValue kv = new KeyValue(sm.get(i), mm.get(i));
					ret.add(kv);
					if (logger.isDebugEnabled()) {
						logger.debug("\n,解析出" + sm.get(i) + "," + mm.get(i));
					}
				}
			}
		}

		return ret;

	}

	public static List<KeyValue> getSMNamesFromUrl(HttpServletRequest req,
			String ext, String dflName) {
		List<KeyValue> ret = new ArrayList<KeyValue>();
		String actionName = getActionName(req, null);
		// 不指定默认的名称，这样就可以让xxx-yy.do?s=xxx&m=ddd结合起来使用
		List<String> list = getSMNameFromActionName(actionName, ext, null);
		if (list != null && list.size() == 2) {
			KeyValue kv = new KeyValue(list.get(0), list.get(1));
			ret.add(kv);
		}
		return ret;
	}

	public static List<String> getSMNameFromActionName(String actionName,
			String ext, String dfname) {
		String noextName = StringUtil.replace(actionName, ext, "");
		List<String> list = StringUtil.split(noextName, "[-]");
		if (list != null && list.size() == 1) {
			if (StringUtil.IsNotNullOrEmpty(dfname)) {
				list.add(dfname);// 默认采用excute的方法
				// list.add("use defaultMethodName");
			} else {
				throw new RuntimeException("\n" + actionName
						+ "只能取到serviceName:" + list.get(0));
			}
		}
		return list;
	}

	/**
	 * 
	 * @param req
	 * @param requestName
	 * @return
	 */
	public static StringBuffer getPostData(HttpServletRequest req,
			String requestName) {
		StringBuffer s = getData(req, "utf-8");
		if (s.length() != 0)
			return s;
		if (requestName == null)
			return s;
		String xml = req.getParameter(requestName);
		if (xml == null)
			return s;
		else
			return new StringBuffer(xml);
	}

	/**
	 * 以指定encode的方式把httpRequest中的全部数据 取到StringBuffer中。
	 * 
	 * @param request
	 * @param encode
	 * @return
	 */
	public static StringBuffer getData(HttpServletRequest request, String encode) {
		StringBuffer sb = new StringBuffer();
		String s = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					request.getInputStream(), encode));
			while ((s = br.readLine()) != null)
				sb.append(s).append("\n");
			br.close();
			return sb;
		} catch (IOException e) {
			throw new RuntimeException((new StringBuilder("getXml error"))
					.append(e).toString());
		} catch (Exception e) {
			throw new RuntimeException((new StringBuilder("getXml error"))
					.append(e).toString());
		}
	}

	public static boolean isMultipart(HttpServletRequest request) {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			return true;
		} else {

			return false;
		}
	}

	public static void writeJson(String result, HttpServletResponse response) {

		writeData(result, "text/json", "utf-8", response);
	}

	public static void writeData(String result, String contenttype,
			String encoding, HttpServletResponse response) {
		if (logger.isDebugEnabled()) {
			logger.debug("\n返回的JSON：" + result);
		}
		response.setContentType(contenttype);
		response.setCharacterEncoding(encoding);
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	// try {
	// BufferedReader br = new BufferedReader(new
	// InputStreamReader(request.getInputStream(), encode));
	// String queryString = "", str = "";
	// while ((str = br.readLine()) != null) {
	// queryString += (str);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// 方法二：
	// for (Enumeration eh = request.getParameterNames(); eh.hasMoreElements();)
	// {
	// String parName = (String) eh.nextElement();
	// String parValue = request.getParameter(parName);
	// }

	// InputStream is = request.getInputStream();
	// DataInputStream input = new DataInputStream(is);
	// String str =input.readUTF();
	//
	//
	// 分离参数方法
	// public String splitString(String str,String temp){
	// String result = null;
	// if (str.indexOf(temp) != -1) {
	// if (str.substring(str.indexOf(temp)).indexOf("&") != -1) {
	// result =
	// str.substring(str.indexOf(temp)).substring(str.substring(str.indexOf(temp)).indexOf("=")+1,
	// str.substring(str.indexOf(temp)).indexOf("&"));
	// } else {
	// result =
	// str.substring(str.indexOf(temp)).substring(str.substring(str.indexOf(temp)).indexOf("=")+1);
	// }
	// }
	// return result;
	//
	// }
	//
	//
	// 调用this.splitString(str, "id=")
	//
	//
	//
	// 对post和get传递参数分别获取
	//
	// //获取post参数
	// StringBuffer sb = new StringBuffer() ;
	// InputStream is = request.getInputStream();
	// InputStreamReader isr = new InputStreamReader(is);
	// BufferedReader br = new BufferedReader(isr);
	// String s = "" ;
	// while((s=br.readLine())!=null){
	// sb.append(s) ;
	// }
	// String str =sb.toString();
	//
	// //防止用get传递参数
	// if(str.equals("")){
	// if(request.getQueryString() != null) {
	// str = request.getRequestURL()+"?"+request.getQueryString();
	// } else {
	// str = request.getRequestURL().toString();
	// }
	// }

	// 如果能读到那才是异常。 这是最最起码的常识，当一个流数据直接和网络IO挂接，如果你自己读过一次还能再有数据那就奇怪了。
	//
	// getParameter（）是因为request自己先getInputStream()然后把内容分析出来放在一个map中让你getParameter来获取。而你自己先getInputStream()了，数据已经被read了怎么可能再getParameter（）掉呢？
	//
	//
	// 在getParameter的实现中想当于这样：
	//
	// if(inputStream还没有处理)｛
	// readInputStream;
	// 把read到的数据放到map中；
	// 把标记设为已经处理；
	// 从map返回你要的那个key的值
	// ｝
	// else{
	// 直接从map取你要的那个值
	// }
	//
	// 现在你自己先自理InputStream了，
	// 那行进入if(inputStream还没有处理)｛
	// readInputStream;
	// 把read到的数据放到map中；
	// 把标记设为已经处理；
	// 从map返回你要的那个key的值
	// ｝这段
	//
	// 但read不到数据了，所以map中为空，你getParrameter当然为空。
}
