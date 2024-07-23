package com.redxun.core.database.model;

import com.redxun.core.database.api.model.Column;

public class SqlWhereColumn implements Column{
	
	String fieldName;
	String comment;
	String columnType;
	
	String typeOperate;
	String valueSource;
	String valueDef;

	
	
	public String getTypeOperate() {
		return typeOperate;
	}

	public void setTypeOperate(String typeOperate) {
		this.typeOperate = typeOperate;
	}

	public String getValueSource() {
		return valueSource;
	}

	public void setValueSource(String valueSource) {
		this.valueSource = valueSource;
	}

	public String getValueDef() {
		return valueDef;
	}

	public void setValueDef(String valueDef) {
		this.valueDef = valueDef;
	}

	@Override
	public String getFieldName() {
		// TODO Auto-generated method stub
		return this.fieldName;
	}

	@Override
	public String getComment() {
		// TODO Auto-generated method stub
		return this.comment;
	}

	@Override
	public boolean getIsPk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getIsNull() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getColumnType() {
		// TODO Auto-generated method stub
		return this.columnType;
	}

	@Override
	public int getCharLen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntLen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDecimalLen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDefaultValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFieldName(String name) {
		// TODO Auto-generated method stub
		this.fieldName=name;
	}

	@Override
	public void setColumnType(String columnType) {
		// TODO Auto-generated method stub
		this.columnType=columnType;
	}

	@Override
	public void setComment(String comment) {
		// TODO Auto-generated method stub
		this.comment=comment;
	}

	@Override
	public void setIsNull(boolean isNull) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIsPk(boolean isPk) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharLen(int charLen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIntLen(int intLen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDecimalLen(int decimalLen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultValue(String defaultValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTableName(String tableName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIsRequired() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setIsRequired(int isRequired) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getOriginName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOriginName(String originName) {
		// TODO Auto-generated method stub
		
	}

}
