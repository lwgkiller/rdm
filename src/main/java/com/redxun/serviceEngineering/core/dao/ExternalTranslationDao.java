package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface ExternalTranslationDao {
    // 表单的新增、更新和删除
    void insertApply(JSONObject param);

    void updateApply(JSONObject param);

    void updateApplyNumber(JSONObject param);

    void deleteApply(JSONObject param);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // 父表的基本信息
    JSONObject queryBaseInfo(JSONObject params);

    int countApplyList(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    // 需求项的增删改查
    List<JSONObject> queryDemandList(JSONObject params);

    void insertDemand(JSONObject param);

    //需求项更新
    void updateDemand(JSONObject params);

    void deleteDemand(JSONObject param);

    //..马辉在回传文件是也调用updateDemand，会造成原始信息丢失，就不做前端处理了，专门写个更新回传文件信息的后端方法
    void updateReturnFile(JSONObject params);

    //..这个模块整体的重用写的混乱不堪，只能采取冗余增量的方式，避免干涉，只更新：文件类型,文件数量,外发字符数,重复率
    void update4Attribute(JSONObject params);

    JSONObject queryDemand(JSONObject params);

    //..新页面，只显示外发翻译文件
    List<JSONObject> externalTranslationFileListQuery(Map<String, Object> params);

    List<JSONObject> externalMultilanguagebuildFileListQuery(Map<String, Object> params);


    //..
    int externalTranslationFileListQueryCount(Map<String, Object> params);
    int externalMultilanguagebuildFileListQueryCount(Map<String, Object> params);
}
