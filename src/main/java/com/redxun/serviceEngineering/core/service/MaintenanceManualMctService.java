package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.dao.CommonBpmDao;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualMctDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class MaintenanceManualMctService {
    private static Logger logger = LoggerFactory.getLogger(MaintenanceManualMctService.class);
    @Autowired
    private MaintenanceManualMctDao maintenanceManualMctDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private MaintenanceManualDemandDao maintenanceManualDemandDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private BpmTaskManager bpmTaskManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        if (request.getParameter("demandId") != null && !request.getParameter("demandId").equalsIgnoreCase("")) {
            params.put("demandId", request.getParameter("demandId"));
        }
        List<Map<String, Object>> businessList = maintenanceManualMctDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (business.get("UPDATE_TIME_") != null) {
                business.put("UPDATE_TIME_", DateUtil.formatDate((Date) business.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = maintenanceManualMctDao.countDataListQuery(params);
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
            params.put("sortField", "businessStatus");
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
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        List<JSONObject> files = getBusinessFileList(businessIds);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualMct").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        maintenanceManualMctDao.deleteBusinessFile(param);
        maintenanceManualDemandDao.deleteManualMatch(param);
        maintenanceManualMctDao.deleteBusiness(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public List<JSONObject> getBusinessFileList(List<String> businessIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        businessFileList = maintenanceManualMctDao.queryFileList(param);
        return businessFileList;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = maintenanceManualMctDao.queryDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("busunessNo", sysSeqIdManager.genSequenceNo("maintenanceManualMct", ContextUtil.getCurrentTenantId()));
        formData.put("applyUserId", ContextUtil.getCurrentUserId());
        formData.put("applyUser", ContextUtil.getCurrentUser().getFullname());
        formData.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        //@lwgkiller:此处是因为草稿状态无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
        formData.put("businessStatus", "A-editing");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        //基本信息
        maintenanceManualMctDao.insertBusiness(formData);
        //明细信息
        //this.manualMatchProcess(formData.getString("id"), formData.getJSONArray("changeManualMatchListGrid"));
        this.manualMatchProcess(formData);
    }

    //..
    public void updateBusiness(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        //基本信息
        maintenanceManualMctDao.updateBusiness(formData);
        //明细信息
        //this.manualMatchProcess(formData.getString("id"), formData.getJSONArray("changeManualMatchListGrid"));
        this.manualMatchProcess(formData);
        //钉钉钉钉发送预处理
        sendDingDingPri(formData);
    }

    //..钉钉发送预处理
    private void sendDingDingPri(JSONObject formData) {
        JSONObject detail = maintenanceManualMctDao.queryDetailById(formData.getString("id"));
        if (detail.getString("estimateTime") != null &&
                !detail.getString("estimateTime").equalsIgnoreCase(formData.getString("estimateTime"))) {
            Collection<TaskExecutor> collection =
                    bpmTaskManager.getInstNodeUsers(formData.getString("INST_ID_"), "A-editing");
            Collection<TaskExecutor> collection2 =
                    bpmTaskManager.getInstNodeUsers(formData.getString("INST_ID_"), "D-reviewingService");
            StringBuilder stringBuilder = new StringBuilder();
            for (TaskExecutor taskExecutor : collection) {
                stringBuilder.append(taskExecutor.getId()).append(",");
            }
            for (TaskExecutor taskExecutor : collection2) {
                stringBuilder.append(taskExecutor.getId()).append(",");
            }
            String userIds = stringBuilder.substring(0, stringBuilder.length() - 1);
            sendDingDing(formData, userIds);
        }
    }

    //..钉钉发送
    private void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【操保手册预计完成时间变更通知】:编号为 ");
        stringBuilder.append(jsonObject.getString("busunessNo"));
        stringBuilder.append(" 的操保手册新增/变更单的预计完成时间有变化！").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }

    //..
    private void manualMatchProcess(String businessId, JSONArray subArr) {
        if (subArr == null || subArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < subArr.size(); index++) {
            JSONObject oneObject = subArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                //新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("businessId", businessId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                maintenanceManualDemandDao.insertManualMatch(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            maintenanceManualDemandDao.deleteManualMatch(param);
        }
    }

    //..
    private void manualMatchProcess(JSONObject formData) {
        JSONArray subArr = formData.getJSONArray("changeManualMatchListGrid");
        String businessId = formData.getString("id");
        String businessStatus = formData.getString("businessStatus");
        if (subArr == null || subArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < subArr.size(); index++) {
            JSONObject oneObject = subArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                //新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("businessId", businessId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                if (businessStatus.equalsIgnoreCase("A-editing")) {
                    oneObject.put("REF_ID_", "1");
                }
                maintenanceManualDemandDao.insertManualMatch(oneObject);
            } else if ("removed".equals(state)) {
                //删除
                if (businessStatus.equalsIgnoreCase("A-editing")
                        || (!businessStatus.equalsIgnoreCase("A-editing")
                        && !oneObject.containsKey("REF_ID_"))
                        || (!businessStatus.equalsIgnoreCase("A-editing")
                        && !oneObject.getString("REF_ID_").equalsIgnoreCase("1"))) {
                    param.getJSONArray("ids").add(oneObject.getString("id"));
                }
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            maintenanceManualDemandDao.deleteManualMatch(param);
        }
    }

    //..
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualMct").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        maintenanceManualMctDao.deleteBusinessFile(param);
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = maintenanceManualMctDao.queryFileList(param);
        return fileList;
    }

    //..
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualMct").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String businessId = toGetParamVal(parameters.get("businessId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + businessId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("mainId", businessId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            maintenanceManualMctDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..
    public ResponseEntity<byte[]> templateDownload(String type) {
        try {
            String fileName = "CE版操作与保养手册点检表新编.xlsx";
            switch (type) {
                case "新编":
                    fileName = "CE版操作与保养手册点检表新编.xlsx";
                    break;
                case "变更":
                    fileName = "操作与保养手册点检表变更.xlsx";
                    break;
                case "翻译":
                    fileName = "资料翻译申请单.docx";
                    break;
                default:
                    break;
            }
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/serviceEngineering/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in templateDownload", e);
            return null;
        }
    }

    //..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = dataListQueryForExport(request, response);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> map : listData) {
            String businessStatus = map.get("businessStatus") != null ? map.get("businessStatus").toString() : "";
            switch (businessStatus) {
                case "A-editing":
                    map.put("businessStatus", "编辑中");
                    break;
                case "B-proofreading":
                    map.put("businessStatus", "服务工程校对中");
                    break;
                case "C-reviewing":
                    map.put("businessStatus", "产品所长审核中");
                    break;
                case "D-reviewingService":
                    map.put("businessStatus", "服务工程所长审核");
                    break;
                case "E-approving":
                    map.put("businessStatus", "分管领导批准");
                    break;
                case "F-executing":
                    map.put("businessStatus", "执行中");
                    break;
                case "G-close":
                    map.put("businessStatus", "结束-文件归档");
                    break;
            }

            String status = map.get("status") != null ? map.get("status").toString() : "";

            switch (status) {
                case "RUNNING":
                    map.put("status", "运行中");
                    break;
                case "DRAFTED":
                    map.put("status", "草稿");
                    break;
                case "SUCCESS_END":
                    map.put("status", "成功结束");
                    break;
                case "DISCARD_END":
                    map.put("status", "作废");
                    break;
                case "ABORT_END":
                    map.put("status", "异常中止结束");
                    break;
            }


        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "操保手册制作_变更_翻译";
        String excelName = nowDate + title;
        String[] fieldNames = {"流程状态", "单据编号", "需求申请单编号", "原始需求单号", "原始需求异动", "异动处理状态", "申请类型", "销售型号",
                "设计型号", "物料编码", "销售区域", "语言", "是否CE版",
                "申请时间", "需求时间", "变更或翻译的手册编号", "新编参考手册编号", "申请人",
                "申请部门", "审批状态", "当前处理人", "服务工程处理人", "流程结束时间", "备注"};
        String[] fieldCodes = {"status", "busunessNo", "demandBusunessNo", "demandListNo", "demandListStatus", "amHandle", "instructions", "salesModel",
                "designModel", "materialCode", "salesArea", "manualLanguage", "isCE",
                "applyTime", "publishTime", "manualCode", "manualCodeNew", "applyUser",
                "applyDep", "businessStatus", "currentProcessUser", "Bconfirming", "UPDATE_TIME_", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..不分页
    public JsonPageResult<?> dataListQueryForExport(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        int businessListCount = maintenanceManualMctDao.countDataListQuery(params);
        params.put("pageSize", businessListCount);
        List<Map<String, Object>> businessList = maintenanceManualMctDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (business.get("UPDATE_TIME_") != null) {
                business.put("UPDATE_TIME_", DateUtil.formatDate((Date) business.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }
}
