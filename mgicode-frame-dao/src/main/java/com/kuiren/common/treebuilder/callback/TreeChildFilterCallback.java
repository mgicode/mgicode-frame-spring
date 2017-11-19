package com.kuiren.common.treebuilder.callback;

import java.util.List;

public abstract class TreeChildFilterCallback extends DefaultTreeCallBack {

	public abstract boolean preBuildChildren(Object d);
}
