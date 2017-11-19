package com.kuiren.common.dao;

public interface DaoCallback {

	public boolean beforeAdd(Object entity);

	public void afterAdd(Object entity);

	public boolean beforeUpdate(Object entity);

	public void afterUpdate(Object entity);

	public boolean beforeDelete(Object entity);

	public void afterDelete(Object entity);

	public boolean beforeHQLDel(String hql, Object... params);

	public void afterHQLDel(String hql, Object... params);
	
	public boolean beforeHQLUpdate(String hql, Object... params);

	public void afterHQLUpdate(String hql, Object... params);

}
