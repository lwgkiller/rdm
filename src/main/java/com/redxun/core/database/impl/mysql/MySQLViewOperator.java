package com.redxun.core.database.impl.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.redxun.core.database.api.IViewOperator;
import com.redxun.core.database.api.model.Column;
import com.redxun.core.database.api.model.Table;
import com.redxun.core.database.base.BaseViewOperator;
import com.redxun.core.database.colmap.MySQLColumnMap;
import com.redxun.core.database.model.DefaultTable;

/**
 * Mysql 视图操作的实现类。
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 * 
 */
public class MySQLViewOperator extends BaseViewOperator implements
		IViewOperator {

	// private static final String
	private static final String sqlAllView = "SELECT TABLE_NAME FROM information_schema.`TABLES` WHERE TABLE_TYPE LIKE 'VIEW'";

	private static final String SQL_GET_COLUMNS_BATCH = "SELECT"
			+ " TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_OCTET_LENGTH LENGTH,"
			+ " NUMERIC_PRECISION PRECISIONS,NUMERIC_SCALE SCALE,COLUMN_KEY,COLUMN_COMMENT "
			+ " FROM" + " INFORMATION_SCHEMA.COLUMNS "
			+ " WHERE TABLE_SCHEMA=DATABASE() ";


	
	@Override
	public void createOrRep(String viewName, String sql) throws Exception {
		String getSql = "CREATE OR REPLACE VIEW " + viewName + " as (" + sql + ")";
		jdbcTemplate.execute(getSql);
	}

	
	@Override
	public List<String> getViews(String viewName) throws SQLException {
		String sql = "show table status where comment='view'";
		if (StringUtils.isNotEmpty(viewName))
			sql += " AND NAME LIKE '" + viewName + "%'";
		List<String> list = new ArrayList<String>();
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> line : results) {
			list.add(line.get("Name").toString());
		}
		
		return list;
	}

	

	
	@Override
	public List<Table> getViewsByName(String viewName)
			throws Exception {
		String sql = sqlAllView;
		if (StringUtils.isNotEmpty(viewName)) {
			sql += " AND TABLE_NAME LIKE '" + viewName + "%'";
		}

		RowMapper<Table> rowMapper = new RowMapper<Table>() {
			@Override
			public Table mapRow(ResultSet rs, int row) throws SQLException {
				Table table = new DefaultTable();
				table.setTableName(rs.getString("table_name"));
				table.setComment(table.getTableName());
				return table;
			}
		};
		
		
		
		List<Table> tableModels = jdbcTemplate.query(sql, rowMapper);
		
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
		sql += " AND TABLE_NAME IN (" + buf.toString() + ") ";
		
		List<Column> columns = jdbcTemplate.query(sql, new MySQLColumnMap());
		
		convertToMap(map,columns);
	
		return map;
	}

	
	@Override
	public String getType(String type) {
		type = type.toLowerCase();
		if (type.indexOf("number") > -1)
			return Column.COLUMN_TYPE_NUMBER;
		else if (type.indexOf("date") > -1) {
			return Column.COLUMN_TYPE_DATE;
		} else if (type.indexOf("char") > -1) {
			return Column.COLUMN_TYPE_VARCHAR;
		}
		return Column.COLUMN_TYPE_VARCHAR;
	}

}
