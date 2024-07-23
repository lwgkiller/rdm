package com.redxun.core.entity;

import java.io.Serializable;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.query.QueryParam;
import com.redxun.sys.core.enums.MiniGridColumnType;

/**
 * 表头元数据
 * @author mansan
 *
 */
public class GridHeader implements Serializable{
	/**
	 * 显示的标签
	 */
	private String fieldLabel;
	/**
	 * 显示的字段名称
	 */
	private String fieldName;
	/**
	 * 字段类型
	 */
	private String fieldType;
	
	/**
	 * 数据库字段类型
	 */
	private int dbFieldType;
	
	/**
	 * 字段的格式化
	 */
	private String format;
	/**
	 * 字段长度
	 */
	private int length;
	
	/**
	 * 小数点长度
	 */
	private int precision;
	/**
	 * 是否为可空
	 */
	private String isNull;
	
	/**
	 * 后端渲染类型
	 */
	private String renderType;
	
	/**
	 * 后端渲染类型
	 */
	private String renderTypeName;
	/**
	 * 渲染配置
	 */
	private String renderConf;
	
	/**
	 * 字段在手机端列表显示方式。
	 */
	private String mobileShowMode="";
	
	
	
	private JSONObject renderConfObj=new JSONObject();
	
	
	public GridHeader() {
		
	}
	
	public GridHeader(String fieldName,String fieldType){
		this.fieldName=fieldName;
		this.fieldType=fieldType;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldType() {
		return fieldType;
	}
	
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getFieldLabel() {
		if(StringUtils.isEmpty(fieldLabel)){
			return fieldName;
		}
		return fieldLabel;
	}

	public int getDbFieldType() {
		return dbFieldType;
	}

	public void setDbFieldType(int dbFieldType) {
		this.dbFieldType = dbFieldType;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public String getIsNull() {
		return isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public String getDataType(){
		if(Types.VARCHAR==dbFieldType || Types.CLOB==dbFieldType){
			return "string";
		}else if(Types.BIGINT==dbFieldType || Types.INTEGER==dbFieldType || Types.DOUBLE==dbFieldType ||Types.FLOAT==dbFieldType || Types.DECIMAL==dbFieldType){
			return "number";
		}else if(Types.DATE==dbFieldType || Types.TIMESTAMP==dbFieldType){
			return "date";
		}else{
			return "string";
		}
	}
	
	/**
	 * 获得查询的比较类型
	 * @return
	 */
	public String getQueryDataType(){
		if(Types.VARCHAR==dbFieldType || Types.CLOB==dbFieldType){
			return QueryParam.FIELD_TYPE_STRING;
		}else if(Types.BIGINT==dbFieldType){
			return QueryParam.FIELD_TYPE_LONG;
		}else if(Types.DATE==dbFieldType || Types.TIMESTAMP==dbFieldType){
			return QueryParam.FIELD_TYPE_DATE;
		}else if(Types.DECIMAL==dbFieldType){
			return QueryParam.FIELD_TYPE_BIGDECIMAL;
		}else if(Types.DOUBLE==dbFieldType ){
			return QueryParam.FIELD_TYPE_DOUBLE;
		}else if(Types.FLOAT==dbFieldType){
			return QueryParam.FIELD_TYPE_FLOAT;
		}else{
			return QueryParam.FIELD_TYPE_STRING;
		}
	}

	public String getRenderType() {
		if(StringUtils.isEmpty(renderType)){
			renderType=MiniGridColumnType.COMMON.name();
		}
		return renderType;
	}

	public void setRenderType(String renderType) {
		this.renderType = renderType;
	}

	public String getRenderConf() {
		return renderConf;
	}

	public void setRenderConf(String renderConf) {
		this.renderConf = renderConf;
	}

	public String getRenderTypeName() {
		return renderTypeName;
	}

	public void setRenderTypeName(String renderTypeName) {
		this.renderTypeName = renderTypeName;
	}

	public JSONObject getRenderConfObj() {
		return renderConfObj;
	}

	public void setRenderConfObj(JSONObject renderConfObj) {
		this.renderConfObj = renderConfObj;
	}

	public String getMobileShowMode() {
		return mobileShowMode;
	}

	public void setMobileShowMode(String mobileShowMode) {
		this.mobileShowMode = mobileShowMode;
	}
}
