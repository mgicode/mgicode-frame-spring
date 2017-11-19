package com.kuiren.common.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.kuiren.common.easyui.JsonRequest;
//import com.kuiren.common.easyui.JsonRequestUtil;
//import com.kuiren.common.exception.BizRuntimeException;
import com.kuiren.common.spring.SpringInit;
//import com.kuiren.common.util.BeanUtil;
//import com.kuiren.common.util.StringUtil;

public class JsonDyncConvert extends IJsonConvert {
	protected final Log logger = LogFactory.getLog(getClass());
	private String serverName;
	private String methodName;

	public static JsonDyncConvert build(String serverName, String methodName) {
		JsonDyncConvert dc = new JsonDyncConvert(serverName, methodName);
		return dc;
	}

	public JsonDyncConvert(String serverName, String methodName) {
		super();
		this.serverName = serverName;
		this.methodName = methodName;

	}

	@Override
	public Object convert(Object obj, String name, Object value) {

		if (isNullOrEmpty(serverName)) {
			if (logger.isErrorEnabled()) {
				throw new RuntimeException(
						"\nJsonDyncConvert中的serverName不能为null");
			}
		}

		if (isNullOrEmpty(methodName)) {
			if (logger.isErrorEnabled()) {
				throw new RuntimeException(
						"\nJsonDyncConvert中的methodName不能为null");
			}
		}

		try {
			if (getJsonCreator() != null
					&& getJsonCreator().getLimitClz() != null) {
				if (obj.getClass().getName()
						.equals(getJsonCreator().getLimitClz().getName())) {
					if (logger.isDebugEnabled()) {
						logger.debug("\n" + getJsonCreator().getLimitClz()
								+ "限定，已经进入");

					}
					return invoke(this.serverName, this.methodName, obj, name,
							value);
				}

			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("\n 没有指定LimitClz，如果需要，可以在调用JsonCreator的setLimitClz来指定。");
				}
				return invoke(this.serverName, this.methodName, obj, name,
						value);
			}
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error(e);
			}
			e.printStackTrace();
		}

		return null;
	}

	private Object invoke(String servername, String methodname, Object obj,
			String attName, Object val) throws Throwable {
		// IJsonConvert

		Object service = SpringInit.getApplicationContext().getBean(servername);
		Method m = getMethod(service, methodname);
		if (m == null) {
			if (logger.isDebugEnabled()) {
				logger.error("\n没找到" + methodname
						+ "方法，可能是名称或其参数个数或参数类型不对，参数类型为Object,String,Object");
			}
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("servername:" + servername + ",methodname:"
					+ methodname);
		}

		Object str = null;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("servername:" + servername + ",methodname:"
						+ methodname);
			}

			str = (String) callMethod(service, m, new Object[] { obj, attName,
					val });
			if (logger.isDebugEnabled()) {
				logger.debug("转换的结果为：" + str + "");
			}
		} catch (NoSuchMethodException e) {
			logger.error(e);
			e.printStackTrace();
			return null;
		}
		return str;
	}

	public Method getMethod(Object obj, String method) throws Throwable {

		Method[] listMethod = obj.getClass().getMethods();
		for (Method tmp : listMethod) {
			if (tmp.getName().equals(method)) {
				Class[] paramsClazz = tmp.getParameterTypes();
				if ((paramsClazz.length == 3)
						&& (paramsClazz[0].getName() == Object.class.getName())
						&& (paramsClazz[1].getName() == String.class.getName())
						&& (paramsClazz[2].getName() == Object.class.getName())) {
					return tmp;
				}

			}

		}
		return null;

	}



	public static boolean isNullOrEmpty(String str) {

		if (str == null) {
			return true;
		}
		if ("".equals(str.trim())) {
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @author 彭仁夔于2015年12月23日下午7:43:09创建
	 * @param obj
	 * @param method
	 * @param req
	 * @return
	 * @throws Throwable
	 * 
	 */
	public Object callMethod(Object obj, Method method, Object[] req)
			throws Throwable {
		Method tmp = method;
		if (tmp.getReturnType().equals(Void.TYPE)) {
			tmp.invoke(obj, req);
			return null;
		} else {
			Object res;
			try {
				res = tmp.invoke(obj, req);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
			return res;
		}

	}

}
