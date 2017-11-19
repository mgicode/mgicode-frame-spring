package com.kuiren.common.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;

import com.kuiren.common.util.StringUtil;

public class Order extends org.hibernate.criterion.Order {

	private String nameOrSql = null;
	protected final Log logger = LogFactory.getLog(getClass());

	protected Order(String propertyName, boolean ascending) {
		super(propertyName, ascending);
		nameOrSql = propertyName;
	}
	
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {
		String sql = super.toSqlString(criteria, criteriaQuery);
		if (logger.isDebugEnabled()) {
			logger.debug("Hibernater的属性：" + this.nameOrSql + "生成sql：" + sql);
		}
		// if (StringUtil.isNullOrEmpty(sql)) {
		// sql = nameOrSql;
		// if (logger.isDebugEnabled()) {
		// logger.debug("Hibernater的属性：" + this.nameOrSql
		// + "没有生成sql，采用原生的sql" + sql);
		// }
		// }

		return sql;
	}

	public static Order asc(String propertyName) {
		return new Order(propertyName, true);
	}

	public static Order desc(String propertyName) {
		return new Order(propertyName, false);
	}

}
