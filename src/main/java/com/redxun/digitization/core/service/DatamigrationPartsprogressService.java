package com.redxun.digitization.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.digitization.core.dao.DatamigrationPartsprogressDao;
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
public class DatamigrationPartsprogressService {
    private static Logger logger = LoggerFactory.getLogger(DatamigrationPartsprogressService.class);
    @Autowired
    private DatamigrationPartsprogressDao datamigrationPartsprogressDao;


    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = datamigrationPartsprogressDao.dataListQuery(params);
        int businessListCount = datamigrationPartsprogressDao.countDataListQuery(params);
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


    public JsonResult deleteData(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        datamigrationPartsprogressDao.deleteData(param);
        return result;
    }

    //
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
                datamigrationPartsprogressDao.insertData(businessObj);
            } else {
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                datamigrationPartsprogressDao.updateData(businessObj);
            }
        }
    }

    //
    public void calculateBusiness(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            //totalPercentage = conversionPercentage*0.7+proofreadingPercentage*0.2+windchillPercentage*0.1
            BigDecimal conversionPercentage = new BigDecimal(businessObj.getString("conversionPercentage"));
            BigDecimal proofreadingPercentage = new BigDecimal(businessObj.getString("proofreadingPercentage"));
            BigDecimal windchillPercentage = new BigDecimal(businessObj.getString("windchillPercentage"));
            BigDecimal totalPercentage = conversionPercentage.multiply(new BigDecimal("0.7")).
                    add(proofreadingPercentage.multiply(new BigDecimal("0.2"))).
                    add(windchillPercentage.multiply(new BigDecimal("0.1"))).setScale(0, BigDecimal.ROUND_HALF_UP);
            businessObj.put("totalPercentage", totalPercentage.stripTrailingZeros().toPlainString());
            //machinePercentage = totalPercentage * partsPercentage / 100
            BigDecimal machinePercentage = totalPercentage.multiply(new BigDecimal(businessObj.getString("partsPercentage"))).
                    divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            businessObj.put("machinePercentage", machinePercentage.stripTrailingZeros().toPlainString());
            //machineTotalPercentage = sum(machinePercentage)
            JSONObject params = new JSONObject();
            Map<String, String> mapClu = new HashMap<>();
            params.put("materialCode", businessObj.getString("materialCode"));
            List<JSONObject> businessList = datamigrationPartsprogressDao.dataListQuery(params);
            if (businessList.size() > 0) {
                for (JSONObject jsonObject : businessList) {
                    mapClu.put(jsonObject.getString("subMaterial"), jsonObject.getString("machinePercentage"));
                }
            }
            //覆盖已经计算的某条记录的machinePercentage值
            mapClu.put(businessObj.getString("subMaterial"), businessObj.getString("machinePercentage"));
            BigDecimal machineTotalPercentage = new BigDecimal("0");
            //然后将同类项的machinePercentage值合并给machineTotalPercentage
            Iterator<String> iterator = mapClu.keySet().iterator();
            while (iterator.hasNext()) {
                machineTotalPercentage = machineTotalPercentage.add(new BigDecimal(mapClu.get(iterator.next())));
            }
            machineTotalPercentage = machineTotalPercentage.setScale(0, BigDecimal.ROUND_HALF_UP);
            businessObj.put("machineTotalPercentage", machineTotalPercentage.stripTrailingZeros().toPlainString());
            //更新目标实体
            businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            businessObj.put("UPDATE_TIME_", new Date());
            datamigrationPartsprogressDao.updateData(businessObj);
            //更新同类项
//            if (businessList.size() > 0) {
//                for (JSONObject jsonObject : businessList) {
//                    jsonObject.put("machineTotalPercentage", machineTotalPercentage.stripTrailingZeros().toPlainString());
//                    //如果是更新情况过滤掉目标实体
//                    if(!jsonObject.getString("subMaterial").equalsIgnoreCase(businessObj.getString("subMaterial"))){
//                        datamigrationPartsprogressDao.updateData(jsonObject);
//                    }
//                }
//            }
            params.put("machineTotalPercentage",machineTotalPercentage.stripTrailingZeros().toPlainString());
            //更新同类项
            datamigrationPartsprogressDao.updateDataSameClass(params);
        }
    }
}
