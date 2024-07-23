package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.SchematicApplyDao;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
@Slf4j
public class SchematicApplyService {
    private Logger logger = LogManager.getLogger(SchematicApplyService.class);
    @Autowired
    private SchematicApplyDao schematicApplyDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> querySchematicApply(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "schematicApply_baseinfo.CREATE_TIME_");
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
        rdmZhglUtil.addPage(request, params);
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        // addSchematicApplyRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> schematicApplyList = schematicApplyDao.querySchematicApply(params);
        for (JSONObject schematicApply : schematicApplyList) {
            if (schematicApply.get("CREATE_TIME_") != null) {
                schematicApply.put("CREATE_TIME_", DateUtil.formatDate((Date)schematicApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (schematicApply.get("UPDATE_TIME_") != null) {
                schematicApply.put("UPDATE_TIME_", DateUtil.formatDate((Date)schematicApply.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(schematicApplyList, ContextUtil.getCurrentUserId());
        result.setData(schematicApplyList);
        int schematicApplyListSize = schematicApplyDao.countSchematicApply(params);
        result.setTotal(schematicApplyListSize);
        return result;
    }

    public void createSchematicApply(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        schematicApplyDao.createSchematicApply(formData);
        if (StringUtils.isNotBlank(formData.getString("detail"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("detail"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    schematicApplyDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateSchematicApply(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        schematicApplyDao.updateSchematicApply(formData);
        if (StringUtils.isNotBlank(formData.getString("detail"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("detail"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    schematicApplyDao.createDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    schematicApplyDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    schematicApplyDao.deleteDetail(param);
                    List<String> idsList = Arrays.asList(oneObject.getString("id"));
                    List<JSONObject> detailFiles = getSchematicApplyFileList(idsList);//原理图附件
                    for (JSONObject oneFile : detailFiles) {
                        rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                                oneFile.getString("belongId"), SysPropertiesUtil.getGlobalProperty("schematicApplyFilePathBase"));
                    }
                    for (String oneJstbId : idsList) {
                        rdmZhglFileManager.deleteDirFromDisk(oneJstbId, SysPropertiesUtil.getGlobalProperty("schematicApplyFilePathBase"));
                    }
                    param.clear();
                    param.put("ids", idsList);
                    schematicApplyDao.deleteSchematicApplyFile(param);
                }
            }
        }
    }

    public List<JSONObject> queryDetail(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> detailList = schematicApplyDao.queryDetail(param);
        return detailList;
    }

    public void saveSchematicApplyUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = SysPropertiesUtil.getGlobalProperty("schematicApplyFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find schematicApplyFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("id"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("fileId", fileId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("belongId", belongId);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileType", fileType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            schematicApplyDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getSchematicApplyDetail(String id) {
        JSONObject detailObj = schematicApplyDao.querySchematicApplyById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate((Date)detailObj.get("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> getSchematicApplyFileList(List<String> idList) {
        List<JSONObject> schematicApplyFileList = new ArrayList<>();
        if(idList!=null&&!idList.isEmpty()){
            Map<String, Object> param = new HashMap<>();
            param.put("ids", idList);
            schematicApplyFileList = schematicApplyDao.querySchematicApplyFileList(param);
        }
        return schematicApplyFileList;
    }

    public void deleteOneSchematicApplyFile(String fileId, String fileName, String id) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, id, SysPropertiesUtil.getGlobalProperty("schematicApplyFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        schematicApplyDao.deleteSchematicApplyFile(param);
    }

    public JsonResult deleteSchematicApply(String[] ids,String instIdStr) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idsList = Arrays.asList(ids);
        List<String> detailIdList = new ArrayList<>();
        List<JSONObject> changeFiles = getSchematicApplyFileList(idsList);//更改通知单附件
        for (String mainId : idsList) {
            List<JSONObject> detailList = queryDetail(mainId);
            for (JSONObject detail : detailList) {
                detailIdList.add(detail.getString("id"));
            }
        }
        Map<String, Object> param = new HashMap<>();
        String schematicApplyFilePathBase = SysPropertiesUtil.getGlobalProperty("schematicApplyFilePathBase");
        //删除原理图附件
        if(detailIdList!=null&&!detailIdList.isEmpty()){
            List<JSONObject> detailFiles = getSchematicApplyFileList(detailIdList);//原理图附件
            for (JSONObject oneFile : detailFiles) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                        oneFile.getString("belongId"), schematicApplyFilePathBase);
            }
            for (String oneJstbId : detailIdList) {
                rdmZhglFileManager.deleteDirFromDisk(oneJstbId, schematicApplyFilePathBase);
            }
            param.put("ids", detailIdList);
            schematicApplyDao.deleteSchematicApplyFile(param);//删除原理图附件信息
        }
        //删除更改通知单附件
        for (JSONObject oneFile : changeFiles) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                oneFile.getString("belongId"), schematicApplyFilePathBase);
        }
        for (String oneJstbId : idsList) {
            rdmZhglFileManager.deleteDirFromDisk(oneJstbId, schematicApplyFilePathBase);
        }
        param.clear();
        param.put("ids", idsList);
        schematicApplyDao.deleteSchematicApplyFile(param);//删除更改通知单附件信息
        schematicApplyDao.deleteSchematicApply(param);
        schematicApplyDao.deleteDetail(param);


        //删除流程实例
        if (StringUtils.isNotBlank(instIdStr)) {
            String[] instIds = instIdStr.split(",");
            for (int index = 0; index < instIds.length; index++) {
                bpmInstManager.deleteCascade(instIds[index], "");
            }
        }
        return result;
    }

    public JSONObject checkUploadMds(String id, JSONObject approveValid) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", id);
        List<JSONObject> detailList = schematicApplyDao.queryDetail(param);
        for (JSONObject data : detailList) {
            String detailId = data.getString("id");
            String detailMaterialCode = data.getString("detailMaterialCode");
            List<String> idList = Arrays.asList(detailId);
            List<JSONObject> schematicApplyFileList = getSchematicApplyFileList(idList);
            if(schematicApplyFileList .size() == 0){
                approveValid.put("success", false);
                approveValid.put("message", "原理图物料编码"+detailMaterialCode+"未上传MDS源文件!");
            }
        }
        return approveValid;
    }

    public void exportSchematicApplyList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = querySchematicApply(request, response);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> one:listData){
            if (!one.containsKey("status")){
                one.put("status","");
                one.put("UPDATE_TIME_","");
            }else if(one.get("status").toString().equalsIgnoreCase("DRAFTED")){
                one.put("status","草稿");
                one.put("UPDATE_TIME_","");
            }else if(one.get("status").toString().equalsIgnoreCase("RUNNING")){
                one.put("status","审批中");
                one.put("UPDATE_TIME_","");
            }else if(one.get("status").toString().equalsIgnoreCase("SUCCESS_END")){
                one.put("status","审批完成");
            }else if(one.get("status").toString().equalsIgnoreCase("DISCARD_END")){
                one.put("status","作废");
                one.put("UPDATE_TIME_","");
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "原理图需求申请";
        String excelName = nowDate + title;
        String[] fieldNames = {"单据编号", "销售型号", "物料编码", "设计型号", "销售区域", "收集负责人",
                "申请人", "申请时间", "需求时间","当前任务", "当前处理人","流程状态","流程结束时间","备注"};
        String[] fieldCodes = {"noticeNo", "salesModel", "materialCode", "modelName", "saleArea", "zrrName",
                "creator", "CREATE_TIME_", "needTime", "taskName", "allTaskUserNames", "status", "UPDATE_TIME_",
                "note"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
