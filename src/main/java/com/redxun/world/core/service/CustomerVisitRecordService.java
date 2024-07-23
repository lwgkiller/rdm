package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.componentTest.core.dao.ComponentTestKanbanDao;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.entity.SysTree;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.sys.core.manager.SysTreeManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.world.core.dao.CustomerVisitRecordDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.collections.map.HashedMap;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class CustomerVisitRecordService {
    private static Logger logger = LoggerFactory.getLogger(CustomerVisitRecordService.class);
    private static String BUSINESS_STATUS_EDIT = "编辑中";
    private static String BUSINESS_STATUS_EXECUTE = "执行中";
    @Autowired
    private CustomerVisitRecordDao customerVisitRecordDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = customerVisitRecordDao.dataListQuery(params);
        int businessListCount = customerVisitRecordDao.countDataListQuery(params);
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
            params.put("sortField", "CREATE_TIME_");
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
    public JSONObject queryDataById(String businessId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(businessId)) {
            return result;
        }
        JSONObject jsonObject = customerVisitRecordDao.queryDataById(businessId);
        if (jsonObject == null) {
            return result;
        }
        return jsonObject;
    }

    //..
    public void saveBusiness(JsonResult result, String dataStr) {
        try {
            JSONObject formDataJson = JSONObject.parseObject(dataStr);
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                formDataJson.put("id", IdUtil.getId());
                formDataJson.put("businessStatus", this.BUSINESS_STATUS_EDIT);
                formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("CREATE_TIME_", new Date());
                this.processJsonItem(formDataJson);
                customerVisitRecordDao.insertBusiness(formDataJson);
            } else {
                formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("UPDATE_TIME_", new Date());
                this.processJsonItem(formDataJson);
                customerVisitRecordDao.updateBusiness(formDataJson);
            }
            result.setData(formDataJson.getString("id"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("保存失败！" + e.getMessage());
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

    //..
    public JsonResult deleteBusiness(String[] ids) {
        try {
            JsonResult result = new JsonResult(true, "操作成功！");
            List<String> businessIds = Arrays.asList(ids);
            JSONObject param = new JSONObject();
            param.put("businessIds", businessIds);
            customerVisitRecordDao.deleteBusiness(param);
            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    //..
    public JsonResult executeBusiness(String id) {
        try {
            JsonResult result = new JsonResult(true, "操作成功！");
            JSONObject jsonObject = this.queryDataById(id);
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            jsonObject.put("businessStatus", this.BUSINESS_STATUS_EXECUTE);
            customerVisitRecordDao.updateBusiness(jsonObject);
            this.sendDingDing(jsonObject);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    private void sendDingDing(JSONObject jsonObject) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【客户拜访跟踪记录通知】:被拜访公司为 ");
        stringBuilder.append(jsonObject.getString("companyVisited"));
        stringBuilder.append(" 的拜访任务完成情况跟踪记录需要你处理").
                append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(jsonObject.getString("taskExecutorId"), noticeObj);
    }
}
