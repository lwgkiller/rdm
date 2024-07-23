package com.redxun.core.database.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.redxun.core.database.api.model.Column;
import com.redxun.core.database.api.model.Table;

/**
 * 默认表对象。
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 */
public class DefaultTable implements Table {
	// 表名
	private String name = "";
	// 表注释
	private String comment = "";
	
	private String entName="";
	//表类型
	private String type="";
	// 列列表
	private List<Column> columnList = new ArrayList<Column>();
	
	private boolean isMain=false;

	public String getTableName() {
		return name;
	}

	public String getComment() {
		if (StringUtils.isNotEmpty(comment))
			comment = comment.replace("'", "''");
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * 添加列对象。
	 * 
	 * @param model
	 */
	public void addColumn(Column model) {
		this.columnList.add(model);
	}

	public List<Column> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}

	@JSONField(serialize = false)
	public List<Column> getPrimayKey() {
		List<Column> pks = new ArrayList<Column>();
		for (Column column : columnList) {
			if (column.getIsPk())
				pks.add(column);
		}
		return pks;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}

	@Override
	public void setTableName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isMain() {
		return this.isMain;
	}

	@Override
	public void setMain(boolean isMain) {
		this.isMain=isMain;
	}
	
	public static void main(String[] args) {
		Table table=new DefaultTable();
		table.setComment("订单表");
		table.setTableName("T_ORDER");
		
		Column column=new DefaultColumn();
		column.setIsPk(true);
		column.setCharLen(64);
		column.setColumnType(Column.COLUMN_TYPE_VARCHAR);
		column.setFieldName("id");
		column.setComment("主键");
		
		Column column1=new DefaultColumn();
		column1.setColumnType(Column.COLUMN_TYPE_VARCHAR);
		column1.setCharLen(64);
		column1.setFieldName("name");
		column1.setComment("名称");
		
		Column column2=new DefaultColumn();
		column2.setColumnType(Column.COLUMN_TYPE_DATE);
		column2.setFieldName("createTime");
		column2.setComment("创建时间");
		
		
		Column column3=new DefaultColumn();
		column3.setColumnType(Column.COLUMN_TYPE_NUMBER);
		column3.setFieldName("createTime");
		column3.setComment("创建时间");
		column3.setIntLen(10);
		column3.setDecimalLen(2);
		
		table.addColumn(column);
		table.addColumn(column1);
		table.addColumn(column2);
		table.addColumn(column3);
		
		System.out.println(table);
		
		
		
		
	}

	@Override
	public String getEntName() {
		return this.entName;
	}

	@Override
	public void setEntName(String entName) {
		this.entName=entName;
	}

	@Override
	public void setType(String type) {
		this.type=type;
		
	}

	@Override
	public String getType() {
		return this.type;
	}

	
}
