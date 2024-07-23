package com.redxun.core.database.base;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.redxun.core.database.api.IViewOperator;
import com.redxun.core.database.api.model.Column;
import com.redxun.core.database.api.model.Table;
import com.redxun.core.database.model.DefaultColumn;
import com.redxun.core.database.model.DefaultTable;
import org.springframework.jdbc.support.JdbcUtils;


/**
 * 获取视图信息基类。
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 * 
 */
public abstract class BaseViewOperator extends BaseDbType implements  IViewOperator{
	
	/**
	 * 获取数据类型
	 * 
	 * @param type
	 *            数据类型
	 * @return
	 */
	public abstract String getType(String type);

	/**
	 * 根据视图名称获取model。
	 * @param viewName	视图名
	 * @return
	 * @throws SQLException
	 */
	public Table getModelByViewName(String viewName) throws SQLException {


		Statement stmt = null;
		ResultSet rs = null;

		Table table = new DefaultTable();
		table.setTableName(viewName);
		table.setComment(viewName);
		Connection conn=null;
		try {
			conn = jdbcTemplate.getDataSource().getConnection();
			stmt = (Statement) conn.createStatement();
			rs = stmt.executeQuery("select * from " + viewName);
			ResultSetMetaData metadata = rs.getMetaData();
			int count = metadata.getColumnCount();
			// 从第二条记录开始
			for (int i = 1; i <= count; i++) {
				Column column = new DefaultColumn();
				String columnName = metadata.getColumnName(i);
				String typeName = metadata.getColumnTypeName(i);
				String dataType = getType(typeName);
				column.setFieldName(columnName);
				column.setColumnType(dataType);
				column.setComment(columnName);
				table.addColumn(column);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			JdbcUtils.closeConnection(conn);
		}
		return table;
	}

	
	
	protected abstract Map<String, List<Column>> getColumnsByTableName(List<String> tableNames);
	
	protected void setComlumns(List<Table> tableModels){
		List<String> tableNames = new ArrayList<String>();
		// get all table names
		for (Table table : tableModels) {
			tableNames.add(table.getTableName());
		}
		// batch get table columns
		Map<String, List<Column>> tableColumnsMap = getColumnsByTableName(tableNames);
		// extract table columns from paraTypeMap by table name;
		for (Entry<String, List<Column>> entry : tableColumnsMap.entrySet()) {
			// set TableModel's columns
			for (Table table : tableModels) {
				if (table.getTableName().equalsIgnoreCase(entry.getKey())) {
					table.setColumnList(entry.getValue());
				}
			}
		}
	}
	
	protected void convertToMap(Map<String, List<Column>> map,List<Column> columnModels){
		for (Column columnModel : columnModels) {
			String tableName = columnModel.getTableName();
			if (map.containsKey(tableName)) {
				map.get(tableName).add(columnModel);
			} else {
				List<Column> cols = new ArrayList<Column>();
				cols.add(columnModel);
				map.put(tableName, cols);
			}
		}
	}
	
	
	
	

}
