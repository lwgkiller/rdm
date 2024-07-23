package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualDomesticoverseasProgressDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualMassproductionDistributionDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class DecorationManualMassproductionDistributionService {
    private static Logger logger = LoggerFactory.getLogger(DecorationManualMassproductionDistributionService.class);
    @Autowired
    private DecorationManualMassproductionDistributionDao decorationManualMassproductionDistributionDao;
    @Autowired
    private DecorationManualDomesticoverseasProgressDao decorationManualDomesticoverseasProgressDao;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = decorationManualMassproductionDistributionDao.dataListQuery(params);
        int businessListCount = decorationManualMassproductionDistributionDao.countDataListQuery(params);
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
        decorationManualMassproductionDistributionDao.deleteData(param);
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
            JSONObject params = new JSONObject();
            params.put("signYear", businessObj.getString("signYear"));
            params.put("materialCode", businessObj.getString("materialCode"));
            params.put("salesModel", businessObj.getString("salesModel"));
            params.put("designModel", businessObj.getString("designModel"));
            JSONObject domesticoverseasProgress = decorationManualDomesticoverseasProgressDao.selectByMap(params);
            if (domesticoverseasProgress != null) {
                businessObj.put("decorationManual", domesticoverseasProgress.getString("decorationManual"));
                businessObj.put("disassemblyAndAssemblyManual", domesticoverseasProgress.getString("disassemblyAndAssemblyManual"));
                businessObj.put("structurefunctionAndMaintenanceManual", domesticoverseasProgress.getString("structurefunctionAndMaintenanceManual"));
                businessObj.put("testAndAdjustmentManual", domesticoverseasProgress.getString("testAndAdjustmentManual"));
                businessObj.put("troubleshootingManual", domesticoverseasProgress.getString("troubleshootingManual"));
                businessObj.put("torqueAndToolStandardValueTable", domesticoverseasProgress.getString("torqueAndToolStandardValueTable"));
                businessObj.put("maintenanceStandardValueTable", domesticoverseasProgress.getString("maintenanceStandardValueTable"));
                businessObj.put("engineManual", domesticoverseasProgress.getString("engineManual"));
            }
            if (StringUtils.isBlank(businessObj.getString("id"))) {
                businessObj.put("id", IdUtil.getId());
                businessObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("CREATE_TIME_", new Date());
                decorationManualMassproductionDistributionDao.insertData(businessObj);
            } else {
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                decorationManualMassproductionDistributionDao.updateData(businessObj);
            }
        }
    }

    //..
    public void calculateBusiness(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            JSONObject params = new JSONObject();
            params.put("signYear", businessObj.getString("signYear"));
            params.put("planType", businessObj.getString("planType"));
            params.put("materialCode", businessObj.getString("materialCode"));
            params.put("salesModel", businessObj.getString("salesModel"));
            params.put("designModel", businessObj.getString("designModel"));
            JSONObject domesticoverseasProgress = decorationManualDomesticoverseasProgressDao.selectByMap(params);
            if (domesticoverseasProgress != null) {
                businessObj.put("decorationManual", domesticoverseasProgress.getString("decorationManual"));
                businessObj.put("disassemblyAndAssemblyManual", domesticoverseasProgress.getString("disassemblyAndAssemblyManual"));
                businessObj.put("structurefunctionAndMaintenanceManual", domesticoverseasProgress.getString("structurefunctionAndMaintenanceManual"));
                businessObj.put("testAndAdjustmentManual", domesticoverseasProgress.getString("testAndAdjustmentManual"));
                businessObj.put("troubleshootingManual", domesticoverseasProgress.getString("troubleshootingManual"));
                businessObj.put("torqueAndToolStandardValueTable", domesticoverseasProgress.getString("torqueAndToolStandardValueTable"));
                businessObj.put("maintenanceStandardValueTable", domesticoverseasProgress.getString("maintenanceStandardValueTable"));
                businessObj.put("engineManual", domesticoverseasProgress.getString("engineManual"));
            }
            businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            businessObj.put("UPDATE_TIME_", new Date());
            decorationManualMassproductionDistributionDao.updateData(businessObj);
        }
    }

    //..量产机型手册制作计划及进度柱状图专用
    public List<JSONObject> kanbanData(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("signYear", postDataJson.getString("signYear"));
        List<JSONObject> businessList = decorationManualMassproductionDistributionDao.dataListQuery(params);
        HashMap<String, JSONObject> hashMap = new HashMap<>();
        for (JSONObject business : businessList) {
            //如果已经有某个部门的计数统计记录了
            if (hashMap.containsKey(business.getString("productInstitute"))) {
                JSONObject jsonObject = hashMap.get(business.getString("productInstitute"));
                //取当前生产计划计数
                int planNum = jsonObject.getInteger("生产计划");
                //生产计划计数++
                jsonObject.put("生产计划", ++planNum);
                //取当前制作计划数
                int makeNum = jsonObject.getInteger("制作计划");
                //如果计划类型不是“无”的话制作计划++
                jsonObject.put("制作计划", business.getString("planType").equalsIgnoreCase("无") ? makeNum : ++makeNum);
                //取当前完成情况数
                int clearNum = jsonObject.getInteger("完成情况");
                //如果装修手册是100的话完成情况++
                jsonObject.put("完成情况", business.getString("decorationManual").equalsIgnoreCase("100") ? ++clearNum : clearNum);
                //加入map
                hashMap.put(business.getString("productInstitute"), jsonObject);
            }
            //如果还没有某个部门的计数统计记录
            else {
                JSONObject jsonObject = new JSONObject();
                //计入这个所
                jsonObject.put("产品所", business.getString("productInstitute"));
                //生产计划计1
                jsonObject.put("生产计划", 1);
                //如果计划类型不是“无”的话制作计划计1
                jsonObject.put("制作计划", business.getString("planType").equalsIgnoreCase("无") ? 0 : 1);
                //如果装修手册是100的话完成情况计1
                jsonObject.put("完成情况", business.getString("decorationManual").equalsIgnoreCase("100") ? 1 : 0);
                //加入map
                hashMap.put(business.getString("productInstitute"), jsonObject);
            }
        }

        List<JSONObject> resultList = new ArrayList<>();
        Iterator<String> iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            resultList.add(hashMap.get(key));
        }
        return resultList;
    }

    //..量产机型数量饼状图专用
    public List<JSONObject> kanbanData2(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("signYear", postDataJson.getString("signYear"));
        List<JSONObject> businessList = decorationManualMassproductionDistributionDao.dataListQuery(params);
        HashMap<String, JSONObject> hashMap = new HashMap<>();
        for (JSONObject business : businessList) {
            if (hashMap.containsKey(business.getString("productInstitute"))) {
                JSONObject jsonresult = hashMap.get(business.getString("productInstitute"));
                int count = jsonresult.getInteger("value");
                jsonresult.put("value", ++count);
                hashMap.put(business.getString("productInstitute"), jsonresult);
            } else {
                JSONObject jsonresult = new JSONObject();
                jsonresult.put("name", business.getString("productInstitute"));
                jsonresult.put("value", 1);
                hashMap.put(business.getString("productInstitute"), jsonresult);
            }
        }
        List<JSONObject> resultList = new ArrayList<>();
        Iterator<String> iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            resultList.add(hashMap.get(key));
        }
        return resultList;
    }
}
