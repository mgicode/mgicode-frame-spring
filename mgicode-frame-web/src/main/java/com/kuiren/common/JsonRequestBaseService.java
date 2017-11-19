package com.kuiren.common;

import java.util.List;
import java.util.Map;

import com.kuiren.common.easyui.EuiPage;
import com.kuiren.common.easyui.JsonRequest;
import com.kuiren.common.page.Page;
import com.kuiren.common.service.BaseService;
import com.kuiren.common.vo.KeyValue;

/**
 * 
 * @author 彭仁夔 于2014年10月23日上午10:29:39创建
 * 
 * @param <T>
 * @param <PK>
 */
public interface JsonRequestBaseService<T, PK> extends BaseService<T, PK> {

	
	/**
	 * 根据，分配的id查询字符串取得对应的实体列表 ，查询格式类似：aa.do?id=1,2,3
	 * 
	 * @param ids
	 *            当前实体必须有id主键，不然出错
	 * @return 列表的json
	 */
	List<T> findByIds(String ids);

	
	/**
	 * 批量更新map中id:value的记录，其更新的属性名由updateProp指定
	 * 
	 * @author:彭仁夔 于2014年10月23日下午11:06:10创建
	 * @param map
	 * @param updateProp
	 * @param idProp
	 */
	void batchUpdate(Map<String, Object> map, String updateProp, String idProp);

	
	
	void batchUpdate(Map<String, Object> map, String updateProp, String idProp,
			String type);

	/**
	 * 自动增加或更新当前实体
	 * 
	 * @author:彭仁夔 于2014年12月18日上午11:42:16创建
	 * @param obj
	 */
	void saveOrUpdateEmit(T obj);

	

	/**
	 * 执行Sql查询，查询一条数据，把该数据转换为Map 主要用于只需要几个字段的的Sql查询
	 * 
	 * @see com.kuiren.common.JsonRequestBaseService#queryUniqueToMap(java.lang.String)
	 * @author 彭仁夔于2015年12月28日下午3:58:12创建
	 */
	Map<String, Object> queryUniqueToMap(String sql);

	/**
	 * 将sql中的全部数据查出来，返回值为一个List<Map<String, Object>>
	 * 
	 * @see com.kuiren.common.JsonRequestBaseService#queryUniqueToMap(java.lang.String)
	 * @author 彭仁夔于2015年12月28日下午3:58:12创建
	 */
	List<Map<String, Object>> queryToListMap(String sql);

	/**
	 * 取得查询的总数
	 * 
	 * @author 彭仁夔于2015年12月28日下午4:29:40创建
	 * @param sql

	 * @return
	 * 
	 */
	int getTotal(String sql);

	/**
	 * 将sql中的全部数据查出来，返回值为一个List<Map<String, Object>> ,加上分页
	 * 
	 * @author 彭仁夔于2015年12月28日下午4:37:19创建
	 * @param sql
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 * 
	 */
	List<Map<String, Object>> queryToListMap(String sql, int pageSize,
			int pageNumber);

	/**
	 * sql中的全部数据查出来，返回值为一个Page<Map<String, Object>> ,加上分页
	 * 
	 * @author 彭仁夔于2015年12月28日下午4:46:47创建
	 * @param sql
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 * 
	 */
	Page<Map<String, Object>> queryToPageMap(String sql, int pageSize,
			int pageNumber);

}
