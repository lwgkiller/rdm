package com.redxun.core.database.impl.db2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.redxun.core.database.api.model.Column;
import com.redxun.core.database.api.model.Table;
import com.redxun.core.database.base.BaseTableMeta;
import com.redxun.core.database.colmap.DB2ColumnMap;
import com.redxun.core.database.model.DefaultTable;

/**
 * DB2 表元数据的实现类
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 * 
 */
public class DB2TableMeta extends BaseTableMeta {

	private final String SQL_GET_COLUMNS = "" + "SELECT "
			+ "TABNAME TAB_NAME, " + "COLNAME COL_NAME, "
			+ "TYPENAME COL_TYPE, " + "REMARKS COL_COMMENT, "
			+ "NULLS IS_NULLABLE, " + "LENGTH LENGTH, " + "SCALE SCALE, "
			+ "KEYSEQ  " + "FROM  " + "SYSCAT.COLUMNS " + "WHERE  "
			+ "TABSCHEMA IN (SELECT CURRENT SQLID FROM SYSIBM.DUAL) "
			+ "AND UPPER(TABNAME) = UPPER('%s') ";

	private final String SQL_GET_COLUMNS_BATCH = "SELECT "
			+ "TABNAME TAB_NAME, " + "COLNAME COL_NAME, "
			+ "TYPENAME COL_TYPE, " + "REMARKS COL_COMMENT, "
			+ "NULLS IS_NULLABLE, " + "LENGTH LENGTH, " + "SCALE SCALE, "
			+ "KEYSEQ  " + "FROM  " + "SYSCAT.COLUMNS " + "WHERE  "
			+ "TABSCHEMA IN (SELECT CURRENT SQLID FROM SYSIBM.DUAL) ";

	private final String SQL_GET_TABLE_COMMENT = "" + "SELECT "
			+ "TABNAME TAB_NAME, " + "REMARKS TAB_COMMENT " + "FROM "
			+ "SYSCAT.TABLES " + "WHERE "
			+ "TABSCHEMA IN (SELECT CURRENT SQLID FROM SYSIBM.DUAL) "
			+ "AND UPPER(TABNAME) =UPPER('%s')";

	private final String SQL_GET_ALL_TABLE_COMMENT = ""
			+ "SELECT "
			+ "TABNAME TAB_NAME, "
			+ "REMARKS TAB_COMMENT "
			+ "FROM "
			+ "SYSCAT.TABLES "
			+ "WHERE "
			+ "TABSCHEMA IN (SELECT CURRENT SQLID FROM SYSIBM.DUAL) "
			+ "AND UPPER(TABSCHEMA) = (SELECT UPPER(CURRENT SCHEMA) FROM SYSIBM.DUAL)";

	@Override
	public Table getTableByName(String tableName) {
		Table model = getTableModel(tableName);
		if (model == null)
			return null;
		// 获取列对象
		List<Column> columnList = getColumnsByTableName(tableName);
		model.setColumnList(columnList);
		return model;

	}

	@Override
	public Map<String, String> getTablesByName(String tableName) {
		String sql = SQL_GET_ALL_TABLE_COMMENT;
		if (StringUtils.isNotEmpty(tableName))
			sql += " AND UPPER(TABNAME) LIKE UPPER('%" + tableName + "%')";
		List<Map<String, String>> list=jdbcTemplate.query(sql, tableMapRowMapper);
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> tmp = (Map<String, String>) list.get(i);
			String name = tmp.get("name");
			String comments = tmp.get("comments");
			map.put(name, comments);
		}
		return map;
	}

	@Override
	public List<Table> getTableModelByName(String tableName)
			throws Exception {
		String sql = SQL_GET_ALL_TABLE_COMMENT;
		if (StringUtils.isNotEmpty(tableName))
			sql += " AND UPPER(TABNAME) LIKE '%" + tableName.toUpperCase() + "%'";
		List<Table> tables = jdbcTemplate.query(sql, tableModelRowMapper);
		
		setComlumns(tables);
		
		return tables;

	}

	@Override
	public Map<String, String> getTablesByName(List<String> tableNames) {
		Map<String, String> map = new HashMap<String, String>();
		String sql = SQL_GET_ALL_TABLE_COMMENT;
		if (tableNames == null || tableNames.size() == 0)	return map;

		StringBuffer buf = new StringBuffer();
		for (String str : tableNames) {
			buf.append("'" + str + "',");
		}
		buf.deleteCharAt(buf.length() - 1);
		sql += " AND UPPER(TABNAME) IN (" + buf.toString().toUpperCase() + ") ";
		
		List<Map<String, String>> list =jdbcTemplate.query(sql, tableMapRowMapper);
		
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> tmp = (Map<String, String>) list.get(i);
			String name = tmp.get("name");
			String comments = tmp.get("comments");
			map.put(name, comments);
		}
		return map;

	}

	/**
	 * 根据表名获取tableModel。
	 * 
	 * @param tableName
	 * @return
	 */
	private Table getTableModel(final String tableName) {
		
		String sql = String.format(SQL_GET_TABLE_COMMENT, tableName);
		Table tableModel =  jdbcTemplate.queryForObject(sql, tableModelRowMapper);
	
		return tableModel;
	}

	/**
	 * 根据表名获取列
	 * 
	 * @param tableName
	 * @return
	 */
	private List<Column> getColumnsByTableName(String tableName) {
		String sql = String.format(SQL_GET_COLUMNS, tableName);
		List<Column> list = jdbcTemplate.query(sql,new DB2ColumnMap());
		return list;
	}

	/**
	 * 根据表名获取列。此方法使用批量查询方式。
	 * 
	 * @param tableName
	 * @return
	 */
	@Override
	protected Map<String, List<Column>> getColumnsByTableName(
			List<String> tableNames) {
		String sql = SQL_GET_COLUMNS_BATCH;
		Map<String, List<Column>> map = new HashMap<String, List<Column>>();
		if (tableNames != null && tableNames.size() == 0)  return map;

		StringBuffer buf = new StringBuffer();
		for (String str : tableNames) {
			buf.append("'" + str + "',");
		}
		buf.deleteCharAt(buf.length() - 1);
		sql += " AND UPPER(TABNAME) IN (" + buf.toString().toUpperCase() + ") ";

		List<Column> columnModels=jdbcTemplate.query(sql,  new DB2ColumnMap());
		
		convertToMap(map, columnModels);
	
		return map;
	}

	RowMapper<Table> tableModelRowMapper = new RowMapper<Table>() {
		@Override
		public Table mapRow(ResultSet rs, int row) throws SQLException {
			Table tableModel = new DefaultTable();
			String tabName = rs.getString("TAB_NAME");
			String tabComment = rs.getString("TAB_COMMENT");
			tableModel.setTableName(tabName);
			tableModel.setComment(tabComment);
			return tableModel;
		}
	};

	RowMapper<Map<String, String>> tableMapRowMapper = new RowMapper<Map<String, String>>() {
		@Override
		public Map<String, String> mapRow(ResultSet rs, int row)
				throws SQLException {
			String tableName = rs.getString("TAB_NAME");
			String comments = rs.getString("TAB_COMMENT");
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", tableName);
			map.put("comments", comments);
			return map;
		}
	};

}
