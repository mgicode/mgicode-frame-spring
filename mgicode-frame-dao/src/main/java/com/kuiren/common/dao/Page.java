package com.kuiren.common.dao;

import java.util.ArrayList;
import java.util.List;


import com.kuiren.common.hibernate.Order;
import com.kuiren.common.vo.KeyValue;

/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class Page<Map> implements java.io.Serializable  {
	/**
	 * 当前页的数据集合
	 */
	private List<Map> result = null;

	/**
	 * 当前页的序号
	 */
	private int pageNo = 1;

	
	/**
	 * 页面大小
	 */
	private int pageSize = -1;

	/**
	 * 总记录数
	 */
	private int totalCount = -1;
	/**
	 * 总页数
	 */
	private int totalPages;
	
	public Page() {
	}

	public Page(int pageSize) {
		this.pageSize = pageSize;
	}

	

	public Page(List<Map> list, int totalCount, int pageSize, int pageNo) {
		this.result=list;		
		this.setResult(list);
		this.setTotalCount(totalCount);
		this.setPageNo(pageNo);
		this.setPageSize(pageSize);
	}

	
	public List<Map> getResult() {

		return result;
	}

	public void setResult(List<Map> result) {
		this.result = result;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPages() {
		if (totalCount == -1)
			return -1;

		int count = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			count++;
		}
		return count;
	}

	public boolean hasNext() {
		return (pageNo + 1 <= getTotalPages());
	}

	public int getNextPage() {
		if (hasNext())
			return pageNo + 1;
		else
			return pageNo;
	}

	public boolean hasPre() {
		return (pageNo - 1 >= 1);
	}

	public int getPrePage() {
		if (hasPre())
			return pageNo - 1;
		else
			return pageNo;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}


	
}
