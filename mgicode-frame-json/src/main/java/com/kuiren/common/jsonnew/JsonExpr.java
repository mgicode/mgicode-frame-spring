package com.kuiren.common.jsonnew;

public class JsonExpr {

	private String propName;
	private String replPropName;
	private String target;

	private INameConvert nameConvert;
	private IValueConvert valueConvert;
	public String getPropName() {
		return propName;
	}
	public void setPropName(String propName) {
		this.propName = propName;
	}
	public String getReplPropName() {
		return replPropName;
	}
	public void setReplPropName(String replPropName) {
		this.replPropName = replPropName;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public INameConvert getNameConvert() {
		return nameConvert;
	}
	public void setNameConvert(INameConvert nameConvert) {
		this.nameConvert = nameConvert;
	}
	public IValueConvert getValueConvert() {
		return valueConvert;
	}
	public void setValueConvert(IValueConvert valueConvert) {
		this.valueConvert = valueConvert;
	}

	// private String nameExpr;

	// private String valueExpr;

}
