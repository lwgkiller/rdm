package com.redxun.bpm.view.control;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.FastjsonUtil;
/**
 * 
 * @author mansan
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class MiniOfficeViewHandler implements MiniViewHanlder {
	
	@Override
	public String getPluginName() {
		return "mini-office";
	}

	@Override
	public void parse(Element el, Map<String, Object> params, JSONObject jsonObj) {
		String name=el.attr("name");		
		String uploadmodel=el.attr("uploadmodel");
		String val=FastjsonUtil.getString(jsonObj, name);
		
		if(StringUtils.isNotEmpty(val)) {
			if(val.contains("type")) {
				el.attr("value",val);
				return;
			}
			JSONObject obj = JSONArray.parseArray(val).getJSONObject(0);
			String fileId = obj.getString("fileId");
			String fileName = obj.getString("fileName");
			String type = fileName.substring(fileName.indexOf(".")+1);
			JSONObject json = new JSONObject();
			json.put("type", type);
			json.put("id", fileId);
			el.attr("value", json.toJSONString());
			return;
		}
		
		if("uploadShow".equals(uploadmodel)) {
			Element div = new Element(Tag.valueOf("div"), "");
			div.attr("name", el.attr("name"));
			div.attr("label", el.attr("label"));
		    div.attr("class", "upload-panel rxc");
		    div.attr("plugins", "upload-panel");
		    div.attr("isdown", "false");
		    div.attr("isprint", "false");
		    div.attr("isone","true");
		    div.attr("filetype", "Office");
		    
		    el.replaceWith(div);
		}
	}
		

	@Override
	public void convertToReadOnly(Element el, Map<String, Object> params,
			JSONObject jsonObj) {
		el.attr("readonly", "true");
	}


}
