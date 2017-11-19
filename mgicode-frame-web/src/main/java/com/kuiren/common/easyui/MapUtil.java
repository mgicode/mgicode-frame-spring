package com.kuiren.common.easyui;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.kuiren.common.util.DateUtil;
import com.kuiren.common.util.StringUtil;

public class MapUtil {

	public static Map clone(Map source) {
		return copy(source, null, false, false);
	}

	public static Map fetchFromRequestParameterMap(Map source, boolean only,
			boolean force) {
		return dealDotJson(copy(source, null, only, force));
	}

	/**
	 * 
	 * @author:彭仁夔 于2014年11月3日上午8:31:21创建
	 * @param map1
	 * @return
	 */
	public static Map dealDotJson(Map map1) {

		Map map = new HashMap();
		boolean loop = false;
		for (Object s : map1.keySet()) {
			if (s instanceof String) {
				buildDotMap((String) s, map1.get(s), map);
			} else {
				Object v = map1.get(s);
				if (v instanceof String && v != null) {
					map.put(s, ((String) v).trim());
				} else {
					map.put(s, v);
				}

			}

		}
		return map;
	}

	/**
	 * 
	 * @author:彭仁夔 于2014年11月3日上午8:46:56创建,，trim string
	 * @param s
	 * @param v
	 * @param map
	 */
	public static void buildDotMap(String s, Object v, Map map) {
		if (StringUtil.IsNullOrEmpty(s))
			return;
		String[] strs = s.split(".");
		if (strs.length > 1) {
			int subpos = s.indexOf(".");
			String sub = s.substring(subpos);

			Map cmap = new HashMap();
			map.put(strs[0], cmap);

			if (sub.split("[.]").length > 1) {
				buildDotMap(sub, v, cmap);
			} else {
				if (v instanceof String && v != null) {
					cmap.put(sub, ((String) v).trim());
				} else {
					cmap.put(sub, v);
				}
			}
		} else {
			if (v instanceof String && v != null) {
				map.put(s, ((String) v).trim());
			} else {
				map.put(s, v);
			}
		}

	}

	/**
	 * 去除id为null的关联对象，条件id的名称统一为id,
	 * 
	 * @author:彭仁夔 于2014年11月4日下午4:33:39创建
	 * @param map
	 * @return
	 */
	// public static Map removeEmptyObj(Map map) {
	// Map retMap = new HashMap();
	//
	// for (Object obj : map.keySet()) {
	// Object val = map.get(obj);
	// if (val instanceof List) {
	// for (Object ov : (List) val) {
	// if (ov instanceof Map) {
	// Map vMap = (Map) val;
	//
	// if (vMap.containsKey("id") && vMap.get("id") == null) {
	//
	// } else {
	// retMap.put(obj, removeEmptyObj(vMap));
	// }
	//
	// } else {
	//
	// }
	// }
	//
	// } else if (val instanceof Map) {
	// Map vMap = (Map) val;
	// if (vMap.containsKey("id") && vMap.get("id") == null) {
	// } else {
	// retMap.put(obj, removeEmptyObj(vMap));
	// }
	//
	// } else {
	// retMap.put(obj, map.get(obj));
	// }
	//
	// }
	// return retMap;
	// }

	public static Map copy(Map source, Map dest, boolean only, boolean force) {

		if (dest == null)
			dest = new HashMap<Object, Object>();
		for (Object obj : source.keySet()) {
			if (only == true) {
				Object object = source.get(obj);
				if (object.getClass().isArray()) {
					Object[] objs = (Object[]) object;
					if (objs == null || objs.length == 0)
						continue;
					Object o = objs[0];
					if (objs.length == 1 || force == true) {
						dest.put(obj, o);
					} else {
						dest.put(obj, source.get(obj));

					}

				}
			} else {
				dest.put(obj, source.get(obj));
			}
		}

		return dest;
	}

	public static void remove(String names, Map map) {

		if (StringUtil.IsNullOrEmpty(names) || map == null)
			return;

		String[] ns = names.split(",");
		if (ns != null) {
			for (String os : ns) {
				String s = os.trim();
				if (map.containsKey(s)) {
					map.remove(s);
				}
			}
		}
	}

	/**
	 * 将一个 Map 对象转化为一个 JavaBean
	 * 
	 * @param type
	 *            要转化的类型
	 * @param map
	 *            包含属性值的 map
	 * @return 转化出来的 JavaBean 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InstantiationException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	public static <T> T map2Obj(Class<T> type, Map map)
			throws IntrospectionException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		T obj = type.newInstance(); // 创建 JavaBean 对象

		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();

			if (map.containsKey(propertyName)) {
				// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
				Object value = map.get(propertyName);
				if (value instanceof String) {
					// String 进行转换
					Class<?> t = descriptor.getPropertyType();
					// BeanUtil.isSimpleType(property)
					if (String.class.isAssignableFrom(t)) {

					} else if (Integer.class.isAssignableFrom(t)) {
						value = Integer.parseInt(value + "");
					} else if (Boolean.class.isAssignableFrom(t)) {
						value = Boolean.parseBoolean(value + "");
					} else if (Date.class.isAssignableFrom(t)) {
						value = DateUtil.parseDateFormat(value + "");
					}
				}

				Object[] args = new Object[1];
				args[0] = value;

				descriptor.getWriteMethod().invoke(obj, args);
			}
		}
		return obj;
	}

	/**
	 * 将一个 JavaBean 对象转化为一个 Map
	 * 
	 * @param bean
	 *            要转化的JavaBean 对象
	 * @return 转化出来的 Map 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	public static Map obj2Map(Object bean) throws IntrospectionException,
			IllegalAccessException, InvocationTargetException {
		Class type = bean.getClass();
		Map returnMap = new HashMap();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);

		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result);
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}
}
