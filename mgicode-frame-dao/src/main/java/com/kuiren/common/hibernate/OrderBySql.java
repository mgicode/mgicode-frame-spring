package com.kuiren.common.hibernate;

//import org.hibernate.criterion.Order;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;

public class OrderBySql extends Order {
	private String sqlFormula;

	protected OrderBySql(String sqlFormula) {
		super(sqlFormula, true);
		this.sqlFormula = sqlFormula;
	}

	public String toString() {
		return sqlFormula;
	}

	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {
		return sqlFormula;
	}

	public static Order sql(String sql) {
		return new OrderBySql(sql);
	}
}