package com.kuiren.common.code;

import java.util.List;

public interface ICodeBuilder<T, PK> {

	public T buildLevel(T d, T parentNode);

	public List<T> buildLevel(final List<T> data, T parentNode,
			final boolean codeReStarted);
	public String buildSortedNum(String buzCode);
}
