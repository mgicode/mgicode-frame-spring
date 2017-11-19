package com.kuiren.common.dao.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.kuiren.common.exception.DAOException;
import com.kuiren.common.util.StringUtil;

public class CriteriaUtil {
	protected final static Log logger = LogFactory.getLog(CriteriaUtil.class);

	public static Criteria createCriteria(Session session, Class entityClass,
			Criterion... criterions) throws DAOException {
		// Criteria criteria = session.createCriteria(entityClass);
		// if (criterions != null && criterions.length > 0) {
		// for (Criterion c : criterions) {
		// criteria.add(c);
		// }
		// }
		// DeleteUtil.addLogicalDeleteCriteria(criteria, entityClass);
		// return criteria;

		List<Criterion> criterionsList = new ArrayList<Criterion>();
		if (criterions != null && criterions.length > 0) {
			for (Criterion c : criterions) {
				criterionsList.add(c);
			}
		}
		return createCriteria(session, entityClass, criterionsList);
	}

	public static Criteria createCriteria(Session session, Class entityClass,
			List<Criterion> criterions) throws DAOException {
		Criteria criteria = session.createCriteria(entityClass);
		if (criterions != null && criterions.size() > 0) {
			for (Criterion c : criterions) {
				criteria.add(c);
				if (logger.isDebugEnabled()) {
					logger.debug("\n" + entityClass.getName() + "  查询条件："
							+ c.toString());
				}
			}
		}
		DeleteUtil.addLogicalDeleteCriteria(criteria, entityClass);
		return criteria;
	}

	public static void createChildCriterions(Criteria c,
			Map<String, List<Criterion>>... children) {

		if (children == null || children.length < 1) {
			return;
		}
		for (Map<String, List<Criterion>> maps : children) {
			createChildCriterion(c, maps);

		}

	}

	public static void createChildCriterion(Criteria c,
			Map<String, List<Criterion>> child) {
		if (child == null) {
			return;
		}
		for (String key : child.keySet()) {
			Criteria criteria = c.createCriteria(key, key);
			// c.createAlias(paramString1, paramString2)
			for (Criterion c1 : child.get(key)) {
				criteria.add(c1);
				if (logger.isDebugEnabled()) {
					logger.debug("\n" + key + "  查询条件：" + c.toString());
				}
			}
		}

	}

	/**
	 * 根据逗号分隔的字符串查询是否在数据库中对应字段是否存在其中指定的字符串<br>
	 * 主要用来DICTS这种情况，如A用户有擅长：aa,bb,cc,dd,A用户有擅长：aa,bb,dd<br>
	 * 现在查询擅长aa,bb,dd的用户 
	 * mysql /oracle兼容
	 * 
	 * @param cs
	 * @param filedname
	 * @return 彭仁夔 于 2016年3月29日 下午1:49:54 创建
	 */
	public static Criterion buildArrInCondition(Collection<String> cs,
			String filedname) {
		if (cs == null || cs.size() < 1) {
			if (logger.isErrorEnabled()) {
				logger.error("集合不能为null或空");
			}
			throw new RuntimeException("集合不能为null或空");
		}
		if (StringUtil.isNullOrEmpty(filedname)) {
			if (logger.isErrorEnabled()) {
				logger.error("需要表的字段名");
			}
			throw new RuntimeException("需要表的字段名,现在为空");
		}
		Criterion lastCriterion = null;
		for (String s : cs) {
			String sql = " instr({alias}." + filedname + ",'" + s + "') > 0 ";
			if (logger.isDebugEnabled()) {
				logger.debug(sql);
			}
			Criterion c1 = Restrictions.sqlRestriction(sql);
			if (lastCriterion != null) {
				Criterion corCriterion = Restrictions.and(lastCriterion, c1);
				lastCriterion = corCriterion;
			} else {
				lastCriterion = c1;
			}
		}
		return lastCriterion;

		// select * from XT_REGION where instr(all_name,'南昌') >0 or
		// instr(all_name,'北京') >0

		// Criterion c = Restrictions.sqlRestriction(
		// "lower({alias}.SKILLED_SCOPES) like (?)", "%%"
		// + sandArtist.getSkilledScopes() + "%%",

	}

	public static  Criterion buildArrInCondition(String dotsepStr, String filedname) {
		if (StringUtil.isNullOrEmpty(dotsepStr)) {
			if (logger.isErrorEnabled()) {
				logger.error("需要，分隔的字符串");
			}
			throw new RuntimeException("采用，分隔的字符串，现在为空");
		}
		List<String> list = StringUtil.split(dotsepStr, "[,，]");
		return buildArrInCondition(list, filedname);
	}

}
