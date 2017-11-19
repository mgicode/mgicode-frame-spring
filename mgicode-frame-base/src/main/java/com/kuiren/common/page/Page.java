package com.kuiren.common.page;

import java.util.ArrayList;
import java.util.List;

//import com.kuiren.common.hibernate.Order;
import com.kuiren.common.vo.KeyValue;
import org.springframework.core.annotation.Order;

/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class Page<T> extends QueryParameter implements java.io.Serializable  {
	/**
	 * 当前页的数据集合
	 */
	private List<T> result = null;

	/**
	 * 主要用来设定小量的前台分页 所有的数据集合
	 */
	private List<T> allResult = null;
	/**
	 * 总记录数
	 */
	private int totalCount = -1;
	/**
	 * 总页数
	 */
	private int totalPages;
	/**
	 * 组合排序的方式 使用方式如下： <code>
	 * 	questionsPage.getOrderbyMap().add(new KeyValue("sendDate", "desc"));
	 * 	questionsPage.getOrderbyMap().add(new KeyValue("questionId", "asc"));
	 * 	</code>
	 */
	private List<KeyValue> orderbyMap = new ArrayList<KeyValue>();

	//todo:
	//private List<Order> orderList = new ArrayList<Order>();

	public Page() {
	}

	public Page(int pageSize) {
		this.pageSize = pageSize;
	}

	public Page(int pageSize, boolean autoCount) {
		this.pageSize = pageSize;
		this.autoCount = autoCount;
	}

	public String getInverseOrder() {
		if (order.endsWith(DESC))
			return ASC;
		else
			return DESC;
	}

	public List<T> getResult() {

		return result;
	}

	public void setResult(List<T> result) {
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

	public List<T> getAllResult() {
		return allResult;
	}

	public void setAllResult(List<T> allResult) {
		this.allResult = allResult;
		if (this.allResult != null) {
			this.totalCount = allResult.size();
			int start = (pageNo - 1) * pageSize;
			int end = pageNo * pageSize;
			if (this.totalCount < end + 1) {
				end = this.totalCount - 1;
			}
			for (int i = start; i < end + 1; i++) {
				if (this.result == null) {
					this.result = new ArrayList<T>();
				}
				this.result.add(allResult.get(i));
			}

		} else {

			this.totalCount = 0;
		}

	}

	public List<KeyValue> getOrderbyMap() {
		return orderbyMap;
	}

	public void setOrderbyMap(List<KeyValue> orderbyMap) {
		this.orderbyMap = orderbyMap;
	}

	public void addOrderBy(String name, String order) {

		getOrderbyMap().add(new KeyValue(name, order));
	}

	//todo:
//	public List<Order> getOrderList() {
//		return orderList;
//	}
//
//	public void setOrderList(List<Order> orderList) {
//		this.orderList = orderList;
//	}

	@Deprecated
	@Override
	public void setOrderBy(String orderBy) {
		// this.orderBy = orderBy;
		getOrderbyMap().add(new KeyValue(orderBy, getOrder()));
	}

}
