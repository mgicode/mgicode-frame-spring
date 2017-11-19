package com.kuiren.common.treebuilder.callback;

import java.util.List;

public class DefaultTreeCallBack implements ITreeCallBack {

	@Override
	public boolean needSelf(Object d) {

		return true;
	}

	@Override
	public boolean preBuildChildren(Object d) {
		return true;
	}

	@Override
	public boolean afterBuildChildren(Object d, List children) {
		return true;
	}

}
