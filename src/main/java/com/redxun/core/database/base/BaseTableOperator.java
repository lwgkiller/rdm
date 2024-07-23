package com.redxun.core.database.base;

import com.redxun.core.database.api.ITableOperator;

/**
 * 操作数据表基类。
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 * 
 */
public abstract class BaseTableOperator extends BaseDbType implements ITableOperator {
	
	
	@Override
	public boolean hasData(String tableName) {
		String sql="select count(*) from " + tableName;
		int rtn= jdbcTemplate.queryForObject(sql, Integer.class);
		return rtn>0;
	}

	protected String replaceLineThrough(String partition){
		return partition.toUpperCase().replaceAll("-", "");
	}

	@Override
	public void createIndex(String name, String table, String colName) {
		String sql="CREATE INDEX idx_"+name+" ON "+table+" ("+colName+")";
		jdbcTemplate.execute(sql);
	}
	
	public void dropColumn(String tableName,String column){
		String sql="ALTER TABLE "+tableName+"   DROP COLUMN	" + column;
		jdbcTemplate.execute(sql);
	}

	

}
