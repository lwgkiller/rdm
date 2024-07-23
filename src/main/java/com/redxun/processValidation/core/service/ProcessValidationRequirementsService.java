package com.redxun.processValidation.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.processValidation.core.dao.ProcessValidationRequirementsDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MixedinputDao;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class ProcessValidationRequirementsService {
    private static Logger logger = LoggerFactory.getLogger(ProcessValidationRequirementsService.class);
    private static final int EXCEL_ROW_OFFSET = 1;
    @Autowired
    private ProcessValidationRequirementsDao processValidationRequirementsDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    public JsonPageResult<?> applyDataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<Map<String, Object>> businessList = processValidationRequirementsDao.applyDataListQuery(params);
        //查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = processValidationRequirementsDao.countApplyDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "applyTime");
            params.put("sortOrder", "desc");
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
    public JsonResult deleteApply(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        processValidationRequirementsDao.deleteApply(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JSONObject getApplyDetail(String businessId) {
        JSONObject jsonObject = processValidationRequirementsDao.queryApplyDetailById(businessId);
        if (jsonObject == null) {
            JSONObject newjson = new JSONObject();
            newjson.put("applyUserId", ContextUtil.getCurrentUserId());
            newjson.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            newjson.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            newjson.put("recordItems", "[]");
            return newjson;
        }
        return jsonObject;
    }

    //..
    public void createApply(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            formData.put("businessStatus", "A");
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            this.processJsonItem(formData);
            processValidationRequirementsDao.insertApply(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void updateApply(JSONObject formData) {
        try {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            this.processJsonItem(formData);
            processValidationRequirementsDao.updateApply(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..@lwgkiller:无表化设计参考，简单情况
    private void processJsonItem(JSONObject formData) {
        JSONArray jsonArray = new JSONArray();
        if (formData.containsKey("recordItems") && StringUtil.isNotEmpty(formData.getString("recordItems"))) {
            String recordItems = formData.getString("recordItems");
            JSONArray jsonArrayItemData = JSONObject.parseArray(recordItems);
            for (Object itemDataObject : jsonArrayItemData) {
                JSONObject itemDataJson = (JSONObject) itemDataObject;
                if (itemDataJson.containsKey("_state")) {//处理新增和修改
                    if (itemDataJson.getString("_state").equalsIgnoreCase("added")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        itemDataJson.put("id", IdUtil.getId());
                        jsonArray.add(itemDataJson);
                    } else if (itemDataJson.getString("_state").equalsIgnoreCase("modified")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        jsonArray.add(itemDataJson);
                    }
                } else {//处理没变化的
                    itemDataJson.remove("_state");
                    itemDataJson.remove("_id");
                    itemDataJson.remove("_uid");
                    jsonArray.add(itemDataJson);
                }

            }
            formData.put("recordItems", jsonArray.toString());
        } else {
            formData.put("recordItems", "[]");
        }
    }
}
