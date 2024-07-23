package com.redxun.core.elastic;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Cat;
import io.searchbox.core.CatResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.MultiGet;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.mapping.GetMapping;


public class JestService {
	
	@Resource
	JestClient jestClient;
	
	/**
	 * 获取索引
	 * @param pre
	 * @return
	 * @throws IOException
	 */
	public CatResult getCatIndexList(String pre) throws IOException{
		Cat.IndicesBuilder builder=new Cat.IndicesBuilder();
		if(StringUtil.isNotEmpty(pre)){
			builder.addIndex(pre +"*");
		}
		Cat cat=builder.build();
		CatResult result= jestClient.execute(cat);
		return result;
	}
	
	/**
	 * 根据查询获取查询查询数据。
	 * <pre>
	 * 根据term查询
	 * {\"query\": { \"term\": {\"real_name\": { \"value\": \"夏蓓\" } }}}
	 * 根据关键字查询
	 * "{\"query\": { \"match\": {\"real_name\": { \"value\": \"夏蓓\" } }}}"
	 * </pre>
	 * @param index
	 * @param query
	 * @return
	 * @throws IOException 
	 */
	public SearchResult searchByParams(String index, String query) throws IOException{
		Search search = new Search.Builder(query)
	            .addIndex(index)
	            .build();
		SearchResult result = jestClient.execute(search);
		return result;
	} 
	
	/**
	 * 根据ID和索引获取数据。
	 * @param index
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public DocumentResult getById(String index,String id) throws IOException{
		Get search=new Get.Builder(index, id).build();
		DocumentResult result = jestClient.execute(search);
		return result;
	}
	
	/**
	 * 获取多个文档。
	 * @param index
	 * @param ids
	 * @return
	 * @throws IOException
	 */
	public JestResult mGetById(String index,Collection<String> ids) throws IOException{
		MultiGet search=new MultiGet.Builder.ById(index, "_doc")
				.addId(ids)
				.build();
		JestResult result=jestClient.execute(search);
		return result;
	}
	
	/**
	 * 根据SQL获取数据。
	 * @param sql
	 * @return
	 * @throws IOException
	 */
	public JestResult searchBySql(String sql) throws IOException{
		String query="{\"query\": \" "+sql+" \"}";
		SearchSql search = new SearchSql.Builder(query)
                .build();
		JestResult result = jestClient.execute(search);
		return result;
	}
	
	/**
	 * 批量添加索引。
	 * @param index
	 * @param map
	 * 	map 键 id, 值 文档
	 * @return
	 * @throws IOException
	 */
	public BulkResult bulk(String index,Map<String,String> map) throws IOException{
		Bulk.Builder bulkBuild = new Bulk.Builder();
		for (Map.Entry<String, String> ent : map.entrySet()) { 
			bulkBuild.addAction(new Index.Builder(ent.getValue()).index(index).type("_doc").id(ent.getKey()).build());
		}
        BulkResult result = jestClient.execute(bulkBuild.build());
        return result;
	}
	
	public BulkResult bulk(String index,List<String> docs) throws IOException{
		Bulk.Builder bulkBuild = new Bulk.Builder();
		for (String doc : docs) { 
			bulkBuild.addAction(new Index.Builder(doc).index(index).type("_doc").build());
		}
        BulkResult result = jestClient.execute(bulkBuild.build());
        return result;
	}
	
	/**
	 * 保存获取更新数据。
	 * @param index		索引名称
	 * @param doc		文档
	 * @param id		文档ID
	 * @return
	 * @throws IOException
	 */
	public DocumentResult save(String index,String doc,String id) throws IOException{
		Index.Builder builder=new Index.Builder(doc).index(index).type("_doc");
        if(StringUtil.isNotEmpty(id)){
        	builder.id(id);
        }
        DocumentResult result = jestClient.execute(builder.build());
		return result;
	}
	
	/**
	 * 保存对象。
	 * @param index
	 * @param doc
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public DocumentResult saveObject(String index,Object doc,String id) throws IOException{
		Index.Builder builder=new Index.Builder(doc).index(index).type("_doc");
        if(StringUtil.isNotEmpty(id)){
        	builder.id(id);
        }
        DocumentResult result = jestClient.execute(builder.build());
		return result;
	} 
	
	/**
	 * 批量操作对象。
	 * @param index
	 * @param map
	 * @return
	 * @throws IOException
	 */
	public BulkResult bulkObject(String index,Map<String,Object> map) throws IOException{
		Bulk.Builder bulkBuild = new Bulk.Builder();
		for (Map.Entry<String, Object> ent : map.entrySet()) { 
			bulkBuild.addAction(new Index.Builder(ent.getValue()).index(index).type("_doc").id(ent.getKey()).build());
		}
        BulkResult result = jestClient.execute(bulkBuild.build());
        return result;
	}
	
	/**
	 * 根据ID删除。
	 * @param index
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public DocumentResult delById(String index,String id) throws IOException{
		  Delete del=new Delete.Builder(id).index(index).build();
	      DocumentResult result = jestClient.execute(del);
	      return result;
	}
	
	/**
	 * 根据索引名称获取索引的Mapping。
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public JsonResult getMapping(String index) throws IOException{
		GetMapping mapping=new GetMapping.Builder().addIndex(index).build();
        JestResult result = jestClient.execute(mapping);
        //设置
        JsonResult jsonResult= convertToAry(result.getJsonString(),index);
        return jsonResult;
	}
	
	/**
	 * {
		  "demo1": {
		    "mappings": {
		      "doc": {
		        "properties": {
		          "name": {
		            "type": "text",
		            "fields": {
		              "keyword": {
		                "type": "keyword",
		                "ignore_above": 256
		              }
		            }
		          }
		        }
		      }
		    }
		  }
		}
	 * @param json
	 * @return
	 */
	private JsonResult  convertToAry(String json,String key){
		JsonResult result=new JsonResult<>();
		
		JSONObject obj=JSONObject.parseObject(json);
		if(obj.containsKey("error")) {
			result.setSuccess(false);
			result.setData(json);
			return result;
		}
		result=new JsonResult(true);
		
		JSONArray ary=getFields( obj,key);
		result.setData(ary);
		
		return result;
	}
	

	
	private static JSONArray getFields(JSONObject obj,String index){
		JSONObject mapping= obj.getJSONObject(index);
		JSONObject doc= mapping.getJSONObject("mappings");
		Set<String> keySet= doc.keySet();
		Iterator<String> it=keySet.iterator();
		String key=it.next();
		JSONObject prop=doc.getJSONObject(key);
		JSONObject propObj=prop.getJSONObject("properties");
		
		JSONArray ary=new JSONArray();
		
		Set<String> propSet=propObj.keySet();
		Iterator<String> propIt=propSet.iterator();
		while(propIt.hasNext()){
			JSONObject field=new JSONObject();
			String propKey=propIt.next();
			
			field.put("name", propKey);
			JSONObject fieldObj=propObj.getJSONObject(propKey);
			field.put("type", fieldObj.getString("type"));
			field.put("keyword", false);
			
			ary.add(field);
			
			if(fieldObj.containsKey("fields") ){
				JSONObject fields=fieldObj.getJSONObject("fields");
				
				Set<String> extKeySet= fields.keySet();
				Iterator<String> extKeyIt=extKeySet.iterator();
				String extKey=extKeyIt.next();
				
				JSONObject extObj= fields.getJSONObject(extKey);
				JSONObject extField=new JSONObject();
				
				extField.put("name", propKey +"." + extKey);
				extField.put("type", extObj.getString("type"));
				extField.put("keyword", true);
				
				ary.add(extField);
			}
			
		}
		
		return ary;
	}
	
	/**
	 * 添加Maping对象。
	 * @param index
	 * @param source 
	 * <pre>
	 * {
			"mappings": {
				"doc": {
					"properties": {
						"comment": {
							"type": "text"
						},
						"name": {
							"type": "keyword"
						},
						"id": {
							"type": "keyword"
						}
					}
				}
			}
		}</pre>
	 * @return
	 * @throws IOException
	 */
	public JestResult createMapping(String index,String source) throws IOException{
		CreateMapping mapping=new CreateMapping.Builder(index,  source).build();
		
        JestResult result = jestClient.execute(mapping);
        
        return result;
	}
	
	/**
	 * 删除mapping 时，实际上是删除索引。
	 * @param index	索引名称
	 * @return
	 * @throws IOException
	 */
	public JestResult removeMapping(String index) throws IOException{
		RemoveMapping  mapping=new RemoveMapping.Builder(index).build();
	    JestResult result = jestClient.execute(mapping);
	    return result;
	}
	
	

}
