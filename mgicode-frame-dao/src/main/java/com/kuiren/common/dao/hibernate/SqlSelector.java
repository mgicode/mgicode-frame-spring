package com.kuiren.common.dao.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.jdbc.core.JdbcTemplate;

import com.kuiren.common.util.AppconfigUtil;
import com.kuiren.common.util.ListUtil;
import com.kuiren.common.util.StringUtil;

public class SqlSelector {
	protected final Log logger = LogFactory.getLog(getClass());

	private Session session;
	private JdbcTemplate jdbcTemplate;
	private static Map<String, String> fnNameMap = new HashMap<String, String>();

	public SqlSelector(Session session) {
		this.session = session;
	}

	public SqlSelector setJdbcTemplate(JdbcTemplate jdbcTemplate) {

		this.jdbcTemplate = jdbcTemplate;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <M> List<M> findListBySql(String sql, Class<M> clz, String alias,
			Object parm) {
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + sql);
		}
		List<M> olist = null;
		SQLQuery q = session.createSQLQuery(sql);

		if (StringUtil.isNotNullOrEmpty(alias)) {
			q.addEntity(alias, clz);
		} else {
			q.addEntity(clz);
		}
		if (parm != null) {
			if (parm instanceof List<?>) {
				List<?> parms = (List<?>) parm;
				for (int i = 0; i < parms.size(); i++) {
					q.setParameter(i, parms.get(i));
					if (logger.isDebugEnabled()) {
						logger.debug("第" + i + "个参数：" + parms.get(i));
					}
				}
			} else if (parm instanceof Map<?, ?>) {
				Map<String, ?> parms = (Map<String, ?>) parm;
				for (String key : parms.keySet()) {
					q.setParameter(key, parms.get(key));
					if (logger.isDebugEnabled()) {
						logger.debug(key + "参数：" + parms.get(key));
					}
				}
			}

		}

		// q.setResultTransformer(
		// Transformers.ALIAS_TO_ENTITY_MAP).list();
		olist = (List<M>) q.list();
		return olist;
	}

	public List<Map<String, Object>> findMapsBySql(String sql,
			List<Object> parms) {
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + sql);
		}

		SQLQuery q = session.createSQLQuery(sql);

		if (parms != null) {
			for (int i = 0; i < parms.size(); i++) {
				q.setParameter(i, parms.get(i));
				if (logger.isDebugEnabled()) {
					logger.debug("第" + i + "个参数：" + parms.get(i));
				}
			}
		}

		List<Map<String, Object>> list = q.setResultTransformer(
				Transformers.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}

	/**
	 * 从指定节点的某行开始，找到所有子节点
	 * 
	 * @param tableName
	 *            表名
	 * @param id
	 *            id字段的名称 一般为id ,数据库中的字段
	 * @param pid
	 *            父节点的名称，一般为pid,parent_id，数据库中的字段
	 * @param clz
	 *            转换为类型
	 * @param containSelf
	 *            是否包括自身行（查询的起始的节点值）
	 * @param pidvalue
	 *            查询的起始的节点值
	 * @return 彭仁夔 于 2016年3月24日 上午11:44:12 创建
	 */
	public <T1> List<T1> findNestedEnities(String tableName, String id,
			String pid, Class<T1> clz, boolean containSelf, String pidvalue) {

		String v = AppconfigUtil.val("mgicode.dbType");
		if ("oracle".equals(v)) {
			return findNestedEnitiesForOracle(tableName, id, pid, clz,
					containSelf, pidvalue);

		} else if ("mysql".equals(v)) {
			return findNestedEnitiesForMysql(tableName, id, pid, clz,
					containSelf, pidvalue);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("树形结构数据查询只支持oracle和mysql");
			}
			return null;
		}
	}

	public <T1> List<T1> findNestedEnitiesForOracle(String tableName,
			String id, String pid, Class<T1> clz, boolean containSelf,
			String pidvalue) {
		String sql = "";

		if (StringUtil.isNullOrEmpty(pidvalue)) {
			sql = "select {a.*} from " + tableName + " a ";
		} else if (containSelf == true) {// --包括本行
			// select * from XT_COMMON_TYPE start with id = '' connect by pid=
			// prior id ;
			sql = "select {a.*} from " + tableName + " a start with " + id
					+ " = '" + pidvalue + "' connect by " + pid + " = prior  "
					+ id;
		} else { // --不包括本行
			// select * from XT_COMMON_TYPE start with pid = '' connect by pid=
			// prior id ;
			sql = "select {a.*} from " + tableName + " a start with " + pid
					+ " = '" + pidvalue + "' connect by " + pid + " = prior  "
					+ id;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("\n" + sql);
		}
		List<T1> olist = findListBySql(sql, clz, "a", null);
		return olist;

	}

	public void createFn(String name, String tableName, String id, String pid,
			boolean containSelf, String pidvalue) {
		jdbcTemplate.execute(" DROP FUNCTION IF EXISTS " + name + " ;");
		String str = ""

		+ "  CREATE FUNCTION `" + name + "` (pidval VARCHAR(2000))  "
				+ "   RETURNS VARCHAR(4000)  " + "   BEGIN  "
				+ "   DECLARE sTemp VARCHAR(4000);  "
				+ "   DECLARE sTempChd VARCHAR(4000);  "
				+ "   SET sTemp = '$';  " + "   SET sTempChd = pidval;  "
				+ "   WHILE sTempChd is not NULL DO  "
				+ "   SET sTemp = CONCAT(sTemp,',',sTempChd);  "
				+ "   SELECT group_concat(" + id + ") INTO sTempChd FROM "
				+ tableName + " where  " + "   FIND_IN_SET(" + pid
				+ ",sTempChd)>0;  " + "   END WHILE;  "

				+ "  	 return sTemp;  " + "  	 END;  ";
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + str);
		}

		jdbcTemplate.execute(str);
		// session.createSQLQuery(str).executeUpdate();

	}

	public <T> List<T> findNestedEnitiesForMysql(String tableName, String id,
			String pid, Class<T> clz, boolean containSelf, String pidvalue) {
		String fnname = "fn_" + tableName;
		if (!fnNameMap.containsKey(fnname)) {
			try {
				createFn(fnname, tableName, id, pid, containSelf, pidvalue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String sql = "";
		if (StringUtil.isNullOrEmpty(pidvalue)) {
			sql = "select {a.*} from " + tableName + " a ";
		} else {
			sql = "select {a.*} from " + tableName
					+ " a where FIND_IN_SET(id, " + fnname + "('" + pidvalue
					+ "'))";
		}

		// containSelf 要包括当前id
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + sql);
		}
		List<T> olist = findListBySql(sql, clz, "a", null);
		return olist;

		// --调用方式
		// select queryChildrenAreaInfo(1);
		// select * from t_areainfo where FIND_IN_SET(id,
		// queryChildrenAreaInfo(1));

	}

	public Object queryForObject(String sql, List<Object> params, Class clz) {
		List<Map<String, Object>> list = findMapsBySql(sql,
				(List<Object>) params);
		if (ListUtil.isListEmpty(list)) {
			if (logger.isDebugEnabled()) {
				logger.debug("\n" + sql + ",没有找到记录");
			}
			return null;
		}
		if (list.size() > 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("\n" + sql + "查询出多条记录，只读取第一条!");
			}
		}
		Map<String, Object> map = list.get(0);
		if (map == null) {
			return null;
		}

		if (map.size() > 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("\n" + sql + "查询出多个字段，只读取第一个");
			}
		}
		Object o = null;
		for (String key : map.keySet()) {
			o = map.get(key);
			break;
		}
		if (o == null) {
			return null;
		} else if (clz == Integer.class) {
			return Integer.parseInt(o + "");
		} else if (clz == Float.class) {
			return Float.parseFloat(o + "");
		} else if (clz == String.class) {
			return o + "";
		} else {
			return o;
		}

	}

}
