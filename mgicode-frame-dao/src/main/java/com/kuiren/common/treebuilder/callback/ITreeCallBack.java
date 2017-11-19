package com.kuiren.common.treebuilder.callback;

import java.util.List;

public interface ITreeCallBack {

	/**
	 * 主要用来setchecked状态的
	 * 
	 * @param d
	 *            彭仁夔 于 2016年4月28日 上午11:58:31 创建
	 */

	public boolean needSelf(Object d);

	public boolean preBuildChildren(Object d);

	public boolean afterBuildChildren(Object d, List children);
}
