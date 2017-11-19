package com.kuiren.common.util;

public class IntUtil {

	public static int getInt(Integer i) {
		if (i == null) {
			return 0;
		} else {
			return i.intValue();
		}
	}

	public static boolean eq(Integer i, int i1) {
		if (i == null) {
			return false;
		} else if (i.intValue() == i1) {
			return true;
		}

		return false;

	}

	public static int parse(String s, int d) {

		int i = d;
		try {
			i = Integer.parseInt(s);
		} catch (Exception e) {
		}
		return i;
	}
}
