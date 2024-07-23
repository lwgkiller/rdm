package com.redxun.core.database.impl.db2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.redxun.core.database.api.IViewOperator;
import com.redxun.core.database.api.model.Column;
import com.redxun.core.database.api.model.Table;
import com.redxun.core.database.base.BaseViewOperator;
import com.redxun.core.database.colmap.DB2ColumnMap;
import com.redxun.core.database.model.DefaultTable;
import com.redxun.core.util.BeanUtil;

/**
 * DB2 视图操作的实现类。
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 * 
 */
public class DB2ViewOperator extends BaseViewOperator implements IViewOperator {

	private static final String SQL_GET_ALL_VIEW = "SELECT " + "VIEWNAME "
			+ "FROM " + "SYSCAT.VIEWS " + "WHERE  "
			+ "VIEWSCHEMA IN (SELECT CURRENT SQLID FROM SYSIBM.DUAL) ";

	private final String SQL_GET_COLUMNS_BATCH = "SELECT "
			+ "TABNAME TAB_NAME, " + "COLNAME COL_NAME, "
			+ "TYPENAME COL_TYPE, " + "REMARKS COL_COMMENT, "
			+ "NULLS IS_NULLABLE, " + "LENGTH LENGTH, " + "SCALE SCALE, "
			+ "KEYSEQ  " + "FROM  " + "SYSCAT.COLUMNS " + "WHERE  "
			+ "TABSCHEMA IN (SELECT CURRENT SQLID FROM SYSIBM.DUAL) ";

	private static final String SQL_GET_COLUMNS = "SELECT "
			+ "TABNAME TAB_NAME, " + "COLNAME COL_NAME, "
			+ "TYPENAME COL_TYPE, " + "REMARKS COL_COMMENT, "
			+ "NULLS IS_NULLABLE, " + "LENGTH LENGTH, " + "SCALE SCALE, "
			+ "KEYSEQ  " + "FROM  " + "SYSCAT.COLUMNS " + "WHERE  "
			+ "TABSCHEMA IN (SELECT CURRENT SQLID FROM SYSIBM.DUAL) "
			+ "AND UPPER(TABNAME) = UPPER('%s') ";


	@Override
	public void createOrRep(String viewName, String sql) throws Exception {
	}


	@Override
	public List<String> getViews(String viewName) throws Exception {
		String sql = SQL_GET_ALL_VIEW;
		if (StringUtils.isNotEmpty(viewName))
			sql += " and UPPER(view_name) like '" + viewName.toUpperCase()
					+ "%'";
		RowMapper<String> rowMapper = new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("VIEWNAME");
			}
		};

		return jdbcTemplate.query(sql, rowMapper) ;
	}

	
	@Override
	public List<Table> getViewsByName(String viewName)
			throws Exception {
		String sql = SQL_GET_ALL_VIEW;
		if (StringUtils.isNotEmpty(viewName))
			sql += " AND UPPER(VIEWNAME) LIKE '%" + viewName.toUpperCase()+ "%'";

		List<Table> tableModels = jdbcTemplate.query(sql, tableModelRowMapper) ;
		
		setComlumns(tableModels);

		return tableModels;
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
		sql += " AND UPPER(TABNAME) IN (" + buf.toString().toUpperCase()+ ") ";
		
		List<Column> columnModels=jdbcTemplate.query(sql, new DB2ColumnMap());
		
		convertToMap(map,columnModels);
		
		return map;
	}

	
	@Override
	public String getType(String type) {
		String dbtype = type.toLowerCase();
		if (dbtype.endsWith("bigint") || dbtype.endsWith("decimal")
				|| dbtype.endsWith("double") || dbtype.endsWith("integer")
				|| dbtype.endsWith("real") || dbtype.endsWith("smallint")) {
			return Column.COLUMN_TYPE_NUMBER;
		} else if (dbtype.endsWith("blob") || dbtype.endsWith("clob")
				|| dbtype.endsWith("dbclob") || dbtype.endsWith("graphic")
				|| dbtype.endsWith("long vargraphic")
				|| dbtype.endsWith("vargraphic") || dbtype.endsWith("xml")) {
			return Column.COLUMN_TYPE_CLOB;
		} else if (dbtype.endsWith("character")
				|| dbtype.endsWith("long varchar")
				|| dbtype.endsWith("varchar")) {
			return Column.COLUMN_TYPE_VARCHAR;
		} else if (dbtype.endsWith("date") || dbtype.endsWith("time")
				|| dbtype.endsWith("timestamp")) {
			return Column.COLUMN_TYPE_DATE;
		} else {
			return Column.COLUMN_TYPE_VARCHAR;
		}
	}

	RowMapper<Table> tableModelRowMapper = new RowMapper<Table>() {
		@Override
		public Table mapRow(ResultSet rs, int row) throws SQLException {
			Table tableModel = new DefaultTable();
			String tabName = rs.getString("VIEWNAME");
			tableModel.setTableName(tabName);
			tableModel.setComment(tabName);
			return tableModel;
		}
	};

	
	@Override
	public Table getModelByViewName(String viewName) throws SQLException {
		String sql = SQL_GET_ALL_VIEW;
		sql += " AND UPPER(VIEWNAME) = '" + viewName.toUpperCase() + "'";
		
		Table tableModel = null;
		List<Table> tableModels = jdbcTemplate.query(sql, tableModelRowMapper);
		if (BeanUtil.isEmpty(tableModels)) {
			return null;
		} else {
			tableModel = tableModels.get(0);
		}
		// 获取列对象
		List<Column> columnList = getColumnsByTableName(viewName);
		tableModel.setColumnList(columnList);
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
		
		List<Column> list = jdbcTemplate.query(sql,  new DB2ColumnMap());
		return list;
	}

}
