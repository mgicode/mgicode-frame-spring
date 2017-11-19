package com.kuiren.common.treefilter;


public class TreeConfig {

	private IPropQuery propQuery;

	private String childrenName = "children";
	// 是否需要保存父亲节点
	private boolean needStayParent = true;
	public IPropQuery getPropQuery() {
		return propQuery;
	}
	public void setPropQuery(IPropQuery propQuery) {
		this.propQuery = propQuery;
	}
	public String getChildrenName() {
		return childrenName;
	}
	public void setChildrenName(String childrenName) {
		this.childrenName = childrenName;
	}
	public boolean isNeedStayParent() {
		return needStayParent;
	}
	public void setNeedStayParent(boolean needStayParent) {
		this.needStayParent = needStayParent;
	}
	
	
}
