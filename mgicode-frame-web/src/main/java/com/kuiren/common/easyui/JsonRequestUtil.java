package com.kuiren.common.easyui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.kuiren.common.context.Executor;
import com.kuiren.common.exception.BizRuntimeException;
import com.kuiren.common.util.JacksonHelper;
import com.kuiren.common.util.StringUtil;

public class JsonRequestUtil {
	private static final Log log = LogFactory.getLog(JsonRequestUtil.class);
	public static Map<String, Object[]> clzMethodMap = new HashMap<String, Object[]>();

	public static String TYPE_EASYUI_JSON = "json";
	public static String TYPE_EASYUI_PAGE = "page";

	/**
	 * 把post的json字符串转换为map
	 * 
	 * @author 彭仁夔于2015年12月25日上午11:23:20创建
	 * @param postStr
	 * @param needTrim
	 *            如果为true,把其前后的空格去掉
	 * @return
	 * 
	 */
	public static Map getMapFromJson(String postStr, boolean needTrim) {
		if (postStr == null) {
			return new HashMap<String, Object>();
		}
		if (postStr.startsWith("[")) {
			postStr = "{defvalue:" + postStr + "}";
		}
		Map map =  new Gson().fromJson(postStr,Map.class);//JacksonHelper.getMapFromJson(postStr);

		if (needTrim) {
			Map retMap = new HashMap();
			for (Object key : map.keySet()) {

				Object v = map.get(key);

				if (v instanceof String && (v != null)) {
					v = ((String) v).trim();
				}
				retMap.put(key, v);

			}
			return retMap;
		}

		return map;
	}

	/**
	 * 多条件的从request取得值，即request的name可以为多种，如果service,s取出的值都代表同一个
	 * 
	 * @author 彭仁夔于2015年12月25日上午11:25:48创建
	 * @param request
	 * @param name
	 * @return
	 * 
	 */

	public static String getParams(HttpServletRequest request, String name) {
		if (name == null)
			return null;
		String[] ns = name.split(",");
		if (ns != null) {
			for (String os : ns) {
				String s = os.trim();
				String v = request.getParameter(s);
				if (v != null && !v.equals("")) {
					return v;

				}
			}
		}

		return null;
	}

	/**
	 * 去掉所有
	 * 
	 * @author:彭仁夔 于2014年12月2日上午9:27:12创建
	 * @param map
	 * @return
	 */
	public static Map removeEmptyObj(Map map) {
		return map;
		// return removeEmptyObj(null, null, map, "id");
	}

	// public static Map removeEmptyObj(Map pMap, String mapkey, Map map,
	// String idname) {
	// if (map.isEmpty()) {
	// if (pMap != null) {
	// pMap.remove(mapkey);
	// }
	// }
	// if (map.containsKey(idname)) {
	// String id = (String) map.get(idname);
	// if (StringUtil.IsNullOrEmpty(id)) {
	// if (pMap != null) {
	// pMap.remove(mapkey);
	// }
	// }
	// }
	//
	// for (Object o : map.keySet()) {
	// Object v = map.get(o);
	// if (v instanceof Map && (o instanceof String)) {
	// removeEmptyObj(map, (String) o, (Map) v, idname);
	//
	// }
	//
	// }
	// return pMap;
	// }

	public static boolean removeEmptyObj(Map pMap, String mapkey, Map map,
			String idname) {

		if (map.isEmpty()) {
			return false;
		} else if (map.containsKey(idname)) {
			String id = (String) map.get(idname);
			if (StringUtil.IsNullOrEmpty(id)) {
				return false;
			}
		}

		Iterator<Map.Entry> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = it.next();
			Object key = entry.getKey();
			Object value = map.get(key);
			if (value instanceof Map) {
				boolean flag = removeEmptyObj(pMap, (String) key, (Map) value,
						idname);
				if (flag == false) {
					it.remove();
				}
			}

		}

		return true;

	}

	/**
	 * 
	 * @author 彭仁夔于2015年12月23日下午7:43:14创建
	 * @param obj
	 * @param method
	 * @return
	 * @throws Throwable
	 * 
	 */
	public static Method getMethod(Object obj, String method, String type)
			throws Throwable {

		Method[] listMethod = obj.getClass().getMethods();
		for (Method tmp : listMethod) {
			if (tmp.getName().equals(method)) {
				Class[] paramsClazz = tmp.getParameterTypes();
				if (type.equals(JsonRequestUtil.TYPE_EASYUI_JSON)) {
					if ((paramsClazz.length == 1)
							&& (paramsClazz[0].getName() == JsonRequest.class
									.getName())) {
						return tmp;
					}
				} else if (type.equals(JsonRequestUtil.TYPE_EASYUI_PAGE)) {
					if (paramsClazz.length == 0)
						return tmp;
					else if ((paramsClazz.length == 1)
							&& (paramsClazz[0].getName() == JsonRequest.class
									.getName())) {
						return tmp;
					} else if ((paramsClazz.length == 2)
							&& (paramsClazz[0].getName() == HttpServletRequest.class
									.getName())
							&& (paramsClazz[1].getName() == HttpServletResponse.class
									.getName())) {
						return tmp;
					}

				}
			}

		}
		return null;
		// throw new BizRuntimeException("类：" + obj.getClass().getName() +
		// "中未找到"
		// + method + "方法!");
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
	public static Object callMethod(Object obj, String method, Object[] req)
			throws Throwable {

		for (Method tmp : obj.getClass().getMethods()) {
			if (tmp.getName().equals(method)) {
				Class[] paramsClazz = tmp.getParameterTypes();
				// if (paramsClazz.length != 1) {
				// throw new WlfpRuntimeException("方法输入参数超过一个！无法调用！");
				// }
				if (tmp.getReturnType().equals(Void.TYPE)) {
					tmp.invoke(obj, req);
					return null;
				} else {
					Object res;
					// try {
					res = tmp.invoke(obj, req);
					// } catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					// throw e.getTargetException();
					// }
					return res;
				}
			}
		}
		throw new BizRuntimeException("类：" + obj.getClass().getName() + "中未找到"
				+ method + "方法!");
	}

	/**
	 * 
	 * @author 彭仁夔于2015年12月23日下午7:43:02创建
	 * @param obj
	 * @param method
	 * @param req
	 * @return
	 * @throws Throwable
	 * 
	 */
	public static Object callMethod(Object obj, Method method, Object[] req)
			throws Throwable {
		Method tmp = method;
		if (tmp.getReturnType().equals(Void.TYPE)) {
			tmp.invoke(obj, req);
			return null;
		} else {
			Object res;
			// try {
			res = tmp.invoke(obj, req);
			// } catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			// throw e.getTargetException();
			// }
			return res;
		}

	}

	/**
	 * 根据 s和m找到对应的处理类和方法名， 通过动态计算，在原来的service类和请求之间加上一个Action层，
	 * 能兼容于现在的代码，如果action类不存在就找其对应的service类，
	 * 如果action类存在，其方法不存在，找service对应的方法，如果不存在说明有问题
	 * action类以Action结尾，把其结尾Action去掉，加上Service就是对应的service类，
	 * 如果其不存在，就找不加的service名的类（userCenter有这个问题）
	 * 
	 * @author 彭仁夔于2015年12月23日下午5:50:03创建
	 * @param serviceName
	 * @param serviceMethod
	 * @return
	 * @throws Throwable
	 * 
	 */
	public static Object[] dyncCalClzMethod(String serviceName,
			String serviceMethod, String type) throws Throwable {
		if (serviceName.equals("userCenter")) {// 兼容现有userCenter
			serviceName = "userCenterService";
		}
		// 根据名称进行缓存
		String mapName = serviceName + "_" + serviceMethod;
		if (clzMethodMap.containsKey(mapName)) {
			return clzMethodMap.get(mapName);
		}

		if (type == TYPE_EASYUI_PAGE && !serviceName.endsWith("Action")) {
			// TYPE_EASYUI_PAGE中默认不需要加Action
			serviceName = serviceName + "Action";
		}

		// 第一步是名称不做变化
		Object[] rets = commonMap(serviceName, serviceMethod, null, null,
				mapName, type);

		// 看看对应的Action中有没有
		if (rets == null && (serviceName.endsWith("Service"))) {
			rets = commonMap(serviceName, serviceMethod, "Service", "Action",
					mapName, type);
		}
		// 看看对应的Service中有没有
		if (rets == null && (serviceName.endsWith("Action"))) {
			rets = commonMap(serviceName, serviceMethod, "Action", "Service",
					mapName, type);
		}
		// 看看把后缀的Service去掉看看
		if (rets == null && (serviceName.endsWith("Service"))) {
			rets = commonMap(serviceName, serviceMethod, "Service", "",
					mapName, type);
		}

		// 看看把后缀的Action去掉看看
		if (rets == null && (serviceName.endsWith("Action"))) {
			rets = commonMap(serviceName, serviceMethod, "Action", "", mapName,
					type);
		}

		if (rets == null) {
			String str = "类：" + serviceName + "中未找到" + serviceMethod + "方法!";

			if (log.isErrorEnabled()) {
				log.error(str);
			}

			throw new BizRuntimeException(str);

		} else {
			return rets;
		}

	}

	public static Object[] commonMap(String serviceName, String serviceMethod,
			String removeName, String addName, String mapName, String type)
			throws Throwable {

		ApplicationContext ac = JsonRequestContext.getContext();

		String sname = serviceName;
		if (removeName == null && addName == null) {
			sname = serviceName;
		} else if (removeName != null && addName != null) {
			String sbname = StringUtil.replaceString(serviceName, removeName,
					"");// todo:
			sname = sbname + addName;
		} else if (removeName != null) {
			sname = StringUtil.replaceString(serviceName, removeName, "");// todo:
		} else if (addName != null) {
			sname = serviceName + addName;
		}

		String str = "\n从" + sname + "类查找" + serviceMethod + "方法";
		// System.out.print(str);

		if (ac.containsBean(sname)) {
			Object service = ac.getBean(sname);
			Method m = JsonRequestUtil.getMethod(service, serviceMethod, type);
			if (m != null) {
				// System.out.println("，已经找到！！");
				if (log.isDebugEnabled()) {
					log.debug(str + "，已经找到！！");
				}
				clzMethodMap.put(mapName, new Object[] { service,
						serviceMethod, m });
				return clzMethodMap.get(mapName);
			} else {

				str = "\n找到了" + sname + "的Spring服务,但是没有找到" + serviceMethod
						+ "方法";
			}
		} else {

			str = "\n没有找到" + sname
					+ "的Spring服务，可能该类没有注册到Spring中，或类所在jar包或项目没有加载。";
		}

		// str = "\n没有找到" + sname + "类，或该类中没有找" + serviceMethod + "方法";
		// System.out.println(str);
		if (log.isDebugEnabled()) {
			log.debug(str);
		}
		return null;

	}

	public static TransactionTemplate getTransactionTemplate() {

		// todo:srpingInit取不到transactionTemplate报错
		TransactionTemplate tt = (TransactionTemplate) JsonRequestContext
				.getContext().getBean("transactionTemplate");

		return tt;

	}

	public static Object transactionExcute(final Executor e) throws Throwable {
		// try {
		JsonRequest request = JsonRequestContext.getJsonRequest();
		boolean f = ExcuteInterceptor.before(request);
		// 前拦截返回false,不执行
		if (f == true) {
			Object obj = e.execute();
			// 后拦截返回null,直接返回执行结果，否则返回后拦截的结果
			Object ret = ExcuteInterceptor.after(request, obj);
			if (ret == null) {
				return obj;
			} else {
				return ret;
			}
		}
		// } catch (Throwable e1) {
		// e1.printStackTrace();
		// }
		return null;
		// TransactionTemplate tt = getTransactionTemplate();
		// return tt.execute(new TransactionCallback() {
		// public Object doInTransaction(TransactionStatus status) {
		// try {
		// if (log.isDebugEnabled()) {
		// log.debug("\n" + status.toString() + "，是否为新事务："
		// + status.isNewTransaction());
		// }
		// return e.execute();
		// } catch (Throwable e) {
		// status.setRollbackOnly();
		// e.printStackTrace();
		// if (log.isErrorEnabled()) {
		// log.error(e);
		// }
		// }
		//
		// return null;
		// }
		// });

	}
	// public void doLogic1() {
	// transactionTemplate.execute(new TransactionCallbackWithoutResult() {
	// protected void doInTransactionWithoutResult(TransactionStatus status) {
	// try {
	// doComplexLogic();
	// } catch (Exception ex) {
	// // 通过调用 TransactionStatus 对象的 setRollbackOnly() 方法来回滚事务。
	// status.setRollbackOnly();
	// ex.printStackTrace();
	// }
	// }
	// });
	// }

	// public String doLogic2() {
	// return transactionTemplate.execute(new TransactionCallback<String>() {
	// public String doInTransaction(TransactionStatus status) {
	// String result = "success";
	// try {
	// doComplexLogic();
	// } catch (Exception e) {
	// status.setRollbackOnly();
	// e.printStackTrace();
	// result = "fail";
	// }
	// return result;
	// }
	// });
	// }
	// public static Object transactionExecute(Executor executor) throws
	// Throwable {
	// boolean flag = false;
	// if (ApplicationContextUtils.getThreadLocal().get() == null) {
	// ApplicationContextUtils.getThreadLocal().set(new BizContext());
	// flag = true;
	// }
	// Object ret = null;
	// try {
	// ret = executor.execute();
	// if (flag && ApplicationContextUtils.getThreadLocal().get() != null) {
	// // 如果存在数据库事务
	// if (ApplicationContextUtils.getThreadLocal().get().ut != null) {
	// // datawindow事物
	// if (ApplicationContextUtils.getBizContext().trfcSave.size() > 0) {
	//
	// }
	// ApplicationContextUtils.getBizContext().trfcSave.clear();
	//
	// ApplicationContextUtils.getThreadLocal().get().ut.commit();
	// }
	// if (ApplicationContextUtils.getBizContext().transactionEndCommands
	// .size() > 0) {
	// // 这里应该修改为通过JMS异步的方式发送请求，这里暂时定为同步调用；
	// try {
	// for (Runnable command : ApplicationContextUtils
	// .getBizContext().transactionEndCommands) {
	// AsynTaskService.getInstance().execute(command);
	//
	// }
	// } catch (Throwable e) {
	// e.printStackTrace();
	// log.error("transactionEndCommands执行失败！错误原因如下");
	// log.error(e);
	// }
	// ApplicationContextUtils.getBizContext().transactionEndCommands
	// .clear();
	//
	// }
	// }
	// } catch (Throwable e) {
	// e.printStackTrace();
	// log.error(e);
	// if (flag && ApplicationContextUtils.getThreadLocal().get() != null) {//
	// // 如果存在数据库事务
	// if (ApplicationContextUtils.getThreadLocal().get().ut != null) {
	// // datawindow事物
	// ApplicationContextUtils.getThreadLocal().get().ut
	// .rollback();
	// }
	// }
	// throw e;
	// } finally {
	// // 将事务状态置成空
	// if (flag && ApplicationContextUtils.getThreadLocal().get() != null) {
	//
	// ApplicationContextUtils.getThreadLocal().set(null);
	// }
	//
	// }
	// return ret;
	// }

}
