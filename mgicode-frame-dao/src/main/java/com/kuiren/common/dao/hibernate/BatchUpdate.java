package com.kuiren.common.dao.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;

import com.kuiren.common.dao.GenericHibernateDao;
import com.kuiren.common.vo.KeyValuePair;

public class BatchUpdate {
	protected final Log logger = LogFactory.getLog(getClass());

	// @Override
	public void updateByProperty(GenericHibernateDao hd,
			List<KeyValuePair> list, List<KeyValuePair> where) {
		String ename = hd.getEntityClass().getSimpleName();
		String hql = "update " + ename + " set " + buildUpdateSet(list)
				+ "  where " + buildUpdateWhere(where);
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + hql);
		}
		Query query = hd.getSession().createQuery(hql);
		int m = 0;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				query.setParameter(m, list.get(i).getValue());
				if (logger.isDebugEnabled()) {
					logger.debug("第" + i + "个参数：" + m + ":"
							+ list.get(i).getValue());
				}
				m++;
			}
		}
		if (where != null) {
			for (int i = 0; i < where.size(); i++) {
				query.setParameter(m, where.get(i).getValue());
				if (logger.isDebugEnabled()) {
					logger.debug("第" + i + "where参数：" + m + ":"
							+ where.get(i).getValue());
				}
				m++;
			}
		}
		if (hd.getDaoCallback().beforeHQLUpdate(hql, list, where) == true) {
			query.executeUpdate();
			hd.getDaoCallback().afterHQLUpdate(hql, list, where);
		}

	}

	private String buildUpdateSet(List<KeyValuePair> list) {

		String str = "";
		for (KeyValuePair kv : list) {
			if (str.length() > 0) {
				str = str + "," + kv.getName() + "= ?";
			} else {
				str = kv.getName() + "= ?";
			}
		}
		return str;
	}

	private String buildUpdateWhere(List<KeyValuePair> list) {

		String str = "";
		for (KeyValuePair kv : list) {
			if (str.length() > 0) {
				if ("or".equals(kv.getRelative())) {
					str = str + " or " + kv.getName() + "= ?";
				} else {
					str = str + " and " + kv.getName() + "= ?";
				}

			} else {
				str = kv.getName() + "= ? ";
			}
		}
		return str;
	}

}
