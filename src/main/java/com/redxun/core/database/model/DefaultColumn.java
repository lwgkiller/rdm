package com.redxun.core.database.model;

import org.apache.commons.lang.StringUtils;

import com.redxun.core.database.api.model.Column;

/**
 * 默认列对象。 用于产生数据库列。
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 */
public class DefaultColumn implements Column {
	// 列名
	private String name = "";
	// 列注释
	private String comment = "";
	// 是否主键
	private boolean isPk = false;
	// 是否外键
	private boolean isFk = false;
	// 是否可为空
	private boolean isNull = true;
	// 列类型
	private String columnType;
	// 字符串长度
	private int charLen = 0;
	// 小数位
	private int decimalLen = 0;
	// 整数位长度
	private int intLen = 0;
	// 外键reference table
	private String fkRefTable = "";
	// 外键reference column
	private String fkRefColumn = "";
	// 默认值
	private String defaultValue = "";
	// 列所有的表
	private String tableName = "";
	// 列所有的表
	private int isRequired = 0;
	// Select 'x' as label的label
	private String label;
	// 与ResutlSet的列索引等同。以1开始计数。
	private int index;
	
	private String originName="";
	

	public String getFieldName() {
		return name;
	}

	public void setFieldName(String name) {
		this.name = name;
	}

	public String getComment() {
		if (StringUtils.isNotEmpty(comment))
			comment = comment.replace("'", "''");

		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean getIsPk() {
		return isPk;
	}

	public void setIsPk(boolean isPk) {
		this.isPk = isPk;
	}

	public boolean getIsNull() {
		return isNull;
	}

	public void setIsNull(boolean isNull) {
		this.isNull = isNull;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public int getCharLen() {
		return charLen;
	}

	public void setCharLen(int charLen) {
		this.charLen = charLen;
	}

	public int getDecimalLen() {
		return decimalLen;
	}

	public void setDecimalLen(int decimalLen) {
		this.decimalLen = decimalLen;
	}

	public int getIntLen() {
		return intLen;
	}

	public void setIntLen(int intLen) {
		this.intLen = intLen;
	}

	public boolean getIsFk() {
		return isFk;
	}

	public void setIsFk(boolean isFk) {
		this.isFk = isFk;
	}

	public String getFkRefTable() {
		return fkRefTable;
	}

	public void setFkRefTable(String fkRefTable) {
		this.fkRefTable = fkRefTable;
	}

	public String getFkRefColumn() {
		return fkRefColumn;
	}

	public void setFkRefColumn(String fkRefColumn) {
		this.fkRefColumn = fkRefColumn;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int getIsRequired() {
		return this.isRequired;
	}

	@Override
	public void setIsRequired(int isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public String getOriginName() {
		return this.originName;
	}

	@Override
	public void setOriginName(String name) {
		this.originName=name;
	}
}
