package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface MonthUnProjectPlanDao {


    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getPlanLst(Map<String, Object> params);

    /**
     * 保存
     * @param param
     * @return
     * */
    int addObject(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateObject(Map<String, Object> params);
    /**
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 批量子计划
     *
     * @param params
     * */
    void batchDeleteItems(Map<String, Object> params);
    /**
     * 批量子计划
     *
     * @param params
     * */
    void batchDeleteItemsById(Map<String, Object> params);

    /**
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     * */
    JSONObject getObjectById(String id);

    /**
     * 保存
     * @param param
     * @return
     * */
    int addItem(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateItem(Map<String, Object> params);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delItem(String id);
    /**
     * 查询详情列表
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> getItemList(JSONObject jsonObject);
    /**
     * 批量修改
     *
     * @param params
     * */
    void batchUpdate(Map<String, Object> params);
    /**
     * 根据id获取详情信息
     *
     * @return map
     * @param id
     * */
    Map<String,Object> getObjMap(String id);
    /**
     * 子表数据
     *
     * @param mainId
     * @return list
     * */
    List<Map<String, Object>> getItemMapList(String mainId);
    /**
     * 获取编号
     *
     * @param paramJson
     * @return json
     * */
    JSONObject getProjectCode(JSONObject paramJson);
    /**
     * 更新编号
     *
     * @param paramJson
     * */
    void updateProjectCode(JSONObject paramJson);
    /**
     * 添加编号
     *
     * @param obj
     * */
    void addCode(JSONObject obj);
    /**
     * 获取部门简称
     *
     * @param deptId
     * @return json
     * */
    JSONObject getDeptShortName(String deptId);
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getPersonUnPlanList(Map<String, Object> params);
    /**
     * 查询未完成项目
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getDeptUnFinishList(Map<String, Object> params);

    /**
     * 根据年月和编号获取项目
     *
     * @param paramJson
     * @return json
     * */
    JSONObject getPlanByCodeAndMonth(JSONObject paramJson);
}
