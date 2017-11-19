package com.kuiren.common.treefilter;

import java.util.ArrayList;
import java.util.List;

import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.ListUtil;

/**
 * 会破坏原来的数据结构
 * 
 * @author prk
 * 
 */
public class TreeFilter {

	private TreeConfig config;

	public List filter(List data, IPropQuery propQuery) {
		return this.filter(data, "children", propQuery);
	}

	public List filter(List data, String childrenName, IPropQuery propQuery) {

		config = new TreeConfig();
		config.setChildrenName(childrenName);
		config.setPropQuery(propQuery);

		return this.init(config).filter(data);
	}

	public List filter(List data, TreeConfig config) {

		return this.init(config).filter(data);
	}

	public TreeFilter init(TreeConfig config) {

		this.config = config;
		return this;
	}

	public List copy(List data) {

		List retList = new ArrayList();
		if (data == null || data.size() < 1) {
			return retList;
		}
		for (Object o : data) {
			Object o1 = BeanUtil.clone(o.getClass(), o);
			List oldChildren = getChildren(o);

			if (!ListUtil.isListEmpty(oldChildren)) {// 有子节点的children
				List newChildren = copy(oldChildren);
				setChildren(o1, newChildren);
			}
			// 把新创建的数据加入到返回的集合中去
			// addToList(retList, o1);
			retList.add(o1);
		}

		return retList;

	}

	public List filter(List data) {
		List list = copy(data);
		filterData(list);
		return list;
	}

	private void filterData(List data) {
		if (data != null) {
			List removeList = new ArrayList();
			for (Object o : data) {
				List oldChildren = getChildren(o);
				if (oldChildren != null && oldChildren.size() > 0) {// 有子节点的children
					filterData(oldChildren);
				}
				// 有子节点，不参与其条件过滤
				if (!(retainParent(oldChildren) || buzDecide(o))) {
					removeList.add(o);
				}
			}
			
			
			data.removeAll(removeList);
		}
	}
	
	

	private boolean buzDecide(Object o) {

		boolean flag = config.getPropQuery().decide(o);
		return flag;
	}
	
	

	// // 有子节点，不参与其条件过滤
	private boolean retainParent(List oldChildren) {

		boolean flag = (config.isNeedStayParent() == true && !ListUtil
				.isListEmpty(oldChildren));
		return flag;
	}

	public List filterNoCopy(List data) {

		List retList = new ArrayList();
		if (data == null || data.size() < 1) {
			return retList;
		}

		for (Object o : data) {

			Object o1 = BeanUtil.clone(o.getClass(), o);
			List oldChildren = getChildren(o);
			List newChildren = null;

			if (oldChildren != null && oldChildren.size() > 0) {// 有子节点的children
				newChildren = filterNoCopy(oldChildren);
				setChildren(o1, newChildren);
			}

			// 有子节点，不参与其条件过滤
			if (config.isNeedStayParent() == true && newChildren != null
					&& newChildren.size() > 0) {
				addToList(retList, o1);

			} else {
				if (config.getPropQuery().decide(o) == true) {
					addToList(retList, o1);
				}
			}

		}
		return retList;
	}

	// private boolean needAdd(){
	//
	// // 有子节点，不参与其条件过滤
	// if (config.isNeedStayParent() == true && newChildren != null
	// && newChildren.size() > 0) {
	// addToList(retList, o);
	//
	// } else {
	// if (config.getPropQuery().decide(o) == true) {
	// addToList(retList, o);
	// }
	// }
	//
	// return false;
	//
	// }

	private void addToList(List retList, Object o) {

		retList.add(o);

	}

	// private boolean hasChildren(Object o) {
	// return BeanUtil.hasField(o, config.getChildrenName());
	// }

	private List getChildren(Object o) {
		return (List) BeanUtil.getFieldValue(o, config.getChildrenName());

	}

	private void setChildren(Object o, List clist) {
		try {
			BeanUtil.setFieldValue(o, config.getChildrenName(), clist);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
