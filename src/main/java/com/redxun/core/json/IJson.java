/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redxun.core.json;

import java.util.List;
import java.util.Map;

/**
 * JSON的常用操作接口，可由多种不同的JSON框架实现
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public interface IJson {
     /**
     * 把Object转化为Json字符串
     *
     * @param object can be pojo entity,list,map etc.
     */
    public String toJson(Object object);

    /**
     * Json转List
     *
     * @param json json字符串
     * @return list
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> toList(String json);
    
    /**
     * 通过JSON串返回对象列表
     * @param jsonString
     * @param 转化后的对象
     */
    public <T> List<T> toList(String jsonString,Class<T> clazz);
    
    /**
     * Json转Map
     * @param json
     * @return 
     * Map<String,Object>
     * @exception 
     * @since  1.0.0
     */
    public Map<String,Object> toMap(String json);

    /**
     * json转换为java对象
     *
     * @param json 字符串
     * @param clazz 对象的class
     * @return 返回对象
     */
    public <T> T toObject(String json, Class<T> clazz);

    /**
     * 返回分页列表的Json格式
     *
     * @param list
     * @param totalCounts
     * @return
     */
    public String toPageJson(List<?> list, Integer totalCounts);

    /**
     * 返回带有结果的Json格式
     *
     * @param object can be pojo entity,list,map etc.
     * @return {success:true,data:['...']}
     */
    public String toDataJson(Object object);

    /**
     * 返回带有结果的Json格式
     *
     * @param object can be pojo entity,list,map etc.
     * @return {success:true,result:['...']}
     */
    public String toResultJson(Object object);
}
