package com.kuiren.common.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ognl.Ognl;
import ognl.OgnlException;



public class ListUtil {

	public static boolean isListEmpty(List list) {
		if (list == null || list.size() == 0)
			return true;
		return false;
	}

	public static <T> T getFirst(List<T> list) {
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public static List copy(List list) {
		if (list == null)
			return list;
		List<Object> ret = new ArrayList<Object>();

		for (Object t : list) {
			ret.add(t);

		}

		return ret;

	}

	/**
	 * Hibernate如果进行多表查询的话，其会返回多个对象的集合，这时一般需要取出其中第一个形成list,例如：
	 * 
	 * <pre>
	 * String sql = &quot; select {a.*}, {t.*} from T_PT_CHECK_INDEX a left join T_PT_CHECK_INDEX_TYPE t  on a.index_type_id=t.id &quot;
	 * 		+ &quot;start with  a.id='&quot; + id + &quot;'&quot; + &quot; connect by prior a.id=pid &quot;;
	 * List&lt;Object&gt; list1 = (List&lt;Object&gt;) entityDao.getSession().createSQLQuery(sql)
	 * 		.addEntity(&quot;a&quot;, TCheckIndex.class).addJoin(&quot;t&quot;, &quot;a.indexType&quot;).list();
	 * 
	 * </pre>
	 * 
	 * @author:彭仁夔 于2014年10月21日下午2:28:05创建
	 * @return
	 */
	public <T> List<T> getAllFirst(List<Object> olist, Class<T> clz) {

		// getAllOne(olist, order)<T>;
		List<T> list = new ArrayList<T>();
		for (Object obj : olist) {
			if (obj.getClass().isArray()) {
				Object[] objs = (Object[]) obj;
				list.add((T) objs[0]);
			}
		}
		return list;
	}

	/**
	 * 与getAllFirst相类似，采用泛型参数及集合的序号。见@see

	 * 
	 * @author:彭仁夔 于2014年10月21日下午2:34:38创建
	 * @param olist
	 * @param order
	 * @return
	 */
	public <T1> List<T1> getAllOne(List<Object> olist, int order, Class<T1> cls) {

		List<T1> list = new ArrayList<T1>();
		for (Object obj : olist) {
			if (obj.getClass().isArray()) {
				Object[] objs = (Object[]) obj;
				list.add((T1) objs[order]);
			}
		}
		return list;
	}

	/**
	 * 把采用childname进行串接的数据变成List的结构
	 * 
	 * @author:彭仁夔 于2014年12月1日下午7:20:18创建
	 * @param list
	 * @param childname
	 * @return
	 */
	public static <T> List<T> changeChildToList(List<T> list, String childname) {
		List<T> retList = new ArrayList<T>();
		for (T t : list) {
			changeChildToList(retList, t, childname);
		}

		return retList;
	}

	/**
	 * 把实体及实体的children转换为平的列表
	 * 
	 * @author:彭仁夔 于2014年12月2日上午11:40:03创建
	 * @param t
	 *            实体
	 * @param childname
	 * @return
	 */
	public static <T> List<T> changeChildToList(T t, String childname) {
		List<T> retList = new ArrayList<T>();

		changeChildToList(retList, t, childname);

		return retList;
	}

	private static <T> void changeChildToList(List<T> retList, T t,
			String childname) {

		retList.add(t);
		try {
			List<T> cList = (List<T>) Ognl.getValue(childname, t);
			if (cList != null) {
				for (T c : cList) {
					changeChildToList(retList, c, childname);
				}
			}
		} catch (OgnlException e) {

			e.printStackTrace();
		}

	}

	public static <T> T[] list2Arr(List<T> list, Class<?> clz) {
		if (list == null)
			return null;
		int size = list.size();
		T[] arrs = (T[]) Array.newInstance(clz, size);

		return list.toArray(arrs);
	}

	public static <T> List<T> arr2List(T[] arr) {
		return Arrays.asList(arr);
	}

	public static <T> List<T> arr2List(String[] arr, Class<?> clz) {
		List<T> list = new ArrayList<T>();
		for (String s : arr) {
			if (!StringUtil.IsNullOrEmpty(s)) {
				if (clz == Integer.class || clz == Integer.TYPE) {
					Object object = Integer.parseInt(s);
					list.add((T) object);
				} else if (clz == Float.class || clz == Float.TYPE) {
					Object object = Float.parseFloat(s);
					list.add((T) object);
				} else {
					list.add((T) s);
				}

			}
		}

		return list;

	}

	public static String listToStr(Collection<?> list) {
		if (list == null || list.size() < 1) {
			return "";
		}
		StringBuffer sBuffer = new StringBuffer();
		for (Object o : list) {
			if (BeanUtil.isSimpleType(o)) {
				sBuffer.append(o);
				sBuffer.append(",");
			}
		}
		sBuffer.delete(sBuffer.length() - 1, sBuffer.length());
		return sBuffer.toString();
	}

	public static String listToStr(List<?> list, String name) {
		if (list == null || list.size() < 1) {
			return "";
		}
		StringBuffer sBuffer = new StringBuffer();
		for (Object o : list) {
			if (BeanUtil.isSimpleType(o)) {
				sBuffer.append(o);
				sBuffer.append(",");
			} else {
				Object object = BeanUtil.getFieldValue(o, name);
				if (BeanUtil.isSimpleType(object)) {
					sBuffer.append(object);
					sBuffer.append(",");
				}
			}

		}
		sBuffer.delete(sBuffer.length() - 1, sBuffer.length());
		return sBuffer.toString();
	}

	public static String listToStr(List<?> list, String name, boolean addsingle) {
		if (list == null || list.size() < 1) {
			return "";
		}
		StringBuffer sBuffer = new StringBuffer();
		for (Object o : list) {
			if (BeanUtil.isSimpleType(o)) {
				sBuffer.append(o);
				sBuffer.append(",");
			} else {
				Object object = BeanUtil.getFieldValue(o, name);
				if (BeanUtil.isSimpleType(object)) {
					if (addsingle == true) {
						sBuffer.append(StringUtil.singleQuote(object));
					} else {
						sBuffer.append(object);
					}
					sBuffer.append(",");
				}
			}

		}
		sBuffer.delete(sBuffer.length() - 1, sBuffer.length());
		return sBuffer.toString();
	}

	public static String[] listToArr(List<?> list, String name) {
		String[] ret = new String[list.size()];
		if (list == null || list.size() < 1) {
			return ret;
		}
		int i = 0;
		for (Object o : list) {
			if (BeanUtil.isSimpleType(o)) {
				ret[i] = o + "";
			} else {
				Object object = BeanUtil.getFieldValue(o, name);
				ret[i] = object.toString();
			}
			i++;
		}
		return ret;

	}

	public static String listToStr(Object[] list) {
		if (list == null || list.length < 1) {
			return "";
		}
		StringBuffer sBuffer = new StringBuffer();
		for (Object o : list) {
			if (BeanUtil.isSimpleType(o)) {
				sBuffer.append(o);
				sBuffer.append(",");
			}
		}
		sBuffer.delete(sBuffer.length() - 1, sBuffer.length());
		return sBuffer.toString();
	}

	public static String listToStr(List<?> list) {
		if (list == null || list.size() < 1) {
			return "";
		}
		StringBuffer sBuffer = new StringBuffer();
		for (Object o : list) {
			if (BeanUtil.isSimpleType(o)) {
				sBuffer.append(o);
				sBuffer.append(",");
			}
		}
		sBuffer.delete(sBuffer.length() - 1, sBuffer.length());
		return sBuffer.toString();
	}

	public static List<String> strToList(String str, String split) {
		List<String> list = new ArrayList<String>();
		if (StringUtil.IsNullOrEmpty(str))
			return null;
		if (StringUtil.IsNullOrEmpty(split)) {
			split = ",";
		}
		String[] strs = str.split(split);
		for (String s : strs) {
			if (!StringUtil.IsNullOrEmpty(s)) {
				list.add(s);
			}
		}
		return list;
	}

	public static <T> List<T> strToList(String str, String split, Class<?> clz) {
		List<T> list = new ArrayList<T>();
		if (StringUtil.IsNullOrEmpty(str))
			return null;
		if (StringUtil.IsNullOrEmpty(split)) {
			split = ",";
		}
		String[] strs = str.split(split);
		for (String s : strs) {
			if (!StringUtil.IsNullOrEmpty(s)) {
				if (clz == Integer.class || clz == Integer.TYPE) {
					Object object = Integer.parseInt(s);
					list.add((T) object);
				} else if (clz == Float.class || clz == Float.TYPE) {
					Object object = Float.parseFloat(s);
					list.add((T) object);
				} else {
					list.add((T) s);
				}

			}
		}
		return list;
	}

	public static <T> List<T> collect2List(Collection<T> cs) {
		List<T> list = new ArrayList<T>();
		for (T t : cs) {
			list.add(t);
		}

		return list;
	}

	public static void main(String[] args) {
		try {

			List<String> list = new ArrayList<String>();
			list.add("233");
			list.add("234");

			String[] arrsStrings = ListUtil.list2Arr(list, String.class);

			String s = arrsStrings[0];
			System.out.println(s);

			list = ListUtil.arr2List(arrsStrings);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public static Object[] List2Array(List<Object> oList) {
		Object[] oArray = oList.toArray(new Object[] {});
		// TODO 需要在用到的时候另外写方法，不支持泛型的Array.
		return oArray;
	}

	public static Object[] Set2Array(Set<Object> oSet) {
		Object[] oArray = oSet.toArray(new Object[] {});
		// TODO 需要在用到的时候另外写方法，不支持泛型的Array.
		return oArray;
	}

	public static <T extends Object> List<T> Set2List(Set<T> oSet) {
		List<T> tList = new ArrayList<T>(oSet);
		// TODO 需要在用到的时候另外写构造，根据需要生成List的对应子类。
		return tList;
	}

	public static <T extends Object> List<T> Array2List(T[] tArray) {
		List<T> tList = Arrays.asList(tArray);
		// TODO 单纯的asList()返回的tList无法add(),remove(),clear()等一些影响集合个数的操作，
		// 因为Arrays$ArrayList和java.util.ArrayList一样，都是继承AbstractList，
		// 但是Arrays$ArrayList没有override这些方法，而java.util.ArrayList实现了。
		// TODO 建议使用List的子类做返回，而不是Arrays$ArrayList。根据需要吧。如下行注释:
		// List<T> tList = new ArrayList<T>(Arrays.asList(tArray));
		return tList;
	}

	public static <T extends Object> Set<T> List2Set(List<T> tList) {
		Set<T> tSet = new HashSet<T>(tList);
		// TODO 具体实现看需求转换成不同的Set的子类。
		return tSet;
	}

	public static <T extends Object> Set<T> Array2Set(T[] tArray) {
		Set<T> tSet = new HashSet<T>(Arrays.asList(tArray));
		// TODO 没有一步到位的方法，根据具体的作用，选择合适的Set的子类来转换。
		return tSet;
	}

}
