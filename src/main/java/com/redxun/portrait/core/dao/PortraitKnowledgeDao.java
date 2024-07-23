package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitKnowledgeDao {
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);

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
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     * */
    JSONObject getObjectById(String id);
    /**
     *获取个人知识产权
     *
     * @param jsonObject
     * @return json
     * */
    List<Map<String,Object>> getPersonKnowledgeList(JSONObject jsonObject);

    /**
     * 批量导入知识产权
     *
     * @param dataList
     * */
    void batchInsertKnowledge(List<Map<String, Object>> dataList);
    /**
     * 根据产权号查询是否已经存在
     *
     * @param knowledgeCode
     * @return json
     * */
    JSONArray getObjectByCode(String knowledgeCode);
    /**
     * 获取知识产权数据列表
     *
     * @return list
     * */
    List<JSONObject> getPaperList();

    /**
     * 更新知识产权数据
     *
     * @param id
     * */
    void updatePaperStatus(String id);
    /**
     * 获取软著数据列表
     *
     * @return list
     * */
    List<JSONObject> getSoftList();

    /**
     * 更新软著数据
     *
     * @param id
     * */
    void updateSoftStatus(String id);
    /**
     * 获取授权专利列表
     *
     * @return list
     * */
    List<JSONObject> getAuthorizePatentList();

    /**
     * 更新授权专利数据
     *
     * @param id
     * */
    void updateAuthorizePatent(String id);
    /**
     * 根据原始数据id删除给分。
     *
     * @param orgId
     * */
    void delPatentByOrgId(String orgId);
}
