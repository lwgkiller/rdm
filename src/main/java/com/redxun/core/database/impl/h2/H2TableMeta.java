package com.redxun.core.database.impl.h2;

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
import com.redxun.core.database.colmap.H2ColumnMap;
import com.redxun.core.database.model.DefaultTable;
import com.redxun.core.util.BeanUtil;

/**
 * H2 表元数据的实现类
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 * 
 */
public class H2TableMeta extends BaseTableMeta {

	private final String SQL_GET_COLUMNS = "SELECT "
			+ "A.TABLE_NAME, "
			+ "A.COLUMN_NAME, "
			+ "A.IS_NULLABLE, "
			+ "A.TYPE_NAME, "
			+ "A.CHARACTER_OCTET_LENGTH LENGTH, "
			+ "A.NUMERIC_PRECISION PRECISIONS, "
			+ "A.NUMERIC_SCALE SCALE, "
			+ "B.COLUMN_LIST, "
			+ "A.REMARKS "
			+ "FROM "
			+ "INFORMATION_SCHEMA.COLUMNS A  "
			+ "JOIN INFORMATION_SCHEMA.CONSTRAINTS B ON A.TABLE_NAME=B.TABLE_NAME "
			+ "WHERE  " + "A.TABLE_SCHEMA=SCHEMA() "
			+ "AND B.CONSTRAINT_TYPE='PRIMARY KEY' "
			+ "AND UPPER(A.TABLE_NAME)=UPPER('%s') ";

	private final String SQL_GET_COLUMNS_BATCH = "SELECT "
			+ "A.TABLE_NAME, "
			+ "A.COLUMN_NAME, "
			+ "A.IS_NULLABLE, "
			+ "A.TYPE_NAME, "
			+ "A.CHARACTER_OCTET_LENGTH LENGTH, "
			+ "A.NUMERIC_PRECISION PRECISIONS, "
			+ "A.NUMERIC_SCALE SCALE, "
			+ "B.COLUMN_LIST, "
			+ "A.REMARKS "
			+ "FROM "
			+ "INFORMATION_SCHEMA.COLUMNS A  "
			+ "JOIN INFORMATION_SCHEMA.CONSTRAINTS B ON A.TABLE_NAME=B.TABLE_NAME "
			+ "WHERE  " + "A.TABLE_SCHEMA=SCHEMA() "
			+ "AND B.CONSTRAINT_TYPE='PRIMARY KEY' ";


	private final String SQL_GET_ALL_TABLE = "SELECT " + "TABLE_NAME, "
			+ "REMARKS " + "FROM " + "INFORMATION_SCHEMA.TABLES T " + "WHERE "
			+ "T.TABLE_TYPE='TABLE' " + "AND T.TABLE_SCHEMA=SCHEMA() ";

	/**
	 * 获取表对象
	 */
	@Override
	public Table getTableByName(String tableName) {
		Table model = getTable(tableName);
		// 获取列对象
		List<Column> columnList = getColumnsByTableName(tableName);
		model.setColumnList(columnList);
		return model;
	}

	/**
	 * 根据表名获取列
	 * 
	 * @param tableName
	 * @return
	 */
	private List<Column> getColumnsByTableName(String tableName) {
		String sql = String.format(SQL_GET_COLUMNS, tableName.toUpperCase());
		
		// sqlColumns语句的column_key包含了column是否为primary key，并在H2ColumnMap中进行了映射。
		List<Column> list = jdbcTemplate.query(sql, new H2ColumnMap());
			
		for (Column model : list) {
			model.setTableName(tableName);
		}
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
		sql += " AND A.TABLE_NAME IN (" + buf.toString().toUpperCase() 	+ ") ";
		
	
		List<Column> columnModels = jdbcTemplate.query(sql, new H2ColumnMap());
		
		convertToMap(map, columnModels);
		
		return map;
	}

	/**
	 * 根据表名获取tableModel。
	 * 
	 * @param tableName
	 * @return
	 */
	private Table getTable(final String tableName) {

		String sql = SQL_GET_ALL_TABLE + " AND UPPER(TABLE_NAME) = '" + tableName.toUpperCase() + "'";
		Table tableModel = (Table) jdbcTemplate.queryForObject(sql, tableRowMapper);
		if (BeanUtil.isEmpty(tableModel))
			tableModel = new DefaultTable();
		return tableModel;
	}

	@Override
	public Map<String, String> getTablesByName(String tableName) {
		String sql = SQL_GET_ALL_TABLE;
		if (StringUtils.isNotEmpty(tableName))
			sql += " AND UPPER(TABLE_NAME) LIKE '%" + tableName.toUpperCase() + "%'";
		List<Map<String, Object>> list = jdbcTemplate.query(sql, tableMapRowMapper);
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> tmp = list.get(i);
			String name = tmp.get("name").toString();
			String comments = tmp.get("comment").toString();
			map.put(name, comments);
		}
		return map;

	}

	@Override
	public Map<String, String> getTablesByName(List<String> names) {
		StringBuffer sb = new StringBuffer();
		for (String name : names) {
			sb.append("'");
			sb.append(name);
			sb.append("',");
		}
		sb.deleteCharAt(sb.length() - 1);
		String sql = SQL_GET_ALL_TABLE + " AND  UPPER(TABLE_NAME) IN (" + sb.toString().toUpperCase() + ")";

		
		List<Map<String, Object>> list = jdbcTemplate.query(sql, tableMapRowMapper);
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> tmp = list.get(i);
			String name = tmp.get("name").toString();
			String comments = tmp.get("comment").toString();
			map.put(name, comments);
		}
		return map;
	}

	

	RowMapper<Table> tableRowMapper = new RowMapper<Table>() {
		@Override
		public Table mapRow(ResultSet rs, int rowNum) throws SQLException {
			Table model = new DefaultTable();
			String tableName = rs.getString("TABLE_NAME");
			String tableComment = rs.getString("REMARKS");
			model.setTableName(tableName);
			model.setComment(tableComment);
			return model;
		}
	};

	RowMapper<Map<String, Object>> tableMapRowMapper = new RowMapper<Map<String, Object>>() {
		@Override
		public Map<String, Object> mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			Map<String, Object> model = new HashMap<String, Object>();
			String tableName = rs.getString("TABLE_NAME");
			String tableComment = rs.getString("REMARKS");
			model.put("name", tableName);
			model.put("comment", tableComment);
			return model;
		}
	};

	@Override
	public List<Table> getTableModelByName(String tableName) throws Exception {
		String sql = SQL_GET_ALL_TABLE;
		if (StringUtils.isNotEmpty(tableName))
			sql += " AND UPPER(TABLE_NAME) LIKE '%" + tableName.toUpperCase() + "%'";

		RowMapper<Table> rowMapper = new RowMapper<Table>() {
			@Override
			public Table mapRow(ResultSet rs, int row) throws SQLException {
				Table tableModel = new DefaultTable();
				tableModel.setTableName(rs.getString("TABLE_NAME"));
				String comments = rs.getString("REMARKS");
				tableModel.setComment(comments);
				return tableModel;
			}
		};
		List<Table> tableModels = jdbcTemplate.query(sql, rowMapper);
		
		setComlumns(tableModels);

	
		return tableModels;
	}
}
