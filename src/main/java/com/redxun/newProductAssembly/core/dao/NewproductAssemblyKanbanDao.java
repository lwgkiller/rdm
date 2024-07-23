package com.redxun.newProductAssembly.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface NewproductAssemblyKanbanDao {
    //..
    List<JSONObject> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    JSONObject queryDataById(String businessId);

    //..
    void deleteBusiness(Map<String, Object> params);

    //..
    void deleteExceptions(Map<String, Object> params);

    //..
    JSONObject queryExceptionDetailById(String businessId);

    //..
    JSONObject queryExceptionDetailByRefId(String refId);

    //..
    void deleteExceptionFile(Map<String, Object> param);

    //..
    List<JSONObject> queryExceptionList(String businessId);

    //..
    List<JSONObject> queryExceptionFileList(Map<String, Object> param);

    //..
    void insertBusiness(Map<String, Object> params);

    //..
    void updateBusiness(Map<String, Object> params);

    //..
    void deleteException(Map<String, Object> param);

    //..
    List<JSONObject> queryFileList(Map<String, Object> param);

    //..
    void insertException(Map<String, Object> params);

    //..
    void updateException(Map<String, Object> params);

    //..
    void addFileInfos(JSONObject fileInfo);

    //..p
    List<JSONObject> getUserByFullName(String fullName);

    //..p
    List<JSONObject> getDeptByFullName(String fullName);

    //..以下异常汇总相关
    //..
    List<JSONObject> exceptionSummaryListQuery(Map<String, Object> params);

    //..
    Integer countExceptionSummaryListQuery(Map<String, Object> params);

    //..以下年度计划相关
    //..
    List<JSONObject> annualPlanDepListQuery(Map<String, Object> params);

    //..
    int countAnnualPlanDepListQuery(Map<String, Object> params);

    //..
    void deleteAnnualPlanDep(Map<String, Object> param);

    //..
    void insertAnnualPlanDep(JSONObject dataObj);

    //..
    void updateAnnualPlanDep(JSONObject dataObj);

    //..
    List<JSONObject> annualPlanPrdListQuery(Map<String, Object> params);

    //..
    int countAnnualPlanPrdListQuery(Map<String, Object> params);

    //..
    void deleteAnnualPlanPrd(Map<String, Object> param);

    //..
    void insertAnnualPlanPrd(JSONObject businessObj);

    //..
    void updateAnnualPlanPrd(JSONObject businessObj);
}
