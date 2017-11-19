package com.kuiren.common.dao.hibernate;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

import com.kuiren.common.exception.DAOException;
import com.kuiren.common.page.Page;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.StringUtil;

public class PageUtil {
	protected final static Log logger = LogFactory.getLog(PageUtil.class);

	public static String removeSelect(String hql) {
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().indexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql
				+ " must has a keyword 'from'");
		return hql.substring(beginPos);
	}

	public static String removeOrders(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	// 把hql前半部分和后部分给拆分，把其实中的 order by ,group by
	public static List<String> splitHql(String hql) {
		Assert.hasText(hql);
		List<String> strs = new ArrayList<String>();
		Pattern p = Pattern.compile("(.*)(order\\s*by[\\w|\\W|\\s|\\S]*)*",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		if (m.find()) {
			String name = m.group(1);
			strs.add(name);
			String order = m.group(2);
			if (StringUtil.isNullOrEmpty(order)) {
				strs.add(order);
			}
		}
		return strs;
	}

	@SuppressWarnings("unchecked")
	public static int countQueryResult(Page page, Criteria c)
			throws DAOException {
		try {
			CriteriaImpl impl = (CriteriaImpl) c;
			Projection projection = impl.getProjection();
			ResultTransformer transformer = impl.getResultTransformer();

			List<CriteriaImpl.OrderEntry> orderEntries = null;
			try {
				orderEntries = (List) BeanUtil.getFieldValue(impl,
						"orderEntries");
				BeanUtil.setFieldValue(impl, "orderEntries", new ArrayList());
			} catch (Exception e) {
				logger.error("绑定对象时出错");
			}

			//hibernate 3.6使用了long,而不是int
			Object object = c.setProjection(Projections.rowCount())
					.uniqueResult();
			int totalCount = -1;
			if (object instanceof Long) {
				totalCount = ((Long) object).intValue();
			} else {
				totalCount = (Integer) object;
			}

			// int totalCount = (Integer)
			// c.setProjection(Projections.rowCount())
			// .uniqueResult();
			if (totalCount < 1)
				totalCount = -1;

			c.setProjection(projection);

			if (projection == null) {
				c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
			}
			if (transformer != null) {
				c.setResultTransformer(transformer);
			}

			try {
				BeanUtil.setFieldValue(impl, "orderEntries", orderEntries);
			} catch (Exception e) {
				logger.error("绑定对象时出错");
			}

			return totalCount;
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	public static String mixupNoLogicalDeleteSql(String sql, Class entityClass) {
		String qs = sql;
		PropertyDescriptor pd = DeleteUtil.canLogicalDelete(entityClass);
		if (pd != null) {
			List<String> strings = PageUtil.splitHql(sql);
			if (strings != null && strings.size() > 0) {
				qs = strings.get(0);
				String string = "( " + pd.getName() + " is null or "
						+ pd.getName() + " = "
						+ DeleteUtil.LOGICALDELETE_NO_DELETE + " )";
				if (qs.contains("where")) {
					qs = qs + " and  " + string;
				} else {
					qs = qs + " where " + string;
				}
				if (strings.size() > 1 && strings.get(1) != null) {
					qs = qs + strings.get(1);
				}
			}
		}

		return qs;
	}

	public static void calPageCount(Page page, Criteria c) {

		if (page.isAutoCount()) {
			try {
				int count = PageUtil.countQueryResult(page, c);

				page.setTotalCount(count);
				if (logger.isDebugEnabled()) {
					logger.debug("\n查询的总的记录为：" + count);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug(e);
			}
		}
		if (page.isFirstSetted()) {
			if (logger.isDebugEnabled()) {
				logger.debug("\n查询的分页的起始位置为：" + page.getFirst());
			}
			c.setFirstResult(page.getFirst());
		}
		if (page.isPageSizeSetted()) {
			if (logger.isDebugEnabled()) {
				logger.debug("\n查询的分页的每页条数（pagesize)为：" + page.getPageSize());
			}
			c.setMaxResults(page.getPageSize());
		}

	}
}
