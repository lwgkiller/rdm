package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.dao.PatentInterpretationTechnologyBranchDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class PatentInterpretationTechnologyBranchService {
    private static Logger logger = LoggerFactory.getLogger(PatentInterpretationTechnologyBranchService.class);
    @Autowired
    private PatentInterpretationTechnologyBranchDao patentInterpretationTechnologyBranchDao;

    //..
    public List<JSONObject> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = patentInterpretationTechnologyBranchDao.dataListQuery(params);
        return businessList;
    }

    //..p
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "description");
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
    public JsonResult deleteData(String id) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Integer childrenCount = patentInterpretationTechnologyBranchDao.getChildrenCount(id);
        if (childrenCount == 0) {
            List<String> businessIds = new ArrayList<>();
            businessIds.add(id);
            Map<String, Object> param = new HashMap<>();
            param.put("businessIds", businessIds);
            patentInterpretationTechnologyBranchDao.deleteData(param);
        } else {
            result.setSuccess(false);
            result.setMessage("存在下级节点的记录不能删除！");
        }
        return result;
    }

    //..
    public void saveData(JsonResult result, String dataStr) {
        JSONObject dataObj = JSONObject.parseObject(dataStr);
        if (dataObj == null || dataObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(dataObj.getString("id"))) {
            dataObj.put("id", IdUtil.getId());
            dataObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            dataObj.put("CREATE_TIME_", new Date());
            patentInterpretationTechnologyBranchDao.insertData(dataObj);
            result.setData(dataObj.getString("id"));
        } else {
            dataObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            dataObj.put("UPDATE_TIME_", new Date());
            patentInterpretationTechnologyBranchDao.updateData(dataObj);
            result.setData(dataObj.getString("id"));
        }
    }

    //..
    public JSONObject queryDataById(String businessId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(businessId)) {
            return result;
        }
        JSONObject businessObj = patentInterpretationTechnologyBranchDao.queryDataById(businessId);
        if (businessObj == null) {
            return result;
        }
        return businessObj;
    }

    //..递归获取根节点
    public JSONObject queryRootDataById(String businessId) {
        JSONObject businessObj = patentInterpretationTechnologyBranchDao.queryDataById(businessId);
        if (StringUtils.isEmpty(businessObj.getString("parentId"))) {
            return businessObj;
        } else {
            return queryRootDataById(businessObj.getString("parentId"));
        }
    }
}
