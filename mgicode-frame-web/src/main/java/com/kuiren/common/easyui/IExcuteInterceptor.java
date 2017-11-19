package com.kuiren.common.easyui;

/**
 * 运行spring的方法之前或之后进行拦截处理
 * 
 * 彭仁夔 于 2016年4月17日 下午9:49:34 创建
 */
public interface IExcuteInterceptor {

	/**
	 * 在spring 方法运行之前的拦截
	 * 
	 * @param request
	 * @return false不执行spring拦截的方法<br>
	 *         彭仁夔 于 2016年4月17日 下午9:50:48 创建
	 */
	public boolean before(JsonRequest request);

	/**
	 * 在spring 方法运行之后的拦截
	 * 
	 * @param request
	 * @param excuteObj
	 * @return 彭仁夔 于 2016年4月17日 下午9:54:41 创建
	 */
	public Object after(JsonRequest request, Object excuteObj);

}
