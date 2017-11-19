package com.kuiren.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Transactional;

import com.kuiren.common.easyui.EasyUIServlet;

import com.kuiren.common.easyui.EuiPage;
import com.kuiren.common.easyui.JsonRequest;
import com.kuiren.common.easyui.JsonRequestContext;
import com.kuiren.common.easyui.RetData;
import com.kuiren.common.json.JsonCreator;
//import com.kuiren.common.json.JsonHelper;
//import com.kuiren.common.msg.ContinueMsg;
import com.kuiren.common.page.Page;
import com.kuiren.common.service.BaseServiceImpl;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.EnumUtil;
import com.kuiren.common.util.ListUtil;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.KeyValue;

/**
 * 
 * @author 彭仁夔 于2014年10月23日上午10:29:29创建
 * 
 * @param <T>
 * @param <PK>
 */
public class JsonRequestBaseServiceImpl<T, PK> extends BaseServiceImpl<T, PK>
		implements JsonRequestBaseService<T, PK> {

	//public static JsonHelper jackson = JsonHelper.buildNonDefaultBinder();
	final static String IS_FIRST = "null";
	public static final int PAGE_SIZE = 10;

	// protected DaoHelperDW daoHelperDW = null;

	public String toStr(Collection<?> list) {
		return ListUtil.listToStr(list);
	}

	public String toStr(Object[] list) {
		return ListUtil.listToStr(list);
	}

	public int getInt(String s) {
		if (StringUtil.IsNullOrEmpty(s))
			return 0;
		if (StringUtil.isNum(s)) {

			return Integer.parseInt(s);
		}
		return 0;
	}

	public float getFloat(Float f) {

		if (f == null) {
			return 0f;
		} else
			return f.floatValue();
	}

	public float getFloat(String f) {
		if (StringUtil.IsNullOrEmpty(f))
			return 0f;
		if (StringUtil.isNum(f)) {
			return getFloat(Float.parseFloat(f));
		}
		return 0f;
	}

	@Transactional(readOnly = false)
	@Override
	public void saveOrUpdateEmit(T obj) {
		this.updateEmit(obj, false, false);

	}

	public Page<T> buildSearchPage(JsonRequest req) {

		// 获取每页大小
		int pageSize = req.param("pageSize,pagesize,psize").toInt(PAGE_SIZE);

		// 获取当前页数
		int pageNumber = req.param("pageNumber,pagenumber,pageNo,pageno,pno")
				.toInt(1);

		Page<T> page = new Page<T>(pageSize);

		page.setAutoCount(true);
		page.setPageNo(pageNumber);
		return page;
	}

	/**
	 * 根据逗号分隔的Ids找到所有实体
	 */
	@Override
	public List<T> findByIds(String ids) {
		return findByInStr("id", ids);
	}

	/**
	 * 批量更新map中id:value的记录，其更新的属性名由updateProp指定 该类只用于当前的实体
	 * 
	 * @author:彭仁夔 于2014年10月23日下午11:06:10创建
	 * @param map
	 * @param updateProp
	 * @param idProp
	 */
	@Override
	public void batchUpdate(Map<String, Object> map, String updateProp,
			String idProp) {
		for (Object o : map.keySet()) {
			updateByProperty(updateProp, map.get(o), idProp, String.valueOf(o));
		}
	}

	@Override
	public void batchUpdate(Map<String, Object> map, String updateProp,
			String idProp, String type) {
		for (Object o : map.keySet()) {

			if ("string".equals(type)) {
				updateByProperty(updateProp, String.valueOf(map.get(o)),
						idProp, o);
			} else if ("float".equals(type)) {
				updateByProperty(updateProp,
						Float.parseFloat(String.valueOf(map.get(o))), idProp,
						String.valueOf(o));
			} else {
				updateByProperty(updateProp, map.get(o), idProp, o);
			}

		}
	}

	public String errorMsg(String msg) {

		RetData retMsg = new RetData();
		retMsg.setSuccess(false);
		retMsg.setMsg(msg);
		return  new Gson().toJson(retMsg);// jackson.toJson(retMsg);
	}

	/**
	 * 执行Sql查询，查询一条数据，把该数据转换为Map 主要用于只需要几个字段的的Sql查询 (non-Javadoc)
	 * 
	 * @see com.kuiren.common.BasicService#queryUniqueToMap(java.lang.String)
	 * @author 彭仁夔于2015年12月28日下午3:58:12创建
	 */
	@Override
	public Map<String, Object> queryUniqueToMap(String sql) {
		Query query = getSession().createSQLQuery(sql);
		return (Map) query.setResultTransformer(
				Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult(); // 返回值为map集合
																	// 且为唯一值（只能返回一条数据）

	}

	/**
	 * 将sql中的全部数据查出来，返回值为一个List<Map<String, Object>>
	 * 
	 * @author 彭仁夔于2015年12月28日下午4:00:53创建
	 * @param sql
	 * @return
	 * 
	 */
	@Override
	public List<Map<String, Object>> queryToListMap(String sql) {
		Query query = getSession().createSQLQuery(sql);
		return (List) query.setResultTransformer(
				Transformers.ALIAS_TO_ENTITY_MAP).list();

	}

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
	@Override
	public List<Map<String, Object>> queryToListMap(String sql, int pageSize,
			int pageNumber) {
		Query query = getSession().createSQLQuery(sql);
		// new Object[] {
		// pageNumber * pageSize, (pageNumber - 1) * pageSize
		return (List<Map<String, Object>>) query
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.setFirstResult((pageNumber - 1) * pageSize)
				.setMaxResults(pageSize).list();

	}

	@Override
	public Page<Map<String, Object>> queryToPageMap(String sql, int pageSize,
			int pageNumber) {
		Page<Map<String, Object>> page = new Page<Map<String, Object>>();
		page.setTotalCount(getTotal(sql));
		page.setPageNo(pageNumber);
		page.setPageSize(pageSize);
		Query query = getSession().createSQLQuery(sql);
		List<Map<String, Object>> list = (List<Map<String, Object>>) query
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.setFirstResult((pageNumber - 1) * pageSize)
				.setMaxResults(pageSize).list();
		page.setResult(list);
		return page;
	}

	/**
	 * 取得查询的总数
	 * 
	 * @author 彭仁夔于2015年12月28日下午4:29:40创建
	 * @param sql

	 * @return
	 * 
	 */
	@Override
	public int getTotal(String sql) {
		String total = "SELECT count(1) as total from (" + sql + ") s ";
		return queryForInt(total);
	}




}
