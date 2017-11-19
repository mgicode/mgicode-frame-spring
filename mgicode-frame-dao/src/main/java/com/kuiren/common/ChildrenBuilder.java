package com.kuiren.common;

import java.util.ArrayList;
import java.util.List;

import com.kuiren.common.treebuilder.TreeBuilder;
import com.kuiren.common.treebuilder.callback.ITreeCallBack;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.ListUtil;
import com.kuiren.common.util.StringUtil;


/**
 * 
 * @author 彭仁夔
 * 
 * @param <PK>
 *            主键类型，如String ,Integer
 */
public class ChildrenBuilder<PK> {

	// 需要继承 ParentEntity
	private boolean needParentNode = false;
	// 对于非第一级需要传入其父亲节点，只有needParentNode== true时使用
	private Object firstParentNode = null;

	public ChildrenBuilder setNeedParentNode(boolean needParentNode) {
		this.needParentNode = needParentNode;
		return this;
	}

	public ChildrenBuilder setFirstParentNode(Object firstParentNode) {
		this.firstParentNode = firstParentNode;
		return this;
	}

	public <T> List<T> build(List<T> data, String idName, String pidName) {

		return build(data, idName, pidName, "children");
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
	 * @return
	 * 
	 */
	public <T> List<T> build(List<T> data, String idName, String pidName,
			String childrenName) {

		return build(data, idName, pidName, childrenName, (String) null);
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
	 * @return
	 * 
	 */
	public <T> List<T> build(List<T> data, String idName, String pidName,
			String childrenName, String pidValue) {
		return build(data, idName, pidName, childrenName, pidValue,
				(String) null);
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
	 *            节点的名称，节点为叶子节点，其值为1，不是为0，向该字段写入1，或0
	 * @return
	 * 
	 */
	public <T> List<T> build(List<T> data, String idName, String pidName,
			String childrenName, String pidValue, String leafName) {
		return build(data, idName, pidName, childrenName, pidValue, leafName,
				null);
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
	 * @param parentIdvalue
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
			String levelName) {
		return new TreeBuilder<PK>()
				.setNeedParentNode(needParentNode)
				.setFirstParentNode(firstParentNode)
				.build(data, idName, pidName, childrenName, parentIdvalue,
						leafName, levelName, null);
	}

	public <T> List<T> build(List<T> data, String idName, String pidName,
			String childrenName, String parentIdvalue, String leafName,
			String levelName, ITreeCallBack itd) {
		return new TreeBuilder<PK>()
				.setNeedParentNode(needParentNode)
				.setFirstParentNode(firstParentNode)
				.build(data, idName, pidName, childrenName, parentIdvalue,
						leafName, levelName, itd);
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
	 * @return
	 * 
	 */
	public <T> List<T> buildDiy(List<T> data, String idName, String pidName,
			String childrenName, String pidValue, ITreeCallBack itd) {
		return build(data, idName, pidName, childrenName, pidValue, null, null,
				itd);
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
	 * @return
	 * 
	 */
	public <T> List<T> buildDiy(List<T> data, String idName, String pidName,
			String childrenName, ITreeCallBack itd) {
		return buildDiy(data, idName, pidName, childrenName, (String) null, itd);
	}
	
//	
//	List<TResources> rets = new ChildrenBuilder<String>()
//			.setNeedParentNode(true).buildDiy(list, "id", "parentId",
//					"children", new ITreeCallBack() {
//						@Override
//						public boolean afterBuildChildren(Object d,
//								List children) {
//							// 去掉空的文件夹
//							TResources r = (TResources) d;
//							if (!ListUtil.isListEmpty(children)) {
//								List<TResources> removeList = new ArrayList<TResources>();
//								for (TResources c : (List<TResources>) children) {
//									if (ListUtil.isListEmpty(c
//											.getChildren())) {
//										removeList.add(c);
//									}
//								}
//								if (r != null && r.getChildren() != null)
//									r.getChildren().removeAll(removeList);
//							}
//							return true;
//						}
//
//						@Override
//						public boolean needSelf(Object d) {
//							if (d instanceof TResources) {
//								TResources r = (TResources) d;
//								if ("SOURCE_YM".equals(r.getResTypeCode())
//										|| "SOURCE_WJJ".equals(r
//												.getResTypeCode())) {
//									return true;
//								} else {
//									return false;
//								}
//							}
//							return true;
//						}
//
//						@Override
//						public boolean preBuildChildren(Object d) {
//							return true;
//						}
//					});

}
