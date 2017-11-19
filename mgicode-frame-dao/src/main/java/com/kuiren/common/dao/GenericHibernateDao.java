package com.kuiren.common.dao;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.kuiren.common.dao.hibernate.BatchDelete;
import com.kuiren.common.dao.hibernate.BatchUpdate;
import com.kuiren.common.dao.hibernate.CriteriaUtil;
import com.kuiren.common.dao.hibernate.DeleteUtil;
import com.kuiren.common.dao.hibernate.OrderUtil;
import com.kuiren.common.dao.hibernate.PageUtil;
import com.kuiren.common.dao.hibernate.PropertyQueryUtil;
import com.kuiren.common.dao.hibernate.QueryUtil;
import com.kuiren.common.exception.DAOException;
import com.kuiren.common.hibernate.BaseEntity;
import com.kuiren.common.hibernate.EntityUtil;
import com.kuiren.common.hibernate.Order;
import com.kuiren.common.page.Page;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.KeyValue;
import com.kuiren.common.vo.KeyValuePair;
import com.kuiren.common.vo.PropertyQuery;

//import org.hibernate.criterion.Order;

/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL 基于Hibernate的通用的进行数据库操作的类
 * 
 */
public class GenericHibernateDao<T, PK> implements GenericDao<T, PK>,
		java.io.Serializable {
	private static final long serialVersionUID = -9010994443690877889L;
	protected final Log logger = LogFactory.getLog(getClass());
	protected SessionFactory sessionFactory;
	protected Class<T> entityClass;

	private DaoCallback daoCallback = new DefaultDaoCallback();

	public Map<String, String> entityNamesMap = new HashMap<String, String>();

	@SuppressWarnings("unchecked")
	public GenericHibernateDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		entityClass = BeanUtil.getSuperClassGenricType(getClass());
	}

	public GenericHibernateDao(SessionFactory sessionFactory,
			Class<T> entityClass) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
	}

	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() throws DAOException {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public PK save(T entity) throws DAOException {
		Assert.notNull(entity);
		boolean flag = daoCallback.beforeAdd(entity);
		if (flag == true) {
			if (entity instanceof BaseEntity) {
				Map map = ((BaseEntity) entity).getParamMap();
				if (map != null && map.containsKey("idValue")) {
					String sql = "insert into "
							+ BeanUtil.getTableName(getEntityClass())
							+ "(id) values('"
							+ ((BaseEntity) entity).getParamMap()
									.get("idValue") + "')";
					if (logger.isDebugEnabled()) {
						logger.debug(sql);
					}
					getSession().createSQLQuery(sql).executeUpdate();
					getSession().update(entity);
				}

			}
			PK pk = (PK) getSession().save(entity);
			daoCallback.afterAdd(entity);
			return pk;
		}
		return null;

	}

	public void saveOrUpdate(final T entity) throws DAOException {

		Assert.notNull(entity);
		// pengrk modefy at 2016.6.15
		PK idPk = EntityUtil.getId(entity);
		if (idPk == null) {
			save(entity);
		} else {
			update(entity);
		}
		// getSession().saveOrUpdate(entity);

	}

	public void saveAll(List<T> entities) {
		if (entities == null)
			return;
		for (T entity : entities) {
			saveOrUpdate(entity);
		}
	}

	public void update(T entity) throws DAOException {
		boolean flag = daoCallback.beforeUpdate(entity);
		if (flag == true) {
			getSession().update(entity);
			daoCallback.afterUpdate(entity);
			if (logger.isDebugEnabled()) {
				logger.debug("save entity: " + entity.toString());
			}
		}

	}

	public void delete(T entity) throws DAOException {
		if (null != entity) {
			PropertyDescriptor pd = DeleteUtil.canLogicalDelete(entityClass);
			if (pd != null) {
				DeleteUtil.setLogicalDeleteState(entity, pd);
				this.update(entity);
				if (logger.isDebugEnabled()) {
					logger.debug("\n逻辑删除成功");
				}
			} else {
				if (daoCallback.beforeDelete(entity)) {
					getSession().delete(entity);
					daoCallback.afterDelete(entity);
					if (logger.isDebugEnabled()) {
						logger.debug("\n删除成功");
					}
				}

			}
		}
	}

	public void deleteById(PK id) {
		T entity = findByPK(id);
		if (logger.isDebugEnabled()) {
			if (entity == null) {
				logger.debug("\n根据" + id + "找不到实体");
			} else {
				logger.debug("\n根据" + id + "找到了实体");
			}
		}
		delete(entity);

	}

	public void deleteAll(List<T> entities) {
		if (entities == null)
			return;
		for (T entity : entities) {
			delete(entity);
		}
	}

	public void updateAll(List<T> entities) throws DAOException {
		if (entities.size() > 0) {
			for (T entity : entities) {
				update(entity);
			}
		}
	}

	@Override
	public void deleteByProperty(String name, Object value) {
		new BatchDelete().deleteByProperty(this, name, value);
	}

	@Override
	public void deleteByProperties(KeyValuePair... pro) {
		new BatchDelete().deleteByProperties(this, pro);
	}

	@Override
	public void updateByProperty(List<KeyValuePair> list,
			List<KeyValuePair> where) {
		new BatchUpdate().updateByProperty(this, list, where);

	}

	@Deprecated
	public void merge(T entity) throws DAOException {
		// getSession().merge(entity);
	}

	@SuppressWarnings("unchecked")
	private List<T> find(final Criterion... criterions) {
		return CriteriaUtil.createCriteria(getSession(), entityClass,
				criterions).list();
	}

	@SuppressWarnings("unchecked")
	public Page<T> findByPageHql(Page<T> page, String hql, Object... values)
			throws DAOException {
		Assert.notNull(page, "page不能为空");

		Query q = QueryUtil.createQuery(getSession(), entityClass, hql, values);

		QueryUtil.calPageCount(getSession(), entityClass, q, page, hql, values);

		List result = q.list();
		if (result != null && result.size() > 0
				&& !(result.get(0) instanceof Integer)) {
			page.setResult(result);
		}
		return page;

	}

	@Override
	public List<T> findByHql(String hql, Object... values) {
		return QueryUtil.createQuery(getSession(), entityClass, hql, values)
				.list();
	}

	@Override
	public Object findObjByHql(String hql, Object... values) {
		return QueryUtil.createQuery(getSession(), entityClass, hql, values)
				.uniqueResult();
	}

	public List<T> findByProperty(String propertyName, Object value) {
		Assert.hasText(propertyName, "propertyName不能为空");
		// Criterion criterion = Restrictions.eq(propertyName, value);
		Criterion criterion = null;
		if (value == null) {
			criterion = Restrictions.isNull(propertyName);
		} else {
			criterion = Restrictions.eq(propertyName, value);
		}
		return find(criterion);
	}

	@SuppressWarnings("unchecked")
	public T findUniqueByProperty(String propertyName, Object value)
			throws DAOException {
		Assert.hasText(propertyName);
		Criterion c = null;
		if (value == null) {
			c = Restrictions.isNull(propertyName);
		} else {
			c = Restrictions.eq(propertyName, value);
		}
		return (T) CriteriaUtil.createCriteria(getSession(), entityClass, c)
				.uniqueResult();

	}

	@Override
	public List<T> findByPropertyQueryOnly(PropertyQuery... propertyQueries) {
		return findByPropertyQueryOrder((List<KeyValue>) null, propertyQueries);
	}

	@Override
	public List<T> findByPropertyQueryOrder(List<KeyValue> kvs,
			PropertyQuery... pqs) {
		return findByPropertyQueryOrdyTop(kvs, null, pqs);
	}

	@Override
	public List<T> findByPropertyQueryOrdyTop(List<KeyValue> kvs, Integer top,
			PropertyQuery... pqs) {
		// Criteria criteria = getSession().createCriteria(entityClass);
		Criteria criteria = CriteriaUtil.createCriteria(getSession(),
				entityClass);
		for (PropertyQuery pq : pqs) {
			Criterion criterion = PropertyQueryUtil.createCriterion(pq);
			criteria.add(criterion);
		}

		if (kvs != null && kvs.size() > 0) {
			OrderUtil.addOrders(kvs, criteria);
		}
		if (top != null) {
			criteria.setFirstResult(0);
			criteria.setMaxResults(top);
		}
		return criteria.list();
	}

	@Override
	public List<T> findByPropertyQuery(String kvs, Integer top,
			PropertyQuery... propertyQueries) {
		List<KeyValue> list = StringUtil.getKeyValues(kvs);
		return findByPropertyQueryOrdyTop(list, top, propertyQueries);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> findByCriteria(Page page, Criterion... criterion)
			throws DAOException {
		Assert.notNull(page);
		Criteria c = CriteriaUtil.createCriteria(getSession(), entityClass,
				criterion);
		PageUtil.calPageCount(page, c);

		List result = c.list();
		if (result != null && result.size() > 0
				&& !(result.get(0) instanceof Integer)) {
			page.setResult(result);
		}
		return page;

	}

	public List<T> findAll() throws DAOException {
		return find();
	}

	public Page<T> findAll(Page<T> page) throws DAOException {
		return findByCriteria(page);
	}

	@Override
	public T findByPK(final PK id) {
		return (T) getSession().get(entityClass, (Serializable) id);
	}

	@Override
	public List<T> findAllByCriterions(List<Criterion> criterions) {
		Criteria c = CriteriaUtil.createCriteria(getSession(), entityClass,
				criterions);
		List result = c.list();
		return result;

	}

	@Override
	public List<T> findAllByCriterions(List<Criterion> criterions,
			List<Order> orders) {
		Criteria c = CriteriaUtil.createCriteria(getSession(), entityClass,
				criterions);
		OrderUtil.addOrderList(orders, c);
		List result = c.list();
		return result;

	}

	@Override
	public Page<T> findByCriterions(Page<T> page, List<Criterion> criterions,
			Map<String, List<Criterion>>... children) throws DAOException {
		Assert.notNull(page);

		Criteria c = CriteriaUtil.createCriteria(getSession(), entityClass,
				criterions);

		if (children != null && children.length > 0) {
			CriteriaUtil.createChildCriterions(c, children);
		}

		PageUtil.calPageCount(page, c);

		OrderUtil.calOrder(page, c);

		List result = c.list();
		if (result != null && result.size() > 0
				&& !(result.get(0) instanceof Integer)) {
			page.setResult(result);
		}

		return page;

	}

	@Override
	public Page<T> findByCriterions(Page<T> page, List<Criterion> criterions)
			throws DAOException {
		return findByCriterions(page, criterions,
				(Map<String, List<Criterion>>) null);
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	@Override
	public DaoCallback getDaoCallback() {
		return daoCallback;
	}

	@Override
	public void setDaoCallback(DaoCallback daoCallback) {
		if (daoCallback == null)
			this.daoCallback = new DefaultDaoCallback();
		else
			this.daoCallback = daoCallback;
	}

}
