package com.kuiren.common.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.kuiren.common.annotation.Desc;
import com.kuiren.common.vo.KeyValue;

/**
 * <pre>
 * author:pengrk
 * email:sjkjs155@126.com
 * 从实体中的Desc或configData.xml取出枚举值
 * 主要用于页面的下拉等
 * 相关类<code> </code>
 * </pre>
 */
public class EnumUtil {
	public static final Log logger = LogFactory.getLog(EnumUtil.class);
	public static Map<String, Map<Integer, String>> enumMap = new HashMap<String, Map<Integer, String>>();
	public static Map<String, List<KeyValue>> enumlistMap = new HashMap<String, List<KeyValue>>();
	private static Pattern enumSelectPattern = Pattern
			.compile("#\\{([^}]*)\\}");
	public static String fILE_NAMES = "configData.xml";
	private static Document document;

	private static List<KeyValue> getEnumFromXml(String name) {

		if (document == null) {
			try {
				if (AppconfigUtil.exist("enum.file")) {
					fILE_NAMES = AppconfigUtil.val("enum.file");
				}
				document = XmlUtil.loadXml(fILE_NAMES);
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		List<KeyValue> list = new ArrayList<KeyValue>();
		// java.lang.Integer.parseInt(s)
		String[] cf = name.split("_");

		if (cf.length != 2) {
			return list;
		}
		String xpath = cf[0] + "/"
				+ StringUtils.uncapitalize(cf[1].substring(3)) + "/value";
		list = selectKeyValues(document.getRootElement(), xpath);
		enumlistMap.put(name, list);

		return list;

		// CommonXMLDeal.selectElement(document, xpath)
	}

	private static Map<Integer, String> getEnumMapFromXml(String name) {
		if (enumMap.get(name) != null) {
			return enumMap.get(name);
		}
		List<KeyValue> vs = new ArrayList<KeyValue>();
		if (enumlistMap.get(name) != null) {
			vs = enumlistMap.get(name);
		} else {
			vs = getEnumFromXml(name);
		}

		Map<Integer, String> map = new HashMap<Integer, String>();

		for (KeyValue kv : vs) {
			map.put(Integer.parseInt(kv.getKey()), kv.getValue());
		}
		return map;
	}

	private static List<KeyValue> selectKeyValues(Element element, String xpath) {

		List<Element> list = XmlUtil.selectElements(element, xpath);
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		for (Element e : list) {
			KeyValue kValue = new KeyValue(e.attributeValue("name"),
					e.getTextTrim());
			kvs.add(kValue);
		}

		return kvs;
	}

	// 根据key找到其对应的显示中文名
	public static String getEnumName(Integer i, String clzname) {
		Map<Integer, String> map = getEnumMap(clzname);
		if (map.containsKey(i)) {
			return map.get(i);
		}
		return "";
	}

	/**
	 * 根据名称找到其key
	 * 
	 * @author 彭仁夔于2015年8月7日上午11:19:56创建
	 * @param name
	 * @param clzname
	 * @return
	 * 
	 */
	public static Integer getEnumKeyByName(String name, String clzname) {
		Map<Integer, String> map = getEnumMap(clzname);
		for (Integer i : map.keySet()) {
			String v = map.get(i);
			if (name != null && v.equalsIgnoreCase(name)) {
				return i;
			}
		}

		return null;
	}

	public static String getEnumNames(String is, String clzname) {

		Map<Integer, String> map = getEnumMap(clzname);
		String s[] = is.split(",");
		if (s == null || s.length < 1)
			return "";
		else {
			List<String> slist = new ArrayList<String>();
			for (String s1 : s) {
				if (s1 == null)
					continue;
				int i = Integer.parseInt(s1);
				if (map.containsKey(i)) {
					slist.add(map.get(i));
				}
			}
			return ListUtil.listToStr(slist);
		}
	}

	public static String getEnumJson(String clzname) {

		Map<Integer, String> map = getEnumMap(clzname);
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		Set<Integer> key = map.keySet();
		for (Iterator it = key.iterator(); it.hasNext();) {

			Integer s = (Integer) it.next();
			sb.append(s);
			sb.append(":");
			sb.append("\"" + map.get(s) + "\"");
			sb.append(",");

		}

		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("}");
		return sb.toString();

	}

	public static Map<Integer, String> getEnumMap(String name) {
		name = concatName(name);
		if (enumMap.get(name) != null) {
			return enumMap.get(name);
		}
		// java.lang.Integer.parseInt(s)
		String[] cf = name.split("_");

		if (cf.length != 2) {
			return new HashMap<Integer, String>();
		}

		try {
			Class<?> entityClass = Thread.currentThread()
					.getContextClassLoader().loadClass(cf[0]);
			Method method = entityClass.getMethod(cf[1]);
			if (method.isAnnotationPresent(Desc.class)) {
				Desc desc = method.getAnnotation(Desc.class);
				String selects = desc.enums();
				if (StringUtil.IsNullOrEmpty(selects)) {
					return getEnumMapFromXml(name);
				}
				// return new HashMap<Integer, String>();
				Matcher matcher = enumSelectPattern.matcher(selects);
				boolean flag = matcher.matches();
				if (flag) {
					selects = matcher.group(1);
				}

				String keyvalues[] = selects.split("[，,]");
				if (keyvalues != null) {

					Map<Integer, String> map = new HashMap<Integer, String>();
					for (String keyvalue : keyvalues) {
						if (keyvalue != null) {
							keyvalue = keyvalue.trim();
						}
						String[] kv = keyvalue.split("[：:]");
						if (kv == null || kv.length != 2) {
							continue;
						}
						map.put(Integer.parseInt(kv[0].trim()), kv[1].trim());
					}
					enumMap.put(name, map);
					return map;
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e2) {
			e2.printStackTrace();
		}
		return new HashMap<Integer, String>();
	}

	private static String concatName(String name) {
		String domainName = AppconfigUtil.val("domainPackage");
		if (domainName != null) {
			name = domainName + name;
		}
		return name;
	}

	/**
	 * com.morik.mgicode.domain.TMedia_getType
	 * 
	 * @param name
	 * @return
	 */
	public static List<KeyValue> getEnumList(String name) {
		name = concatName(name);
		List<KeyValue> list = new ArrayList<KeyValue>();
		String[] cf = name.split("_");
		if (cf.length != 2) {
			return list;
		}

		try {
			Class<?> entityClass = Thread.currentThread()
					.getContextClassLoader().loadClass(cf[0]);
			Method method = entityClass.getMethod(cf[1]);
			if (method.isAnnotationPresent(Desc.class)) {
				Desc desc = method.getAnnotation(Desc.class);
				String selects = desc.enums();
				if (StringUtil.IsNullOrEmpty(selects)) {
					return getEnumFromXml(name);
					// return list;
				}

				Matcher matcher = enumSelectPattern.matcher(selects);
				boolean flag = matcher.matches();
				if (flag) {
					selects = matcher.group(1);
				}

				String keyvalues[] = selects.split("[，,]");
				if (keyvalues != null) {

					for (String keyvalue : keyvalues) {
						if (keyvalue != null) {
							keyvalue = keyvalue.trim();
						}
						String[] kv = keyvalue.split("[：:]");
						if (kv == null || kv.length != 2) {
							continue;
						}
						list.add(new KeyValue(kv[0].trim(), kv[1].trim()));
					}
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e2) {
			e2.printStackTrace();
		}
		return list;
	}

}