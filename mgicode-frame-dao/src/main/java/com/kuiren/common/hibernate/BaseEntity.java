package com.kuiren.common.hibernate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.kuiren.common.annotation.Desc;
import com.kuiren.common.annotation.LogicalDelete;

public class BaseEntity implements java.io.Serializable {

	protected Map<String, String> paramMap = new HashMap<String, String>();
	
	protected Map<String, Object> tempMap=new HashMap<String, Object>();
	

	/**
	 * 临时的参数
	 * 
	 * @return 彭仁夔 于 2016年3月23日 下午1:38:54 创建
	 */
	@Transient
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public void setCreateUser(String createUser) {

	}
	public void setSortedNum(Integer sortedNum) {

	}
	public void setCreateTime(Date createTime) {

	}
	
	@Transient
	public Map<String, Object> getTempMap() {
		return tempMap;
	}

	public void setTempMap(Map<String, Object> tempMap) {
		this.tempMap = tempMap;
	}

}
