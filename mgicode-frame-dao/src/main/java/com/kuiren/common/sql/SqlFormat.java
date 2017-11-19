package com.kuiren.common.sql;

import java.lang.reflect.Field;
import java.util.Date;

import ognl.Ognl;

import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.StringUtil;

public class SqlFormat {

	private String toNull(Object o, String name) throws Exception {
		// deal category.id
		if (name.contains(".")) {
			Object ov = Ognl.getValue(name, o);
			if (ov == null) {
				return "NULL";
			} else {
				return "'" + ov.toString() + "'";
			}
		}
		Field field = BeanUtil.getDeclaredField(o, name);
		Object v = BeanUtil.getFieldValue(o, name);// field.get(o);
		if (field.getType().isAssignableFrom(String.class)||field.getType().isAssignableFrom(Date.class)) {

			if (v == null) {
				return "NULL";
			} else {
				return "'" + v.toString() + "'";
			}
		} else if (v == null)
			return "NULL";
		else
			return v.toString() + "";
	}

	public String format(String foratStr, Object o, String[] names) {
		try {
			if (names != null) {
				String[] s = new String[names.length];
				for (int i = 0; i < names.length; i++) {
					s[i] = toNull(o, names[i]);
				}
				String c = String.format(foratStr, s) + "  ;";
				return c + StringUtil.NEWLINE;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return foratStr;
	}

}
