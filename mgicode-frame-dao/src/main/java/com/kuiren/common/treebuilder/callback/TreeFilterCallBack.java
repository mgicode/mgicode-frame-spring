package com.kuiren.common.treebuilder.callback;

public abstract class TreeFilterCallBack extends DefaultTreeCallBack {
	@Override
	public abstract boolean needSelf(Object d);
}
