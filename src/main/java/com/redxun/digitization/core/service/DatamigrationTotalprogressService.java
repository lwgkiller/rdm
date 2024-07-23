package com.redxun.digitization.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.digitization.core.dao.DatamigrationTotalprogressDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Service
public class DatamigrationTotalprogressService {
    private static Logger logger = LoggerFactory.getLogger(DatamigrationTotalprogressService.class);
    @Autowired
    private DatamigrationTotalprogressDao datamigrationTotalprogressDao;


    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = datamigrationTotalprogressDao.dataListQuery(params);
        int businessListCount = datamigrationTotalprogressDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }


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


    public JsonResult deleteData(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        datamigrationTotalprogressDao.deleteData(param);
        return result;
    }


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
                datamigrationTotalprogressDao.insertData(businessObj);
            } else {
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                datamigrationTotalprogressDao.updateData(businessObj);
            }
        }
    }


    public List<JSONObject> getTotalprogressKanbanData(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        Map<String, Object> params = new HashMap<>();
        params.put("action", postDataJson.getString("action"));
        List<JSONObject> totalprogressList = datamigrationTotalprogressDao.getTotalprogressList(params);
        LinkedHashMap<String, JSONObject> totalMap = new LinkedHashMap<>();
        for (JSONObject jsonObject : totalprogressList) {
            if (params.get("action").toString().equalsIgnoreCase("productInstitute")) {
                totalMap.put(jsonObject.getString("productInstitute"), jsonObject);
            } else if (params.get("action").toString().equalsIgnoreCase("tonnageRange")) {
                totalMap.put(jsonObject.getString("tonnageRange"), jsonObject);
            }
        }
        List<JSONObject> resultDataList = new ArrayList<>();
        Iterator<String> iterator = totalMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject jsonObject = new JSONObject();
            if (params.get("action").toString().equalsIgnoreCase("productInstitute")) {
                jsonObject.put("产品所", key);
            } else if (params.get("action").toString().equalsIgnoreCase("tonnageRange")) {
                jsonObject.put("吨位段", key);
            }
            jsonObject.put("序时进度", totalMap.get(key).getString("chronologicalProgress"));
            jsonObject.put("实际进度", totalMap.get(key).getString("chronologicalActualProgress"));
            resultDataList.add(jsonObject);
        }
        return resultDataList;
    }
}
