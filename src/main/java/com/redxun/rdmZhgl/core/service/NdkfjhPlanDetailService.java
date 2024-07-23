package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.rdmCommon.core.util.RdmConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.*;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class NdkfjhPlanDetailService {
    private static final Logger logger = LoggerFactory.getLogger(NdkfjhPlanDetailService.class);
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    private NdkfjhPlanDetailDao ndkfjhPlanDetailDao;
    @Resource
    private NdkfjhBudgetDao ndkfjhBudgetDao;
    @Resource
    private NdkfjhPlanDao ndkfjhPlanDao;
    @Resource
    private CommonInfoDao commonInfoDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductService productService;
    @Resource
    private ProductConfigDao productConfigDao;
    @Resource
    private PortraitDocumentDao portraitDocumentDao;

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("startDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("endDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("ckMonth".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("stageFinishDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            String id = request.getParameter("id");
            String userId = ContextUtil.getCurrentUserId();
            JSONObject adminJson = commonInfoManager.hasPermission("NDKFJH-JHGLY");
            JSONObject fGzrJson = commonInfoManager.hasPermission("JSZXZR");
            Boolean techLeader = commonInfoManager.judgeUserIsDeptRespor(userId, RdmConst.JSGLB_NAME);
            params = CommonFuns.getSearchParam(params, request, true);
            if (StringUtil.isNotEmpty(id)) {
                params.put("id", id);
            }
            if (adminJson.getBoolean("NDKFJH-JHGLY") || adminJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || fGzrJson.getBoolean("JSZXZR") || techLeader
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            } else {
                params.put("userId", userId);
            }
            list = ndkfjhPlanDetailDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            if (StringUtil.isNotEmpty(id)) {
                params.put("id", id);
            }
            if (adminJson.getBoolean("NDKFJH-JHGLY") || adminJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || fGzrJson.getBoolean("JSZXZR") || techLeader
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            } else {
                params.put("userId", userId);
            }
            totalList = ndkfjhPlanDetailDao.query(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("startDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd HH:mm");
                    }
                }
                if (mapKey.equals("endDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd HH:mm");
                    }
                }
                if (mapKey.equals("stageFinishDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd HH:mm");
                    }
                }
                if (mapKey.equals("yearMonth")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            String chargerMan = CommonFuns.nullToString(objBody.get("chargerMan"));
            if (StringUtil.isNotEmpty(chargerMan)) {
                String deptId = commonInfoDao.getDeptIdByUserId(chargerMan);
                objBody.put("chargerDept", deptId);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            ndkfjhPlanDetailDao.updateObject(objBody);
            resultJson.put("id", objBody.get("id"));
            // 判断历史表中是否存在，存在则更新，不存在则添加
            String yearMonth = CommonFuns.genYearMonth("cur");
            JSONObject paramJson = new JSONObject();
            paramJson.put("yearMonth", yearMonth);
            paramJson.put("detailId", objBody.get("id"));
            JSONObject processHistoryObj = ndkfjhPlanDetailDao.getProcessHistory(paramJson);
            String detailId = objBody.get("id").toString();
            if (processHistoryObj != null) {
                objBody.put("id", processHistoryObj.get("id"));
                objBody.put("processDate", yearMonth);
                objBody.put("detailId", processHistoryObj.get("detailId"));
                ndkfjhPlanDetailDao.updateProcessHistory(objBody);
            } else {
                objBody.put("id", IdUtil.getId());
                objBody.put("processDate", yearMonth);
                objBody.put("detailId", detailId);
                ndkfjhPlanDetailDao.addProcessHistory(objBody);
            }

        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            ndkfjhPlanDetailDao.batchDelete(params);
            for (String id : idList) {
                ndkfjhPlanDetailDao.delHistoryById(id);
            }
        } catch (Exception e) {
            logger.error("Exception in update 删除信息异常", e);
            return ResultUtil.result(false, "删除信息异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public List<JSONObject> getProcessList(HttpServletRequest request) {
        List<JSONObject> processList = new ArrayList<>();
        try {
            JSONObject params = new JSONObject();
            String yearMonth = request.getParameter("yearMonth");
            params.put("yearMonth", yearMonth);
            processList = ndkfjhPlanDetailDao.getProcessList(params);
        } catch (Exception e) {
            logger.error("Exception in getProcessList", e);
        }
        return processList;
    }

    public JSONObject asyncBudgetPlan(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String reportYear = request.getParameter("reportYear");
            List<JSONObject> budgetList = ndkfjhBudgetDao.getBudgetListByYear(reportYear);
            for (JSONObject budgetObj : budgetList) {
                String planId = IdUtil.getId();
                savePlan(budgetObj, planId);
                savePlanDetail(budgetObj, planId);
            }
        } catch (Exception e) {
            logger.error("Exception in asyncBudgetPlan", e);
            return ResultUtil.result(false, "同步信息异常！", "");
        }
        return ResultUtil.result(true, "同步成功", resultJson);
    }

    public void savePlan(JSONObject budgetObj, String planId) {
        try {
            JSONObject obj = ndkfjhPlanDao.getObjectByBudgetId(budgetObj.getString("id"));
            if (obj == null) {
                JSONObject params = new JSONObject();
                params.put("id", planId);
                params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                params.put("planCode", budgetObj.getString("projectCode"));
                params.put("planYear", budgetObj.getString("budgetYear"));
                params.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
                params.put("budgetId", budgetObj.getString("id"));
                ndkfjhPlanDao.addObject(params);
            }
        } catch (Exception e) {
            logger.error("Exception in savePlan", e);
        }
    }

    public void savePlanDetail(JSONObject budgetObj, String planId) {
        try {
            String budgetId = budgetObj.getString("id");
            List<JSONObject> budgetDetailList = ndkfjhBudgetDao.getBudgetDetailList(budgetId);
            for (JSONObject detailObj : budgetDetailList) {
                JSONObject obj = ndkfjhPlanDetailDao.getObjectByDetailId(detailObj.getString("id"));
                if (obj == null) {
                    JSONObject params = new JSONObject();
                    params.put("id", IdUtil.getId());
                    params.put("mainId", planId);
                    params.put("productName", detailObj.getString("products"));
                    params.put("detailId", detailObj.getString("id"));
                    params.put("chargerMan", detailObj.getString("charge"));
                    String chargerMan = CommonFuns.nullToString(detailObj.get("chargerMan"));
                    if (detailObj.get("deptId") != null) {
                        params.put("chargerDept", detailObj.get("deptId"));
                    } else {
                        if (StringUtil.isNotEmpty(chargerMan)) {
                            String deptId = commonInfoDao.getDeptIdByUserId(chargerMan);
                            params.put("chargerDept", deptId);
                        }
                    }
                    params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    ndkfjhPlanDetailDao.addObject(params);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in savePlanDetail", e);
        }
    }

    public JSONObject getObjectById(String id) {
        JSONObject jsonObject = ndkfjhPlanDetailDao.getObjectById(id);
        return jsonObject;
    }

    public JsonPageResult<?> getSpecialOrderList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = ndkfjhPlanDetailDao.getSpecialOrderList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = ndkfjhPlanDetailDao.getSpecialOrderList(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询特殊订单异常", e);
        }
        return result;
    }

    public JsonPageResult<?> getNewProductList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = ndkfjhPlanDetailDao.getNewProductList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = ndkfjhPlanDetailDao.getNewProductList(params);
            dealNewProductInfo(list);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询新品试制订单异常", e);
        }
        return result;
    }

    public JsonPageResult<?> getProjectList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            Map<String, String> deptMap = commonInfoManager.queryDeptUnderJSZX();
            List<String> idList = new ArrayList<>(deptMap.keySet());
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("deptIds", idList);
            list = ndkfjhPlanDetailDao.getProjectList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("deptIds", idList);
            totalList = ndkfjhPlanDetailDao.getProjectList(params);
            dealProjectInfo(list);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询科技项目异常", e);
        }
        return result;
    }

    public void dealProjectInfo(List<Map<String, Object>> list) {
        try {
            for (Map<String, Object> params : list) {
                Date planEndTime = (Date)params.get("stageFinishDate");
                long currentTime = DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();
                if (planEndTime != null) {
                    if (currentTime > planEndTime.getTime()) {
                        params.put("isDelay", "1");
                        params.put("delayDays", (currentTime - planEndTime.getTime()) / (3600 * 1000 * 24));
                    } else {
                        params.put("isDelay", "1");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dealNewProductInfo(List<Map<String, Object>> list) {
        try {
            for (Map<String, Object> params : list) {
                String mainId = CommonFuns.nullToString(params.get("id"));
                JSONObject obj = productDao.getPlanDate(mainId);
                List<Date> dateList = new ArrayList<>();
                for (String key : obj.keySet()) {
                    if (key.endsWith("_date")) {
                        if (obj.get(key) != null && !"".equals(obj.getString("key"))) {
                            dateList.add(obj.getDate(key));
                        }
                    }
                }
                params.put("startDate", Collections.min(dateList));
                params.put("endDate", Collections.max(dateList));
                getNewProductStageInfo(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNewProductStageInfo(Map<String, Object> obj) {
        try {
            List<JSONObject> itemList = productConfigDao.getItemList(new JSONObject());
            JSONObject itemObj = new JSONObject();
            for (JSONObject temp : itemList) {
                itemObj.put(temp.getString("itemCode"), temp.getString("itemName"));
            }
            String mainId = CommonFuns.nullToString(obj.get("id"));
            JSONObject json = new JSONObject();
            json.put("mainId", mainId);
            json.put("type", "type");
            List<JSONObject> resultArray = productDao.getItemList(json);
            if (resultArray != null && resultArray.size() == 2) {
                JSONObject planJson = resultArray.get(0);
                JSONObject actJson = resultArray.get(1);
                String field = "";
                String stage = "";
                int sort = 0;
                for (String key : actJson.keySet()) {
                    if (key.endsWith("_date")) {
                        Object finishDate = actJson.get(key);
                        if (finishDate != null) {
                            int sortIndex = productService.getSort(key);
                            if (sortIndex > sort) {
                                sort = sortIndex;
                                field = key;
                            }
                        }
                    }
                }
                if (StringUtil.isNotEmpty(field)) {
                    for (int i = 0; i < itemList.size(); i++) {
                        JSONObject itemJson = itemList.get(i);
                        int index = itemJson.getInteger("sort");
                        if (index == itemList.size()) {
                            stage = itemJson.getString("itemCode");
                            break;
                        }
                        if (index > sort) {
                            String currentField = itemJson.getString("itemCode");
                            if (planJson.get(currentField) != null) {
                                stage = currentField;
                                break;
                            }
                        }
                    }
                } else {
                    // 返回第一个节点
                    // 返回最新计划第一个不等于null的
                    JSONObject planObj = productDao.getProductObjByMainId(mainId);
                    String stageCode = "";
                    int planSort = 26;
                    for (String key : planObj.keySet()) {
                        if (key.endsWith("_date")) {
                            Object planDate = planObj.get(key);
                            if (planDate != null) {
                                int sortIndex = productService.getSort(key);
                                if (sortIndex < planSort) {
                                    planSort = sortIndex;
                                    stageCode = key;
                                }
                            }
                        }
                    }
                    if (StringUtil.isNotEmpty(stageCode)) {
                        stage = stageCode;
                    } else {
                        stage = itemList.get(0).getString("itemCode");
                    }
                }
                if (StringUtil.isNotEmpty(stage)) {
                    obj.put("currentStage", itemObj.getString(stage));
                    Date actDate = planJson.getDate(stage);
                    obj.put("stageFinishDate", actDate);
                    long diff = System.currentTimeMillis() - actDate.getTime();
                    if (diff > 0) {
                        obj.put("isDelay", "1");
                        long days = diff / (1000 * 60 * 60 * 24);
                        obj.put("delayDays", days);
                    } else {
                        obj.put("isDelay", "0");
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject reportResponseFinishRate(HttpServletRequest request) {
        List<JSONObject> totalList = new ArrayList<>();
        try {
            String yearMonth = request.getParameter("yearMonth");
            JSONObject paramJson = new JSONObject();
            paramJson.put("yearMonth", yearMonth);
            totalList = ndkfjhPlanDetailDao.reportResponseFinishRate(paramJson);
            paramJson.put("yearMonth", yearMonth);
            int totalPlanNum = 0;
            int totalFinishNum = 0;
            for (JSONObject obj : totalList) {
                paramJson.put("responsor", obj.get("responsor"));
                int totalNum = obj.getIntValue("totalNum");
                Integer delayNum = ndkfjhPlanDetailDao.getPlanNumByResponse(paramJson);
                int unFinishNum = 0;
                int finishNum = 0;
                if (delayNum != null) {
                    unFinishNum = delayNum;
                }
                finishNum = totalNum - unFinishNum;
                double finishRate = (double)finishNum / totalNum * 100;
                obj.put("unFinishNum", unFinishNum);
                obj.put("finishRate", finishRate);
                totalPlanNum += totalNum;
                totalFinishNum += finishNum;
            }
            if (totalPlanNum != 0) {
                double avgFinishRate = (double)totalFinishNum / totalPlanNum * 100;
                for (JSONObject obj : totalList) {
                    obj.put("avgFinishRate", avgFinishRate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", totalList);
        }
        return ResultUtil.result(true, "获取数据成功", totalList);
    }

    public JSONObject reportDeptFinishRate(HttpServletRequest request) {
        List<JSONObject> totalList = new ArrayList<>();
        try {
            String yearMonth = request.getParameter("yearMonth");
            JSONObject paramJson = new JSONObject();
            paramJson.put("yearMonth", yearMonth);
            totalList = ndkfjhPlanDetailDao.reportDeptFinishRate(paramJson);
            paramJson.put("yearMonth", yearMonth);
            int totalPlanNum = 0;
            int totalFinishNum = 0;
            for (JSONObject obj : totalList) {
                paramJson.put("chargerDept", obj.get("chargerDept"));
                int totalNum = obj.getIntValue("totalNum");
                Integer delayNum = ndkfjhPlanDetailDao.getPlanNumByDept(paramJson);
                int unFinishNum = 0;
                int finishNum = 0;
                if (delayNum != null) {
                    unFinishNum = delayNum;
                }
                finishNum = totalNum - unFinishNum;
                double finishRate = (double)finishNum / totalNum * 100;
                obj.put("unFinishNum", unFinishNum);
                obj.put("finishRate", finishRate);
                totalPlanNum += totalNum;
                totalFinishNum += finishNum;
            }
            if (totalPlanNum != 0) {
                double avgFinishRate = (double)totalFinishNum / totalPlanNum * 100;
                for (JSONObject obj : totalList) {
                    obj.put("avgFinishRate", avgFinishRate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", totalList);
        }
        return ResultUtil.result(true, "获取数据成功", totalList);
    }

    public List<Map<String, Object>> getReportDetailList(HttpServletRequest request) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String barType = request.getParameter("barType");
            String userName = request.getParameter("userName");
            String deptName = request.getParameter("deptName");
            String yearMonth = request.getParameter("yearMonth");
            JSONObject paramJson = new JSONObject();
            if ("unFinish".equals(barType)) {
                paramJson.put("isDelay", "1");
            }
            paramJson.put("userName", userName);
            paramJson.put("deptName", deptName);
            paramJson.put("yearMonth", yearMonth);
            list = ndkfjhPlanDetailDao.getReportDetailList(paramJson);
            convertDate(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
