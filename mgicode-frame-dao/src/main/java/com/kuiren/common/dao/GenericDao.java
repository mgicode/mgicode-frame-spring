package com.kuiren.common.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;

//import org.hibernate.criterion.Order;
import com.kuiren.common.hibernate.Order;
import com.kuiren.common.exception.DAOException;
import com.kuiren.common.page.Page;
import com.kuiren.common.vo.KeyValue;
import com.kuiren.common.vo.KeyValuePair;
import com.kuiren.common.vo.PropertyQuery;

/**
 * 
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 * 
 * @param <T>
 *            实体
 * @param <PK>
 *            主键
 */
public interface GenericDao<T, PK> {

	public PK save(T entity) throws DAOException;

	public void saveOrUpdate(T entity) throws DAOException;

	public void saveAll(List<T> entities);

	public void merge(T entity) throws DAOException;

	public void update(T entity) throws DAOException;

	public void updateAll(List<T> entities) throws DAOException;

	public void delete(T entity) throws DAOException;

	public void deleteById(PK id);

	public void deleteAll(List<T> entities);

	/**
	 * 根据Id载入实体
	 * 
	 * @param id
	 * @return 实体
	 */
	public T findByPK(final PK id);

	/**
	 * 通过hql和指定参数来查询实体（集合）
	 * 
	 * @param hql
	 * @param values
	 * @return 实体（集合）
	 */
	public List<T> findByHql(String hql, Object... values);

	public List<T> findAll() throws DAOException;

	public Page<T> findAll(Page<T> page) throws DAOException;

	public List<T> findByProperty(String propertyName, Object value)
			throws DAOException;

	public T findUniqueByProperty(String propertyName, Object value)
			throws DAOException;

	public Page<T> findByPageHql(Page<T> page, String hql, Object... values)
			throws DAOException;

	public Page<T> findByCriteria(Page<T> page, Criterion... criterion)
			throws DAOException;

	public Page<T> findByCriterions(Page<T> page, List<Criterion> criterions)
			throws DAOException;

	public List<T> findAllByCriterions(List<Criterion> criterions);

	public Session getSession();

	public Page<T> findByCriterions(Page<T> page, List<Criterion> criterions,
			Map<String, List<Criterion>>... children) throws DAOException;


	List<T> findByPropertyQueryOnly(PropertyQuery... propertyQueries);

	List<T> findByPropertyQueryOrder(List<KeyValue> kValues,
			PropertyQuery... propertyQueries);

	List<T> findByPropertyQueryOrdyTop(List<KeyValue> kValues, Integer top,
			PropertyQuery... propertyQueries);

	List<T> findByPropertyQuery(String kvs, Integer top,
			PropertyQuery... propertyQueries);

	void deleteByProperty(String name, Object value);

	public void updateByProperty(List<KeyValuePair> list,
			List<KeyValuePair> where);

	void deleteByProperties(KeyValuePair... pro);

	List<T> findAllByCriterions(List<Criterion> criterions, List<Order> orders);

	Object findObjByHql(String hql, Object... values);

	DaoCallback getDaoCallback();

	void setDaoCallback(DaoCallback daoCallback);

}
