package com.redxun.core.database.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.redxun.core.database.api.IDbMeta;
import com.redxun.core.database.api.ITableMeta;
import com.redxun.core.database.api.IViewOperator;
import com.redxun.core.database.api.model.Table;
import com.redxun.core.database.datasource.DbContextHolder;
import com.redxun.core.database.model.DefaultTable;
import com.redxun.core.database.util.MetaDataUtil;
import com.redxun.core.util.AppBeanUtil;


public class DBMetaImpl implements IDbMeta {
	
	

	@Override
	public List<Table> getObjectsByName(String name) throws Exception{
		ITableMeta iTableMeta=(ITableMeta) AppBeanUtil.getBean("tableMetaFactoryBean");
		
		Map<String,String> tables=iTableMeta.getTablesByName(name);
		IViewOperator iViewOperator=MetaDataUtil.getIViewOperatorAfterSetDT(DbContextHolder.getDbType());
		List<String> views=iViewOperator.getViews(name);
		List<Table> tableList=new ArrayList<Table>();
		
		//处理table
		for (Map.Entry<String, String> table : tables.entrySet()) {  
			Table tmpTable=new DefaultTable();
			tmpTable.setTableName(table.getKey());
			tmpTable.setType(Table.TYPE_TABLE);
			tmpTable.setComment(table.getValue());
			tableList.add(tmpTable);
		} 
		
		//处理视图
		for (String viewName : views) {
			Table tmpTable=new DefaultTable();
			tmpTable.setTableName(viewName);
			tmpTable.setType(Table.TYPE_VIEW);
			tmpTable.setComment(viewName);
			tableList.add(tmpTable);
		}
		return tableList;
	}

	@Override
	public Table getByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table getModelByName(String name) throws Exception {
		ITableMeta iTableMeta=(ITableMeta) AppBeanUtil.getBean("tableMetaFactoryBean");
		Table table=iTableMeta.getTableByName(name);
		if(table!=null) return table;
		IViewOperator iViewOperator=MetaDataUtil.getIViewOperatorAfterSetDT(DbContextHolder.getDbType());
		Table view=iViewOperator.getModelByViewName(name);
		return view;
	}

}
