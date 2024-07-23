package com.redxun.serviceEngineering.core.service;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.redxun.serviceEngineering.core.dao.SchematicDownloadApplyDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;

@Service
public class SchematicDownloadApplyService {
    private static final Logger logger = LoggerFactory.getLogger(SchematicDownloadApplyService.class);

    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SchematicDownloadApplyDao schematicDownloadApplyDao;

    // ..
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "service_engineering_schematic_downloadapply.CREATE_BY_", "desc");
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        // 增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> applyList = schematicDownloadApplyDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人--非会签
        // xcmgProjectManager.setTaskCurrentUser(businessList);
        // 查询当前处理人--会签
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());
        result.setData(applyList);
        int countApplyList = schematicDownloadApplyDao.countApplyList(params);
        result.setTotal(countApplyList);
        return result;
    }

    // ..
    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    // ..
    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = schematicDownloadApplyDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
        }
        return obj;
    }

    // ..
    public void saveInProcess(JsonResult result, String data) {
        JSONObject object = JSONObject.parseObject(data);
        if (object == null || object.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(object.getString("id"))) {
            createApply(object);
            result.setData(object.getString("id"));
        } else {
            updateApply(object);
            result.setData(object.getString("id"));
        }
    }

    // ..
    public void createApply(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("creatorDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        schematicDownloadApplyDao.insertApply(formData);
        // 成员信息
        demandProcess(formData.getString("id"), formData.getJSONArray("changeDemandGrid"));

    }

    // ..
    public void updateApply(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        schematicDownloadApplyDao.updateApply(formData);
        // 成员信息
        demandProcess(formData.getString("id"), formData.getJSONArray("changeDemandGrid"));
    }

    // ..
    private void demandProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                // 新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                schematicDownloadApplyDao.insertDemand(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            schematicDownloadApplyDao.deleteDemand(param);
        }
    }

    // ..
    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);
        // 删除需求子表
        schematicDownloadApplyDao.deleteDemand(param);

        // 删除主表
        param.put("ids", applyIdList);
        schematicDownloadApplyDao.deleteApply(param);
        return result;
    }

    // ..
    public List<JSONObject> queryDemandList(JSONObject params) {
        List<JSONObject> demandList = schematicDownloadApplyDao.queryDemandList(params);
        return demandList;
    }

}
