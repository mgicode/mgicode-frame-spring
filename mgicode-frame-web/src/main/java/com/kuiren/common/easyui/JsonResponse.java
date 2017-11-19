package com.kuiren.common.easyui;

import com.kuiren.common.context.DataObjectRes;



public class JsonResponse extends DataObjectRes {

	public JsonResponse() {
		super();
	}

	public JsonResponse(Object data) {
		super(data);
	}

	public JsonResponse put(String name, Object v) {
		if (getMap() != null)
			getMap().put(name, v);
		return this;
	}

}
