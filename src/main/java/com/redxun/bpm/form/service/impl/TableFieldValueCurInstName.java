package com.redxun.bpm.form.service.impl;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.form.service.ITableFieldValueHandler;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.bo.manager.DataHolder;
/**
 * 当前机构名称
 * @author think
 *
 */
public class TableFieldValueCurInstName implements ITableFieldValueHandler{
	
	@Override
	public String getMapType() {
		return TYPE_CUR_INST_NAME;
	}

	@Override
	public String getMapTypeName() {
		return "当前机构名称";
	}

	

	

	@Override
	public boolean isParameterize() {
		return true;
	}

	@Override
	public Object getFieldValue(String dataType, String srcPkId,DataHolder dataHolder,
			JSONObject rowData, JSONObject oldRow, String mapValue,Map<String,Object> extParams) {
		return ContextUtil.getTenant().getTenantName();
	}

}
