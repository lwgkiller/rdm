package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitAnalysisImproveDao {
    /**
     * 查询
     *
     * @MapKey params<>
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);
    /**
     * 添加
     *
     * @param param
     * */
    void addAnalysisImprove(Map<String, Object> param);
    /**
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsert(List<Map<String, Object>> dataList);

    /**
     * 获取质量问题改进数据列表
     *
     * @return list
     * */
    List<JSONObject> getAnalysisImproveList();

    /**
     *获取个人质量问题改进
     *
     * @param jsonObject
     * @return json
     * */
    List<JSONObject> getPersonAnalysisImproveList(JSONObject jsonObject);

    /**
     * 获取是否提交延期申请
     *
     * @param paramJson
     * @return json
     * */
    JSONObject getDelayApply(JSONObject paramJson);
    /**
     * 删除所有的数据
     * */
    void delAllInfo();
    /**
     * 删除所有的数据
     * @param dayType
     * @return json
     * */
    JSONObject getDay(String dayType);
}
