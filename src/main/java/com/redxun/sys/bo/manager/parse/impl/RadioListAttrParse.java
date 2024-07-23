package com.redxun.sys.bo.manager.parse.impl;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.StringUtil;
import com.redxun.sys.bo.entity.SysBoAttr;
import com.redxun.sys.bo.manager.parse.AbstractBoAttrParse;
import com.redxun.sys.bo.manager.parse.ParseUtil;

import net.sf.json.JSONArray;

public class RadioListAttrParse extends AbstractBoAttrParse {

	@Override
	public String getPluginName() {
		return "mini-radiobuttonlist";
	}

	@Override
	protected void parseExt(SysBoAttr field, Element el) {
		ParseUtil.setStringLen(field, el);
		String defaultVal= el.attr("defaultvalue");
		JSONObject json=new JSONObject();
		if(StringUtil.isNotEmpty(defaultVal)){
			json.put("defaultvalue", defaultVal);
		}
		Attributes attributes=el.attributes();
		String from =attributes.get("from");
		json.put("from", from);
		if("dic".equals(from)){
			String dickey=attributes.get("dickey");
			json.put("dickey",dickey);
		} else if("self".equals(from)){
			String data=attributes.get("data").replaceAll("&quot;", "");
			JSONArray jsonArray=JSONArray.fromObject(data);
			json.put("data", jsonArray);

		} else if("sql".equals(from)||"url".equals(from)){
			String fromText=attributes.get(from);
			String textfield=attributes.get("textfield");
			String valuefield=attributes.get("valuefield");
			json.put(from, fromText);
			json.put("textfield", textfield);
			json.put("valuefield", valuefield);
		} 
		String required=el.attr("required");
		if(StringUtil.isEmpty(required)){
			required="false";
		}
		json.put("required",required);
		field.setExtJson(json.toJSONString());
	}

	@Override
	public JSONObject getInitData(SysBoAttr attr) {
		if(StringUtil.isEmpty(attr.getExtJson())) return null;
		
		JSONObject obj=JSONObject.parseObject(attr.getExtJson());
		
		JSONObject jsonObject=new JSONObject();
		
		AttrParseUtil.addKey(jsonObject, obj.getString("defaultvalue"));
		
		return jsonObject;
		
	}

	@Override
	public boolean isSingleAttr() {
		return false;
	}
	
	@Override
	public String getDescription() {
		return "单选控件";
	}
}
