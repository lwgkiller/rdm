package com.redxun.core.json;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
/**
 * 实现JAVA类型与JSON相互转换
 *
 * @author csx
 *
 *@Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class JacksonImpl extends ObjectMapper implements IJson{
    
    protected Logger logger=LogManager.getLogger(JacksonImpl.class);
    /**
     *
     */
    private static final long serialVersionUID = 1232645849307489985L;

    public JacksonImpl() {
    	 this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 带是否懒加载 的构造方法
     *
     * @param forceLazyLoading 是否懒加载 true为懒加载，否则false
     */
    public JacksonImpl(boolean forceLazyLoading) {
        Hibernate4Module mod = new Hibernate4Module();
        mod.configure(Hibernate4Module.Feature.FORCE_LAZY_LOADING,forceLazyLoading);
        registerModule(mod);
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 带是时间格式化 的构造方法
     *
     * @param dateFormat 格式如 yyyy-MM-dd
     *
     */
    public JacksonImpl(String dateFormat) {
    	 this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        setDateFormat(new SimpleDateFormat(dateFormat));
    }

    /**
     * 构造方法
     *
     * @param forceLazyLoading 是否懒加载 true 为懒加载，否则false
     * @param dateFormat 格式如： yyyy-MM-dd
     */
    public JacksonImpl(boolean forceLazyLoading, String dateFormat) {
        this(forceLazyLoading);
        setDateFormat(new SimpleDateFormat(dateFormat));
    }

    /**
     * 把Object转化为Json字符串
     *
     * @param object can be pojo entity,list,map etc.
     */
    public String toJson(Object object) {
        try {
            return this.writeValueAsString(object);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("解析对象错误");
        }
    }

    /**
     * Json转List
     *
     * @param json json字符串
     * @return list
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> toList(String json) {
        try {
            return this.readValue(json, List.class);
        } catch (Exception e) {
            logger.equals(e.getMessage());
            throw new RuntimeException("解析json错误");
        }
    }
    /**
     * Json转Map
     * @param json
     * @return 
     * Map<String,Object>
     * @exception 
     * @since  1.0.0
     */
    public Map<String,Object> toMap(String json){
    	 try {
             return this.readValue(json,Map.class);
         } catch (Exception e) {
             logger.equals(e.getMessage());
             throw new RuntimeException("解析json错误");
         }
    }
    /**
     * 通过JSON串返回对象列表
     * @param jsonString
     * @param 转化后的对象
     */
    public <T> List<T> toList(String jsonString,Class<T> clazz){
    	try{
    		List<T> list=this.readValue(
    	            jsonString,this.getTypeFactory().constructCollectionType( List.class, clazz));
    		return list;
    	}catch (Exception e) {
            logger.equals(e.getMessage());
            throw new RuntimeException("解析json错误");
        }
    }

    /**
     * json转换为java对象
     *
     * @param json 字符串
     * @param clazz 对象的class
     * @return 返回对象
     */
    public <T> T toObject(String json, Class<T> clazz) {
        try {
            return this.readValue(json, clazz);
        } catch (Exception e) {
           e.printStackTrace();
            throw new RuntimeException("解析json错误");
        }
    }

    /**
     * 返回分页列表的Json格式
     *
     * @param list
     * @param totalCounts
     * @return
     */
    public String toPageJson(List<?> list, Integer totalCounts) {
        StringBuilder sb = new StringBuilder("{success:true,totalCounts:").append(totalCounts).append(",results:");
        sb.append(toJson(list));
        sb.append("}");
        return sb.toString();
    }

    /**
     * 返回带有结果的Json格式
     *
     * @param object can be pojo entity,list,map etc.
     * @return {success:true,data:['...']}
     */
    public String toDataJson(Object object) {
        StringBuilder sb = new StringBuilder("{success:true,data:");
        sb.append(toJson(object));
        sb.append("}");
        return sb.toString();
    }

    /**
     * 返回带有结果的Json格式
     *
     * @param object can be pojo entity,list,map etc.
     * @return {success:true,result:['...']}
     */
    public String toResultJson(Object object) {
        StringBuilder sb = new StringBuilder("{success:true,result:");
        sb.append(toJson(object));
        sb.append("}");
        return sb.toString();
    }
}
