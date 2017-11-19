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
public interface BasicService<T, PK> extends BaseService<T, PK> {

	/**
	 * 与Ajax的页面交互，采用json形式传入当前实体对象
	 * 
	 * @param req
	 *            Json请求的对象
	 * @return
	 */
	String add(JsonRequest req);

	/**
	 * 根据指定的id删除对象，需要页面传入id的查询字符串，如aa.do?id="1"
	 * 
	 * @param req
	 *            Json请求的对象
	 * @return
	 */
	String del(JsonRequest req);

	/**
	 * <pre>
	 * 根据指定的实体的Json对象，修改数据库中已有的数据，
	 * 需要注意的是：如果该对象的某些属性没有传入，其数据库中的数据会置空
	 * </pre>
	 * 
	 * @param req
	 *            当前实体类型的Json对象
	 * @return
	 */
	String modefy(JsonRequest req);

	/**
	 * 根据指定的实体的Json对象，修改数据库中已有的数据， 需要注意的是：如果该对象的某些属性没有传入，其数据库中的数据不会修改
	 * 如果传入的为空字符串，则会置空
	 * 
	 * @param req
	 *            当前实体类型的Json对象
	 * @return
	 */
	String modefyEmit(JsonRequest req);

	/**
	 * 根据传入的id取得实体并返回其json对象
	 * 
	 * @param req
	 *            需要指定id查询参数
	 * @return 实体的json数据
	 */
	String load(JsonRequest req);

	/**
	 * 根据查询的参数获取实体列表
	 * 
	 * @param req
	 *            实体的json对象
	 * @return 数组形式的json字符串
	 */
	String list(JsonRequest req);

	/**
	 * <pre>
	 *  根据查询的参数获取实体分页列表
	 *  分页的参数可以为：pageSize,pagesize,psize和
	 * 		pageNumber,pagenumber,pageNo,pageno,pno
	 * </pre>
	 * 
	 * @param req
	 *            实体的json对象和分页参数
	 * @return
	 */
	String pagelist(JsonRequest req);

	/**
	 * @see BasicService#get
	 * @param req
	 * @return
	 */
	String getById(JsonRequest req);

	/**
	 * <pre>
	 * 根据实体参数的查询条件取得实体并返回，
	 * 如果查询到多个实体，取第一个返回
	 * </pre>
	 * 
	 * @param req
	 *            json的实体对象
	 * @return
	 */
	String fetch(JsonRequest req);

	/**
	 * 与BasicService#load相类似，根据id参数取得实体对象，该对象封装成RetData返回
	 * 
	 * @param req
	 * @return RetData形式的数据@see RetData
	 */
	String get(JsonRequest req);

	/**
	 * 取得当前实体指定属性字段的枚举,
	 * 
	 * @param req
	 *            必须指定fname的查询字符串，指定哪个属性，如state字段的getState
	 * @return 枚举的json，把List<KeyValue>json化，见{@link KeyValue}
	 */
	public String enums(JsonRequest req);

	/**
	 * 取得枚举的map的json 数据，其查询的格式见{@link BasicService#enums }
	 * 
	 * @param req
	 *            见{@link BasicService#enums }
	 * @return 枚举的map json ,如｛1：保存，2：提交，3：运行｝
	 */
	String enumMap(JsonRequest req);

	/**
	 * <pre>
	 *  取得枚举的map的js文件，在页面中定义<script></script>中引用
	 *  如script type="text/javascript"
	 * src='a.eui?s=compInfoService&m=enumMapJs&fname=getCreater'></script>
	 *  接下来就可以通过返回 enum_+fname（上例中为getCreater）来获取该枚举
	 * </pre>
	 * 
	 * @param req
	 * @return js文件
	 */
	String enumMapJs(JsonRequest req);

	/**
	 * 根据，分配的id查询字符串取得对应的实体列表 ，查询格式类似：aa.do?id=1,2,3
	 * 
	 * @param ids
	 *            当前实体必须有id主键，不然出错
	 * @return 列表的json
	 */
	List<T> findByIds(String ids);

	/**
	 * 构建列表中数据层级等级，指定其是否是叶子节点， 如果指定了childName，其采用层级集合的形式的树形数据
	 * 
	 * @author:彭仁夔 于2014年10月22日上午11:10:11创建
	 * @param list
	 *            列表数据
	 * @param lname
	 *            层级等级属性名
	 * @param idname
	 *            id属性名
	 * @param pidName
	 *            pid属性名
	 * @param leafname
	 *            叶子属性名
	 * @param childName
	 *            子集合的属性名
	 * @return
	 */
//	List<T> buildLevelAndLeaf(List<T> list, String lname, String idname,
		//	String pidName, String leafname, String childName);

	/**
	 * 批量更新map中id:value的记录，其更新的属性名由updateProp指定
	 * 
	 * @author:彭仁夔 于2014年10月23日下午11:06:10创建
	 * @param map
	 * @param updateProp
	 * @param idProp
	 */
	void batchUpdate(Map<String, Object> map, String updateProp, String idProp);

	EuiPage euiPageList(JsonRequest req);

	/**
	 * 可以增加记录，也可以编辑记录
	 * 
	 * @author:彭仁夔 于2014年11月3日下午12:52:54创建
	 * @param req
	 * @return
	 */
	String addOrModefy(JsonRequest req);

//	/**
//	 *
//	 * @author:彭仁夔 于2014年11月28日上午11:29:34创建
//	 * @param list1
//	 *            ，如果该list中有hibernate中的延迟加载对象就会出现问题
//	 * @param idname
//	 * @param pidName
//	 * @param pidValue
//	 * @param childrename
//	 * @return
//	 */
	//List<T> buildChildren(List<T> list1, String idname, String pidName,
		//	String pidValue, String childrename);

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
	 * 调整当前实体的树形结构的排序 必须传入fname字段，与前面的common,adjustOrder进行联合使用
	 * 
	 * @author:彭仁夔 于2014年12月23日下午5:11:02创建
	 * @param req
	 * @return
	 */
	String adjustOrder(JsonRequest req);

	/**
	 * 执行Sql查询，查询一条数据，把该数据转换为Map 主要用于只需要几个字段的的Sql查询
	 * 
	 * @see com.kuiren.common.BasicService#queryUniqueToMap(java.lang.String)
	 * @author 彭仁夔于2015年12月28日下午3:58:12创建
	 */
	Map<String, Object> queryUniqueToMap(String sql);

	/**
	 * 将sql中的全部数据查出来，返回值为一个List<Map<String, Object>>
	 * 
	 * @see com.kuiren.common.BasicService#queryUniqueToMap(java.lang.String)
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
