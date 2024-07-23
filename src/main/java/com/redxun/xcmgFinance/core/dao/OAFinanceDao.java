package com.redxun.xcmgFinance.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OAFinanceDao {

    void insertOAFinanceBasic(JSONObject param);

    void updateOAFinanceBasic(JSONObject param);

    List<JSONObject> queryOAFinanceFlowList(Map<String, Object> params);

    JSONObject getOAFinanceDetailById(@Param("oaFlowId") String oaFlowId);

    List<Map<String, Object>> queryOAFinanceDetailList(@Param("oaFlowId") String oaFlowId);

    void deleteOAFinanceBasicById(@Param("oaFlowId") String oaFlowId);

    void deleteOAFinanceFormById(@Param("oaFlowId") String oaFlowId);

    void insertOAFinanceForm(JSONObject param);

    void updateOAFinanceDetailList(JSONObject param);

    void updateOAFinanceStatus(JSONObject param);

    List<JSONObject> queryOAFileList(@Param("oaFlowId") String oaFlowId);

    void addFileInfos(JSONObject fileInfo);

    void updateFileInfos(JSONObject fileInfo);

    void deleteFileById(@Param("id") String id);

    List<JSONObject> getOAFinanceDetailByCurrentUserId(Map<String, Object> params);
}
