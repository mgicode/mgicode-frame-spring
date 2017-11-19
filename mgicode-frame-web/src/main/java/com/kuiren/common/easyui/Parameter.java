package com.kuiren.common.easyui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.kuiren.common.util.StringUtil;

/**
 * 
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 * 
 */
public class Parameter {

	private Object value = null;

	public Parameter(JsonRequest request, String name) {

		if (name == null)
			return;
		String[] ns = name.split(",");
		if (ns != null) {
			for (String os : ns) {
				String s = os.trim();
				if (request.getMap().containsKey(s)) {
					this.value = request.getMap().get(s);
					break;
				}
			}
		}

	}

	public Parameter(Map map, String name) {

		if (name == null)
			return;
		String[] ns = name.split(",");
		if (ns != null) {
			for (String os : ns) {
				String s = os.trim();
				if (map.containsKey(s)) {
					this.value = map.get(s);
					break;
				}
			}
		}

	}

	public int toInt(int defaut) {
		try {
			if (null != value) {
				if (value instanceof Integer) {
					return ((Integer) value).intValue();
				}
				return Integer.parseInt(value.toString().trim());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return defaut;
		}
		return defaut;
	}

	public String toStr() {
		if (value == null)
			return null;
		return value.toString();
	}

	public String toStr(String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof String) {
			if (StringUtil.isNullOrEmpty((String) value)) {
				return defaultValue;
			}
		}
		return value.toString();
	}

	public int toInt() {
		return toInt(-1);
	}

	public float toFloat(float defalut) {
		if (null != value) {
			if (value instanceof Float) {
				return ((Float) value).floatValue();
			}
			return Float.parseFloat(value.toString().trim());
		}
		return defalut;
	}

	public void each(IParaListConvert convert) {
		List<Map> list = new ArrayList<Map>();
		if (value instanceof List) {
			list = (List<Map>) value;
			for (Map map : list) {
				convert.convert(new JsonRequest(map));
			}

		} else {

		}
	}

	/**
	 * 取得页面传入的参数，并转换为Float,没有值返回-1
	 * 
	 * @param param
	 * @return
	 */
	public float toFloat() {
		return toFloat(-1);
	}

	public Date toDate(Date defalut, String fmt) {
		if (null != value) {
			if (StringUtil.isNotNullOrEmpty(value.toString())) {
				if (StringUtil.isNullOrEmpty(fmt)) {
					fmt = "yyyy-MM-dd";
				}
				SimpleDateFormat sdf = new SimpleDateFormat(fmt);
				try {
					return sdf.parse(value.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else{
				return defalut;
			}
		}
		return defalut;
	}

	public Date toDate(String fmt) {
		return toDate(null, fmt);
	}

	public Date toDate() {
		return toDate("yyyy-MM-dd");
	}

	public Date toDateNow(String fmt) {
		Calendar calendar = Calendar.getInstance();
		return toDate(calendar.getTime(), fmt);
	}

	public Date toDateNow() {
		return toDateNow("yyyy-MM-dd");
	}
}
