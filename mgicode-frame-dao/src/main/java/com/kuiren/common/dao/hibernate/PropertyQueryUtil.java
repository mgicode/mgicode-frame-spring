package com.kuiren.common.dao.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.kuiren.common.hibernate.Order;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.KeyValue;
import com.kuiren.common.vo.PropertyQuery;

public class PropertyQueryUtil {

	protected final static Log logger = LogFactory
			.getLog(PropertyQueryUtil.class);

	public static Criterion createCriterion(PropertyQuery pq) {
		Criterion criterion;
		// pengrk modefy at 2016/07/01
		// if (pq.getValue() == null) {

		if (pq.isNull()) {
			criterion = Restrictions.isNull(pq.getName());
		} else if ("like".equals(pq.getRelative())) {
			// Restrictions.like(propertyName, value, matchMode)
			criterion = Restrictions.ilike(pq.getName(), pq.getValue() + "",
					MatchMode.ANYWHERE);
		} else if (">".equals(pq.getRelative())) {
			criterion = Restrictions.gt(pq.getName(), pq.getValue());
		} else if ("<".equals(pq.getRelative())) {
			criterion = Restrictions.lt(pq.getName(), pq.getValue());
		} else if ("<>".equals(pq.getRelative())) {
			criterion = Restrictions.ne(pq.getName(), pq.getValue());
		} else if ("isNull".equalsIgnoreCase(pq.getRelative())) {
			criterion = Restrictions.isNull(pq.getName());
			// Restrictions.isNotNull(propertyName)
		} else if ("isNullOrEmpty".equalsIgnoreCase(pq.getRelative())) {
			Criterion c1 = Restrictions.isNull(pq.getName());
			Criterion c2 = Restrictions.eq(pq.getName(), "");
			criterion = Restrictions.or(c1, c2);
			// Restrictions.isNotNull(propertyName)
		} else if ("isNotNull".equalsIgnoreCase(pq.getRelative())) {
			criterion = Restrictions.isNotNull(pq.getName());
		} else {
			criterion = Restrictions.eq(pq.getName(), pq.getValue());
		}
		return criterion;
	}

	public static void buildCriterions(Criteria criteria, String[] names,
			Object[] objs) {
		int i = 0;
		for (String s : names) {
			Criterion criterion = Restrictions.eq(s, objs[i]);
			i++;
			criteria.add(criterion);
		}
	}

	public static PropertyQuery[] buildPropertyQueries(String[] names,
			Object[] objs) {
		PropertyQuery[] pqs = new PropertyQuery[names.length];
		for (int i = 0; i < names.length; i++) {

			PropertyQuery pq = new PropertyQuery(names[i], "=", objs[i]);
			pqs[i] = pq;
		}

		return pqs;

	}
}
