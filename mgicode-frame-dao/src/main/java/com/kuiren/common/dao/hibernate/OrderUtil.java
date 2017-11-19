package com.kuiren.common.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;

import com.kuiren.common.hibernate.Order;
import com.kuiren.common.page.Page;
import com.kuiren.common.page.QueryParameter;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.KeyValue;

public class OrderUtil {

	protected final static Log logger = LogFactory.getLog(OrderUtil.class);

	public static void addOrders(List<KeyValue> kvs, Criteria criteria) {

		for (int i = 0; i < kvs.size(); i++) {
			String key = kvs.get(i).getKey();
			String value = kvs.get(i).getValue();
			if (logger.isDebugEnabled()) {
				logger.debug("\n排序 ：" + key + " " + value + " ,默认为asc");
			}
			// if ("desc".equalsIgnoreCase(value.trim())) {
			// criteria.addOrder(Order.desc(key));
			// } else {
			// criteria.addOrder(Order.asc(key));
			// }
			criteria.addOrder(getOrder(key, value.trim()));
		}
	}

	public static void addOrderSql(List<Order> list, Criteria criteria) {
		for (Order o : list) {
			addOrder(o, criteria);
		}
	}

	public static void addOrder(Order o, Criteria criteria) {
		criteria.addOrder(o);
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + o.toString());
		}
	}

	public static void addOrderList(List<Order> orders, Criteria criteria) {
		if (orders != null) {
			for (Order o : orders) {
				addOrder(o, criteria);
			}
		}
	}

	public static Order getOrder(String key, String value) {
		if ("desc".equals(value)) {
			return Order.desc(key);
		} else {
			return Order.asc(key);
		}
	}

	public static List<Order> getOrders(Map<String, String> map) {
		List<Order> list = new ArrayList<Order>();
		for (String key : map.keySet()) {
			String val = map.get(key);
			list.add(getOrder(key, val));
		}
		return list;
	}

	public static List<Order> getOrders(String kvs) {
		List<Order> list = new ArrayList<Order>();
		if (!StringUtil.isNullOrEmpty(kvs)) {
			String[] kvarr = kvs.split(",");
			for (String kv : kvarr) {
				if (StringUtil.isNullOrEmpty(kv)) {
					continue;
				}
				String[] kvss = kv.split(":");
				if (kvss != null && kvss.length == 2) {
					list.add(getOrder(kvss[0], kvss[1]));
				}
			}

		}
		return list;
	}

	public static void calOrder(Page page, Criteria c) {

		if (page.isOrderBySetted()) {
			if (logger.isDebugEnabled()) {
				logger.debug("\n排序 ：" + page.getOrderBy() + " "
						+ page.getOrder() + " ,默认为desc");
			}
			if (page.getOrder().endsWith(QueryParameter.ASC)) {
				c.addOrder(Order.asc(page.getOrderBy()));
			} else {
				c.addOrder(Order.desc(page.getOrderBy()));
			}
		}

		if (page.getOrderbyMap().size() > 0) {
			OrderUtil.addOrders(page.getOrderbyMap(), c);

		}

		//todo:20171118
//		if (page.getOrderList() != null && page.getOrderList().size() > 0) {
//			OrderUtil.addOrderSql(page.getOrderList(), c);
//
//		}
	}
}
