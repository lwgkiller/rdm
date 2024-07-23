package com.redxun.standardManager.core.manager;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.dao.StandardSystemDao;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class StandardSystemManager {
    private static Logger logger = LoggerFactory.getLogger(StandardSystemManager.class);
    @Autowired
    private StandardSystemDao standardSystemDao;

    // 查询体系类别
    public List<Map<String, Object>> systemCategoryQuery() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> result = standardSystemDao.querySystemCategory(params);
            return result;
        } catch (Exception e) {
            logger.error("Exception in systemCategoryQuery", e);
            return null;
        }
    }

    // 查询体系
    public List<Map<String, Object>> systemQuery(String systemCategoryId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            if (StringUtils.isNotBlank(systemCategoryId)) {
                params.put("systemCategoryId", systemCategoryId);
            }
            List<Map<String, Object>> result = standardSystemDao.querySystem(params);
            // 拼接可见组Names
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneData : result) {
                    if (oneData.get("visibleGroupIds") != null
                        && StringUtils.isNotBlank(oneData.get("visibleGroupIds").toString())) {
                        String groupNames = queryGroupNamesByIds(oneData.get("visibleGroupIds").toString());
                        oneData.put("visibleGroupNames", groupNames);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in systemQuery", e);
            return Collections.emptyList();
        }
    }

    private String queryGroupNamesByIds(String groupIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupIds", Arrays.asList(groupIds.split(",", -1)));
        List<JSONObject> groupNameInfos = standardSystemDao.queryGroupNamesByIds(params);
        StringBuilder stringBuilder = new StringBuilder();
        for (JSONObject oneGroup : groupNameInfos) {
            stringBuilder.append(oneGroup.getString("NAME_")).append(",");
        }
        if (stringBuilder.length() > 0) {
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        } else {
            return "";
        }
    }

    // 保存体系
    public void treeSave(JSONObject result, String changedDataStr) {
        JSONArray changedDataArray = JSONObject.parseArray(changedDataStr);
        if (changedDataArray == null || changedDataArray.isEmpty()) {
            logger.warn("数据为空");
            result.put("message", "数据为空！");
            return;
        }
        try {
            for (int i = 0; i < changedDataArray.size(); i++) {
                JSONObject oneObject = changedDataArray.getJSONObject(i);
                String state = oneObject.getString("_state");
                if ("added".equals(state)) {
                    // 新增
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    standardSystemDao.saveSystemNode(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    standardSystemDao.updateSystemNode(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    standardSystemDao.delSystemNode(oneObject.getString("id"));
                }
            }
            result.put("message", "数据保存成功！");
        } catch (Exception e) {
            logger.error("Exception in treeSave", e);
            result.put("message", "系统异常，保存失败！");
        }
    }

    // 查询这些体系是否有关联的标准，返回个数
    public void queryStandardBySystemIds(JSONObject result, String systemIds) {
        JSONArray systemIdArr = JSONArray.parseArray(systemIds);
        if (systemIdArr == null || systemIdArr.size() == 0) {
            logger.warn("requestBody is blank");
            result.put("num", 0);
            return;
        }
        Set<String> systemIdSet = new HashSet<>();
        for (int i = 0; i < systemIdArr.size(); i++) {
            JSONObject object = systemIdArr.getJSONObject(i);
            if (StringUtils.isNotBlank(object.getString("id"))) {
                systemIdSet.add(object.getString("id"));
            }
        }
        if (systemIdSet.isEmpty()) {
            result.put("num", 0);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("systemIds", systemIdSet);
        int standardNum = standardSystemDao.queryStandardBySystemIds(params);
        result.put("num", standardNum);
    }

    // 导出标准体系
    public void exportStandardSystem(HttpServletRequest request, HttpServletResponse response) {
        String systemCategoryId = RequestUtil.getString(request, "systemCategoryId");
        if (StringUtils.isBlank(systemCategoryId)) {
            logger.error("导出标准体系的类别参数为空！");
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("systemCategoryId", systemCategoryId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> queryExportSystemInfos = standardSystemDao.queryExportSystem(params);
        // 处理得到上层节点的名字
        if (queryExportSystemInfos != null) {
            Map<String, String> systemId2Name = new HashMap<>();
            for (Map<String, Object> oneMap : queryExportSystemInfos) {
                systemId2Name.put(oneMap.get("id").toString(), oneMap.get("systemName").toString());
            }
            for (Map<String, Object> oneMap : queryExportSystemInfos) {
                String parentId = oneMap.get("parentId") == null ? "" : oneMap.get("parentId").toString();
                String parentSystemName = "";
                if (systemId2Name.containsKey(parentId)) {
                    parentSystemName = systemId2Name.get(parentId);
                }
                oneMap.put("parentSystemName", parentSystemName);
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = StandardConstant.SYSTEMCATEGORY_JS.equalsIgnoreCase(systemCategoryId) ? "技术" : "管理" + "标准体系列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"标准体系名称", "标准体系编号", "标准体系类目代号", "上层体系名称"};
        String[] fieldCodes = {"systemName", "systemNumber", "systemCode", "parentSystemName"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryExportSystemInfos, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
