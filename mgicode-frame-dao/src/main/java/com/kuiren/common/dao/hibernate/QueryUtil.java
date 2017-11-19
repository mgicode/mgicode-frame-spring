package com.kuiren.common.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.util.Assert;

import com.kuiren.common.exception.DAOException;
import com.kuiren.common.page.Page;
import com.kuiren.common.util.StringUtil;

public class QueryUtil {
	protected final static Log logger = LogFactory.getLog(QueryUtil.class);

	public static Query createQuery(Session session, Class entityClass,
			String queryStr, Object... values) throws DAOException {
		Assert.hasText(queryStr, "queryString为空");
		// 加上逻辑删除相关东西
		String q = PageUtil.mixupNoLogicalDeleteSql(queryStr, entityClass);
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + q);
		}
		Query queryObject = session.createQuery(q);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
				if (logger.isDebugEnabled()) {
					logger.debug("\n第" + i + "参数为" + values[i]);
				}
			}
		}
		return queryObject;

	}

	// 什么时间替换createQuery
	public static Query createQuery1(Session session, Class entityClass,
			String queryStr, Object... values) throws DAOException {
		Assert.hasText(queryStr, "queryString为空");
		// 加上逻辑删除相关东西
		String q = PageUtil.mixupNoLogicalDeleteSql(queryStr, entityClass);
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + q);
		}
		List<Integer> allPos = StringUtil.getPos(q, "[?]");

		List<Object> objList = new ArrayList<Object>();
		List<Integer> delPoss = new ArrayList<Integer>();

		for (int i = 0; i < values.length; i++) {
			Object v = values[i];
			if (v == null) {
				delPoss.add(allPos.get(i));
			} else {
				objList.add(v);
			}
		}
		// 把对象采用 is null 来替换
		q = StringUtil.replace(q, "?", " is null ", delPoss);

		if (logger.isDebugEnabled()) {
			logger.debug("\n" + q);
		}
		
		Query queryObject = session.createQuery(q);
		if (values != null) {
			for (int i = 0; i < objList.size(); i++) {
				Object v = objList.get(i);
				if (logger.isDebugEnabled()) {
					logger.debug("\n第" + i + "参数为" + values[i]);
				}
				queryObject.setParameter(i, v);
			}
		}

		return queryObject;

	}

	public static void calPageCount(Session session, Class entityClass,
			Query q, Page page, String hql, Object... values) {
		if (logger.isDebugEnabled()) {
			logger.debug("hql:" + hql);
		}
		String countQuery = " select count (*) "
				+ PageUtil.removeSelect(PageUtil.removeOrders(hql));

		if (page.isAutoCount()) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("\n" + countQuery);
				}

				Query cq = QueryUtil.createQuery(session, entityClass,
						countQuery, values);
				Long count = (Long) cq.uniqueResult();
				page.setTotalCount(count.intValue());
			} catch (Exception e) {
				page.setTotalCount(0);
			}
		}
		if (page.getPageNo() > page.getTotalPages()) {
			page.setPageNo(1);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("PageNo=" + page.getPageNo() + ",TotalPages="
					+ page.getTotalPages());
		}

		if (page.isFirstSetted()) {
			q.setFirstResult(page.getFirst());
		}
		if (page.isPageSizeSetted()) {
			q.setMaxResults(page.getPageSize());
		}
	}
}
