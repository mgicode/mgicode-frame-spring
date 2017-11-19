package com.kuiren.common.dao;

public class DefaultDaoCallback implements DaoCallback {

	public boolean beforeAdd(Object entity) {
		return true;
	}

	public void afterAdd(Object entity) {

	}

	@Override
	public boolean beforeUpdate(Object entity) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void afterUpdate(Object entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean beforeDelete(Object entity) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void afterDelete(Object entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean beforeHQLDel(String hql,Object ...params) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void afterHQLDel(String hql,Object ...params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean beforeHQLUpdate(String hql, Object... params) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void afterHQLUpdate(String hql, Object... params) {
		// TODO Auto-generated method stub
		
	}

}
