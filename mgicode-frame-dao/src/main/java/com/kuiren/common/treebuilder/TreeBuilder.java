package com.kuiren.common.treebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kuiren.common.ChildrenBuilder;
import com.kuiren.common.hibernate.ParentEntity;
import com.kuiren.common.treebuilder.callback.ITreeCallBack;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.StringUtil;

public class TreeBuilder<PK> {
	protected final Log logger = LogFactory.getLog(getClass());
	// 需要继承 ParentEntity
	private boolean needParentNode = false;
	// 对于非第一级需要传入其父亲节点，只有needParentNode== true时使用
	private Object firstParentNode = null;

	public TreeBuilder setNeedParentNode(boolean needParentNode) {
		this.needParentNode = needParentNode;
		return this;
	}

	public TreeBuilder setFirstParentNode(Object firstParentNode) {
		this.firstParentNode = firstParentNode;
		return this;
	}

	/**
	 * 把列表数据转换为数据形结构的数据。当然列表数据有数据结构的逻辑 即其有父节点的id和存放子节点的集合
	 * 
	 * @author 彭仁夔于2015年10月19日下午7:23:17创建
	 * @param data
	 *            list的列表数据
	 * @param idName
	 *            id的字段名称 ,一般为id
	 * @param pidName
	 *            父字段的名称, 一般为pid,parentid
	 * @param childrenName
	 *            子集合字段的名称，一般为children
	 * @param pidValue
	 *            父节点的值
	 * @param leafName
	 *            节点的名称，节点为叶子节点，其值为1，不是为0
	 * @param levelName
	 *            节点登级的名称，向该字段写入其所在层级数
	 * @return
	 * 
	 */
	public <T> List<T> build(List<T> data, String idName, String pidName,
			String childrenName, String parentIdvalue, String leafName,
			String levelName, ITreeCallBack itd) {
		// 构建新的list,所有的树形数据 list AllListData
		List<T> list = copyList(data);
		try {
			// 找到所有的parentList并进行初始化
			List<T> parentlist = new ArrayList<T>();
			for (T sci : list) {
				if (sci != null) {
					initLevelAndLeaf(sci, leafName, levelName);
					// 从All list
					// Data中找到符合parentIdvalue的的记录，该记录可以是第一层，也有可能是第二级...,第n级
					// 如果是第n级的，接下来从第N级开始找
					if (isParent(sci, pidName, parentIdvalue)) {
						initLevel(sci, levelName);
						parentlist.add(sci);
					}
				}
			}
			// 查寻的列表中去掉已经找到第一级父亲节点
			list.removeAll(parentlist);

			// 主要进行过滤处理，调用者可以通过needSelf来过滤该第一级的数据列表
			List<T> plist = buildFilter(parentlist, itd);
			if (logger.isDebugEnabled()) {

				try {
					for (T t : plist) {
						logger.debug(BeanUtil.getFieldValue(t, idName) + ":"
								+ BeanUtil.getFieldValue(t, "name") + ":pid:"
								+ BeanUtil.getFieldValue(t, pidName));

					}
				} catch (Exception e) {
				}
				logger.debug("/******父节点结束*********************/");
			}

			for (T p : plist) {
				// 和父亲节点关联起来
				if ((p instanceof ParentEntity) && (needParentNode)) {
					((ParentEntity) p).setParentNode(firstParentNode);
				}
				buildChildren(p, list, idName, pidName, childrenName, leafName,
						levelName, itd);
			}
			return plist;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private <T> List<T> buildChild(T parent, List<T> list, String idName,
			String pidName, String childrenName, String leafName,
			String levelName, ITreeCallBack itd) throws Exception {
		if (parent == null || list == null || list.size() == 0) {
			return null;
		}
		List<T> rList = new ArrayList<T>();
		PK id = (PK) BeanUtil.readFieldValue(parent, idName);

		// 从剩下的列表中找到其parentid和id相同节点,放到rList
		for (T csi : list) {
			PK pid = (PK) BeanUtil.readFieldValue(csi, pidName);
			if (id.equals(pid)) {
				setLevelAndLeaf(parent, levelName, leafName, csi);
				rList.add(csi);
			}
		}
		// 去掉找到节点
		list.removeAll(rList);
		// 主要进行过滤处理
		List<T> rList1 = buildFilter(rList, itd);
		if (logger.isDebugEnabled()) {

			try {
				for (T t : rList1) {

					// logger.debug(BeanUtil.getFieldValue(t, idName) + ":"
					// + BeanUtil.getFieldValue(t, "name") + ":pid:"
					// + BeanUtil.getFieldValue(t, pidName));

				}
			} catch (Exception e) {
			}
			logger.debug("/******父节点结束*********************/");
		}
		// 构建子节点
		for (T p : rList1) {
			if ((p instanceof ParentEntity) && (needParentNode)) {
				((ParentEntity) p).setParentNode(parent);
			}
			buildChildren(p, list, idName, pidName, childrenName, leafName,
					levelName, itd);
		}

		return rList1;

	}

	private <T> List<T> copyList(List<T> data) {
		// 构建新的list
		List<T> list = new ArrayList<T>();
		for (T t : data) {
			list.add(t);
		}
		return list;
	}

	private <T> void initLevel(T sci, String levelName) throws Exception {
		if (StringUtil.IsNotNullOrEmpty(levelName)) {
			BeanUtil.writeFieldValue(sci, levelName, 1);
		}

	}

	private <T> void initLevelAndLeaf(T sci, String leafName, String levelName)
			throws Exception {
		if (StringUtil.IsNotNullOrEmpty(leafName)) {
			BeanUtil.writeFieldValue(sci, leafName, 1);
		}
		if (StringUtil.IsNotNullOrEmpty(levelName)) {
			BeanUtil.writeFieldValue(sci, levelName, 999);
		}

	}

	private <T> boolean isParent(T sci, String pidName, String parentIdvalue) {

		String pid = BeanUtil.readFieldValue(sci, pidName) + "";

		// 树形的根结点，采用传入的parentIdvalue==null,其取到的值为null '',0 null等都可以
		boolean rooted = (parentIdvalue == null)
				&& (StringUtil.isNullOrEmpty(pid) || "0".equals(pid)
						|| "null".equals(pid) || "undefined".equals(pid));
		// 中间的节点 作为父亲节点
		boolean midded = (parentIdvalue != null && parentIdvalue.equals(pid));

		if (logger.isDebugEnabled()) {
			logger.debug("pidName:" + pidName + ",pid:" + pid
					+ ",parentIdvalue:" + parentIdvalue + ",rooted:" + rooted
					+ ",midded:" + midded + "");
		}
		return (rooted || midded);
	}

	private <T> void setLevelAndLeaf(T parent, String levelName,
			String leafName, T csi) throws Exception {

		if (StringUtil.IsNotNullOrEmpty(leafName)) {
			BeanUtil.writeFieldValue(parent, leafName, 0);
		}

		if (StringUtil.IsNotNullOrEmpty(levelName)) {
			int level = Integer.parseInt(buildLevelValue(parent, levelName));
			BeanUtil.writeFieldValue(csi, levelName, (level + 1));

		}
	}

	private <T> String buildLevelValue(T parent, String levelName) {
		String levelv = "1";
		if (StringUtil.IsNotNullOrEmpty(levelName)) {
			levelv = String.valueOf(BeanUtil.readFieldValue(parent, levelName));
			if (levelv == null) {
				levelv = "1";
			}
		}
		return levelv;
	}

	private <T> void buildChildren(T p, List<T> list, String idName,
			String pidName, String childrenName, String leafName,
			String levelName, ITreeCallBack itd) throws Exception {
		// 自定义的设定数据
		if (preBuild(itd, p) == false) {
			return;
		}

		List<T> childTs = buildChild(p, list, idName, pidName, childrenName,
				leafName, levelName, itd);

		if (afterbuild(itd, p, childTs) == false) {
			return;
		}
		if (StringUtil.IsNotNullOrEmpty(childrenName)) {
			BeanUtil.writeFieldValue(p, childrenName, childTs);
		}

	}

	private <T> List<T> buildFilter(List<T> parentlist, ITreeCallBack itd) {
		// 主要进行过滤处理
		List<T> pl = new ArrayList<T>();
		for (T p : parentlist) {
			if (needSelf(itd, p)) {
				pl.add(p);
			}
		}

		return pl;
	}

	private boolean afterbuild(ITreeCallBack itd, Object d, List children) {

		if (itd == null) {
			return true;
		}
		boolean f = itd.afterBuildChildren(d, children);
		return f;
	}

	private boolean preBuild(ITreeCallBack itd, Object d) {
		if (itd == null) {
			return true;
		}
		boolean f = itd.preBuildChildren(d);
		return f;

	}

	private boolean needSelf(ITreeCallBack itd, Object d) {
		if (itd == null) {
			return true;
		}
		boolean f = itd.needSelf(d);
		return f;

	}

}
