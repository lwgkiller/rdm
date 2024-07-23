package com.redxun.core.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.redxun.core.util.DateUtil;

/**
 * 
 * <pre>
 * 描述：JSON-LIB的工具类实现
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年11月23日-下午4:31:42
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public class JSONUtil {
	/**
	 * JSON转为Map
	 * 
	 * @param json
	 * @return Map<String,Object>
	 * @exception
	 * @since 1.0.0
	 */
	public static Map<String, Object> json2Map(String json) {
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(json);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Iterator<String> its = jsonObject.keys();
		while (its.hasNext()) {
			String key = its.next();
			Object val = jsonObject.get(key);
			dataMap.put(key, val);
		}
		return dataMap;
	}

	/**
	 * 把Json字符串转成Map返回
	 * 
	 * @param json
	 *            格式如：[{name:'a',value:'xxx',type:'number'},{name:'b',value:'2'}
	 *            ]
	 * @return
	 */
	public static Map<String, Object> jsonArr2Map(String json) {
		
		JSONArray jsonArr = JSONArray.fromObject(json);
		Map<String, Object> vars = new HashMap<String, Object>();
		for (int i = 0; i < jsonArr.size(); i++) {
			JSONObject obj = jsonArr.getJSONObject(i);
			String name = obj.getString("name");
			String value = obj.getString("value");
			String type = obj.getString("type");
			Object val = null;

			if ("null".equals(value)) {
				continue;
			}

			if (type.equals("Number")) {
				val = new Double(value);
			} else if (type.equals("Date")) {
				val = DateUtil.parseDate(value);
			} else {
				val = value;
			}
			vars.put(name, val);
		}
		return vars;
	}

	/**
	 * Json字符串转成JAVA对象
	 * 
	 * @param json
	 * @param beanClass
	 * @return
	 */
	public static Object json2Bean(String json, Class beanClass) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		String[] dateFormats = new String[] { "yyyy-MM-dd HH:mm:ss",
				"yyyy-MM-dd" };
		JSONUtils.getMorpherRegistry().registerMorpher(
				new DateMorpherExt(dateFormats));
		return JSONObject.toBean(jsonObj, beanClass);
	}

	/**
	 * Json字符串转成JAVA对象
	 * 
	 * @param json
	 * @param beanClass
	 * @return
	 */
	public static Object json2Bean(String json, Class beanClass,
			String[] excludeFields) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(excludeFields);
		JSONObject jsonObj = JSONObject.fromObject(json, jsonConfig);
		String[] dateFormats = new String[] { "yyyy-MM-dd HH:mm:ss",
				"yyyy-MM-dd" };

		JSONUtils.getMorpherRegistry().registerMorpher(
				new DateMorpherExt(dateFormats));

		return JSONObject.toBean(jsonObj, beanClass);
	}

	/**
	 * 对象转JSON
	 * 
	 * @param obj
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public static String toJson(Object obj) {
		JSONObject jsonObj = JSONObject.fromObject(obj);
		return jsonObj.toString();
	}

	public static String getString(JSONObject obj, String property) {
		Object item = obj.get(property);
		if (item == null || item instanceof JSONNull) {
			return "";
		} else if ("null".equals(item.toString())) {
			return "";
		} else if ("\"null\"".equals(item.toString())){
			return "";
		}else {
			return item.toString();
		}
	}

	public static String getString(JSONObject obj, String property,
			String defaultStr) {
		Object item = obj.get(property);
		if (item == null || item instanceof JSONNull
				|| "null".equals(item.toString())) {
			return defaultStr;
		} else {
			return item.toString();
		}
	}

	/**
	 * 返回整型
	 * 
	 * @param obj
	 * @param property
	 * @return
	 */
	public static Integer getInt(JSONObject obj, String property) {
		Object item = obj.get(property);
		if (item == null || "null".equals(item.toString())) {
			return 0;
		} else {
			return new Integer(item.toString());
		}
	}

	/**
	 * 
	 * @param obj
	 * @param property
	 * @param defaultVal
	 * @return
	 */
	public static Integer getInt(JSONObject obj, String property, int defaultVal) {
		Object item = obj.get(property);
		if (item == null || "null".equals(item.toString())||"".equals(item.toString())) {
			return defaultVal;
		} else {
			try{
				return new Integer(item.toString());
			}catch(Exception e){
				return defaultVal;
			}
		}
	}

	/**
	 * 把新的json拷到旧的json上，替换原有的json值
	 * 
	 * @param oldJson
	 * @param newJson
	 * @return 返回合并后的Json值
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static String copyJsons(String oldJson, String newJson)
			throws JsonProcessingException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		ObjectNode oldNode = (ObjectNode) objMapper.readTree(oldJson);
		JsonNode newNode = objMapper.readTree(newJson);

		Iterator<String> keys = newNode.fieldNames();

		while (keys.hasNext()) {
			String fieldName = keys.next();
			JsonNode jsonNode = newNode.get(fieldName);
			oldNode.set(fieldName, jsonNode);
		}

		return oldNode.toString();

	}

	/**
	 * JsonNode转化为Map
	 * 
	 * @param node
	 * @return
	 */
	public static Map<String, Object> jsonNode2Map(JsonNode node) {
		Iterator<String> keys = node.fieldNames();
		Map<String, Object> map = new HashMap<String, Object>();
		while (keys.hasNext()) {
			String fieldName = keys.next();
			JsonNode jsonNode = node.get(fieldName);
			if (jsonNode instanceof TextNode) {
				map.put(fieldName, jsonNode.asText());
			} else {
				map.put(fieldName, jsonNode.toString());
			}

		}
		return map;
	}
	
	public static String getJsonString(JsonNode node,String property){
		if(node==null) return "";
		JsonNode pNode=node.get(property);
		return pNode==null?"":pNode.asText();
	}
	
	
	public static Integer getInt(JsonNode node,String property){
		if(node==null) return 0;
		JsonNode pNode=node.get(property);
		if(pNode==null) return 0;
		
		return new Integer(pNode.asText());
	}
	
	

	public static void main(String[] args) throws JsonParseException,
			JsonMappingException, IOException {

		String json = "{\"username\":\"abc\",\"性别\":\"男\",\"company\":\"xxxxxx\"}";
		String jsonV2 = "{\"username\":\"abc\",\"性别\":\"男\",\"company\":\"accc\"}";

		String jsonv = copyJsons(json, jsonV2);
		//System.out.println("v2:" + jsonv);

	}

}
