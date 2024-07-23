package com.redxun.core.json;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

public class FastjsonUtil {
	
	public static void copyProperties(JSONObject dest,JSONObject source){
		Iterator<String> keyIt=source.keySet().iterator();
		while(keyIt.hasNext()){
			String key=keyIt.next();
			dest.put(key,source.get(key));
		}
	}
	
	/**
	 * 返回JSON中属性的整型值。
	 * @param json
	 * @param name
	 * @param defaultVal 缺省值
	 * @return
	 */
	public static Integer getInt(JSONObject json,String name,Integer defaultVal){
		if(json.containsKey(name)){
			return json.getInteger(name);
		}
		return defaultVal;
	}
	
	/**
	 * 返回JSON中属性的整型值。
	 * @param json
	 * @param name
	 * @return
	 */
	public static Integer getInt(JSONObject json,String name){
		return getInt(json,name,0);
	}
	
	/**
	 * 返回字符串数据。
	 * @param json
	 * @param name
	 * @param defaultVa
	 * @return
	 */
	public static String getString(JSONObject json,String name,String defaultVal){
		if(json.containsKey(name)){
			return json.getString(name);
		}
		return defaultVal;
	}
	
	/**
	 * 返回json数据。
	 * @param json
	 * @param name
	 * @return
	 */
	public static String getString(JSONObject json,String name){
		String rtn= getString(json,name,"");
		if(rtn==null) return "";
		return rtn;
	}
	
	/**
	 * 将json转成map数据。
	 * @param jsonData
	 * @return
	 */
	public static Map<String, Object> json2Map(JSONObject jsonData){
		Map<String,Object> map=new HashMap<String, Object>();
		Set<String> keySet=jsonData.keySet();
		for(String key:keySet){
			map.put(key, jsonData.get(key));
		}
		return map;
		
	}
	
	/**
	 * 将json转成map数据。
	 * @param jsonData
	 * @return
	 */
	public static Map<String, Object> json2Map(String preKey,JSONObject jsonData){
		Map<String,Object> map=new HashMap<String, Object>();
		Set<String> keySet=jsonData.keySet();
		for(String key:keySet){
			map.put(preKey+key, jsonData.get(key));
		}
		return map;
	}
	
	
	public static Map<String,Object> mapJson2MapProperties(Map<String,JSONObject> mapJsons){
		Map<String,Object> vars=new HashMap<String, Object>();
		Iterator<String> keyIt=mapJsons.keySet().iterator();
		while(keyIt.hasNext()) {
			String key=keyIt.next();
			JSONObject data=mapJsons.get(key);
			if(StringUtils.isNotEmpty(key)) {
				key=key+"_";
			}
			vars.putAll(json2Map(key,data));
		}
		return vars;
	}
	
	/**
	 * 将json转成map数据。
	 * @param jsonData
	 * @return
	 */
	public static Map<String, Object> jsonArr2Map(JSONArray jsonArr){
		Map<String,Object> map=new HashMap<String, Object>();
		
		for(int i=0;i<jsonArr.size();i++){
			JSONObject jsonData=jsonArr.getJSONObject(i);
			Set<String> keySet=jsonData.keySet();
			for(String key:keySet){
				map.put(key, jsonData.get(key));
			}
		}
		return map;
	}
	

	/**
	 * 将json转成map数据。
	 * @param jsonData
	 * @return
	 */
	public static Map<String, Object> jsonArr2Map(String arrJson){
		if(org.apache.commons.lang.StringUtils.isEmpty(arrJson)){
			arrJson="[]";
		}
		JSONArray jsonArr=JSONArray.parseArray(arrJson);
		return jsonArr2Map(jsonArr);
	}
	
	/**
	 * 格式为JSON
	 * @param obj
	 * @param dateFormat
	 * @return
	 */
	public static String toJSON(Object obj,String dateFormat){
		
		SerializeConfig mapping = new SerializeConfig();
		mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
		mapping.put(java.sql.Date.class, new SimpleDateFormatSerializer(dateFormat));
	    mapping.put(Timestamp.class, new SimpleDateFormatSerializer(dateFormat));
	  
        String text = JSON.toJSONString(obj, mapping);
      
        return text;
	}
	
	/**
	 * 从流程表单中获得默认的一个表单数值
	 * @param json
	 * <pre>
	 * {
			"bos": [{
				"boDefId": "2610000001080003",
				"formKey": "LoanCase",
				"data": {
					"ajh": "CN201804130009",
					"ajm": "3333333333333333",
					"ms": "233333"
				}
			}]
		}
	 * </pre>
	 * @return
	 */
	public static JSONObject getBoJsonByFormData(String json){
		if(StringUtils.isNotEmpty(json)){
			JSONObject data=JSONObject.parseObject(json);
			if(data!=null){
				JSONArray bos=data.getJSONArray("bos");
				if(bos!=null && bos.size()>0){
					return bos.getJSONObject(0);
				}
			}
		}
		return null;
	}
	
	/**
	 * 把对象转成JSON
	 * @param obj
	 * @return
	 */
	public static String toJSON(Object obj){
		
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		
		return toJSON(obj,dateFormat);
	}
}
