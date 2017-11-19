package com.kuiren.common.treebuilder.callback;

import java.util.List;

public abstract class TreeDealCallBack extends DefaultTreeCallBack {

	public abstract boolean afterBuildChildren(Object d, List children);
}
