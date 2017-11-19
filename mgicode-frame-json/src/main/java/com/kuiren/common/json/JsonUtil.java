package com.kuiren.common.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ognl.Ognl;

public class JsonUtil {
	public static Object evalByOgnl(String expr, Object in) {
		Object value = null;
		try {
			value = Ognl.getValue(expr, in);
		} catch (Exception e) {
			//e.printStackTrace();
			value = null;
		}
		return value;
	}

	public static List<JsonConfig> getConfigByTargetName(String targetname,
			List<JsonConfig> list1) {
		List<JsonConfig> list = new ArrayList<JsonConfig>();
		for (JsonConfig kv : list1) {
			// init(kv);
			if (targetname.equals(kv.getTargetName())) {
				list.add(kv);
			}
		}
		return list;
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

	public static boolean isSimpleType(Object property) {
		if (property == null) {
			return true;
		}
		Class<?> type = property.getClass();
		return simpleTypes.contains(type)
				|| Number.class.isAssignableFrom(type)
				|| String.class.isAssignableFrom(type)
				|| Boolean.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type);
	}

	public static boolean isExcludedType(Class<?> type, Class<?> excludedType) {

		if (excludedType.isAssignableFrom(type)) {
			return true;
		} else if (type.isArray()
				&& excludedType.isAssignableFrom(type.getComponentType())) {
			return true;
		}

		return false;
	}

	public static Set<Class<?>> simpleTypes = new HashSet<Class<?>>();
	static {
		simpleTypes.add(Byte.TYPE);
		simpleTypes.add(Short.TYPE);
		simpleTypes.add(Integer.TYPE);
		simpleTypes.add(Long.TYPE);
		simpleTypes.add(Float.TYPE);
		simpleTypes.add(Double.TYPE);
		simpleTypes.add(Boolean.TYPE);
	}
}
