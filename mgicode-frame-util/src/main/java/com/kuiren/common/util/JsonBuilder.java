package com.kuiren.common.util;



import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class JsonBuilder {
	
	static Set<Class<?>> simpleTypes = new HashSet<Class<?>>();
	static {
		simpleTypes.add(Byte.TYPE);
		simpleTypes.add(Short.TYPE);
		simpleTypes.add(Integer.TYPE);
		simpleTypes.add(Long.TYPE);
		simpleTypes.add(Float.TYPE);
		simpleTypes.add(Double.TYPE);
		simpleTypes.add(Boolean.TYPE);

	}

	

	private static Set<Class<?>> excludeClasses(Class<?>[] excludeClass) {
		Set<Class<?>> excludeClasses = new HashSet<Class<?>>();
		if (excludeClasses == null) {
			return excludeClasses;
		}
		
		for (Class<?> type : excludeClasses) {
			excludeClasses.add(type);
		}
	
		excludeClasses.add(Class.class);

		return excludeClasses;
	}

	private static Set<String> excludeProperties(String[] exculdeProperties) {
		Set<String> exculdePropertyNames = new HashSet<String>();
		if (exculdeProperties == null) {
			return exculdePropertyNames;
		}

		for (String name : exculdeProperties) {
			exculdePropertyNames.add(name);
		}
		return exculdePropertyNames;
	}

	private static Map<String, String> replacePropertyNames(String names) {

		Map<String, String> replaceNamesMaps = new HashMap<String, String>();
		if (names != null && "" != names) {// aa:bb,cc:dd
			String[] nameStrings = names.split(",");
			for (String name : nameStrings) {
				String[] params = name.split(":");
				replaceNamesMaps.put(params[0], params[1]);
			}
		}
		return replaceNamesMaps;
	}

	public static String build(Object o) {

		return build(o, null, null, null);
	}

	public static String build(Object o, String replaceNames) {

		return build(o, null, null, replaceNames);
	}

	public static String build(Object o, String[] exculdeProperties) {

		return build(o, null, exculdeProperties, null);
	}

	public static String build(Object o, String[] exculdeProperties,
			String replaceNames) {

		return build(o, null, exculdeProperties, replaceNames);
	}

	public static String build(Object o, Class<?>[] excludeClasses,
			String[] exculdeProperties, String replaceNames) {

		Map<String, String> replaceNamesMaps = replacePropertyNames(replaceNames);
		Set<Class<?>> excludeClz = excludeClasses(excludeClasses);
		Set<String> exculdePropertyNames = excludeProperties(exculdeProperties);
		try {
			return buildNode("actionbean", o, new StringBuilder(), false,
					replaceNamesMaps, excludeClz, exculdePropertyNames);
		} catch (Exception e) {
		}
		return "";
	}

	public static String buildNode(String targetName, Object in,
			StringBuilder out, boolean incollect,
			Map<String, String> replaceNamesMaps, Set<Class<?>> excludeClz,
			Set<String> exculdePropertyNames) throws Exception {
		if (incollect) {
			out.append(targetName);
			out.append(":");
		}
		if (Collection.class.isAssignableFrom(in.getClass())) {

			buildCollection(targetName, in, out, incollect, replaceNamesMaps,
					excludeClz, exculdePropertyNames);

		} else if (in.getClass().isArray()) {

			buildArray(targetName, in, out, incollect, replaceNamesMaps,
					excludeClz, exculdePropertyNames);

		} else if (Map.class.isAssignableFrom(in.getClass())) {

			buildMap(targetName, in, out, incollect, replaceNamesMaps,
					excludeClz, exculdePropertyNames);

		} else {

			buildObject(targetName, in, out, incollect, replaceNamesMaps,
					excludeClz, exculdePropertyNames);
		}
		// this.objectValues.put(targetName, out.toString());
		return out.toString();
	}

	private static boolean isExcludedType(Class<?> type,
			Set<Class<?>> excludeClasses) {
		for (Class<?> excludedType : excludeClasses) {
			if (excludedType.isAssignableFrom(type)) {
				return true;
			} else if (type.isArray()
					&& excludedType.isAssignableFrom(type.getComponentType())) {
				return true;
			}
		}
		return false;
	}

	private static boolean isExcludedName(String name,
			Set<String> exculdePropertyNames) {
		for (String excludedName : exculdePropertyNames) {
			if (excludedName.trim().equalsIgnoreCase(name.trim())) {
				return true;
			}
		}
		return false;
	}

	private static boolean isSimpleType(Object property) {
		if (property == null) {
			return true;
		}
		Class type = property.getClass();
		return simpleTypes.contains(type)
				|| Number.class.isAssignableFrom(type)
				|| String.class.isAssignableFrom(type)
				|| Boolean.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type);
	}

	private static String getSimpleTypeStr(Object property) {
		if (property == null)
			return "null";
		Class type = property.getClass();
		if (String.class.isAssignableFrom(type)) {
			return quote((String) property);
		} else if (Date.class.isAssignableFrom(type)) {
			return "new Date(" + ((Date) property).getTime() + ")";
		} else {
			return property.toString();
		}
	}

	public static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char c = 0;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 10);

		sb.append('"');
		for (int i = 0; i < len; ++i) {
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\').append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ') {
					// The following takes lower order chars and creates
					// unicode style
					// char literals for them (e.g. \u00F3)
					sb.append("\\u");
					String hex = Integer.toHexString(c);
					int pad = 4 - hex.length();
					for (int j = 0; j < pad; ++j) {
						sb.append("0");
					}
					sb.append(hex);
				} else {
					sb.append(c);
				}
			}
		}

		sb.append('"');
		return sb.toString();
	}

	private static void buildCollection(String targetName, Object in,
			StringBuilder out, boolean incollect,
			Map<String, String> replaceNamesMaps, Set<Class<?>> excludeClz,
			Set<String> exculdePropertyNames) throws Exception {

		int length = ((Collection<?>) in).size(), i = 0;

		out.append("[");
		for (Object value : (Collection<?>) in) {
			if (isSimpleType(value)) {
				out.append(getSimpleTypeStr(value));
			} else {
				buildNode(targetName, value, out, false, replaceNamesMaps,
						excludeClz, exculdePropertyNames);
			}
			if (i++ != (length - 1)) {
				out.append(", ");
			}
		}

		out.append("]");
	}

	private static void buildArray(String targetName, Object in,
			StringBuilder out, boolean incollect,
			Map<String, String> replaceNamesMaps, Set<Class<?>> excludeClz,
			Set<String> exculdePropertyNames) throws Exception {

		int length = Array.getLength(in);

		out.append("[");
		for (int i = 0; i < length; i++) {
			Object value = Array.get(in, i);
			if (isSimpleType(value)) {
				out.append(getSimpleTypeStr(value));
			} else {
				buildNode(targetName, value, out, false, replaceNamesMaps,
						excludeClz, exculdePropertyNames);
			}
			if (i != length - 1) {
				out.append(", ");
			}
		}

		out.append("]");

	}

	private static void buildMap(String targetName, Object in,
			StringBuilder out, boolean incollect,
			Map<String, String> replaceNamesMaps, Set<Class<?>> excludeClz,
			Set<String> exculdePropertyNames) throws Exception {

		out.append("{");

		int oldLength = out.length();
		for (Map.Entry<?, ?> entry : ((Map<?, ?>) in).entrySet()) {
			String propertyName = getSimpleTypeStr(entry.getKey());
			Object value = entry.getValue();

			if (isSimpleType(value)) {
				if (out.length() > oldLength) {
					out.append(", ");
				}
				out.append(propertyName);
				out.append(":");
				out.append(getSimpleTypeStr(value));
			} else {
				if (out.length() > oldLength) {
					out.append(", ");
				}
				buildNode(propertyName, value, out, true, replaceNamesMaps,
						excludeClz, exculdePropertyNames);
			}
		}
		out.append("}");
	}

	private static void buildObject(String targetName, Object in,
			StringBuilder out, boolean incollect,
			Map<String, String> replaceNamesMaps, Set<Class<?>> excludeClz,
			Set<String> exculdePropertyNames) throws Exception {

		Class clazz = in.getClass();
		// 去掉动态生成的类
		// if(clazz.getName().contains("$")){
		// return;
		// }

		out.append("{");
		int oldLength = out.length();

		PropertyDescriptor[] props;
		if (clazz.getName().contains("$")) {
			props = Introspector.getBeanInfo(clazz, clazz.getSuperclass())
					.getPropertyDescriptors();
		} else {

			props = Introspector.getBeanInfo(in.getClass())
					.getPropertyDescriptors();
		}

		if (props != null) {
			for (PropertyDescriptor property : props) {
				// 去掉动态生成的方法
				// if (property.getName().contains("$")) {
				// continue;
				// }
				if (isExcludedType(property.getPropertyType(), excludeClz)) {
					continue;
				}

				if (isExcludedName(property.getName(), exculdePropertyNames)) {
					continue;
				}

				try {

					Method accessor = property.getReadMethod();
					Method readMethod = null;

					if (clazz.getName().indexOf("$$") > -1) { // 如果是CGLIB动态生成的类
						try {
							readMethod = Class.forName(
									clazz.getName().substring(0,
											clazz.getName().indexOf("_$$")))
									.getDeclaredMethod(accessor.getName(),
											accessor.getParameterTypes());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} else {
						// 若不是CGLib生成的类，那么要序列化的属性的accessor方法就是该类中的方法。
						readMethod = accessor;
					}

					if (readMethod != null) {
						Object value = property.getReadMethod().invoke(in);
						if (value == null) {
							continue;
						}

						String key = property.getName();
						if (replaceNamesMaps.get(key.trim()) != null) {
							key = replaceNamesMaps.get(key);
						}

						if (isSimpleType(value)) {

							if (out.length() > oldLength) {
								out.append(", ");
							}
							out.append("\"" + key + "\"");
							out.append(":");
							out.append(getSimpleTypeStr(value));
						} else {
							if (out.length() > oldLength) {
								out.append(", ");
							}
							buildNode(key, value, out, true, replaceNamesMaps,
									excludeClz, exculdePropertyNames);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		out.append("}");
	}

}
