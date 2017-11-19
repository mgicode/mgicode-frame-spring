package com.kuiren.common.hibernate;

import javax.persistence.Transient;

public class ParentEntity extends BaseEntity {

	protected Object parentNode;

	@Transient
	public Object getParentNode() {
		return parentNode;
	}

	public void setParentNode(Object parentNode) {
		this.parentNode = parentNode;
	}

}
