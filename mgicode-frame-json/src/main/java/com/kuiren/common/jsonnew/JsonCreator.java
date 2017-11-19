package com.kuiren.common.jsonnew;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.DateUtil;
import com.kuiren.common.util.ListUtil;
import com.kuiren.common.util.StringUtil;

/**
 * 非常方便的Json转换类，示例：
 * 
 * <pre>
 * String subStr = new JsonCreator().exclude(&quot;checkCompType&quot;, &quot;plan&quot;, &quot;indicator&quot;)
 * 		.replace(&quot;pid&quot;, &quot;_parentId&quot;).replace(&quot;indexType.name&quot;, &quot;tname&quot;)
 * 		.add(&quot;rows&quot;, ctsiList).add(&quot;total&quot;, 3).build(plan);
 * </pre>
 * 
 * @author 彭仁夔 于2012年10月21日下午6:05:14创建
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */

public class JsonCreator {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private boolean isRemoveNullProp = true;
	private JsonMatch jsonMatch;

	private String dataFormat = "yyyy-MM-dd";
	// 0:不处理，1：大写，2 小写
	private Integer upperOrLower = 0;

	private List<String> onlyNameList = new ArrayList<String>();

	private boolean onlyFlag = false;

	private Map<String, String> loopMap = new HashMap<String, String>();

	// 使用JsonConvert中需要使用限制的Class，一般为当前实体
	private Class limitClz = null;

	public JsonCreator() {
		this(true);
	}



	public JsonCreator(boolean removeNullProp) {
		jsonMatch = new JsonMatch();
		
	}

	public JsonCreator setDateFormat(String format) {
		if (format != null && !"".equals(format)) {
			dataFormat = format;
		}
		return this;
	}

	/**
	 * 实体字段过多，一个一人地排除（exclude）比较麻烦，<br>
	 * 使用该函数，把需要的几个字段指定即可。<br>
	 * 一般用于不超过5个字段左右的使用该函数比较合适。<br>
	 * 
	 * @param name
	 * @return
	 * 
	 */
	public JsonCreator only(String... name) {
		for (String n : name) {
			onlyNameList.add(n);
			onlyFlag = true;
		}
		return this;
	}

	/**
	 * 
	 * @author:彭仁夔 于2014年12月19日上午9:26:17创建
	 * @param flag
	 *            0:不处理，1：大写，2 小写
	 * @return
	 */
	public JsonCreator setLowerUpper(Integer flag) {
		this.upperOrLower = flag;
		return this;
	}

	/**
	 * 把expr的查找的字段转换为replacedName替换的名称<br>
	 * 
	 * @param expr
	 *            查找表达式
	 * @param replacedName
	 *            替换的名称
	 * @return
	 * 
	 */
	public JsonCreator replace(String expr, String replName) {
		// JsonConfig jc = jsonMatch.parse(expr);
		// jc.setAddName(replacedName);
		// jc.setType("10");
		// configs.get("rep").add(jc);
		//
		// return this;
		return replace(expr, replName, null);
	}

	/**
	 * 把expr的查找的字段转换为INameConvert回调函数运行结果值<br>
	 * 
	 * @param expr
	 *            查找表达式
	 * @param replacedName
	 *            INameConvert 转换名称的结果值
	 * @return *
	 */
	public JsonCreator replace(String expr, INameConvert replName) {
		return replace(expr, replName, null);
	}

	
	/**
	 * 把expr表达式查找到字段采用replacedName的名称来替换字段名，<br>
	 * 采用value采用替换其字段值
	 * 
	 * @param expr
	 *            查找字段表达式
	 * @param replacedName
	 *            替换的名称
	 * @param value
	 *            查找字段的值需要进行转换，一般使用IJSonConvert
	 * 
	 */
	public JsonCreator replace(String expr, String replName, Object value) {
	
		return this;
	}

	/**
	 * 既可以动态定义其转换的字段名称和值
	 * 
	 * @param expr
	 *            查找表达式
	 * @param replacedName
	 *            名称转换对象
	 * @param value
	 *            值转换对象，使用使用基本类型和IJsonConvert
	 * @return
	 * 
	 */
	public JsonCreator replace(String expr, INameConvert replName,
			Object value) {
	

		return this;
	}

	/**
	 * 把不需要的字段排除
	 * 
	 * @param objs
	 * @return
	 */
	public JsonCreator exclude(Object... objs) {
		

		return this;
	}



	/**
	 * 在转换对象中第一层上加上指定name的指定值value
	 * 
	 * @param name
	 * @param value
	 *            基本类型的值和IJsonConvert
	 * @return
	 * 
	 */
	public JsonCreator add(String name, Object value) {
		return add(name, name + "@^", value);
	}

	
	public JsonCreator add(String name, String expr, Object value) {
		
		return this;
	}

	

	/**
	 * 把对象转换Json字符串
	 * 
	 * @param o对象
	 * @return
	 */
	public String build(Object o) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 把对象转换Json数组字符串<br>
	 * 有时前台需要数组,保证返回的为数组
	 * 
	 * @param o
	 * @return
	 */
	public String buildArr(Object o) {
		if (o == null) {
			return "[]";
		}
		String str = build(o);
		if (str == null)
			return "";
		if (str.startsWith("["))
			return str;
		else
			return "[" + str + "]";
	}

	/**
	 * 和buildArr类似，为空时返回{}
	 * 
	 * @param o
	 * @return
	 */
	public String buildObj(Object o) {
		if (o == null) {
			return "{}";
		}
		String str = build(o);
		if (str == null) {
			return "{}";
		} else
			return str;
	}

	/**
	 * 当o为空时，返回指定clz中字段格式<br>
	 * 减少为空时的手动转换
	 * 
	 * @param o
	 *            转换对象
	 * @param clz
	 *            一般为AppRetData或RetData等相关的对象
	 * @return 彭仁夔 于 2016年4月14日 上午9:09:46 创建
	 */
	public String build(Object o, Class clz) {
		if (o == null) {
			try {
				o = clz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				logger.error(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
		String str = build(o);
		return str;
	}


	


}
