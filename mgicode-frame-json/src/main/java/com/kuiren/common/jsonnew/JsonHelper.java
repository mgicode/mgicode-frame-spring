package com.kuiren.common.jsonnew;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonParser;

import com.kuiren.common.util.StringUtil;

public class JsonHelper {

	// private static Logger logger = LoggerFactory.getLogger(JsonBinder.class);
//
//	private ObjectMapper mapper;
//
//	public JsonHelper(Inclusion inclusion) {
//		mapper = new ObjectMapper();
//		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
//		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//		// 设置输出包含的属性
//		// mapper.getSerializationConfig().setSerializationInclusion(inclusion);
//		// 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
//		mapper.getDeserializationConfig()
//				.set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
//						false);
//	}
//
//	/**
//	 * 创建输出全部属性到Json字符串的Binder.
//	 */
//	public static JsonHelper buildNormalBinder() {
//		return new JsonHelper(Inclusion.ALWAYS);
//	}
//
//	/**
//	 * 创建只输出非空属性到Json字符串的Binder.
//	 */
//	public static JsonHelper buildNonNullBinder() {
//		return new JsonHelper(Inclusion.NON_NULL);
//	}
//
//	/**
//	 * 创建只输出初始值被改变的属性到Json字符串的Binder.
//	 */
//	public static JsonHelper buildNonDefaultBinder() {
//		return new JsonHelper(Inclusion.NON_DEFAULT);
//	}
//
//	/**
//	 * 如果JSON字符串为Null或"null"字符串,返回Null. 如果JSON字符串为"[]",返回空集合.
//	 *
//	 * 如需读取集合如List/Map,且不是List<String>这种简单类型时使用如下语句: List<MyBean> beanList =
//	 * binder.getMapper().readValue(listString, new
//	 * TypeReference<List<MyBean>>() {});
//	 */
//	public <T> T fromJson(String jsonString, Class<T> clazz) {
//		if (StringUtil.IsNullOrEmpty(jsonString)) {
//			return null;
//		}
//
//		try {
//			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
//			// mapper.readValue(jp, valueType, cfg)
//
//			return mapper.readValue(jsonString, clazz);
//		} catch (Exception e) {
//			e.printStackTrace();
//			// logger.warn("parse json string error:" + jsonString, e);
//			return null;
//		}
//	}
//
//	/**
//	 *
//	 * @Title: fromJson
//	 * @description: JSON字符串转换为所对就原实体对象
//	 * @author:郑志斌 于 2014年12月11日 上午11:14:38 创建
//	 * @param jsonString
//	 *            所在转换的JSON字符串
//	 * @param clazz
//	 *            所在转换成的实体对象
//	 * @param pattern
//	 *            实体对像中,时间所在保存的样式,例如: "yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm"
//	 *            等
//	 * @return
//	 * @throws
//	 */
//	public <T> T fromJson(String jsonString, Class<T> clazz, String pattern) {
//		if (StringUtil.IsNullOrEmpty(jsonString)) {
//			return null;
//		}
//
//		try {
//			this.setDateFormat(pattern);
//			// mapper.readValue(jp, valueType, cfg)
//			return mapper.readValue(jsonString, clazz);
//		} catch (Exception e) {
//			e.printStackTrace();
//			// logger.warn("parse json string error:" + jsonString, e);
//			return null;
//		}
//	}
//
//	/**
//	 * 如果对象为Null,返回"null". 如果集合为空集合,返回"[]".
//	 */
//	public String toJson(Object object) {
//
//		try {
//			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
//			return mapper.writeValueAsString(object);
//		} catch (IOException e) {
//			// logger.warn("write to json string error:" + object, e);
//			return null;
//		}
//	}
//
//	public String toJson(Object object, String pattern) {
//
//		try {
//			mapper.setDateFormat(new SimpleDateFormat(pattern));
//			return mapper.writeValueAsString(object);
//		} catch (IOException e) {
//			// logger.warn("write to json string error:" + object, e);
//			return null;
//		}
//	}
//
//	/**
//	 * 设置转换日期类型的format pattern,如果不设置默认打印Timestamp毫秒数.
//	 */
//	private void setDateFormat(String pattern) {
//		if (StringUtil.IsNotNullOrEmpty(pattern)) {
//			DateFormat df = new SimpleDateFormat(pattern);
//			mapper.getSerializationConfig().setDateFormat(df);
//			mapper.getDeserializationConfig().setDateFormat(df);
//		}
//	}
//
//	/**
//	 * 取出Mapper做进一步的设置或使用其他序列化API.
//	 */
//	public ObjectMapper getMapper() {
//		return mapper;
//	}

	// /**
	// * 序列化对象/集合到Json字符串.
	// */
	// @Test
	// public void toJson() throws Exception {
	// //Bean
	// TestBean bean = new TestBean("A");
	// String beanString = binder.toJson(bean);
	// System.out.println("Bean:" + beanString);
	// assertEquals("{\"name\":\"A\"}", beanString);
	//
	// //Map
	// Map<String, Object> map = Maps.newLinkedHashMap();
	// map.put("name", "A");
	// map.put("age", 2);
	// String mapString = binder.toJson(map);
	// System.out.println("Map:" + mapString);
	// assertEquals("{\"name\":\"A\",\"age\":2}", mapString);
	//
	// //List<String>
	// List<String> stringList = Lists.newArrayList("A", "B", "C");
	// String listString = binder.toJson(stringList);
	// System.out.println("String List:" + listString);
	// assertEquals("[\"A\",\"B\",\"C\"]", listString);
	//
	// //List<Bean>
	// List<TestBean> beanList = Lists.newArrayList(new TestBean("A"), new
	// TestBean("B"));
	// String beanListString = binder.toJson(beanList);
	// System.out.println("Bean List:" + beanListString);
	// assertEquals("[{\"name\":\"A\"},{\"name\":\"B\"}]", beanListString);
	//
	// //Bean[]
	// TestBean[] beanArray = new TestBean[] { new TestBean("A"), new
	// TestBean("B") };
	// String beanArrayString = binder.toJson(beanArray);
	// System.out.println("Array List:" + beanArrayString);
	// assertEquals("[{\"name\":\"A\"},{\"name\":\"B\"}]", beanArrayString);
	// }
	//
	// /**
	// * 从Json字符串反序列化对象/集合.
	// */
	// @Test
	// @SuppressWarnings("unchecked")
	// public void fromJson() throws Exception {
	// //Bean
	// String beanString = "{\"name\":\"A\"}";
	// TestBean bean = binder.fromJson(beanString, TestBean.class);
	// System.out.println("Bean:" + bean);
	//
	// //Map
	// String mapString = "{\"name\":\"A\",\"age\":2}";
	// Map<String, Object> map = binder.fromJson(mapString, HashMap.class);
	// System.out.println("Map:");
	// for (Entry<String, Object> entry : map.entrySet()) {
	// System.out.println(entry.getKey() + " " + entry.getValue());
	// }
	//
	// //List<String>
	// String listString = "[\"A\",\"B\",\"C\"]";
	// List<String> stringList = binder.getMapper().readValue(listString,
	// List.class);
	// System.out.println("String List:");
	// for (String element : stringList) {
	// System.out.println(element);
	// }
	//
	// //List<Bean>
	// String beanListString = "[{\"name\":\"A\"},{\"name\":\"B\"}]";
	// List<TestBean> beanList = binder.getMapper().readValue(beanListString,
	// new TypeReference<List<TestBean>>() {
	// });
	// System.out.println("Bean List:");
	// for (TestBean element : beanList) {
	// System.out.println(element);
	// }
	// }
}

// private static JsonBinder binder = JsonBinder.buildNonDefaultBinder();