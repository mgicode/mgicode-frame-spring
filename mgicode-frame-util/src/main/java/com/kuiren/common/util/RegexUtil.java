package com.kuiren.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ognl.Ognl;

/**
 * <pre>
 * author:pengrk
 *  email:sjkjs155@126.com *
 * 正则表达式的转换
 * 
 * <pre>
 */
public class RegexUtil {

	// [$]
	static Pattern actionParamPattern = Pattern.compile("[{]([^}]*?)[}]");

	/**
	 * <pre>
	 * 把占位符位置采用fnObj中对应的属性值取代，如果对属性需要进行转换特定的
	 * 转换，可以在第二个参数fn中传入当前fnObj对象中的某个函数，该函数格式为：	 *
	 * 
	 * <pre>
	 * 
	 * <pre>
	 * content:全部的内容，thismatch：本次匹配的内容，其匹配regex:[{]([^}]*?)[}]
	 * placehode:匹配到的内容，startIndex：匹配的开始位置，endIndex的结束位置
	 * 如project!add.action?id=${id}&name=${name}&sex=${sex}"，其会进行三次匹配，
	 * 第一次，thismatch为：{id},placehode为：id,startIndex为24，endIndex为27
	 *  xxxxx(String content,String thismatch,String placehode,int startIndex,int endIndex ){
	 * }
	 * 
	 * <pre>
	 * 
	 * @param content 进行转换的内容
	 * @param fn 转换为的函数，为null时，直接取得宿主对象对应的属性
	 * @param fnObj 转换函数的宿主对象
	 * @return
	 */
	public static String replaceByFn(String content, String fn, Object fnObj) {
		return replaceByFn(actionParamPattern, content, fn, fnObj);
	}

	/**
	 * 见
	 * <code>public static String convert(String content, String fn, Object fnObj)</code>
	 * 
	 * @param regex
	 *            正则表达式，类似[{]([^}]*?)[}]，采用其中必须要有1个小括号，代码中只替换第1个小括号的内容
	 * @param content
	 * @param fn
	 * @param fnObj
	 * @return
	 */
	public static String replaceByFn(String regex, String content, String fn,
			Object fnObj) {
		Pattern pattern = Pattern.compile(regex);
		return replaceByFn(pattern, content, fn, fnObj);
	}

	/**
	 * 见
	 * <code>public static String convert(String content, String fn, Object fnObj)</code>
	 * 
	 * @param pattern
	 *            正则表达式，类似[{]([^}]*?)[}]，采用其中必须要有1个小括号，代码中只替换第1个小括号的内容
	 * @param content
	 * @param fn
	 * @param fnObj
	 * @return
	 */
	public static String replaceByFn(Pattern pattern, String content,
			String fn, Object fnObj) {

		Matcher matcher = pattern.matcher(content);
		String temp = content;
		int lastEnd = 0;
		int delta = 0;
		while (matcher.find()) {
			try {
				String all = matcher.group(0);
				String place = matcher.group(1);

				int start = matcher.start(0);
				int end = matcher.end(0);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("content", content);
				map.put("thismatch", all);
				map.put("placehode", place);
				map.put("start", start);
				map.put("end", end);
				map.put("action", fnObj);

				// String
				// expr=fn+"(\"#content,#thismatch,#placehode,#start,#end,#action\")";
				String expr = null;
				if (fn == null) {

					expr = "" + place;
				} else {

					expr = fn
							+ "(\"#content,#thismatch,#placehode,#start,#end,#action\")";
				}

				String repl = Ognl.getValue(expr, map, fnObj) + "";

				if (start >= lastEnd) {
					lastEnd = end;
					int sublen = repl.length() - all.length();
					temp = replace(temp, repl, start, end, delta);
					delta = delta + sublen;
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return temp;

	}

	public static String replace(String content, String repl, int start,
			int end, int delta) {
		int preIndex = start + delta;
		int endIndex = end + delta;
		String pre = "", last = "";
		if (preIndex <= content.length())
			pre = content.substring(0, preIndex);
		if (endIndex <= content.length())
			last = content.substring(endIndex);
		return pre + repl + last;
	}

	/**
	 * 
	 * 见String convert(String regex, String regexseg, String content)
	 * 
	 * @param pattern
	 * @param replaces
	 * @param content
	 * @return
	 */
	public static String replaceByArr(String content, String replaces[],
			Pattern pattern) {

		Matcher matcher = pattern.matcher(content);

		String temp = content;

		int lastEnd = 0;
		int delta = 0;

		while (matcher.find()) {

			if (matcher.groupCount() > 0) {
				int len = matcher.groupCount();
				for (int i = 0; i <= len; i++) {
					if (replaces.length > i + 1) {
						String repl = replaces[i];
						if (StringUtil.IsNullOrEmpty(repl)) {
							continue;// 对于空,,这样的字符串不做处理
						}
						repl = repl.trim();
						String sub = matcher.group(i);

						int start = matcher.start(i);
						int end = matcher.end(i);

						if ("-".equals(repl)) {
							repl = "";
						}
						if ("\\-".equals(repl)) {
							repl = "-";
						}

						if (sub == null) {
							continue;
						}
						if (start == -1 || end == -1) {
							continue;
						}

						if (start >= lastEnd) {
							lastEnd = end;
							int sublen = repl.length() - sub.length();
							temp = replace(temp, repl, start, end, delta);
							delta = delta + sublen;
						}

					}
				}
			}

		}
		return temp;
	}

	/**
	 * 根据正则表达式子捕获content内容，有多少个子捕获，就采用有多少个分段的regexseg 。
	 * 
	 * <pre>
	 * String regex = "(@)(\\w{1,})(\\s*(=|[:])\\s*(\")([^\"]*)(\"))?"; 
	 *  String string = convert(regex, "A,-,,,-,-,Test1,-,", "@name=\"test\"@name=\"test\""); \n
	 * </pre>
	 * 
	 * test首字母大写
	 * 
	 * @param content
	 *            内容
	 * @param regex
	 *            正则表达式
	 * @param regexseg
	 *            子捕获替换的内容，不替换的话采用-表式,采用空的子内容进行替换只要连续的，就可以。
	 * @return
	 */
	public static String replaceSplitByDot(String regex, String regexseg,
			String content) {
		if (StringUtil.IsNullOrEmpty(regex)) {
			return content;
		}
		String replaces[] = regexseg.split(",");
		if (StringUtil.IsNullOrEmpty(regex)) {
			return content;
		}
		Pattern pattern = Pattern.compile(regex);
		return replaceByArr(content, replaces, pattern);

	}

	public static void main(String[] args) {

		String string = "project!add.action?id=${id}&name=${name}&sex=${sex}";
		string = replaceByFn(string, null, null);
		// test首字母大写
		System.out.print(string);

		// String regex = "(@)(\\w{1,})(\\s*(=|[:])\\s*(\")([^\"]*)(\"))?";

		// String string = convert(regex, "A,-,,,-,-,Test1,-,",
		// "@name=\"test\"@name=\"test\"");

		// test首字母大写
		// System.out.print(string);
	}

}
