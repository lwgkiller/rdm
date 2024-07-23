package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualDomesticoverseasProgressDao;
import com.redxun.serviceEngineering.core.dao.MaintainabilityDisassemblyPlanDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class DecorationManualDomesticoverseasProgressService {
    private static Logger logger = LoggerFactory.getLogger(DecorationManualDomesticoverseasProgressService.class);
    @Autowired
    private DecorationManualDomesticoverseasProgressDao decorationManualDomesticoverseasProgressDao;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = decorationManualDomesticoverseasProgressDao.dataListQuery(params);
        int businessListCount = decorationManualDomesticoverseasProgressDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "orderNum");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
//                    if ("communicateStartTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
//                    }
//                    if ("communicateEndTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
//                    }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JsonResult deleteData(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        decorationManualDomesticoverseasProgressDao.deleteData(param);
        return result;
    }

    //..
    public void saveBusiness(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            if (StringUtils.isBlank(businessObj.getString("id"))) {
                businessObj.put("id", IdUtil.getId());
                businessObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("CREATE_TIME_", new Date());
                decorationManualDomesticoverseasProgressDao.insertData(businessObj);
            } else {
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                decorationManualDomesticoverseasProgressDao.updateData(businessObj);
            }
        }
    }

    //..计划进度柱状图专用
    public List<JSONObject> kanbanData(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("signYear", postDataJson.getString("signYear"));
        params.put("planType", postDataJson.getString("planType"));
        List<JSONObject> businessList = decorationManualDomesticoverseasProgressDao.dataListQuery(params);
        //不抽离函数了,直接罗代码
        LinkedHashMap<String, JSONObject> linkedHashMap = new LinkedHashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("手册类型", "装修手册");
        jsonObject.put("完成数", 0);
        linkedHashMap.put("decorationManual", jsonObject);
        JSONObject jsonObjectC = (JSONObject) jsonObject.clone();
        jsonObjectC.clear();
        jsonObjectC.put("手册类型", "分解与组装手册");
        jsonObjectC.put("完成数", 0);
        linkedHashMap.put("disassemblyAndAssemblyManual", jsonObjectC);
        jsonObjectC = (JSONObject) jsonObject.clone();
        jsonObjectC.clear();
        jsonObjectC.put("手册类型", "结构功能与保养手册");
        jsonObjectC.put("完成数", 0);
        linkedHashMap.put("structurefunctionAndMaintenanceManual", jsonObjectC);
        jsonObjectC = (JSONObject) jsonObject.clone();
        jsonObjectC.clear();
        jsonObjectC.put("手册类型", "测试与调整手册");
        jsonObjectC.put("完成数", 0);
        linkedHashMap.put("testAndAdjustmentManual", jsonObjectC);
        jsonObjectC = (JSONObject) jsonObject.clone();
        jsonObjectC.clear();
        jsonObjectC.put("手册类型", "故障诊断手册");
        jsonObjectC.put("完成数", 0);
        linkedHashMap.put("troubleshootingManual", jsonObjectC);
        jsonObjectC = (JSONObject) jsonObject.clone();
        jsonObjectC.clear();
        jsonObjectC.put("手册类型", "力矩及工具标准值表");
        jsonObjectC.put("完成数", 0);
        linkedHashMap.put("torqueAndToolStandardValueTable", jsonObjectC);
        jsonObjectC = (JSONObject) jsonObject.clone();
        jsonObjectC.clear();
        jsonObjectC.put("手册类型", "检修标准值表");
        jsonObjectC.put("完成数", 0);
        linkedHashMap.put("maintenanceStandardValueTable", jsonObjectC);
        jsonObjectC = (JSONObject) jsonObject.clone();
        jsonObjectC.clear();
        jsonObjectC.put("手册类型", "发动机手册");
        jsonObjectC.put("完成数", 0);
        linkedHashMap.put("engineManual", jsonObjectC);
        for (JSONObject jsonBusiness : businessList) {
            //不抽离函数了,直接罗代码
            JSONObject jsonResult = linkedHashMap.get("decorationManual");
            int total = jsonResult.getIntValue("完成数");
            if (jsonBusiness.getString("decorationManual").equalsIgnoreCase("100")) {
                jsonResult.put("完成数", ++total);
            }
            linkedHashMap.put("decorationManual", jsonResult);

            jsonResult = linkedHashMap.get("disassemblyAndAssemblyManual");
            total = jsonResult.getIntValue("完成数");
            if (jsonBusiness.getString("disassemblyAndAssemblyManual").equalsIgnoreCase("100")) {
                jsonResult.put("完成数", ++total);
            }
            linkedHashMap.put("disassemblyAndAssemblyManual", jsonResult);

            jsonResult = linkedHashMap.get("structurefunctionAndMaintenanceManual");
            total = jsonResult.getIntValue("完成数");
            if (jsonBusiness.getString("structurefunctionAndMaintenanceManual").equalsIgnoreCase("100")) {
                jsonResult.put("完成数", ++total);
            }
            linkedHashMap.put("structurefunctionAndMaintenanceManual", jsonResult);

            jsonResult = linkedHashMap.get("testAndAdjustmentManual");
            total = jsonResult.getIntValue("完成数");
            if (jsonBusiness.getString("testAndAdjustmentManual").equalsIgnoreCase("100")) {
                jsonResult.put("完成数", ++total);
            }
            linkedHashMap.put("testAndAdjustmentManual", jsonResult);

            jsonResult = linkedHashMap.get("troubleshootingManual");
            total = jsonResult.getIntValue("完成数");
            if (jsonBusiness.getString("troubleshootingManual").equalsIgnoreCase("100")) {
                jsonResult.put("完成数", ++total);
            }
            linkedHashMap.put("troubleshootingManual", jsonResult);

            jsonResult = linkedHashMap.get("torqueAndToolStandardValueTable");
            total = jsonResult.getIntValue("完成数");
            if (jsonBusiness.getString("torqueAndToolStandardValueTable").equalsIgnoreCase("100")) {
                jsonResult.put("完成数", ++total);
            }
            linkedHashMap.put("torqueAndToolStandardValueTable", jsonResult);

            jsonResult = linkedHashMap.get("maintenanceStandardValueTable");
            total = jsonResult.getIntValue("完成数");
            if (jsonBusiness.getString("maintenanceStandardValueTable").equalsIgnoreCase("100")) {
                jsonResult.put("完成数", ++total);
            }
            linkedHashMap.put("maintenanceStandardValueTable", jsonResult);

            jsonResult = linkedHashMap.get("engineManual");
            total = jsonResult.getIntValue("完成数");
            if (jsonBusiness.getString("engineManual").equalsIgnoreCase("100")) {
                jsonResult.put("完成数", ++total);
            }
            linkedHashMap.put("engineManual", jsonResult);
        }
        List<JSONObject> resultList = new ArrayList<>();
        Iterator<String> iterator = linkedHashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            resultList.add(linkedHashMap.get(key));
        }
        return resultList;
    }

    //..机型表专用
    public JsonPageResult<?> modelListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        params.put("signYear", request.getParameter("signYear"));
        params.put("planType", request.getParameter("planType"));
        List<JSONObject> resultList = decorationManualDomesticoverseasProgressDao.dataListQuery(params);
        result.setData(resultList);
        return result;
    }

    //..机型进度柱状图专用
    public List<JSONObject> kanbanData2(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("signYear", postDataJson.getString("signYear"));
        params.put("planType", postDataJson.getString("planType"));
        params.put("materialCode", postDataJson.getString("materialCode"));
        params.put("salesModel", postDataJson.getString("salesModel"));
        params.put("designModel", postDataJson.getString("designModel"));
        List<JSONObject> businessList = decorationManualDomesticoverseasProgressDao.dataListQuery(params);
        List<JSONObject> resultList = new ArrayList<>();
        if (businessList.size() > 0) {
            JSONObject jsonresult = new JSONObject();
//            JSONObject jsonresultC = new JSONObject();
            jsonresult.put("手册类型", "装修手册");
            jsonresult.put("完成率", businessList.get(0).getString("decorationManual"));
            resultList.add(jsonresult);
            jsonresult = (JSONObject)jsonresult.clone();
            jsonresult.put("手册类型", "分解与组装手册");
            jsonresult.put("完成率", businessList.get(0).getString("disassemblyAndAssemblyManual"));
            resultList.add(jsonresult);
            jsonresult = (JSONObject)jsonresult.clone();
            jsonresult.put("手册类型", "结构功能与保养手册");
            jsonresult.put("完成率", businessList.get(0).getString("structurefunctionAndMaintenanceManual"));
            resultList.add(jsonresult);
            jsonresult = (JSONObject)jsonresult.clone();
            jsonresult.put("手册类型", "测试与调整手册");
            jsonresult.put("完成率", businessList.get(0).getString("testAndAdjustmentManual"));
            resultList.add(jsonresult);
            jsonresult = (JSONObject)jsonresult.clone();
            jsonresult.put("手册类型", "故障诊断手册");
            jsonresult.put("完成率", businessList.get(0).getString("troubleshootingManual"));
            resultList.add(jsonresult);
            jsonresult = (JSONObject)jsonresult.clone();
            jsonresult.put("手册类型", "力矩及工具标准值表");
            jsonresult.put("完成率", businessList.get(0).getString("torqueAndToolStandardValueTable"));
            resultList.add(jsonresult);
            jsonresult = (JSONObject)jsonresult.clone();
            jsonresult.put("手册类型", "检修标准值表");
            jsonresult.put("完成率", businessList.get(0).getString("maintenanceStandardValueTable"));
            resultList.add(jsonresult);
            jsonresult = (JSONObject)jsonresult.clone();
            jsonresult.put("手册类型", "发动机手册");
            jsonresult.put("完成率", businessList.get(0).getString("engineManual"));
            resultList.add(jsonresult);
        }
        return resultList;
    }
}
