package com.redxun.componentTest.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ComponentTestKanbanDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    JSONObject queryDataById(String businessId);

    //..
    List<String> getTestContractNameListByBusinessId(String id);

    //..
    List<String> getTestReportNameListByBusinessId(String id);

    //..
    List<String> getTestStandardIdListByBusinessId(String id);

    //..
    void deleteBusiness(Map<String, Object> params);

    //..
    void insertBusiness(Map<String, Object> params);

    //..
    void updateBusiness(Map<String, Object> params);

    //..
    List<JSONObject> queryTestContractFileList(Map<String, Object> param);

    //..
    List<JSONObject> queryTestReportFileList(Map<String, Object> param);

    //..
    List<JSONObject> queryTestMPTFileList(Map<String, Object> param);

    //..
    void deleteContractFile(Map<String, Object> param);

    //..
    void deleteReportFile(Map<String, Object> param);

    //..
    void deleteMPTFile(Map<String, Object> param);

    //..
    void addContractFileInfos(JSONObject fileInfo);

    //..
    void updateContractFileInfos(JSONObject fileInfo);

    //..
    void addReportFileInfos(JSONObject fileInfo);

    //..
    void updateReportFileInfos(JSONObject fileInfo);

    //..
    void addMPTFileInfos(JSONObject fileInfo);

    //..以下标准相关
    List<JSONObject> queryBindingStandardMsgList(Map<String, Object> params);

    //..
    int countBindingStandardMsgList(Map<String, Object> param);

    //..
    void deleteBindingStandardMsgItem(Map<String, Object> param);

    //..
    void deleteBindingStandardMsg(Map<String, Object> param);

    //..
    JSONObject getBindingStandardMsg(String id);

    //..
    List<JSONObject> queryBindingStandardMsgItems(Map<String, Object> params);

    //..
    void createBindingStandardMsg(JSONObject messageObj);

    //..
    void createBindingStandardMsgItem(JSONObject messageObj);

    //..
    void updateBindingStandardMsgItem(JSONObject messageObj);

    //..
    void updateBindingStandardMsg(JSONObject messageObj);

    //..
    List<JSONObject> queryBindingStandardList(Map<String, Object> params);

    //..
    void createBindingStandard(JSONObject messageObj);

    //..以上标准相关
    void deleteStandard(Map<String, Object> param);

    //..
    List<JSONObject> getUserByFullName(String fullName);

    //..
    JSONObject getDeptByUserId(String userid);

    //..
    JSONObject getSeq(JSONObject param);

    //..
    void insertSeq(JSONObject seq);

    //..
    void updateSeq(JSONObject seq);
}
