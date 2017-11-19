package com.kuiren.common.util;//package com.kuiren.common.util;
//
//import java.lang.reflect.Array;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.text.SimpleDateFormat;
//import java.util.Collection;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.Locale;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONException;
//import net.sf.json.JSONNull;
//import net.sf.json.JSONObject;
//import net.sf.json.util.JSONStringer;
//
//
///**
// * @author pengrk
// * @email:sjkjs155@126.com
// * @wetsite:www.mgicode.com
// * @license:GPL
// */
//public class JsonUtil {
//	private static String TAG = "JSONHelper";
//
//	/**
//	 * 将对象转换成Json字符串
//	 */
//	public static String toJSON(Object obj) {
//		JSONStringer js = new JSONStringer();
//		serialize(js, obj);
//		return js.toString();
//	}
//
//	/**
//	 * 序列化为JSON
//	 */
//	private static void serialize(JSONStringer js, Object o) {
//
//		if (isNull(o)) {
//			try {
//				js.value(null);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return;
//		}
//
//		Class<?> clazz = o.getClass();
//		if (TypeUtil.isObject(clazz)) { // 对象
//			serializeObject(js, o);
//		} else if (TypeUtil.isArray(clazz)) { // 数组
//			serializeArray(js, o);
//		} else if (TypeUtil.isCollection(clazz)) { // 集合
//			Collection<?> collection = (Collection<?>) o;
//			serializeCollect(js, collection);
//		} else { // 单个值
//			try {
//				js.value(o);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/** 序列化数组 **/
//
//	private static void serializeArray(JSONStringer js, Object array) {
//		try {
//			js.array();
//			for (int i = 0; i < Array.getLength(array); ++i) {
//				Object o = Array.get(array, i);
//				serialize(js, o);
//			}
//			js.endArray();
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}
//
//	/** 序列化集合 **/
//
//	private static void serializeCollect(JSONStringer js,
//			Collection<?> collection) {
//		try {
//			js.array();
//			for (Object o : collection) {
//				serialize(js, o);
//			}
//
//			js.endArray();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * 
//	 * 序列化对象
//	 * 
//	 * 
//	 * 
//	 * **/
//
//	private static void serializeObject(JSONStringer js, Object obj) {
//		try {
//			js.object();
//			for (Field f : obj.getClass().getFields()) {
//				Object o = f.get(obj);
//				js.key(f.getName());
//				serialize(js, o);
//			}
//
//			js.endObject();
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}
//
//	/**
//	 * 
//	 * 反序列化简单对象
//	 * 
//	 * 
//	 * 
//	 * @throws
//	 **/
//
//	public static <T> T parseObject(JSONObject jo, Class<T> clazz) {
//		if (clazz == null || isNull(jo)) {
//			return null;
//		}
//
//		T obj = createInstance(clazz);
//		if (obj == null) {
//			return null;
//		}
//		// 取出bean里的所有方法
//		Method[] methods = clazz.getDeclaredMethods();
//		Field[] fields = clazz.getDeclaredFields();
//		for (Field f : fields) {
//			String setMetodName = parseMethodName(f.getName(), "set");
//			if (!haveMethod(methods, setMetodName)) {
//				continue;
//			}
//			try {
//				Method fieldMethod = clazz.getMethod(setMetodName, f.getType());
//				setField(obj, fieldMethod, f, jo);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		return obj;
//
//	}
//
//	/**
//	 * 
//	 * 反序列化简单对象
//	 * 
//	 * 
//	 * 
//	 * @throws
//	 **/
//
//	public static <T> T parseObject(String jsonString, Class<T> clazz) {
//
//		if (clazz == null || jsonString == null || jsonString.length() == 0) {
//			return null;
//		}
//		JSONObject jo = null;
//		try {
//			jo = JSONObject.fromObject(jsonString);
//			// new JSONObject(jsonString);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		if (isNull(jo)) {
//			return null;
//		}
//
//		return parseObject(jo, clazz);
//
//	}
//
//	public static int getSize(JSONArray ja) {
//		// Iterator遍历二
//		int i1 = 0;
//		for (Iterator it = ja.iterator(); it.hasNext();) {
//			i1++;
//		}
//		int len = i1;
//		return len;
//	}
//
//	/**
//	 * 
//	 * 反序列化数组对象
//	 * 
//	 * 
//	 * 
//	 * @throws
//	 **/
//
//	public static <T> T[] parseArray(JSONArray ja, Class<T> clazz) {
//		if (clazz == null || isNull(ja)) {
//			return null;
//		}
//		int len = getSize(ja);
//		@SuppressWarnings("unchecked")
//		T[] array = (T[]) Array.newInstance(clazz, len);
//
//		for (int i = 0; i < len; ++i) {
//			try {
//				JSONObject jo = ja.getJSONObject(i);
//				T o = parseObject(jo, clazz);
//				array[i] = o;
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		return array;
//
//	}
//
//	/**
//	 * 
//	 * 反序列化数组对象
//	 * 
//	 * 
//	 * 
//	 * @throws
//	 **/
//
//	public static <T> T[] parseArray(String jsonString, Class<T> clazz) {
//		if (clazz == null || jsonString == null || jsonString.length() == 0) {
//			return null;
//		}
//
//		JSONArray jo = null;
//		try {
//			jo = JSONArray.fromObject(jsonString);
//			// jo = new JSONArray(jsonString);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		if (isNull(jo)) {
//			return null;
//		}
//
//		return parseArray(jo, clazz);
//
//	}
//
//	/**
//	 * 
//	 * 反序列化泛型集合
//	 * 
//	 * 
//	 * 
//	 * @throws
//	 **/
//
//	@SuppressWarnings("unchecked")
//	public static <T> Collection<T> parseCollection(JSONArray ja,
//			Class<?> collectionClazz, Class<T> genericType) {
//		if (collectionClazz == null || genericType == null || isNull(ja)) {
//			return null;
//
//		}
//
//		Collection<T> collection = (Collection<T>) createInstance(collectionClazz);
//		int len = getSize(ja);
//		for (int i = 0; i < len; ++i) {
//			try {
//				JSONObject jo = ja.getJSONObject(i);
//				T o = parseObject(jo, genericType);
//				collection.add(o);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//		return collection;
//
//	}
//
//	/**
//	 * 
//	 * 反序列化泛型集合
//	 * 
//	 * 
//	 * 
//	 * @throws
//	 **/
//
//	public static <T> Collection<T> parseCollection(String jsonString,
//			Class<?> collectionClazz, Class<T> genericType) {
//		if (collectionClazz == null || genericType == null
//				|| jsonString == null || jsonString.length() == 0) {
//			return null;
//		}
//		JSONArray jo = null;
//		try {
//			jo = JSONArray.fromObject(jsonString);
//			// jo = new JSONArray(jsonString);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		if (isNull(jo)) {
//			return null;
//		}
//		return parseCollection(jo, collectionClazz, genericType);
//
//	}
//
//	/** 根据类型创建对象 **/
//
//	private static <T> T createInstance(Class<T> clazz) {
//
//		if (clazz == null)
//			return null;
//		T obj = null;
//		try {
//			obj = clazz.newInstance();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return obj;
//
//	}
//
//	/** 设定字段的值 **/
//
//	private static void setField(Object obj, Field f, JSONObject jo) {
//		String name = f.getName();
//		Class<?> clazz = f.getType();
//
//		try {
//			
//			if (TypeUtil.isArray(clazz)) { // 数组
//				Class<?> c = clazz.getComponentType();
//				JSONArray ja = jo.optJSONArray(name);
//				if (!isNull(ja)) {
//					Object array = parseArray(ja, c);
//					f.set(obj, array);
//				}
//
//			} else if (TypeUtil.isCollection(clazz)) { // 泛型集合
//
//				// 获取定义的泛型类型
//
//				Class<?> c = null;
//				Type gType = f.getGenericType();
//				if (gType instanceof ParameterizedType) {
//					ParameterizedType ptype = (ParameterizedType) gType;
//					Type[] targs = ptype.getActualTypeArguments();
//					if (targs != null && targs.length > 0) {
//						Type t = targs[0];
//						c = (Class<?>) t;
//					}
//
//				}
//
//				JSONArray ja = jo.optJSONArray(name);
//				if (!isNull(ja)) {
//					Object o = parseCollection(ja, clazz, c);
//					f.set(obj, o);
//				}
//
//			} else if (TypeUtil.isSingle(clazz)) { // 值类型
//
//				Object o = jo.opt(name);
//				if (o != null) {
//					f.set(obj, o);
//				}
//
//			} else if (TypeUtil.isObject(clazz)) { // 对象
//				JSONObject j = jo.optJSONObject(name);
//				if (!isNull(j)) {
//					Object o = parseObject(j, clazz);
//					f.set(obj, o);
//				}
//
//			} else {
//
//				// throw new Exception("unknow type!");
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}
//
//	/**
//	 * 设定字段的值
//	 * 
//	 * @param obj
//	 *            待赋值字段的对象
//	 * @param fieldSetMethod
//	 *            字段方法名
//	 * @param field
//	 *            字段
//	 * @param jo
//	 *            json实例
//	 */
//	private static void setField(Object obj, Method fieldSetMethod,
//			Field field, JSONObject jo) {
//		String name = field.getName();
//		Class<?> clazz = field.getType();
//		try {
//			if (TypeUtil.isArray(clazz)) { // 数组
//				Class<?> c = clazz.getComponentType();
//				JSONArray ja = jo.optJSONArray(name);
//				if (!isNull(ja)) {
//					Object array = parseArray(ja, c);
//					setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(),
//							array);
//				}
//			} else if (TypeUtil.isCollection(clazz)) { // 泛型集合
//				// 获取定义的泛型类型
//				Class<?> c = null;
//				Type gType = field.getGenericType();
//				if (gType instanceof ParameterizedType) {
//					ParameterizedType ptype = (ParameterizedType) gType;
//					Type[] targs = ptype.getActualTypeArguments();
//					if (targs != null && targs.length > 0) {
//						Type t = targs[0];
//						c = (Class<?>) t;
//					}
//				}
//
//				JSONArray ja = jo.optJSONArray(name);
//				if (!isNull(ja)) {
//					Object o = parseCollection(ja, clazz, c);
//					setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), o);
//				}
//			} else if (TypeUtil.isSingle(clazz)) { // 值类型
//				Object o = jo.opt(name);
//				if (o != null) {
//					setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), o);
//				}
//			} else if (TypeUtil.isObject(clazz)) { // 对象
//				JSONObject j = jo.optJSONObject(name);
//				if (!isNull(j)) {
//					Object o = parseObject(j, clazz);
//					setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), o);
//				}
//			} else if (TypeUtil.isList(clazz)) { // 列表
//				// JSONObject j = jo.optJSONObject(name);
//				// if (!isNull(j)) {
//				// Object o = parseObject(j, clazz);
//				// f.set(obj, o);
//				// }
//			} else {
//				throw new Exception("unknow type!");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 给对象的字段赋值
//	 * 
//	 * @param obj
//	 *            类实例
//	 * @param fieldSetMethod
//	 *            字段方法
//	 * @param fieldType
//	 *            字段类型
//	 * @param value
//	 */
//	public static void setFiedlValue(Object obj, Method fieldSetMethod,
//			String fieldType, Object value) {
//
//		try {
//			if (null != value && !"".equals(value)) {
//				if ("String".equals(fieldType)) {
//					fieldSetMethod.invoke(obj, value.toString());
//				} else if ("Date".equals(fieldType)) {
//					SimpleDateFormat sdf = new SimpleDateFormat(
//							"yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//					Date temp = sdf.parse(value.toString());
//					fieldSetMethod.invoke(obj, temp);
//				} else if ("Integer".equals(fieldType)
//						|| "int".equals(fieldType)) {
//					Integer intval = Integer.parseInt(value.toString());
//					fieldSetMethod.invoke(obj, intval);
//				} else if ("Long".equalsIgnoreCase(fieldType)) {
//					Long temp = Long.parseLong(value.toString());
//					fieldSetMethod.invoke(obj, temp);
//				} else if ("Double".equalsIgnoreCase(fieldType)) {
//					Double temp = Double.parseDouble(value.toString());
//					fieldSetMethod.invoke(obj, temp);
//				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
//					Boolean temp = Boolean.parseBoolean(value.toString());
//					fieldSetMethod.invoke(obj, temp);
//				} else {
//					fieldSetMethod.invoke(obj, value);
//
//				}
//			}
//
//		} catch (Exception e) {
//			// Log.e(TAG, TAG + ">>>>>>>>>>set value error.",e);
//			e.printStackTrace();
//		}
//
//	}
//
//	/** 判断对象是否为空 **/
//
//	   public static boolean isNull( Object obj ) {
//		      if( obj instanceof JSONObject ){
//		         return ((JSONObject) obj).isNullObject();
//		      }
//		      return JSONNull.getInstance()
//		            .equals( obj );
//		   }
//
//
//	/**
//	 * 拼接某属性的 get或者set方法
//	 * 
//	 * @param fieldName
//	 *            字段名称
//	 * @param methodType
//	 *            方法类型
//	 * @return 方法名称
//	 */
//	public static String parseMethodName(String fieldName, String methodType) {
//		if (null == fieldName || "".equals(fieldName)) {
//			return null;
//		}
//		return methodType + fieldName.substring(0, 1).toUpperCase()
//				+ fieldName.substring(1);
//	}
//
//	/**
//	 * 判断是否存在某属性的 get方法
//	 * 
//	 * @param methods
//	 *            引用方法的数组
//	 * @param fieldMethod
//	 *            方法名称
//	 * @return true或者false
//	 */
//	public static boolean haveMethod(Method[] methods, String fieldMethod) {
//		for (Method met : methods) {
//			if (fieldMethod.equals(met.getName())) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//}