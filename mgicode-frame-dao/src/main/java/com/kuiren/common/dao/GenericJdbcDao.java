package com.kuiren.common.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.kuiren.common.util.AppconfigUtil;

/**
 * JdbcTemppate数据操作.
 */
public class GenericJdbcDao extends JdbcDaoSupport {
	private Log log = LogFactory.getLog(this.getClass());
	private String dbType = null;
	
	/**
	 * 取得数据库类型.
	 * 
	 * @return
	 */
	private String getDefaultDBType() {
		String dbType = AppconfigUtil.val("mgicode.dbType", "mysql");
		return dbType;

	}

	/**
	 * 数据分页查询.
	 * 
	 * @param queryString
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	private String getPageSQL(String queryString, Integer startIndex,
			Integer pageSize) {
		String dbType = this.getDbType();
		return this.getPageSQL(queryString, dbType, startIndex, pageSize);
	}

	/**
	 * 数据分页查询
	 * 
	 * @param queryString
	 *            :SQL
	 * @param dbType
	 *            :数据库类型
	 * @param startIndex
	 *            ,起始索引
	 * @param pageSize
	 *            ,分页大小
	 * @return
	 */
	private String getPageSQL(String queryString, String dbType,
			Integer startIndex, Integer pageSize) {
		String pageSQL = "";
		if (dbType.equals("mysql")) {
			pageSQL = this.getMySQLPageSQL(queryString, startIndex, pageSize);
		} else if (dbType.equals("oracle")) {
			pageSQL = this.getOraclePageSQL(queryString, startIndex, pageSize);
		}
		return pageSQL;
	}

	/**
	 * 构造MySQL数据分页SQL
	 * 
	 * @param queryString
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	private String getMySQLPageSQL(String queryString, Integer startIndex,
			Integer pageSize) {
		String result = "";
		if (null != startIndex && null != pageSize) {
			result = queryString + " limit " + startIndex + "," + pageSize;
		} else if (null != startIndex && null == pageSize) {
			result = queryString + " limit " + startIndex;
		} else {
			result = queryString;
		}
		return result;
	}

	/**
	 * 构造 Oracle数据分页SQL
	 * 
	 * @param queryString
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	private String getOraclePageSQL(String queryString, Integer startIndex,
			Integer pageSize) {
		if (StringUtils.isEmpty(queryString)) {
			return null;
		}
		String itemSource = queryString.toLowerCase();
		int endIndex = startIndex + pageSize;
		String endSql = "select * from (select rOraclePageSQL.*,ROWNUM as currentRow from ("
				+ queryString
				+ ") rOraclePageSQL where rownum <"
				+ endIndex
				+ ") where currentRow>" + startIndex;
		return endSql;
	}

	/**
	 * 构造数据总数查询 SQL
	 * 
	 * @param queryString
	 * @return
	 */
	private String getCountQuerySQL(String queryString) {
		String sql = "";
		if (StringUtils.isNotEmpty(queryString)) {
			sql = "select count(*) from (" + queryString + ") xCount";
		}
		return sql;
	}

	/**
	 * 多参数数据查询
	 * 
	 * @param queryString
	 * @param startIndex
	 * @param pageSize
	 * @param values
	 * @return
	 */
	public Page getPageBySQL(String queryString, Integer startIndex,
			Integer pageSize, Object... values) {
		return this.getPageBySQL(queryString, null, startIndex, pageSize,
				values);
	}

	/**
	 * 数据查询
	 * 
	 * @param queryString
	 * @param countQueryString
	 * @param startIndex
	 * @param pageSize
	 * @param values
	 * @return
	 */
	public Page getPageBySQL(String queryString, String countQueryString,
			Integer startIndex, Integer pageSize, Object... values) {
		if (StringUtils.isEmpty(countQueryString)) {
			countQueryString = this.getCountQuerySQL(queryString);
		}
		String pageQueryString = queryString;
		if (null != startIndex && null != pageSize) {
			pageQueryString = this
					.getPageSQL(queryString, startIndex, pageSize);
		}
		Integer count = this.getCount(countQueryString, values);
		List items = this.getJdbcTemplate().query(pageQueryString, values,
				new ColumnMapRowMapper());
		Page page = new Page(items, count, pageSize, startIndex);
		return page;
	}

	public Page getPageBySQLWithoutCountResult(String queryString,
			Integer startIndex, Integer pageSize, Object... values) {
		List items = this.getJdbcTemplate().query(queryString, values,
				new ColumnMapRowMapper());
		int count = 0;
		if (CollectionUtils.isNotEmpty(items)) {
			count = items.size();
		}
		Page page = new Page(items, count, pageSize, startIndex);
		return page;
	}

	public Integer getCount(String queryString, Object... values) {
		// Integer count = this.getJdbcTemplate().queryForInt(queryString,
		// values);
		Integer count = this.getJdbcTemplate().queryForObject(queryString,
				values, Integer.class);
		return count;
	}

	public String getDbType() {
		if (StringUtils.isEmpty(dbType)) {
			dbType = this.getDefaultDBType();
		}
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
}