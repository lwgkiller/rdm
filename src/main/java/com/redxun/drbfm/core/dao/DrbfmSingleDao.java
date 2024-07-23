package com.redxun.drbfm.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface DrbfmSingleDao {
    List<Map<String, Object>> querySingleList(Map<String, Object> param);

    int countSingleList(Map<String, Object> param);

    void createSingleBase(JSONObject messageObj);

    void updateSingleBase(JSONObject messageObj);

    JSONObject querySingleBaseById(String rapId);

    JSONObject querySingleBaseByStructId(String structId);

    // 某个部件中的部门需求
    List<JSONObject> getSingleDeptDemandList(Map<String, Object> param);

    void createDeptDemand(JSONObject messageObj);

    void updateDeptDemand(JSONObject messageObj);

    JSONObject querySingleDeptDemandById(String id);

    void deleteDeptDemand(Map<String, Object> param);

    // 功能分解
    List<JSONObject> getFunctionList(Map<String, Object> param);

    List<JSONObject> getFunctionListByCollectId(Map<String, Object> param);

    List<JSONObject> getFunctionListByJSId(Map<String, Object> param);

    List<JSONObject> getFunctionCollectList(Map<String, Object> param);

    void createFunction(JSONObject messageObj);

    void updateFunction(JSONObject messageObj);

    JSONObject queryFunctionById(String id);

    void deleteFunction(Map<String, Object> param);

    // 要求分解
    List<JSONObject> getRequestList(Map<String, Object> param);
    List<JSONObject> getRequestAndSxmsList(Map<String, Object> param);

    List<JSONObject> getFinalProcessList(Map<String, Object> param);
    List<JSONObject> getRequestListByCollectId(Map<String, Object> param);

    List<JSONObject> getRequestListByJSId(Map<String, Object> param);

    List<JSONObject> getRequestCollectList(Map<String, Object> param);

    void createRequest(JSONObject messageObj);

    void updateRequest(JSONObject messageObj);

    JSONObject queryRequestById(String rapId);

    void deleteRequest(Map<String, Object> param);

    // 指标分解
    List<JSONObject> getQuotaList(Map<String, Object> param);

    List<JSONObject> getQuotaListByJSList(Map<String, Object> param);

    List<JSONObject> getQuotaCollectList(Map<String, Object> param);

    void createQuota(JSONObject messageObj);

    void updateQuota(JSONObject messageObj);

    JSONObject queryQuotaById(String rapId);

    void deleteQuota(Map<String, Object> param);

    List<JSONObject> getQuotaWithEvaluateList(Map<String, Object> param);

    List<JSONObject> getOneQuotaEvaluateList(Map<String, Object> param);

    void updateQuotaCTQ(JSONObject param);

    void createQuotaEvaluate(JSONObject messageObj);

    void updateQuotaEvaluate(JSONObject messageObj);

    void deleteQuotaEvaluate(JSONObject messageObj);

    void updateQuotaStatus(JSONObject param);

    void updateRequestRiskLevelFinal(JSONObject param);

    void deleteNextWork(JSONObject param);

    List<JSONObject> queryNextWork(Map<String, Object> param);

    void createNextWork(JSONObject messageObj);

    void updateNextWork(JSONObject messageObj);

    void updateStandard(JSONObject messageObj);

    List<JSONObject> querySingleByStruct(JSONObject param);

    void deleteSingleBaseInfo(JSONObject param);

    List<JSONObject> getDeptDemandCollectList(Map<String, Object> param);

    int countDeptDemandCollectList(Map<String, Object> param);

    JSONObject getDeptDemandCollectJson(String id);

    void createDemandCollectFlow(JSONObject messageObj);

    void deleteDemandCollectFlow(Map<String, Object> param);

    List<JSONObject> getOneCollectDemandList(Map<String, Object> param);

    List<JSONObject> getFunctionExport(Map<String, Object> param);

    List<JSONObject> getRequestExport(Map<String, Object> param);

    List<JSONObject> getQuotaExport(Map<String, Object> param);

    JSONObject queryTotalStructDetailById(String id);

    void createSingleInterfaceCollectFlow(JSONObject messageObj);

    List<Map<String, Object>> getInterfaceCollectList(Map<String, Object> param);

    int countInterfaceCollectList(Map<String, Object> param);

    // 功能分解
    List<JSONObject> getFunctionInterfaceList(Map<String, Object> param);

    // 要求分解
    List<JSONObject> getRequestInterfaceList(Map<String, Object> param);

    // 指标分解
    List<JSONObject> getQuotaInterfaceList(Map<String, Object> param);

    List<JSONObject> querySingleIdInterface(String id);

    List<JSONObject> queryInstIdIdInterface(String belongCollectFlowId);

    void deleteInterfaceCollect(Map<String, Object> param);

    void deleteInterfaceFunction(Map<String, Object> param);

    void deleteInterfaceRequest(Map<String, Object> param);

    void deleteInterfaceQuota(Map<String, Object> param);

    List<JSONObject> queryStatusBySingleId(String id);

    List<JSONObject> checkSingleInterface(String id);

    List<JSONObject> getYanzhongduPage();

    List<JSONObject> getFashengduPage();

    List<JSONObject> getTanceduPage();

    void insertRiskLevelSOD(JSONObject SODObj);

    void insertRiskLevelSODFinal(JSONObject SODObj);

    JSONObject querySODByRequestId(String id);

    JSONObject querySODRiskLevelFinal(String id);

    List<JSONObject> getFunctionListByIds(Map<String, Object> param);

    List<JSONObject> getRequestListByIds(Map<String, Object> param);

    List<JSONObject> getQuotaListByIds(Map<String, Object> param);

    void insertFunctionArray(Map<String, Object> params);

    void insertRequestArray(Map<String, Object> params);

    void insertQuotaArray(Map<String, Object> params);

    List<JSONObject> queryFunctionForQuotaExport(Map<String, Object> params);

    List<JSONObject> querySingleBaseForQuotaExport(Map<String, Object> params);

    //失效模式表增删改查
    void insertSxms(JSONObject params);

    void insertSxmsArray(Map<String, Object> params);

    void updateSxms(JSONObject params);

    void batchUpdateSxmsFxfx(JSONObject params);

    void deleteSxms(JSONObject params);

    void deleteSxmsBySingleId(JSONObject params);

    void deleteSxmsRelBySingleId(JSONObject params);

    List<JSONObject> querySxmsList(JSONObject params);

    List<JSONObject> querySxmsListBySingleId(JSONObject params);

    List<JSONObject> querySelectSxmsList(JSONObject params);

    List<JSONObject> queryFunctionListByPartId(JSONObject params);

    List<JSONObject> getCopiedSxmsList(JSONObject params);

    List<JSONObject> getUpSxmsRelList(JSONObject params);
//    List<JSONObject> queryRiskList(JSONObject params);

    List<JSONObject> querySingleSxmsList(JSONObject params);

    List<JSONObject> querySingleChangeList(JSONObject params);

    List<JSONObject> queryModelSxmsList(JSONObject params);

    int countModelSxmsList(JSONObject params);

    int countSelectSxmsList(JSONObject params);

    //失效模式关系增删改查

    List<JSONObject> querySxmsRelList(JSONObject params);

    List<JSONObject> querySxmsRelListBySingleId(JSONObject params);

    void insertSxmsRel(JSONObject params);

    void insertSxmsRelArray(Map<String, Object> params);

    void deleteSxmsRel(JSONObject params);

    void deleteSxyyRel(JSONObject params);


    //查看失效关系网
    List<JSONObject> querySxmsNetList(JSONObject params);

    //查看失效风险分析
    List<JSONObject> queryRiskAnalysisList(JSONObject params);

    JSONObject queryFirstRiskAnalysisPc(JSONObject params);

    List<JSONObject> queryFxpgListByPartId(JSONObject params);

    // 失效原因层次
    void createRiskAnalysis(JSONObject messageObj);

    void batchCreateRiskAnalysis(Map<String, Object> params);

    void updateRiskAnalysis(JSONObject messageObj);

    void deleteRiskAnalysis(JSONObject messageObj);

    void deleteRiskAnalysisBySingleId(JSONObject params);

    void createRiskAnalysisSxms(JSONObject messageObj);

    // 失效模式层次
    void updateRiskAnalysisSxms(JSONObject messageObj);

    JSONObject queryRiskAnalysisDetail(JSONObject params);
    JSONObject queryRiskAnalysisDetailRe(JSONObject params);

    List<JSONObject> querySxms2SxyxList(JSONObject params);
    List<JSONObject> querySxms2SjList(JSONObject params);

    String queryParentIdBySingleId(String singleId);
    int countXjbjBySingleId(String singleId);
    void deleteSxyy(JSONObject params);

    List<String> querySxmsIdByRequestIds(JSONObject params);

    void deleteRiskAnalysisBySxms(JSONObject messageObj);

    List<JSONObject> getChangeDimensionList(JSONObject param);


    //变化维度表增删改查
    void insertDimension(JSONObject params);

    void updateDimension(JSONObject params);

    void deleteDimension(JSONObject params);

    void deleteDimensionBySingleId(JSONObject params);

    List<JSONObject> queryDimensionList(JSONObject params);


    List<JSONObject> getDfmeaExport(JSONObject params);


    //经验教训模块
    List<JSONObject> querySingleExpList(JSONObject params);

    void insertExpRel(JSONObject params);

    void deleteExpRel(JSONObject params);

    List<JSONObject> getRealtionChangePointList(Map<String, Object> param);

    int countRequestAndSxmsList(JSONObject params);

    void updateChangeRelSxms(JSONObject param);
}
