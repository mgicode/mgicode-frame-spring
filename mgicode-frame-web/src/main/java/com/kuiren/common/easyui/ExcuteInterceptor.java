package com.kuiren.common.easyui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kuiren.common.spring.SpringInit;
import com.kuiren.common.util.StringUtil;

public class ExcuteInterceptor {

	protected static final Log logger = LogFactory
			.getLog(ExcuteInterceptor.class);
	public static List<ExcuteInterceptVo> beforeIntercept = new ArrayList<ExcuteInterceptVo>();
	public static List<ExcuteInterceptVo> aftenIntercept = new ArrayList<ExcuteInterceptVo>();

	public boolean check(ExcuteInterceptVo v) {

		return true;
	}

	public static ExcuteInterceptVo findInterceptVo(String service,
			String method, int type) {

		if (StringUtil.IsNullOrEmpty(service)
				|| StringUtil.IsNullOrEmpty(method)) {
			if (logger.isDebugEnabled()) {
				logger.debug("service:" + service + "," + "method:" + method);
			}

			return null;
		}
		List<ExcuteInterceptVo> list = null;
		if (type == 1) {
			list = beforeIntercept;
		} else {
			list = aftenIntercept;
		}
		for (ExcuteInterceptVo v : list) {
			if (service.equals(v.getInterceptService())
					&& method.equals(v.getInterceptMethod())) {
				return v;
			}
		}

		return null;
	}



	public static boolean before(JsonRequest request) {
		if (request == null) {
			return true;
		}
		ExcuteInterceptVo v = findInterceptVo(request.getServiceName(),
				request.getMethodName(), 1);
		if (v == null) {
			return true;
		}
		Object o = SpringInit.getApplicationContext().getBean(
				v.getExcuteService());
		try {
			Object ret = JsonRequestUtil.callMethod(o, v.getExcuteMethod(),
					new Object[] { request });
			if (ret instanceof Boolean) {
				return (Boolean) ret;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return true;
	}

	
	public static List<ExcuteInterceptVo> findInterceptVos(String service,
			String method, int type) {

		if (StringUtil.IsNullOrEmpty(service)
				|| StringUtil.IsNullOrEmpty(method)) {
			if (logger.isDebugEnabled()) {
				logger.debug("service:" + service + "," + "method:" + method);
			}

			return null;
		}
		List<ExcuteInterceptVo> list = null;
		if (type == 1) {
			list = beforeIntercept;
		} else {
			list = aftenIntercept;
		}

		List<ExcuteInterceptVo> retList = new ArrayList<ExcuteInterceptVo>();
		for (ExcuteInterceptVo v : list) {
			if (service.equals(v.getInterceptService())
					&& method.equals(v.getInterceptMethod())) {
				retList.add(v);
				// return v;
			}
		}
		if (retList.size() > 0) {
			return retList;
		}

		return null;
	}
	public static Object after(JsonRequest request, Object excuteObj) {
		if (request == null) {
			return null;
		}
		List<ExcuteInterceptVo> vs = findInterceptVos(request.getServiceName(),
				request.getMethodName(), 2);
		if (vs == null) {
			return null;
		}
		Object ret = null;
		for (ExcuteInterceptVo v : vs) {
			Object o = SpringInit.getApplicationContext().getBean(
					v.getExcuteService());
			try {
				Object retv = JsonRequestUtil.callMethod(o,
						v.getExcuteMethod(),
						new Object[] { request, excuteObj });
				if (logger.isDebugEnabled()) {
					logger.debug("已经执行" + request.getServiceName() + "服务类的"
							+ request.getMethodName() + "方法");
				}
				if (retv != null) {
					ret = retv;
				}
				// return ret;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	// public static Object after(JsonRequest request, Object excuteObj) {
	// if (request == null) {
	// return null;
	// }
	// ExcuteInterceptVo v = findInterceptVo(request.getServiceName(),
	// request.getMethodName(), 2);
	// if (v == null) {
	// return null;
	// }
	// Object o = SpringInit.getApplicationContext().getBean(
	// v.getExcuteService());
	// try {
	// Object ret = JsonRequestUtil.callMethod(o, v.getExcuteMethod(),
	// new Object[] { request, excuteObj });
	// return ret;
	// } catch (Throwable e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }
}
