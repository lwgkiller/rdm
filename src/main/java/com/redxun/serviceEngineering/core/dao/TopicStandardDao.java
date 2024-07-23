package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface TopicStandardDao {
    //列表查询
    List<JSONObject> dataListQuery(Map<String, Object> params);


    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);



    //增删改
    void deleteBusiness(String id);

    void insertBusiness(Map<String, Object> objBody);

    void updateBusiness(Map<String, Object> objBody);

    List<JSONObject> getStandardTopicInfo(JSONObject params);

    // 文件的增删改查
    List<JSONObject> queryFileList(JSONObject params);

    void insertFile(JSONObject param);

    void deleteFile(JSONObject param);

    // 列表导出数据
    List<JSONObject> queryExportApplyList(Map<String, Object> params);

    // 确认已贯标状态位
    void confirmComplete(JSONObject param);


}
