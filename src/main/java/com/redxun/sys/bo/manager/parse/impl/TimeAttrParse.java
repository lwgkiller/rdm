package com.redxun.sys.bo.manager.parse.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.database.api.model.Column;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.sys.bo.entity.SysBoAttr;
import com.redxun.sys.bo.manager.parse.AbstractBoAttrParse;

public class TimeAttrParse extends AbstractBoAttrParse {

	@Override
	public String getPluginName() {
		return "mini-time";
	}

	@Override
	protected void parseExt(SysBoAttr field, Element el) {
		field.setDataType(Column.COLUMN_TYPE_VARCHAR);
		field.setLength(10);
		
		
		parseExtJson(field,el);
		
	}
	
	
	private void parseExtJson(SysBoAttr field, Element el){
		String format = el.attr("format");
		String initcurtime=el.attr("initcurtime");
		
		JSONObject json=new JSONObject();
		
		if(StringUtil.isNotEmpty(format) ){
			json.put("format", format);
		}
		
		if(StringUtil.isNotEmpty(initcurtime) ){
			json.put("initcurtime", initcurtime);
		}
		String required = el.attr("required");
		if (StringUtil.isNotEmpty(required) && "true".equals(required)) {
			json.put("required", required);
		}
		field.setExtJson(json.toJSONString());
	}
	
	
	@Override
	public String getDescription() {
		return "时间选择器";
	}
	
	@Override
	public boolean isSingleAttr() {
		return true;
	}
	
	
	@Override
	public JSONObject getInitData(SysBoAttr attr) {
		String initcurtime=attr.getPropByName("initcurtime");
		String format=attr.getPropByName("format");
		if(StringUtil.isEmpty(initcurtime)) return null;
		
		JSONObject jsonObject=new JSONObject();
		
		if(StringUtils.isEmpty(format)){
			format=DateUtil.DATE_FORMAT_TIME;
		}
		
		if ("true".equals(initcurtime)) {
			Date curDate=new Date();
			SimpleDateFormat sdf=new SimpleDateFormat(format);
			String value=sdf.format(curDate);
			
			AttrParseUtil.addKey(jsonObject,value);
		}
		
		return jsonObject;
		
	}


}
