package com.kuiren.common.dao.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;

import com.kuiren.common.dao.GenericHibernateDao;
import com.kuiren.common.vo.KeyValuePair;

public class BatchDelete {

	protected final Log logger = LogFactory.getLog(getClass());

	public void deleteByProperty(GenericHibernateDao hd, String name,
			Object value) {
		String ename = hd.getEntityClass().getSimpleName();
		String hqlDelete = "delete " + ename + " where " + name + " = :oldName";
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + hqlDelete);
			logger.debug("\n参数：" + name + ":" + value + "");
		}
		if (hd.getDaoCallback().beforeHQLDel(hqlDelete, name, value)) {
			int deletedEntities = hd.getSession().createQuery(hqlDelete)
					.setParameter("oldName", value).executeUpdate();
			hd.getDaoCallback().afterHQLDel(hqlDelete, name, value);
		}

	}

	public void deleteByProperties(GenericHibernateDao hd, KeyValuePair... pro) {
		// String hqlDelete = "delete Customer where name = :oldName";
		if (pro == null || pro.length < 1) {
			return;
		}
		String ename = hd.getEntityClass().getSimpleName();

		String str = "";
		for (KeyValuePair kv : pro) {
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

		String hqlDelete = "delete " + ename + " where " + str;
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + hqlDelete);
		}
		Query query = hd.getSession().createQuery(hqlDelete);
		for (int i = 0; i < pro.length; i++) {

			query.setParameter(i, pro[i].getValue());

			if (logger.isDebugEnabled()) {
				logger.debug("\n参数值：" + pro[i].getValue() + "");
			}
		}

		if (hd.getDaoCallback().beforeHQLDel(hqlDelete, pro)) {
			int deletedEntities = query.executeUpdate();
			hd.getDaoCallback().afterHQLDel(hqlDelete, pro);
		}

	}

}
