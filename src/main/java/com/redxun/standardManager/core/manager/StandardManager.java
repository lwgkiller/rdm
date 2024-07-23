package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.util.FileOperateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.*;
import com.redxun.standardManager.core.model.StandardStatus;
import com.redxun.standardManager.core.util.PdfWaterMarkProcess;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Year;
import java.util.*;

@Service
@Slf4j
public class StandardManager {
    private static Logger logger = LoggerFactory.getLogger(StandardManager.class);
    @Autowired
    private StandardDao standardDao;
    @Autowired
    private StandardUpdateDao standardUpdateDao;
    @Autowired
    private StandardConfigDao standardConfigDao;
    @Autowired
    private StandardSystemManager standardSystemManager;
    @Autowired
    private StandardFieldManagerDao standardFieldManagerDao;
    @Autowired
    private StandardFileInfosDao standardFileInfosDao;
    @Autowired
    private StandardManagementDao standardManagementDao;
    @Autowired
    private StandardDoCheckDao standardDoCheckDao;
    @Autowired
    private StandardSystemDao standardSystemDao;

    // 获取标准管理界面下拉信息
    public Map<String, Object> getStandardSelectInfos(String systemCategoryId) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 类别
        List<Map<String, Object>> categoryMapResults = standardConfigDao.queryStandardCategory(params);
        if (categoryMapResults != null && !categoryMapResults.isEmpty()) {
            result.put("category", categoryMapResults);
        } else {
            result.put("category", Collections.emptyList());
        }
        // 归口部门
        List<Map<String, Object>> belongDepsMapResults = standardConfigDao.queryStandardBelongDep(params);
        if (belongDepsMapResults != null && !belongDepsMapResults.isEmpty()) {
            result.put("belongDep", belongDepsMapResults);
        } else {
            result.put("belongDep", Collections.emptyList());
        }
        // 专业领域
        JSONObject queryParam = new JSONObject();
        if (StringUtils.isNotBlank(systemCategoryId)) {
            queryParam.put("systemCategoryId", systemCategoryId);
        }
        List<JSONObject> fieldMapResults = standardFieldManagerDao.queryFieldObject(queryParam);
        if (fieldMapResults != null && !fieldMapResults.isEmpty()) {
            result.put("fields", fieldMapResults);
        } else {
            result.put("fields", Collections.emptyList());
        }
        return result;
    }

    // 删除标准
    public void deleteStandard(JSONObject result, String standardIds) {
        try {
            // 删除本体标准
            Map<String, Object> param = new HashMap<>();
            String[] idArr = standardIds.split(",", -1);
            List<String> idList = new ArrayList<String>(Arrays.asList(idArr));
            param.put("ids", idList);
            param.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            standardDao.deleteStandard(param);
            for (int i = 0; i < idList.size(); i++) {
                deleteStandardFileFromDisk(idList.get(i));
            }

            // 删除借出的所有标准
            param.clear();
            param.put("borrowFromIdList", idList);
            List<Map<String, Object>> borrowedList = standardDao.queryStandardBorrowList(param);
            if (borrowedList != null && !borrowedList.isEmpty()) {
                List<String> borrowedIdList = new ArrayList<>();
                for (Map<String, Object> oneBorrow : borrowedList) {
                    borrowedIdList.add(oneBorrow.get("id").toString());
                    idList.add(oneBorrow.get("id").toString());
                }
                param.clear();
                param.put("ids", borrowedIdList);
                param.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                standardDao.deleteStandard(param);
                for (int i = 0; i < borrowedIdList.size(); i++) {
                    deleteStandardFileFromDisk(borrowedIdList.get(i));
                }
            }
            // 删除本体和借出标准关联的所有专业领域
            JSONObject param2 = new JSONObject();
            param2.put("standardIds", idList);
            standardFieldManagerDao.deleteStandardFieldRelaByStandard(param2);

            // 删除本体和借出标准的附表记录和文件
            param.clear();
            param.put("standardIds", idList);
            standardFileInfosDao.deleteFileByStandardIds(param);
            String standardAttachFilePath = WebAppUtil.getProperty("standardAttachFilePath");
            if (StringUtils.isNotBlank(standardAttachFilePath)) {
                List<String> filePaths = new ArrayList<>();
                for (String standardId : idList) {
                    StringBuilder sb = new StringBuilder(standardAttachFilePath);
                    sb.append(File.separator).append(standardId).append(File.separator);
                    filePaths.add(sb.toString());
                }
                FileOperateUtil.deleteDirAndFiles(filePaths);
            }
            // TODO 如果是管理标准需要删除附表流程信息

            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in deleteStandard", e);
            result.put("message", "系统异常！");
        }
    }

    // 保存（包括新增保存、编辑保存）
    public void saveStandard(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            MultipartFile fileObj = multipartRequest.getFile("standardFile");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }

            Map<String, Object> objBody = new HashMap<>();
            constructParam(parameters, objBody);
            boolean isAdd = true;
            // 更新标准的场景需要同步更新借用的标准
            if (objBody.get("id") != null && StringUtils.isNotBlank(objBody.get("id").toString())) {
                isAdd = false;
                String borrowFromId = objBody.get("id").toString();
                List<String> borrowFromIdList = new ArrayList<>();
                borrowFromIdList.add(borrowFromId);
                Map<String, Object> params = new HashMap<>();
                params.put("borrowFromIdList", borrowFromIdList);
                List<Map<String, Object>> borrowedList = standardDao.queryStandardBorrowList(params);
                if (borrowedList != null && !borrowedList.isEmpty()) {
                    List<String> borrowedIdList = new ArrayList<>();
                    for (Map<String, Object> oneBorrow : borrowedList) {
                        borrowedIdList.add(oneBorrow.get("id").toString());
                    }
                    params.clear();
                    params.put("ids", borrowedIdList);
                    params.putAll(objBody);
                    params.remove("id");
                    params.remove("systemId");
                    batchUpdateBorrowedList(params, fileObj);
                }
                // 更正记录
                updateNote(objBody);
            } else {
                // 判断编号是否数据库已存在
                if (objBody.get("standardNumber") != null
                    && StringUtils.isNotBlank(objBody.get("standardNumber").toString())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("standardNumber", objBody.get("standardNumber").toString());
                    List<Map<String, Object>> idAndNumbers = standardDao.queryStandardIdNumber(params);
                    if (idAndNumbers != null && !idAndNumbers.isEmpty()) {
                        result.put("message", "当前编号已存在，请修改后重试！");
                        result.put("success", false);
                        return;
                    }
                }
                // 新增场景，如果所属专业领域为空则默认为所属类别的通用领域，如"技术通用"
                if (objBody.get("systemCategoryId") != null && objBody.get("belongFieldIds") == null) {
                    String systemCategoryId = objBody.get("systemCategoryId").toString();
                    String fieldName = "技术通用";
                    if ("GL".equalsIgnoreCase(systemCategoryId)) {
                        fieldName = "管理通用";
                    } else if ("NK".equalsIgnoreCase(systemCategoryId)) {
                        fieldName = "内控通用";
                    }
                    List<JSONObject> fieldObjs = standardFieldManagerDao.queryFieldByName(fieldName);
                    if (fieldObjs != null && !fieldObjs.isEmpty()) {
                        objBody.put("belongFieldIds", fieldObjs.get(0).getString("fieldId"));
                    }
                }
            }
            if (objBody.get("id") != null && StringUtils.isNotBlank(objBody.get("id").toString())) {
                result.put("message", "保存成功！请在更正记录中完善更正原因！");
            } else {
                result.put("message", "保存成功！");
            }
            // 新增或者更新本体标准
            addOrUpdateStandard(objBody, fileObj);
            result.put("id", objBody.get("id"));
            result.put("success", true);
            if (isAdd) {
                if (objBody.get("standardTaskId") != null
                    && StringUtils.isNotBlank(objBody.get("standardTaskId").toString())) {
                    // 将标准id更新到标准开发任务表中
                    Map<String, Object> param = new HashMap<>();
                    param.put("finalStandardId", objBody.get("id"));
                    param.put("id", objBody.get("standardTaskId"));
                    standardManagementDao.updateFinalStandardId2Task(param);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in saveStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    // 更正记录
    public void updateNote(Map<String, Object> newNote) {
        JSONObject update = new JSONObject();
        update.put("updateId", IdUtil.getId());
        update.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        update.put("CREATE_TIME_", new Date());
        update.put("belongbz", newNote.get("id").toString());
        standardUpdateDao.createGzjl(update);
        JSONObject gzjl = new JSONObject();
        Map<String, Object> oldStandard = queryStandardById(newNote.get("id").toString());
        compare("categoryName", "标准类别", newNote, oldStandard, update, gzjl);
        compare("standardNumber", "标准编号", newNote, oldStandard, update, gzjl);
        compare("standardName", "标准名称", newNote, oldStandard, update, gzjl);
        compare("belongDepName", "归口部门", newNote, oldStandard, update, gzjl);
        compare("systemName", "所属体系", newNote, oldStandard, update, gzjl);
        compare("replaceNumber", "替代标准", newNote, oldStandard, update, gzjl);
        compare("beReplaceNumber", "被替代标准", newNote, oldStandard, update, gzjl);
        compare("publisherName", "起草人", newNote, oldStandard, update, gzjl);
        compare("stoperName", "废止人", newNote, oldStandard, update, gzjl);
        compare("publishTime", "发布时间", newNote, oldStandard, update, gzjl);
        compare("stopTime", "废止时间", newNote, oldStandard, update, gzjl);
        compare("sendSupplier", "是否发送供应商", newNote, oldStandard, update, gzjl);
        compare("cbbh", "采标编号", newNote, oldStandard, update, gzjl);
        compare("yzxcd", "一致性程度代号", newNote, oldStandard, update, gzjl);
        compare("banci", "版次", newNote, oldStandard, update, gzjl);
        compare("belongFieldNames", "所属专业领域", newNote, oldStandard, update, gzjl);
        compare("standardStatus", "状态", newNote, oldStandard, update, gzjl);
        compare("fileName", "全文附件", newNote, oldStandard, update, gzjl);
    }

    public void compare(String key, String keyName, Map<String, Object> newNote, Map<String, Object> oldStandard,
        JSONObject update, JSONObject gzjl) {

        if (newNote.get(key) != null && oldStandard.get(key) != null) {
            String oldOne = oldStandard.get(key).toString();
            String newOne = newNote.get(key).toString();
            if (key == "publishTime" || key == "stopTime") {
                oldOne =
                    DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(oldOne, DateUtil.DATE_FORMAT_FULL), -8));
            }
            if (key == "sendSupplier") {
                if ("true".equals(oldOne)) {
                    oldOne = "是";
                } else if ("false".equals(oldOne)) {
                    oldOne = "否";
                }
                if ("true".equals(newOne)) {
                    newOne = "是";
                } else if ("false".equals(newOne)) {
                    newOne = "否";
                }
            }
            if (key == "standardStatus") {
                if ("draft".equals(oldOne)) {
                    oldOne = "草稿";
                } else if ("disable".equals(oldOne)) {
                    oldOne = "已废止";
                } else if ("enable".equals(oldOne)) {
                    oldOne = "有效";
                }
                if ("draft".equals(newOne)) {
                    newOne = "草稿";
                } else if ("disable".equals(newOne)) {
                    newOne = "已废止";
                } else if ("enable".equals(newOne)) {
                    newOne = "有效";
                }
            }
            if (!newOne.equals(oldOne)) {
                gzjl.put("detailId", IdUtil.getId());
                gzjl.put("oldNote", oldOne);
                gzjl.put("newNote", newOne);
                gzjl.put("updateNote", keyName);
                gzjl.put("belongId", update.getString("updateId"));
                gzjl.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                gzjl.put("CREATE_TIME_", new Date());
                standardUpdateDao.createGzjlDetail(gzjl);
            }
        } else if (newNote.get(key) == null && oldStandard.get(key) != null) {
            gzjl.put("detailId", IdUtil.getId());
            gzjl.put("oldNote", oldStandard.get(key).toString());
            gzjl.put("newNote", "未填写");
            gzjl.put("updateNote", keyName);
            gzjl.put("belongId", update.getString("updateId"));
            gzjl.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            gzjl.put("CREATE_TIME_", new Date());
            standardUpdateDao.createGzjlDetail(gzjl);
        } else if (newNote.get(key) != null && oldStandard.get(key) == null) {
            gzjl.put("detailId", IdUtil.getId());
            gzjl.put("oldNote", "未填写");
            gzjl.put("newNote", newNote.get(key).toString());
            gzjl.put("updateNote", keyName);
            gzjl.put("belongId", update.getString("updateId"));
            gzjl.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            gzjl.put("CREATE_TIME_", new Date());
            standardUpdateDao.createGzjlDetail(gzjl);
        }
    }

    public void stopAndNew(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            MultipartFile fileObj = multipartRequest.getFile("standardFile");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }
            Map<String, Object> objBody = new HashMap<>();
            constructParam(parameters, objBody);
            // 新增场景，如果所属专业领域为空则默认为所属类别的通用领域，如"技术通用"
            if (objBody.get("systemCategoryId") != null && objBody.get("belongFieldIds") == null) {
                String systemCategoryId = objBody.get("systemCategoryId").toString();
                String fieldName = "技术通用";
                if ("GL".equalsIgnoreCase(systemCategoryId)) {
                    fieldName = "管理通用";
                } else if ("NK".equalsIgnoreCase(systemCategoryId)) {
                    fieldName = "内控通用";
                }
                List<JSONObject> fieldObjs = standardFieldManagerDao.queryFieldByName(fieldName);
                if (fieldObjs != null && !fieldObjs.isEmpty()) {
                    objBody.put("belongFieldIds", fieldObjs.get(0).getString("fieldId"));
                }
            }
            // 新增标准
            String newStandardId = IdUtil.getId();
            String oldStandardId = objBody.get("id").toString();
            Map<String, Object> oldStandard = queryStandardById(oldStandardId);
            if (fileObj != null) {
                updateStandardFile2Disk(newStandardId, fileObj);
            }
            objBody.put("id", newStandardId);
            objBody.put("replaceId", oldStandardId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            standardDao.insertStandard(objBody);
            // 新增或者更新标准-专业对应关系
            String belongFieldIds = null;
            if (objBody.get("belongFieldIds") != null) {
                belongFieldIds = objBody.get("belongFieldIds").toString();
            }
            addOrUpdateStandardFieldRelation(newStandardId, belongFieldIds, "add");

            result.put("message", "保存成功！");
            result.put("id", objBody.get("id"));
            result.put("success", true);
            if (objBody.get("standardTaskId") != null
                && StringUtils.isNotBlank(objBody.get("standardTaskId").toString())) {
                // 将标准id更新到标准开发任务表中
                Map<String, Object> param = new HashMap<>();
                param.put("finalStandardId", objBody.get("id"));
                param.put("id", objBody.get("standardTaskId"));
                standardManagementDao.updateFinalStandardId2Task(param);
            }
            // 废止旧标准
            oldStandard.put("standardStatus", StandardStatus.Disable.getKey());
            oldStandard.put("beReplacedById", newStandardId);
            oldStandard.put("stoperId", ContextUtil.getCurrentUserId());
            oldStandard.put("stopTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            oldStandard.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            oldStandard.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            standardDao.updateStandard(oldStandard);

            // 更新所有旧标准借出的标准
            String borrowFromId = oldStandardId;
            List<String> borrowFromIdList = new ArrayList<>();
            borrowFromIdList.add(borrowFromId);
            Map<String, Object> params = new HashMap<>();
            params.put("borrowFromIdList", borrowFromIdList);
            List<Map<String, Object>> borrowedList = standardDao.queryStandardBorrowList(params);
            if (borrowedList != null && !borrowedList.isEmpty()) {
                List<String> borrowedIdList = new ArrayList<>();
                for (Map<String, Object> oneBorrow : borrowedList) {
                    borrowedIdList.add(oneBorrow.get("id").toString());
                }
                params.clear();
                params.put("ids", borrowedIdList);
                params.putAll(oldStandard);
                params.remove("id");
                params.remove("systemId");
                standardDao.batchUpdateBorrowedList(params);
            }

        } catch (Exception e) {
            logger.error("Exception in saveStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    /**
     * 批量更新借出的标准
     *
     * @param objBody
     * @param fileObj
     */
    private void batchUpdateBorrowedList(Map<String, Object> objBody, MultipartFile fileObj) {
        // 更新标准全文
        List<String> borrowedIdList = (List<String>)objBody.get("ids");
        for (String borrowedId : borrowedIdList) {
            try {
                if (fileObj != null) {
                    // 更新文件
                    updateStandardFile2Disk(borrowedId, fileObj);
                } else {
                    // fileObj为空，有可能是真的没有附件，也有可能是编辑场景（有fileName）
                    // 如无fileName则用户前台希望删除该标准的文件，否则说明用户没处理
                    String fileName = objBody.get("fileName") == null ? "" : objBody.get("fileName").toString();
                    if (StringUtils.isBlank(fileName)) {
                        deleteStandardFileFromDisk(borrowedId);
                    }
                }
            } catch (Exception e) {
                logger.error("Exception in batchUpdateBorrowedList, standardId is " + borrowedId, e);
            }
        }

        objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        standardDao.batchUpdateBorrowedList(objBody);
    }

    /**
     * 停止老标准，生成并启用新标准
     *
     * @param standardId
     * @param result
     */
    public void stopOldPublishNew(String standardId, JSONObject result) {
        try {
            Map<String, Object> oldStandard = queryStandardById(standardId);
            Map<String, Object> newStandard = new HashMap<>(oldStandard);

            // 生成并启用新标准
            String newStandardNumber = oldStandard.getOrDefault("standardNumber", "").toString() + "-new";
            newStandard.remove("id");
            newStandard.remove("stopTime");
            newStandard.remove("stoperId");
            newStandard.remove("UPDATE_BY_");
            newStandard.remove("UPDATE_TIME_");
            newStandard.put("standardNumber", oldStandard.getOrDefault("standardNumber", "").toString() + "-new");
            newStandard.put("standardStatus", StandardStatus.Enable.getKey());
            newStandard.put("replaceId", standardId);
            newStandard.put("publishTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            newStandard.put("publisherId", oldStandard.getOrDefault("publisherId", "").toString());
            // 从原标准复制一份文件到新标准
            MultipartFile fileObj = null;
            if (oldStandard.get("fileName") != null && StringUtils.isNotBlank(oldStandard.get("fileName").toString())) {

                File oldStandardFile = findStandardFile(standardId, ConstantUtil.DOWNLOAD);
                if (oldStandardFile != null) {
                    FileInputStream oldFileStream = new FileInputStream(oldStandardFile);
                    fileObj = new MockMultipartFile(oldStandardFile.getName(), oldFileStream);
                } else {
                    logger.error("standard {} file can't find!", standardId);
                }

            }
            addOrUpdateStandard(newStandard, fileObj);

            // 废止旧标准
            oldStandard.put("standardStatus", StandardStatus.Disable.getKey());
            oldStandard.put("beReplacedById", newStandard.get("id").toString());
            oldStandard.put("stoperId", ContextUtil.getCurrentUserId());
            oldStandard.put("stopTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            oldStandard.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            oldStandard.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            standardDao.updateStandard(oldStandard);

            // 更新所有旧标准借出的标准
            String borrowFromId = standardId;
            List<String> borrowFromIdList = new ArrayList<>();
            borrowFromIdList.add(borrowFromId);
            Map<String, Object> params = new HashMap<>();
            params.put("borrowFromIdList", borrowFromIdList);
            List<Map<String, Object>> borrowedList = standardDao.queryStandardBorrowList(params);
            if (borrowedList != null && !borrowedList.isEmpty()) {
                List<String> borrowedIdList = new ArrayList<>();
                for (Map<String, Object> oneBorrow : borrowedList) {
                    borrowedIdList.add(oneBorrow.get("id").toString());
                }
                params.clear();
                params.put("ids", borrowedIdList);
                params.putAll(oldStandard);
                params.remove("id");
                params.remove("systemId");
                standardDao.batchUpdateBorrowedList(params);
            }

            result.put("message", "操作成功。新标准编号为：" + newStandardNumber + "。请尽快修改！");
        } catch (Exception e) {
            logger.error("Exception in copy file", e);
            result.put("message", "操作失败，系统异常！");
        }
    }

    // 构建更新或新增的数据
    private void constructParam(Map<String, String[]> parameters, Map<String, Object> objBody) {
        if (parameters.get("id") != null && parameters.get("id").length != 0
            && StringUtils.isNotBlank(parameters.get("id")[0])) {
            objBody.put("id", parameters.get("id")[0]);
        }
        if (parameters.get("standardNumber") != null && parameters.get("standardNumber").length != 0
            && StringUtils.isNotBlank(parameters.get("standardNumber")[0])) {
            objBody.put("standardNumber", parameters.get("standardNumber")[0]);
        }
        if (parameters.get("standardName") != null && parameters.get("standardName").length != 0
            && StringUtils.isNotBlank(parameters.get("standardName")[0])) {
            objBody.put("standardName", parameters.get("standardName")[0]);
        }
        if (parameters.get("standardCategoryId") != null && parameters.get("standardCategoryId").length != 0
            && StringUtils.isNotBlank(parameters.get("standardCategoryId")[0])) {
            objBody.put("standardCategoryId", parameters.get("standardCategoryId")[0]);
        }
        if (parameters.get("belongDepId") != null && parameters.get("belongDepId").length != 0
            && StringUtils.isNotBlank(parameters.get("belongDepId")[0])) {
            objBody.put("belongDepId", parameters.get("belongDepId")[0]);
        }
        if (parameters.get("standardStatus") != null && parameters.get("standardStatus").length != 0
            && StringUtils.isNotBlank(parameters.get("standardStatus")[0])) {
            objBody.put("standardStatus", parameters.get("standardStatus")[0]);
        }
        if (parameters.get("systemId") != null && parameters.get("systemId").length != 0
            && StringUtils.isNotBlank(parameters.get("systemId")[0])) {
            objBody.put("systemId", parameters.get("systemId")[0]);
        }
        if (parameters.get("replaceId") != null && parameters.get("replaceId").length != 0
            && StringUtils.isNotBlank(parameters.get("replaceId")[0])) {
            objBody.put("replaceId", parameters.get("replaceId")[0]);
        }
        if (parameters.get("beReplacedById") != null && parameters.get("beReplacedById").length != 0
            && StringUtils.isNotBlank(parameters.get("beReplacedById")[0])) {
            objBody.put("beReplacedById", parameters.get("beReplacedById")[0]);
        }
        if (parameters.get("publisherId") != null && parameters.get("publisherId").length != 0
            && StringUtils.isNotBlank(parameters.get("publisherId")[0])) {
            objBody.put("publisherId", parameters.get("publisherId")[0]);
        }
        if (parameters.get("publishTime") != null && parameters.get("publishTime").length != 0
            && StringUtils.isNotBlank(parameters.get("publishTime")[0])) {
            String publishTime = parameters.get("publishTime")[0];
            publishTime =
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(publishTime, DateUtil.DATE_FORMAT_FULL), -8));
            objBody.put("publishTime", publishTime);
        }
        if (parameters.get("stoperId") != null && parameters.get("stoperId").length != 0
            && StringUtils.isNotBlank(parameters.get("stoperId")[0])) {
            objBody.put("stoperId", parameters.get("stoperId")[0]);
        }
        if (parameters.get("stopTime") != null && parameters.get("stopTime").length != 0
            && StringUtils.isNotBlank(parameters.get("stopTime")[0])) {
            String stopTime = parameters.get("stopTime")[0];
            stopTime =
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(stopTime, DateUtil.DATE_FORMAT_FULL), -8));
            objBody.put("stopTime", stopTime);
        }
        if (parameters.get("fileName") != null && parameters.get("fileName").length != 0
            && StringUtils.isNotBlank(parameters.get("fileName")[0])) {
            objBody.put("fileName", parameters.get("fileName")[0]);
        }
        if (parameters.get("belongFieldIds") != null && parameters.get("belongFieldIds").length != 0
            && StringUtils.isNotBlank(parameters.get("belongFieldIds")[0])) {
            objBody.put("belongFieldIds", parameters.get("belongFieldIds")[0]);
        }
        if (parameters.get("systemCategoryId") != null && parameters.get("systemCategoryId").length != 0
            && StringUtils.isNotBlank(parameters.get("systemCategoryId")[0])) {
            objBody.put("systemCategoryId", parameters.get("systemCategoryId")[0]);
        }
        if (parameters.get("sendSupplier") != null && parameters.get("sendSupplier").length != 0
            && StringUtils.isNotBlank(parameters.get("sendSupplier")[0])) {
            objBody.put("sendSupplier", parameters.get("sendSupplier")[0]);
        }
        if (parameters.get("cbbh") != null && parameters.get("cbbh").length != 0
            && StringUtils.isNotBlank(parameters.get("cbbh")[0])) {
            objBody.put("cbbh", parameters.get("cbbh")[0]);
        }
        if (parameters.get("yzxcd") != null && parameters.get("yzxcd").length != 0
            && StringUtils.isNotBlank(parameters.get("yzxcd")[0])) {
            objBody.put("yzxcd", parameters.get("yzxcd")[0]);
        }
        if (parameters.get("companyName") != null && parameters.get("companyName").length != 0
            && StringUtils.isNotBlank(parameters.get("companyName")[0])) {
            objBody.put("companyName", parameters.get("companyName")[0]);
        }
        if (parameters.get("banci") != null && parameters.get("banci").length != 0
            && StringUtils.isNotBlank(parameters.get("banci")[0])) {
            objBody.put("banci", parameters.get("banci")[0]);
        }
        if (parameters.get("standardTaskId") != null && parameters.get("standardTaskId").length != 0
            && StringUtils.isNotBlank(parameters.get("standardTaskId")[0])) {
            objBody.put("standardTaskId", parameters.get("standardTaskId")[0]);
        }
        if (parameters.get("categoryName") != null && parameters.get("categoryName").length != 0
            && StringUtils.isNotBlank(parameters.get("categoryName")[0])) {
            objBody.put("categoryName", parameters.get("categoryName")[0]);
        }
        if (parameters.get("belongDepName") != null && parameters.get("belongDepName").length != 0
            && StringUtils.isNotBlank(parameters.get("belongDepName")[0])) {
            objBody.put("belongDepName", parameters.get("belongDepName")[0]);
        }
        if (parameters.get("systemName") != null && parameters.get("systemName").length != 0
            && StringUtils.isNotBlank(parameters.get("systemName")[0])) {
            objBody.put("systemName", parameters.get("systemName")[0]);
        }
        if (parameters.get("replaceNumber") != null && parameters.get("replaceNumber").length != 0
            && StringUtils.isNotBlank(parameters.get("replaceNumber")[0])) {
            objBody.put("replaceNumber", parameters.get("replaceNumber")[0]);
        }
        if (parameters.get("beReplaceNumber") != null && parameters.get("beReplaceNumber").length != 0
            && StringUtils.isNotBlank(parameters.get("beReplaceNumber")[0])) {
            objBody.put("beReplaceNumber", parameters.get("beReplaceNumber")[0]);
        }
        if (parameters.get("publisherName") != null && parameters.get("publisherName").length != 0
            && StringUtils.isNotBlank(parameters.get("publisherName")[0])) {
            objBody.put("publisherName", parameters.get("publisherName")[0]);
        }
        if (parameters.get("stoperName") != null && parameters.get("stoperName").length != 0
            && StringUtils.isNotBlank(parameters.get("stoperName")[0])) {
            objBody.put("stoperName", parameters.get("stoperName")[0]);
        }
        if (parameters.get("belongFieldNames") != null && parameters.get("belongFieldNames").length != 0
            && StringUtils.isNotBlank(parameters.get("belongFieldNames")[0])) {
            objBody.put("belongFieldNames", parameters.get("belongFieldNames")[0]);
        }

    }

    // 保存或者更新
    private void addOrUpdateStandard(Map<String, Object> objBody, MultipartFile fileObj) throws IOException {
        String standardId = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (StringUtils.isBlank(standardId)) {
            // 新增文件
            String newStandardId = IdUtil.getId();
            if (fileObj != null) {
                updateStandardFile2Disk(newStandardId, fileObj);
            }
            objBody.put("id", newStandardId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            standardDao.insertStandard(objBody);
            // 新增或者更新标准-专业对应关系
            String belongFieldIds = null;
            if (objBody.get("belongFieldIds") != null) {
                belongFieldIds = objBody.get("belongFieldIds").toString();
            }
            addOrUpdateStandardFieldRelation(newStandardId, belongFieldIds, "add");
        } else {
            if (fileObj != null) {
                // 更新文件
                updateStandardFile2Disk(standardId, fileObj);
            } else {
                // fileObj为空，有可能是真的没有附件，也有可能是编辑场景（有fileName）
                // 如无fileName则用户前台希望删除该标准的文件，否则说明用户没处理
                String fileName = objBody.get("fileName") == null ? "" : objBody.get("fileName").toString();
                if (StringUtils.isBlank(fileName)) {
                    deleteStandardFileFromDisk(standardId);
                }
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            standardDao.updateStandard(objBody);
            // 新增或者更新标准-专业对应关系
            String belongFieldIds = null;
            if (objBody.get("belongFieldIds") != null) {
                belongFieldIds = objBody.get("belongFieldIds").toString();
            }
            addOrUpdateStandardFieldRelation(standardId, belongFieldIds, "update");
        }
    }

    // 新增或者更新标准-专业对应关系
    private void addOrUpdateStandardFieldRelation(String standardId, String fieldIds, String actionType) {
        if ("update".equalsIgnoreCase(actionType)) {
            // 先删除这个标准关联的专业领域
            JSONObject param = new JSONObject();
            List<String> standardIds = new ArrayList<>();
            standardIds.add(standardId);
            param.put("standardIds", standardIds);
            standardFieldManagerDao.deleteStandardFieldRelaByStandard(param);
        }
        // 新增标准-专业对应关系
        if (StringUtils.isBlank(fieldIds)) {
            return;
        }
        String[] fieldIdArr = fieldIds.split(",", -1);
        List<JSONObject> insertData = new ArrayList<>();
        for (String fieldId : fieldIdArr) {
            JSONObject oneObj = new JSONObject();
            oneObj.put("standardId", standardId);
            oneObj.put("fieldId", fieldId);
            insertData.add(oneObj);
        }
        standardFieldManagerDao.batchInsertStandardFieldRela(insertData);
    }

    private void updateStandardFile2Disk(String standardId, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(standardId) || fileObj == null) {
            logger.warn("no standardId or fileObj");
            return;
        }
        String standardFilePathBase = WebAppUtil.getProperty("standardFilePathBase");
        String standardFilePathBase_preview = WebAppUtil.getProperty("standardFilePathBase_preview");
        if (StringUtils.isBlank(standardFilePathBase) || StringUtils.isBlank(standardFilePathBase_preview)) {
            logger.error("can't find standardFilePathBase or standardFilePathBase_preview");
            return;
        }

        // 处理下载目录的更新
        File pathFile = new File(standardFilePathBase);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileFullPath = standardFilePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        FileCopyUtils.copy(fileObj.getBytes(), file);

        // 处理预览目录的更新
        File pathFile_preview = new File(standardFilePathBase_preview);
        // 目录不存在则创建
        if (!pathFile_preview.exists()) {
            pathFile_preview.mkdirs();
        }
        String fileFullPath_preview = standardFilePathBase_preview + File.separator + standardId + ".pdf";
        File file_preview = new File(fileFullPath_preview);
        // 文件存在则更新掉
        if (file_preview.exists()) {
            logger.warn("File " + fileFullPath_preview + " will be deleted");
            file_preview.delete();
        }
        FileCopyUtils.copy(fileObj.getBytes(), file_preview);
    }

    private void deleteStandardFileFromDisk(String standardId) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return;
        }
        String standardFilePathBase = WebAppUtil.getProperty("standardFilePathBase");
        String standardFilePathBase_preview = WebAppUtil.getProperty("standardFilePathBase_preview");
        if (StringUtils.isBlank(standardFilePathBase) || StringUtils.isBlank(standardFilePathBase_preview)) {
            logger.error("can't find standardFilePathBase or standardFilePathBase_preview");
            return;
        }
        // 处理下载目录的删除
        String fileFullPath = standardFilePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        file.delete();
        // 处理预览目录的删除
        String fileFullPath_preview = standardFilePathBase_preview + File.separator + standardId + ".pdf";
        File file_preview = new File(fileFullPath_preview);
        file_preview.delete();
    }

    private void deletePublicStandardFileFromDisk(String standardId) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return;
        }
        String standardFilePathBase = WebAppUtil.getProperty("publicStandardFilePath");
        // 处理下载目录的删除
        String fileFullPath = standardFilePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        file.delete();
    }

    private File findStandardFile(String standardId, String action) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return null;
        }
        String filePathBase = ConstantUtil.DOWNLOAD.equalsIgnoreCase(action)
            ? WebAppUtil.getProperty("standardFilePathBase") : WebAppUtil.getProperty("standardFilePathBase_preview");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    private File findPublicStandardFile(String standardId) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return null;
        }
        String filePathBase = WebAppUtil.getProperty("publicStandardFilePath");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    // 根据id查询标准详情
    public Map<String, Object> queryStandardById(String standardId) {
        Map<String, Object> param = new HashMap<>();
        param.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        param.put("id", standardId);
        Map<String, Object> oneInfo = standardDao.queryStandard(param);
        if (oneInfo.get("CREATE_TIME_") != null) {
            oneInfo.put("CREATE_TIME_", DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
        }
        if (oneInfo.get("UPDATE_TIME_") != null) {
            oneInfo.put("UPDATE_TIME_", DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
        }
        if (oneInfo.get("publishTime") != null) {
            oneInfo.put("publishTime", DateUtil.formatDate((Date)oneInfo.get("publishTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        if (oneInfo.get("stopTime") != null) {
            oneInfo.put("stopTime", DateUtil.formatDate((Date)oneInfo.get("stopTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        Map<String, String> userId2UserName = new HashMap<>();
        getUserId2Name(userId2UserName);
        // 拼接起草人的姓名
        if (oneInfo.get("publisherId") != null) {
            oneInfo.put("publisherName", toGetUserNameByIds(oneInfo.get("publisherId").toString(), userId2UserName));
        }
        // 查询标准关联的专业领域
        List<JSONObject> belongFields = standardFieldManagerDao.queryFieldObjectByStandardId(standardId);
        if (belongFields != null && !belongFields.isEmpty()) {
            StringBuilder fieldIdSb = new StringBuilder();
            StringBuilder fieldNameSb = new StringBuilder();
            for (JSONObject oneField : belongFields) {
                fieldIdSb.append(oneField.getString("fieldId")).append(",");
                fieldNameSb.append(oneField.getString("fieldName")).append(",");
            }
            oneInfo.put("belongFieldIds", fieldIdSb.substring(0, fieldIdSb.length() - 1));
            oneInfo.put("belongFieldNames", fieldNameSb.substring(0, fieldNameSb.length() - 1));
        }
        return oneInfo;
    }

    // 检索标准
    public JsonPageResult<?> queryStandardList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParam = new HashMap<String, Object>();
            String currentUserId = ContextUtil.getCurrentUserId();
            String componentTestId = RequestUtil.getString(request, "componentTestId");// 零部件试验id侵入
            String belongbj = RequestUtil.getString(request, "belongbj");// 马天宇
            String belongId = RequestUtil.getString(request, "belongId");
            String systemCategoryId = RequestUtil.getString(request, "systemCategoryId");
            getQueryStandardParams(request, queryParam);
            String userGroupIdStr = null;
            // 查询当前用户所属的group，并与标准体系中的group作比对，如果体系中的group不为空且有交集，则可见
            Set<String> userGroupIds = new HashSet<>();
            List<JSONObject> userBelongGroups = standardDao.queryUserBelongGroups(currentUserId);
            for (JSONObject oneGroup : userBelongGroups) {
                userGroupIds.add(oneGroup.getString("PARTY1_"));
            }
            StringBuilder groupIdSb = new StringBuilder();
            for (String groupId : userGroupIds) {
                groupIdSb.append(groupId).append(",");
            }
            if (groupIdSb.length() > 0) {
                userGroupIdStr = groupIdSb.substring(0, groupIdSb.length() - 1);
            }
            if (StringUtils.isNotBlank(userGroupIdStr)) {
                queryParam.put("userGroupIds", userGroupIdStr);
            }
            queryParam.put("currentUserId", ContextUtil.getCurrentUserId());
            if (StringUtils.isNotBlank(componentTestId)) {// 零部件试验id侵入
                queryParam.put("componentTestId", componentTestId);
            }
            if (StringUtils.isNotBlank(belongbj)) {
                queryParam.put("belongbj", belongbj);
            }
            if (StringUtils.isNotBlank(belongId)) {
                queryParam.put("belongId", belongId);
            }
            if (StringUtils.isNotBlank(belongId)) {
                queryParam.put("systemCategoryId", systemCategoryId);
            }
            queryParam.put("currentYear",DateFormatUtil.format(new Date(),"yyyy"));
            List<Map<String, Object>> queryPageResult = standardDao.queryStandardList(queryParam);
            Map<String, String> userId2UserName = new HashMap<>();
            getUserId2Name(userId2UserName);
            if (queryPageResult != null && !queryPageResult.isEmpty()) {
                List<String> standardIds = new ArrayList<>();
                Iterator iterator = queryPageResult.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> oneInfo = (Map<String, Object>)iterator.next();
                    // 查看对应的标准全文是否存在
                    boolean existFile = checkStandardFileExist(oneInfo.get("id").toString(), false);
                    oneInfo.put("existFile", existFile);
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("publishTime") != null) {
                        oneInfo.put("publishTime",
                            DateUtil.formatDate((Date)oneInfo.get("publishTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("stopTime") != null) {
                        oneInfo.put("stopTime",
                            DateUtil.formatDate((Date)oneInfo.get("stopTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    // 拼接起草人的姓名
                    if (oneInfo.get("publisherId") != null) {
                        oneInfo.put("publisherName",
                            toGetUserNameByIds(oneInfo.get("publisherId").toString(), userId2UserName));
                    }
                    // 替换创建人和废止人的姓名
                    if (oneInfo.get("stoperId") != null) {
                        oneInfo.put("stoperName",
                            toGetUserNameByIds(oneInfo.get("stoperId").toString(), userId2UserName));
                    }
                    if (oneInfo.get("CREATE_BY_") != null) {
                        oneInfo.put("creator",
                            toGetUserNameByIds(oneInfo.get("CREATE_BY_").toString(), userId2UserName));
                    }
                    standardIds.add(oneInfo.get("id").toString());
                }
                // 批量查询标准对应的专业领域信息
                if (!standardIds.isEmpty()) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("standardIds", standardIds);
                    List<JSONObject> fields = standardFieldManagerDao.batchQueryFieldByStandardIds(param);
                    Map<String, List<JSONObject>> standardId2FieldList = new HashMap<>();
                    for (JSONObject oneField : fields) {
                        String standardId = oneField.getString("standardId");
                        if (!standardId2FieldList.containsKey(standardId)) {
                            standardId2FieldList.put(standardId, new ArrayList<>());
                        }
                        standardId2FieldList.get(standardId).add(oneField);
                    }
                    // 遍历标准进行专业领域字段的赋值
                    for (Map<String, Object> oneStandard : queryPageResult) {
                        String standardId = oneStandard.get("id").toString();
                        if (!standardId2FieldList.containsKey(standardId)) {
                            continue;
                        }
                        List<JSONObject> belongFields = standardId2FieldList.get(standardId);
                        if (belongFields != null && !belongFields.isEmpty()) {
                            StringBuilder fieldIdSb = new StringBuilder();
                            StringBuilder fieldNameSb = new StringBuilder();
                            for (JSONObject oneField : belongFields) {
                                fieldIdSb.append(oneField.getString("fieldId")).append(",");
                                fieldNameSb.append(oneField.getString("fieldName")).append(",");
                            }
                            oneStandard.put("belongFieldIds", fieldIdSb.substring(0, fieldIdSb.length() - 1));
                            oneStandard.put("belongFieldNames", fieldNameSb.substring(0, fieldNameSb.length() - 1));
                        }
                    }
                }
            }
            result.setData(queryPageResult);
            // 查询标准执行自查情况
            queryStandardDoCheckStatus(queryPageResult);
            // 查询总数
            queryParam.remove("startIndex");
            queryParam.remove("pageSize");
            int totalResult = standardDao.countStandardList(queryParam);
            result.setTotal(totalResult);
        } catch (Exception e) {
            logger.error("Exception in queryStandard", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    // 查询企业标准在本年度的执行自查流程情况，SUCCESS_END的为已完成，RUNNING的为进行中，其他的都是未开始
    private void queryStandardDoCheckStatus(List<Map<String, Object>> queryPageResult) {
        Map<String, Map<String, Object>> needQueryStandardId2Obj = new HashMap<>();
        for (Map<String, Object> oneData : queryPageResult) {
            if ("企业标准".equalsIgnoreCase(oneData.get("categoryName").toString())) {
                oneData.put("yearDoCheckStatus", "未开始");
                needQueryStandardId2Obj.put(oneData.get("id").toString(), oneData);
            }
        }
        if(needQueryStandardId2Obj.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("standardIds", needQueryStandardId2Obj.keySet());
        param.put("doCheckYear", DateFormatUtil.format(new Date(), "yyyy"));
        List<JSONObject> statusData = standardDoCheckDao.queryDoCheckStatusByStandardIds(param);
        if (statusData != null && !statusData.isEmpty()) {
            for (JSONObject oneData : statusData) {
                String standardId = oneData.getString("standardId");
                String status = oneData.getString("status");
                String yearDoCheckStatus = "未开始";
                if (status.equalsIgnoreCase("SUCCESS_END")) {
                    yearDoCheckStatus = "已完成";
                } else if (status.equalsIgnoreCase("RUNNING")) {
                    yearDoCheckStatus = "进行中";
                }
                needQueryStandardId2Obj.get(standardId).put("yearDoCheckStatus", yearDoCheckStatus);
            }
        }
    }

    // 通过userId逗号分隔字符串获得userName逗号分隔字符串
    private String toGetUserNameByIds(String userIdStr, Map<String, String> userId2UserName) {
        StringBuilder userNameSb = new StringBuilder();
        if (StringUtils.isBlank(userIdStr)) {
            return "";
        }
        String[] userIdArr = userIdStr.split(",", -1);
        for (String userId : userIdArr) {
            if (userId2UserName.containsKey(userId)) {
                userNameSb.append(userId2UserName.get(userId)).append(",");
            }
        }
        return userNameSb.substring(0, userNameSb.length() - 1);
    }

    private boolean checkStandardFileExist(String standardId, boolean isPublicStandard) {
        File file = null;
        if (!isPublicStandard) {
            file = findStandardFile(standardId, ConstantUtil.DOWNLOAD);
        } else {
            file = findPublicStandardFile(standardId);
        }
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    // 拼接请求参数和分页排序参数
    private void getQueryStandardParams(HttpServletRequest request, Map<String, Object> queryParam) {
        queryParam.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 分页
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        queryParam.put("startIndex", "0" + pageIndex * pageSize);
        queryParam.put("pageSize", "0" + pageSize);
        // 排序
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            queryParam.put("sortField", sortField);
            queryParam.put("sortOrder", sortOrder);
        }
        // 过滤条件
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("publishTimeFrom".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("publishTimeTo".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("systemIds".equalsIgnoreCase(name)) {
                        JSONArray systemIdArr = JSONArray.parseArray(value);
                        if (systemIdArr != null && !systemIdArr.isEmpty()) {
                            queryParam.put(name, systemIdArr.toJavaList(String.class));
                        }
                    } else {
                        queryParam.put(name, value);
                    }
                }
            }
        }
    }

    // 标准下载(由于出现了技术网上传的原文没有正常解密的情况，因此为了下载加水印，所以从预览文件夹下载)
    public ResponseEntity<byte[]> download(HttpServletRequest request, String standardId, String standardName,
        String standardNumber) {
        try {
            if (StringUtils.isBlank(standardId)) {
                logger.error("standardId is blank");
                return null;
            }
            String standardFilePathBase = WebAppUtil.getProperty("standardFilePathBase_preview");
            /*            if (!StandardManagerUtil.judgeGLNetwork(request)) {
                standardFilePathBase = WebAppUtil.getProperty("standardFilePathBase_preview");
            }*/
            if (StringUtils.isBlank(standardFilePathBase)) {
                logger.error("can't find standardFilePathBase");
                return null;
            }
            String fileName = standardId + ".pdf";
            String originalPdfFullFilePath = standardFilePathBase + File.separator + fileName;
            File originalPdfFile = new File(originalPdfFullFilePath);
            if (!originalPdfFile.exists()) {
                logger.error("can't find originalPdfFile " + originalPdfFullFilePath);
                return null;
            }
            String waterMarkPdfFullFilePath =
                standardFilePathBase + File.separator + StandardConstant.PROCESSTMPDIR + File.separator + fileName;
            File tmpDir = new File(standardFilePathBase + File.separator + StandardConstant.PROCESSTMPDIR);
            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }
            // 删除临时目录中水印文件
            PdfWaterMarkProcess.waterMarkPdfDelete(waterMarkPdfFullFilePath);
            // 生成新的带水印文件
            PdfWaterMarkProcess.waterMarkPdfGenerate(originalPdfFullFilePath, waterMarkPdfFullFilePath);
            // 创建文件实例
            File file = new File(waterMarkPdfFullFilePath);
            byte[] fileByteArr = new byte[0];
            if (!file.exists() || file.length() == 0) {
                // 水印文件不存在，则下载原有文件
                logger.error("file " + waterMarkPdfFullFilePath + " not exists!");
                fileByteArr = FileUtils.readFileToByteArray(originalPdfFile);
            } else {
                fileByteArr = FileUtils.readFileToByteArray(file);
            }

            // 下载文件的名字强制为“编号 标准名.pdf”修改文件名的编码格式
            String downloadFileName = standardNumber + " " + standardName + ".pdf";
            String finalDownloadFileName = new String(downloadFileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(fileByteArr, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in standard file download", e);
            return null;
        }
    }

    // 标准预览
    public void preview(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        File file = findStandardFile(standardId, ConstantUtil.PREVIEW);
        if (file != null && file.exists()) {
            byte[] data = null;
            try {
                FileInputStream input = new FileInputStream(file);
                data = new byte[input.available()];
                input.read(data);
                response.getOutputStream().write(data);
                input.close();
            } catch (Exception e) {
                logger.error("标准" + standardId + "文件处理异常：" + e.getMessage());
            }
        }
    }

    // 导出标准excel
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        getQueryStandardParams(request, queryParam);
        queryParam.remove("startIndex");
        queryParam.remove("pageSize");
        queryParam.remove("sortField");
        queryParam.remove("sortOrder");
        String userGroupIdStr = null;
        // 查询当前用户所属的group，并与标准体系中的group作比对，如果体系中的group不为空且有交集，则可见
        Set<String> userGroupIds = new HashSet<>();
        List<JSONObject> userBelongGroups = standardDao.queryUserBelongGroups(ContextUtil.getCurrentUserId());
        for (JSONObject oneGroup : userBelongGroups) {
            userGroupIds.add(oneGroup.getString("PARTY1_"));
        }
        StringBuilder groupIdSb = new StringBuilder();
        for (String groupId : userGroupIds) {
            groupIdSb.append(groupId).append(",");
        }
        if (groupIdSb.length() > 0) {
            userGroupIdStr = groupIdSb.substring(0, groupIdSb.length() - 1);
        }
        if (StringUtils.isNotBlank(userGroupIdStr)) {
            queryParam.put("userGroupIds", userGroupIdStr);
        }
        queryParam.put("currentUserId", ContextUtil.getCurrentUserId());
        queryParam.put("currentYear",DateFormatUtil.format(new Date(),"yyyy"));
        List<Map<String, Object>> queryPageResult = standardDao.queryStandardList(queryParam);
        Map<String, String> userId2UserName = new HashMap<>();
        getUserId2Name(userId2UserName);
        if (queryPageResult != null && !queryPageResult.isEmpty()) {
            List<String> standardIds = new ArrayList<>();
            Iterator iterator = queryPageResult.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> oneInfo = (Map<String, Object>)iterator.next();
                if (oneInfo.get("publishTime") != null) {
                    oneInfo.put("publishTime", DateUtil.formatDate((Date)oneInfo.get("publishTime"), "yyyy-MM-dd"));
                }
                if (oneInfo.get("stopTime") != null) {
                    oneInfo.put("stopTime", DateUtil.formatDate((Date)oneInfo.get("stopTime"), "yyyy-MM-dd"));
                }
                if (oneInfo.get("standardStatus") != null) {
                    oneInfo.put("standardStatusName", getStatusName(oneInfo.get("standardStatus").toString()));
                }
                // 拼接起草人的姓名
                if (oneInfo.get("publisherId") != null) {
                    oneInfo.put("publisherName",
                        toGetUserNameByIds(oneInfo.get("publisherId").toString(), userId2UserName));
                }
                // 替换创建人和废止人的姓名
                if (oneInfo.get("stoperId") != null) {
                    oneInfo.put("stoperName", toGetUserNameByIds(oneInfo.get("stoperId").toString(), userId2UserName));
                }
                if (oneInfo.get("CREATE_BY_") != null) {
                    oneInfo.put("creator", toGetUserNameByIds(oneInfo.get("CREATE_BY_").toString(), userId2UserName));
                }
                if (oneInfo.get("sendSupplier") != null
                    && "true".equalsIgnoreCase(oneInfo.get("sendSupplier").toString())) {
                    oneInfo.put("sendSupplier", "是");
                } else {
                    oneInfo.put("sendSupplier", "否");
                }

                standardIds.add(oneInfo.get("id").toString());
            }
            // 批量查询标准对应的专业领域信息
            if (!standardIds.isEmpty()) {
                Map<String, Object> param = new HashMap<>();
                param.put("standardIds", standardIds);
                List<JSONObject> fields = standardFieldManagerDao.batchQueryFieldByStandardIds(param);
                Map<String, List<JSONObject>> standardId2FieldList = new HashMap<>();
                for (JSONObject oneField : fields) {
                    String standardId = oneField.getString("standardId");
                    if (!standardId2FieldList.containsKey(standardId)) {
                        standardId2FieldList.put(standardId, new ArrayList<>());
                    }
                    standardId2FieldList.get(standardId).add(oneField);
                }
                // 遍历标准进行专业领域字段的赋值
                for (Map<String, Object> oneStandard : queryPageResult) {
                    String standardId = oneStandard.get("id").toString();
                    if (!standardId2FieldList.containsKey(standardId)) {
                        continue;
                    }
                    List<JSONObject> belongFields = standardId2FieldList.get(standardId);
                    if (belongFields != null && !belongFields.isEmpty()) {
                        StringBuilder fieldIdSb = new StringBuilder();
                        StringBuilder fieldNameSb = new StringBuilder();
                        for (JSONObject oneField : belongFields) {
                            fieldIdSb.append(oneField.getString("fieldId")).append(",");
                            fieldNameSb.append(oneField.getString("fieldName")).append(",");
                        }
                        oneStandard.put("belongFieldIds", fieldIdSb.substring(0, fieldIdSb.length() - 1));
                        oneStandard.put("belongFieldNames", fieldNameSb.substring(0, fieldNameSb.length() - 1));
                    }
                }
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String systemCategoryId = queryParam.get("systemCategoryId") == null ? StandardConstant.SYSTEMCATEGORY_JS
            : queryParam.get("systemCategoryId").toString();
        String title = StandardConstant.SYSTEMCATEGORY_JS.equalsIgnoreCase(systemCategoryId) ? "技术标准资源列表" : "管理标准资源列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"标准所属体系名称", "标准体系编号", "标准类别", "版次", "标准编号", "标准名称", "标准归口部门", "专业领域", "采标编号", "一致性程度代号",
            "替代标准编号", "被替代标准编号", "标准状态", "起草人", "发送供应商", "发布人", "发布时间", "废止人", "废止时间"};
        String[] fieldCodes = {"systemName", "systemNumber", "categoryName", "banci", "standardNumber", "standardName",
            "belongDepName", "belongFieldNames", "cbbh", "yzxcd", "replaceNumber", "beReplaceNumber",
            "standardStatusName", "publisherName", "sendSupplier", "creator", "publishTime", "stoperName", "stopTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryPageResult, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public static String getStatusName(String status) {
        if (StringUtils.isBlank(status)) {
            return "";
        }
        switch (status) {
            case "draft":
                return "草稿";
            case "enable":
                return "有效";
            case "disable":
                return "已废止";
            default:
                return "";
        }

    }

    // 标准导入模板下载
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "标准导入模板.xls";
            // 创建文件实例
            File file = new File(
                StandardManager.class.getClassLoader().getResource("templates/standardManager/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importTemplateDownload", e);
            return null;
        }
    }

    // 批量导入标准
    public void importStandard(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("standardImportFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            HSSFWorkbook wb = new HSSFWorkbook(fileObj.getInputStream());
            HSSFSheet sheet = wb.getSheet("标准批量导入");
            if (sheet == null) {
                logger.error("找不到标准批量导入页");
                result.put("message", "数据导入失败，找不到标准批量导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            HSSFRow titleRow = sheet.getRow(0);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }

            // 验证导入的数据中“所属体系编号”、“标准类别名称”、“归口部门名称”、“标准状态”、“替代标准”、“被替代标准”、"起草人账号"、“废止人账号”
            Map<String, String> systemNumber2Id = new HashMap<>();
            Map<String, String> categoryName2Id = new HashMap<>();
            Map<String, String> belongDepName2Id = new HashMap<>();
            Map<String, String> statusName2Id = new HashMap<>();
            Map<String, String> standardNumber2Id = new HashMap<>();
            Map<String, String> userNo2UserId = new HashMap<>();
            Map<String, Object> standardCategoryAndDepInfos = getStandardSelectInfos(null);
            Map<String, String> systemCategoryName2Id = new HashMap<>();
            Map<String, String> fieldName2Id = new HashMap<>();

            getFieldName2Id(fieldName2Id);
            getSystemCategoryName2Id(systemCategoryName2Id);
            getStandardStatusName2Id(statusName2Id);
            getSystemNumber2Id(systemNumber2Id);
            getCategoryName2Id(categoryName2Id, (List<Map<String, Object>>)standardCategoryAndDepInfos.get("category"));
            getBelongDepName2Id(belongDepName2Id,
                (List<Map<String, Object>>)standardCategoryAndDepInfos.get("belongDep"));

            getStandardNumber2Id(standardNumber2Id);
            getUserNo2Id(userNo2UserId);

            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                HSSFRow row = sheet.getRow(i);
                JSONObject rowParse =
                    generateDataFromRow(row, dataInsert, titleList, systemNumber2Id, categoryName2Id, belongDepName2Id,
                        statusName2Id, standardNumber2Id, userNo2UserId, fieldName2Id, systemCategoryName2Id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            // 重新检查一遍并赋值“替代标准”、“被替代标准”
            for (int i = 0; i < dataInsert.size(); i++) {
                Map<String, Object> oneRowMap = dataInsert.get(i);
                if (oneRowMap.get("replaceNumber") != null
                    && StringUtils.isNotBlank(oneRowMap.get("replaceNumber").toString())) {
                    if (standardNumber2Id.containsKey(oneRowMap.get("replaceNumber").toString())) {
                        oneRowMap.put("replaceId", standardNumber2Id.get(oneRowMap.get("replaceNumber").toString()));
                    } else {
                        // 替换标准不存在
                        result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：替代标准编号不存在");
                        return;
                    }
                }
                if (oneRowMap.get("beReplacedByNumber") != null
                    && StringUtils.isNotBlank(oneRowMap.get("beReplacedByNumber").toString())) {
                    if (standardNumber2Id.containsKey(oneRowMap.get("beReplacedByNumber").toString())) {
                        oneRowMap.put("beReplacedById",
                            standardNumber2Id.get(oneRowMap.get("beReplacedByNumber").toString()));
                    } else {
                        // 被替换标准不存在
                        result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：被替代标准编号不存在");
                        return;
                    }
                }
            }

            // 分批写入数据库(20条一次)
            List<Map<String, Object>> tempInsert = new ArrayList<>();
            List<JSONObject> standardFieldInsert = new ArrayList<>();
            for (int i = 0; i < dataInsert.size(); i++) {
                tempInsert.add(dataInsert.get(i));
                List<String> belongFieldIds = (List<String>)dataInsert.get(i).get("belongFieldIds");
                for (String fieldId : belongFieldIds) {
                    JSONObject oneRela = new JSONObject();
                    oneRela.put("standardId", dataInsert.get(i).get("id").toString());
                    oneRela.put("fieldId", fieldId);
                    standardFieldInsert.add(oneRela);
                }
                if (tempInsert.size() % 20 == 0) {
                    standardDao.batchInsertStandard(tempInsert);
                    tempInsert.clear();
                    if (!standardFieldInsert.isEmpty()) {
                        standardFieldManagerDao.batchInsertStandardFieldRela(standardFieldInsert);
                        standardFieldInsert.clear();
                    }
                }
            }
            if (!tempInsert.isEmpty()) {
                standardDao.batchInsertStandard(tempInsert);
                tempInsert.clear();
                if (!standardFieldInsert.isEmpty()) {
                    standardFieldManagerDao.batchInsertStandardFieldRela(standardFieldInsert);
                    standardFieldInsert.clear();
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importStandard", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    // 数据有效返回true，否则返回false+message
    private JSONObject generateDataFromRow(HSSFRow row, List<Map<String, Object>> dataInsert, List<String> titleList,
        Map<String, String> systemNumber2Id, Map<String, String> categoryName2Id, Map<String, String> belongDepName2Id,
        Map<String, String> statusName2Id, Map<String, String> standardNumber2Id, Map<String, String> userNo2UserId,
        Map<String, String> fieldName2Id, Map<String, String> systemCategoryName2Id) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            HSSFCell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(cell.getStringCellValue());
            }
            switch (title) {
                case "体系类别名称":
                    if (StringUtils.isBlank(cellValue) || !systemCategoryName2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "所属体系类别为空或不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("systemCategoryId", systemCategoryName2Id.get(cellValue));
                    break;
                case "所属体系编号":
                    if (StringUtils.isBlank(cellValue) || !systemNumber2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "所属体系编号为空或不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("systemId", systemNumber2Id.get(cellValue));
                    break;
                case "标准编号":
                    if (StringUtils.isBlank(cellValue) || standardNumber2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "标准编号为空或者在本系统或者本excel中已存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("standardNumber", cellValue);
                    // 生成新的标准Id
                    String id = IdUtil.getId();
                    oneRowMap.put("id", id);
                    standardNumber2Id.put(cellValue, id);
                    break;
                case "标准名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "标准名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("standardName", cellValue);
                    break;
                case "标准类别名称":
                    if (StringUtils.isBlank(cellValue) || !categoryName2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "标准类别为空或不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("standardCategoryId", categoryName2Id.get(cellValue));
                    break;
                case "归口部门名称":
                    // if (StringUtils.isBlank(cellValue) || !belongDepName2Id.containsKey(cellValue)) {
                    // oneRowCheck.put("message", "归口部门为空或不存在");
                    // return oneRowCheck;
                    // }
                    if (StringUtils.isNotBlank(cellValue) && !belongDepName2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "归口部门不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("belongDepId", belongDepName2Id.get(cellValue));
                    break;
                case "标准状态":
                    if (StringUtils.isNotBlank(cellValue) && !statusName2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "标准状态不存在");
                        return oneRowCheck;
                    }
                    if (StringUtils.isBlank(cellValue)) {
                        cellValue = StandardStatus.Draft.getName();
                    }
                    oneRowMap.put("standardStatus", statusName2Id.get(cellValue));
                    break;
                case "起草人账号":
                    // 拼接起草人id
                    StringBuilder userIdSb = new StringBuilder();
                    if (StringUtils.isNotBlank(cellValue)) {
                        String[] userNosArr = cellValue.split(",", -1);
                        for (String userNo : userNosArr) {
                            if (!userNo2UserId.containsKey(userNo)) {
                                oneRowCheck.put("message", "起草人账号不正确，" + userNo + "找不到！");
                                return oneRowCheck;
                            }
                            userIdSb.append(userNo2UserId.get(userNo)).append(",");
                        }
                        cellValue = userIdSb.substring(0, userIdSb.length() - 1);
                    }
                    oneRowMap.put("publisherId", cellValue);
                    break;
                case "发布时间":
                    if (StringUtils.isNotBlank(cellValue)) {
                        cellValue += " 00:00:00";
                        cellValue = parseTime2Utc(cellValue);
                        if (StringUtils.isBlank(cellValue)) {
                            oneRowCheck.put("message", "发布时间格式不正确");
                            return oneRowCheck;
                        }
                    }
                    // 状态为“有效”且发布时间为空，则取当前时间
                    if (StringUtils.isBlank(cellValue) && oneRowMap.get("standardStatus") != null
                        && StandardStatus.Enable.getKey().equals(oneRowMap.get("standardStatus").toString())) {
                        cellValue = XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
                    }
                    if (StringUtils.isBlank(cellValue)) {
                        cellValue = null;
                    }
                    oneRowMap.put("publishTime", cellValue);
                    break;
                case "废止人账号":
                    // 为空且当前状态为“已废止”，则默认为当前用户
                    if (StringUtils.isBlank(cellValue) && oneRowMap.get("standardStatus") != null
                        && StandardStatus.Disable.getKey().equals(oneRowMap.get("standardStatus").toString())) {
                        cellValue = ContextUtil.getCurrentUser().getUserNo();
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (!userNo2UserId.containsKey(cellValue)) {
                            oneRowCheck.put("message", "废止人账号不正确，" + cellValue + "找不到！");
                            return oneRowCheck;
                        }
                        cellValue = userNo2UserId.get(cellValue);
                    }
                    oneRowMap.put("stoperId", cellValue);
                    break;
                case "废止时间":
                    if (StringUtils.isNotBlank(cellValue)) {
                        cellValue += " 00:00:00";
                        cellValue = parseTime2Utc(cellValue);
                        if (StringUtils.isBlank(cellValue)) {
                            oneRowCheck.put("message", "废止时间格式不正确");
                            return oneRowCheck;
                        }
                    }
                    // 状态为“已废止”且废止时间为空，则取当前时间
                    if (StringUtils.isBlank(cellValue) && oneRowMap.get("standardStatus") != null
                        && StandardStatus.Disable.getKey().equals(oneRowMap.get("standardStatus").toString())) {
                        cellValue = XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
                    }
                    if (StringUtils.isBlank(cellValue)) {
                        cellValue = null;
                    }
                    oneRowMap.put("stopTime", cellValue);
                    break;
                case "替代标准编号":
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (standardNumber2Id.containsKey(cellValue)) {
                            oneRowMap.put("replaceId", standardNumber2Id.get(cellValue));
                        } else {
                            oneRowMap.put("replaceNumber", cellValue);
                        }
                    } else {
                        oneRowMap.put("replaceId", "");
                    }
                    break;
                case "被替代标准编号":
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (standardNumber2Id.containsKey(cellValue)) {
                            oneRowMap.put("beReplacedById", standardNumber2Id.get(cellValue));
                        } else {
                            oneRowMap.put("beReplacedByNumber", cellValue);
                        }
                    } else {
                        oneRowMap.put("beReplacedById", "");
                    }
                    break;
                case "所属专业领域名称":
                    if (StringUtils.isBlank(cellValue)) {
                        cellValue = toGetCommonFieldNameByCategoryId(oneRowMap.get("systemCategoryId").toString());
                    }
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "所属专业领域不存在");
                        return oneRowCheck;
                    }
                    String[] fieldNameArr = cellValue.split(",", -1);
                    List<String> belongFieldIds = new ArrayList<>();
                    for (String fieldName : fieldNameArr) {
                        if (fieldName2Id.containsKey(fieldName)) {
                            belongFieldIds.add(fieldName2Id.get(fieldName));
                        }
                    }
                    oneRowMap.put("belongFieldIds", belongFieldIds);
                    break;
                case "是否发送供应商":
                    if ("是".equalsIgnoreCase(cellValue)) {
                        cellValue = "true";
                    } else {
                        cellValue = "false";
                    }
                    oneRowMap.put("sendSupplier", cellValue);
                    break;
                case "采标编号":
                    oneRowMap.put("cbbh", cellValue);
                    break;
                case "一致性程度代号":
                    oneRowMap.put("yzxcd", cellValue);
                    break;
                case "版次":
                    oneRowMap.put("banci", StringUtils.isBlank(cellValue) ? "A0" : cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        dataInsert.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    // 根据体系类别返回“技术通用”/"管理通用"/“内控通用”
    private String toGetCommonFieldNameByCategoryId(String systemCategoryId) {
        switch (systemCategoryId) {
            case "JS":
                return "技术通用";
            case "GL":
                return "管理通用";
            case "NK":
                return "内控通用";
        }
        return "技术通用";
    }

    public static String parseTime2Utc(String timeStr) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        Date date = DateUtil.parseDate(timeStr, DateUtil.DATE_FORMAT_FULL);
        if (date == null) {
            return null;
        }
        return DateUtil.formatDate(DateUtil.addHour(date, -8));
    }

    private void getFieldName2Id(Map<String, String> fieldName2Id) {
        List<JSONObject> result = standardFieldManagerDao.queryFieldObject(new JSONObject());
        if (result != null && !result.isEmpty()) {
            for (JSONObject oneField : result) {
                String id = oneField.getString("fieldId");
                String name = oneField.getString("fieldName");
                if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(name)) {
                    fieldName2Id.put(name, id);
                }
            }
        }

    }

    private void getSystemCategoryName2Id(Map<String, String> systemCategoryName2Id) {
        List<Map<String, Object>> result = standardSystemManager.systemCategoryQuery();
        if (result != null && !result.isEmpty()) {
            for (Map<String, Object> oneMap : result) {
                if (oneMap.get("systemCategoryId") != null && oneMap.get("systemCategoryName") != null) {
                    String id = oneMap.get("systemCategoryId").toString();
                    String name = oneMap.get("systemCategoryName").toString();
                    if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(name)) {
                        systemCategoryName2Id.put(name, id);
                    }
                }
            }
        }

    }

    private void getStandardStatusName2Id(Map<String, String> statusName2Id) {
        for (StandardStatus standardStatus : StandardStatus.values()) {
            statusName2Id.put(standardStatus.getName(), standardStatus.getKey());
        }
    }

    private void getSystemNumber2Id(Map<String, String> systemNumber2Id) {
        List<Map<String, Object>> systemInfos = standardSystemManager.systemQuery(null);
        if (systemInfos != null && !systemInfos.isEmpty()) {
            for (Map<String, Object> oneMap : systemInfos) {
                if (oneMap.get("id") != null && oneMap.get("systemNumber") != null) {
                    String id = oneMap.get("id").toString();
                    String systemNumber = oneMap.get("systemNumber").toString();
                    if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(systemNumber)) {
                        systemNumber2Id.put(systemNumber, id);
                    }
                }
            }
        }
    }

    private void getCategoryName2Id(Map<String, String> categoryName2Id, List<Map<String, Object>> categoryInfos) {
        if (categoryInfos != null && !categoryInfos.isEmpty()) {
            for (Map<String, Object> oneMap : categoryInfos) {
                categoryName2Id.put(oneMap.get("categoryName").toString(), oneMap.get("id").toString());
            }
        }

    }

    private void getBelongDepName2Id(Map<String, String> belongDepName2Id, List<Map<String, Object>> belongDepInfos) {
        if (belongDepInfos != null && !belongDepInfos.isEmpty()) {
            for (Map<String, Object> oneMap : belongDepInfos) {
                belongDepName2Id.put(oneMap.get("belongDepName").toString(), oneMap.get("id").toString());
            }
        }
    }

    private void getFieldName2Id(Map<String, String> fieldName2Id, List<Map<String, Object>> fieldInfos) {
        if (fieldInfos != null && !fieldInfos.isEmpty()) {
            for (Map<String, Object> oneMap : fieldInfos) {
                fieldName2Id.put(oneMap.get("fieldName").toString(), oneMap.get("fieldId").toString());
            }
        }
    }

    private void getStandardNumber2Id(Map<String, String> standardNumber2Id) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> idAndNumbers = standardDao.queryStandardIdNumber(params);
        if (idAndNumbers != null && !idAndNumbers.isEmpty()) {
            for (Map<String, Object> oneMap : idAndNumbers) {
                if (oneMap.get("standardNumber") != null
                    && StringUtils.isNotBlank(oneMap.get("standardNumber").toString())) {
                    standardNumber2Id.put(oneMap.get("standardNumber").toString(), oneMap.get("id").toString());
                }
            }
        }
    }

    private void getUserNo2Id(Map<String, String> userNo2UserId) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userList = standardDao.queryUserList(params);
        if (userList != null && !userList.isEmpty()) {
            for (Map<String, String> oneMap : userList) {
                if (StringUtils.isNotBlank(oneMap.get("userNo")) && StringUtils.isNotBlank(oneMap.get("userId"))) {
                    userNo2UserId.put(oneMap.get("userNo"), oneMap.get("userId"));
                }
            }
        }
    }

    private void getUserId2Name(Map<String, String> userId2UserName) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userList = standardDao.queryUserList(params);
        if (userList != null && !userList.isEmpty()) {
            for (Map<String, String> oneMap : userList) {
                if (StringUtils.isNotBlank(oneMap.get("userName")) && StringUtils.isNotBlank(oneMap.get("userId"))) {
                    userId2UserName.put(oneMap.get("userId"), oneMap.get("userName"));
                }
            }
        }
    }

    public void recordStandardOperate(JSONObject result, String standardId, String action) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("id", IdUtil.getId());
        params.put("checkCategoryId", action);
        params.put("standardId", standardId);
        standardDao.insertStandardRecord(params);
        result.put("message", "记录成功！");
    }

    /**
     * 根据id查询借用产生的标准
     *
     * @param standardFromId
     * @return
     */
    public List<Map<String, Object>> queryStandardBorrowList(String standardFromId) {
        Map<String, Object> params = new HashMap<>();
        List<String> borrowFromIdList = new ArrayList<>();
        borrowFromIdList.add(standardFromId);
        params.put("borrowFromIdList", borrowFromIdList);
        return standardDao.queryStandardBorrowList(params);
    }

    /**
     * 新增标准借用，将标准基本信息及全文附件复制一份到目标体系中。
     *
     * @param postBody
     * @param result
     */
    public void addBorrow(JSONObject postBody, JSONObject result) {
        String borrowFromId = postBody.getString("borrowFromId");
        Map<String, Object> oldStandard = queryStandardById(borrowFromId);
        Map<String, Object> newStandard = new HashMap<>(oldStandard);

        try {
            // 复制借用标准
            newStandard.remove("id");
            newStandard.remove("UPDATE_BY_");
            newStandard.remove("UPDATE_TIME_");
            newStandard.put("systemId", postBody.getString("borrowToSystemId"));
            newStandard.put("borrowFromId", borrowFromId);
            // 从原标准复制一份文件到借用标准
            MultipartFile fileObj = null;
            if (oldStandard.get("fileName") != null && StringUtils.isNotBlank(oldStandard.get("fileName").toString())) {
                File oldStandardFile = findStandardFile(borrowFromId, ConstantUtil.DOWNLOAD);
                if (oldStandardFile != null) {
                    FileInputStream oldFileStream = new FileInputStream(oldStandardFile);
                    fileObj = new MockMultipartFile(oldStandardFile.getName(), oldFileStream);
                } else {
                    logger.error("standard {} file can't find!", borrowFromId);
                }

            }
            addOrUpdateStandard(newStandard, fileObj);

            // 从原标准复制附表到借用标准，数据库同时增加
            String standardAttachFilePath = WebAppUtil.getProperty("standardAttachFilePath");
            if (StringUtils.isNotBlank(standardAttachFilePath)) {
                StringBuilder sourcePathSb = new StringBuilder(standardAttachFilePath);
                sourcePathSb.append(File.separator).append(borrowFromId).append(File.separator);
                StringBuilder targetPathSb = new StringBuilder(standardAttachFilePath);
                targetPathSb.append(File.separator).append(newStandard.get("id").toString()).append(File.separator);
                Map<String, String> oldFile2New = new HashMap<>();
                copyStandardFileInfos(borrowFromId, newStandard.get("id").toString(), oldFile2New);
                FileOperateUtil.copyDirAndReplaceName(sourcePathSb.toString(), targetPathSb.toString(), oldFile2New);
            }
            result.put("message", "操作成功！");
        } catch (Exception e) {
            logger.error("Exception in addBorrow", e);
            result.put("message", "操作失败，系统异常！");
        }
    }

    /**
     * 拷贝数据库中标准附表的数据到借用标准
     *
     * @param sourceStandardId
     * @param targetStandardId
     */
    private void copyStandardFileInfos(String sourceStandardId, String targetStandardId,
        Map<String, String> oldFile2New) {
        Map<String, Object> params = new HashMap<>();
        params.put("standardId", sourceStandardId);
        JSONArray fileArray = standardFileInfosDao.getFiles(params);
        if (fileArray != null) {
            for (int index = 0; index < fileArray.size(); index++) {
                JSONObject oneFileObj = fileArray.getJSONObject(index);
                String oldId = oneFileObj.getString("id");
                String newId = IdUtil.getId();
                oldFile2New.put(oldId, newId);
                oneFileObj.remove("id");
                oneFileObj.remove("standardId");
                oneFileObj.remove("UPDATE_BY_");
                oneFileObj.remove("UPDATE_TIME_");
                oneFileObj.put("id", newId);
                oneFileObj.put("standardId", targetStandardId);
                oneFileObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneFileObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                oneFileObj.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                standardFileInfosDao.addFileInfos(oneFileObj);
            }
        }
    }

    /**
     * 回收借用的标准，直接删除
     *
     * @param standardToId
     * @param result
     */
    public void callBackBorrow(String standardToId, JSONObject result) {
        deleteStandard(result, standardToId);
    }

    // 根据id查询公开标准的详情
    public Map<String, Object> queryPublicStandardById(String standardId) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", standardId);
        Map<String, Object> oneInfo = standardDao.queryPublicStandard(param);
        if (oneInfo.get("publishTime") != null) {
            oneInfo.put("publishTime", DateUtil.formatDate((Date)oneInfo.get("publishTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        return oneInfo;
    }

    // 查询公开标准的列表
    public JsonPageResult<?> queryPublicStandardList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParam = new HashMap<String, Object>();
            getQueryStandardParams(request, queryParam);
            List<Map<String, Object>> queryPageResult = standardDao.queryPublicStandardList(queryParam);
            if (queryPageResult != null && !queryPageResult.isEmpty()) {
                Iterator iterator = queryPageResult.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> oneInfo = (Map<String, Object>)iterator.next();
                    // 查看对应的标准全文是否存在
                    boolean existFile = checkStandardFileExist(oneInfo.get("id").toString(), true);
                    oneInfo.put("existFile", existFile);
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("publishTime") != null) {
                        oneInfo.put("publishTime",
                            DateUtil.formatDate((Date)oneInfo.get("publishTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }

            }
            // 查询总数
            queryParam.remove("startIndex");
            queryParam.remove("pageSize");
            int totalResult = standardDao.countPublicStandardList(queryParam);
            result.setData(queryPageResult);
            result.setTotal(totalResult);
        } catch (Exception e) {
            logger.error("Exception in queryPublicStandardList", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    // 导出标准excel
    public void exportPublicExcel(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        getQueryStandardParams(request, queryParam);
        queryParam.remove("startIndex");
        queryParam.remove("pageSize");
        queryParam.remove("sortField");
        queryParam.remove("sortOrder");
        List<Map<String, Object>> queryPageResult = standardDao.queryPublicStandardList(queryParam);
        if (queryPageResult != null && !queryPageResult.isEmpty()) {
            Iterator iterator = queryPageResult.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> oneInfo = (Map<String, Object>)iterator.next();
                if (oneInfo.get("publishTime") != null) {
                    oneInfo.put("publishTime", DateUtil.formatDate((Date)oneInfo.get("publishTime"), "yyyy-MM-dd"));
                }
                if (oneInfo.get("standardStatus") != null) {
                    oneInfo.put("standardStatusName", getStatusName(oneInfo.get("standardStatus").toString()));
                }
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "产品标准公开列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"企业名称", "标准编号", "标准名称", "标准状态", "发布时间", "创建人"};
        String[] fieldCodes =
            {"companyName", "standardNumber", "standardName", "standardStatusName", "publishTime", "creator"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryPageResult, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    // 删除标准
    public void deletePublicStandard(JSONObject result, String standardIds) {
        try {
            // 删除本体标准
            Map<String, Object> param = new HashMap<>();
            String[] idArr = standardIds.split(",", -1);
            List<String> idList = new ArrayList<String>(Arrays.asList(idArr));
            param.put("ids", idList);
            standardDao.deletePublicStandard(param);
            for (int i = 0; i < idList.size(); i++) {
                deletePublicStandardFileFromDisk(idList.get(i));
            }
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in deletePublicStandard", e);
            result.put("message", "系统异常！");
        }
    }

    // 保存（包括新增保存、编辑保存）
    public void savePublicStandard(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            MultipartFile fileObj = multipartRequest.getFile("standardFile");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }

            Map<String, Object> objBody = new HashMap<>();
            constructParam(parameters, objBody);
            // 新增或者更新本体标准
            addOrUpdatePublicStandard(objBody, fileObj);
            result.put("message", "保存成功！");
            result.put("id", objBody.get("id"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in savePublicStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    private void addOrUpdatePublicStandard(Map<String, Object> objBody, MultipartFile fileObj) throws IOException {
        String standardId = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (StringUtils.isBlank(standardId)) {
            // 新增文件
            String newStandardId = IdUtil.getId();
            if (fileObj != null) {
                updatePublicStandardFile2Disk(newStandardId, fileObj);
            }
            objBody.put("id", newStandardId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            standardDao.insertPublicStandard(objBody);
        } else {
            if (fileObj != null) {
                // 更新文件
                updatePublicStandardFile2Disk(standardId, fileObj);
            } else {
                // fileObj为空，有可能是真的没有附件，也有可能是编辑场景（有fileName）
                // 如无fileName则用户前台希望删除该标准的文件，否则说明用户没处理
                String fileName = objBody.get("fileName") == null ? "" : objBody.get("fileName").toString();
                if (StringUtils.isBlank(fileName)) {
                    deletePublicStandardFileFromDisk(standardId);
                }
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            standardDao.updatePublicStandard(objBody);
        }
    }

    private void updatePublicStandardFile2Disk(String standardId, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(standardId) || fileObj == null) {
            logger.warn("no standardId or fileObj");
            return;
        }
        String standardFilePathBase = WebAppUtil.getProperty("publicStandardFilePath");
        if (StringUtils.isBlank(standardFilePathBase)) {
            logger.error("can't find standardFilePathBase");
            return;
        }

        // 处理下载目录的更新
        File pathFile = new File(standardFilePathBase);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileFullPath = standardFilePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

    // 标准下载(由于出现了技术网上传的原文没有正常解密的情况，因此为了下载加水印，所以技术网的从预览文件夹下载，管理网的从原文件夹下载)
    public ResponseEntity<byte[]> publicDownload(HttpServletRequest request, String standardId, String standardName,
        String standardNumber) {
        try {
            if (StringUtils.isBlank(standardId)) {
                logger.error("standardId is blank");
                return null;
            }
            String standardFilePathBase = WebAppUtil.getProperty("publicStandardFilePath");
            if (StringUtils.isBlank(standardFilePathBase)) {
                logger.error("can't find standardFilePathBase");
                return null;
            }
            String fileName = standardId + ".pdf";
            String originalPdfFullFilePath = standardFilePathBase + File.separator + fileName;
            File originalPdfFile = new File(originalPdfFullFilePath);
            if (!originalPdfFile.exists()) {
                logger.error("can't find originalPdfFile " + originalPdfFullFilePath);
                return null;
            }
            byte[] fileByteArr = new byte[0];
            fileByteArr = FileUtils.readFileToByteArray(originalPdfFile);

            // 下载文件的名字强制为“编号 标准名.pdf”修改文件名的编码格式
            String downloadFileName = standardNumber + " " + standardName + ".pdf";
            String finalDownloadFileName = new String(downloadFileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(fileByteArr, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in publicDownload", e);
            return null;
        }
    }

    // 标准预览
    public void publicPreview(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        File file = findPublicStandardFile(standardId);
        if (file != null && file.exists()) {
            byte[] data = null;
            try {
                FileInputStream input = new FileInputStream(file);
                data = new byte[input.available()];
                input.read(data);
                response.getOutputStream().write(data);
                input.close();
            } catch (Exception e) {
                logger.error("标准" + standardId + "文件处理异常：" + e.getMessage());
            }
        }
    }

    public void createCollect(JSONObject formData) {
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        standardDao.createCollect(formData);
    }

    public JsonResult deleteCollect(String[] jsIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String jsId : jsIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("standardId", jsId);
            param.put("belongId", ContextUtil.getCurrentUser().getUserId());
            standardDao.deleteCollect(param);
        }
        return result;
    }

    /**
     * 查看当前体系下的、有效的标准编号中顺序占位的数值
     * Q/XGWJ 2A112-2020
     * @param systemId
     * @return
     */
    public String getCurrentSystemStandardSeqNum(String systemId){
        Map<String, Object> param = new HashMap<>();
        param.put("systemId", systemId);
        Integer newSeqNum = 0;
        String resNum = "";
        try {
//        测试所有管理的数据长度
            List<String> seqNumList = standardDao.getCurrentSystemStandardSeqNum(param);
            for (String oneNum : seqNumList) {
                int lastIndex = oneNum.split(" ").length - 1;
                String numPart = oneNum.split(" ")[lastIndex];
                String middlePart = numPart.split("-")[0];
                String seqNum = middlePart.substring(3, middlePart.length());
                newSeqNum = Integer.max(newSeqNum, Integer.parseInt(seqNum));
            }
            newSeqNum = newSeqNum + 1;
            if (newSeqNum < 10) {
                resNum = "0" + newSeqNum.toString();
            } else if (newSeqNum >= 10 && newSeqNum < 100) {
                resNum = newSeqNum.toString();
            } else {//当前序列编号只考虑两位
                resNum = "";
            }
        } catch (Exception e) {
            logger.error("Exception in getCurrentSystemStandardSeqNum", e);
        }
        return resNum;
    }
    public JsonResult autoGenerateStandardNum(JsonResult result,String systemId) {
        // 生成标准号
        String autoGenStdNum = "";
        // 根据systemId查询targetNode
        Map<String, Object> param = new HashMap<>();
        param.put("systemId", systemId);
        List<Map<String, Object>> targetNodeList = standardSystemDao.querySystem(param);
        if (targetNodeList != null && !targetNodeList.isEmpty()) {
//            根据systemId如果有结果应只有一个体系
            Map<String, Object> targetNode = targetNodeList.get(0);
            String systemName = targetNode.get("systemName").toString();
            String systemNumber = targetNode.get("systemNumber").toString();
            String systemCode = targetNode.get("systemCode").toString();
            if (systemCode.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("所属体系未设置“类目代号”，无法自动生成标准编号！");
                return result;
            }
            // 企业标注代号Q/
            String indexFir = "Q/";
            String indexSec = "";
            // 标注分类号：2（管理）
            String indexThr = "XGWJJS05".equals(systemNumber) ? "5" : "2";
            String indexFor = "";
            // 顺序号：数据库中获取
            String indexFif = getCurrentSystemStandardSeqNum(systemId);
            if (indexFif.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("所选体系获取顺序号失败！");
                return result;
            }
            Year currentYear = Year.now();
            // 年代号：当前年
            String indexSix = currentYear.toString();

            // 企业名称代号：XGWJ;//XGWJJS05：技术中心内部管理标准
            if (systemName.indexOf("XGWJ") != -1 || "XGWJJS05".equals(systemNumber)) {
                indexSec = "XGWJ";
                //通联租赁
            } else if (systemName.indexOf("XGTL") != -1) {
                indexSec = "XGTL";
            } else {
                result.setSuccess(false);
                result.setMessage("当前体系暂不支持自动生成编号，请手动填写！");
                return result;
            }

            // 标注类目代号：targetNode.systemCode;
            if (systemCode.length() == 1){
                indexFor = systemCode+"0";
            } else if (systemCode.length() == 2) {
                indexFor = systemCode;
            } else {
                result.setSuccess(false);
                result.setMessage("新增体系，请确认标准编号中企业名称代号为XGWJ是否有误！");
                return result;
            }

            // Q/XGWJ 2类目代号顺序号-YYYY
            autoGenStdNum = indexFir + indexSec+" "+ indexThr + indexFor + indexFif + "-" + indexSix;
        } else {
            result.setSuccess(false);
            result.setMessage(String.format("无法查询到编号：%s对应的体系！", systemId));
            return result;
        }
        result.setData(autoGenStdNum);
        return result;
    }
}
