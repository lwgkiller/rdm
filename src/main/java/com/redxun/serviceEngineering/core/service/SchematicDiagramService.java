package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.*;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualFileDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.serviceEngineering.core.dao.SchematicDiagramDao;
import com.redxun.sys.core.manager.SysDicManager;
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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

@Service
public class SchematicDiagramService {
    private static Logger logger = LoggerFactory.getLogger(SchematicDiagramService.class);
    public static final String MANUAL_STATUS_EDITING = "编辑中";
    public static final String MANUAL_STATUS_READY = "可转出";
    public static final String MANUAL_STATUS_REVISION = "历史版本";
    @Autowired
    private SchematicDiagramDao schematicDiagramDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        int businessListCount = 0;
        //由于最后三个条件出现后，会调用不同的查询，来避免join的重复结果干扰主查询的结果
        //所以，加了一个隐藏参数isComplex，有最后三个条件就往后端传true，否则就往后端
        //传false，根据这个值来判断调用哪一个查询
        if (!params.containsKey("isComplex") ||
                params.get("isComplex").toString().equalsIgnoreCase("false")) {
            businessList = schematicDiagramDao.dataListQuery(params);
            businessListCount = schematicDiagramDao.countDataListQuery(params);
        } else {
        }
        if (businessList.size() > 0) {
            for (JSONObject jsonObject : businessList) {
                boolean existFile = checkFileExist(jsonObject.getString("id"));
                jsonObject.put("existFile", existFile);
            }
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private boolean checkFileExist(String id) {
        File file = null;
        file = findFile(id);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    //..
    private File findFile(String id) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return null;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "schematicDiagram").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
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
            params.put("sortField", "diagramCode");
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
//        params.put("startIndex", 0);
//        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public void preview(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        File file = findFile(id);
        if (file != null && file.exists()) {
            byte[] data = null;
            try {
                FileInputStream input = new FileInputStream(file);
                data = new byte[input.available()];
                input.read(data);
                response.getOutputStream().write(data);
                input.close();
            } catch (Exception e) {
                logger.error(id + "文件处理异常：" + e.getMessage());
            }
        }
    }

    //..
    public ResponseEntity<byte[]> Download(HttpServletRequest request, String id, String description) {
        try {
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringUploadPosition", "schematicDiagram").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return null;
            }
            String fileName = id + ".pdf";
            String originalPdfFullFilePath = filePathBase + File.separator + fileName;
            File originalPdfFile = new File(originalPdfFullFilePath);
            if (!originalPdfFile.exists()) {
                logger.error("can't find originalPdfFile " + originalPdfFullFilePath);
                return null;
            }
            byte[] fileByteArr = new byte[0];
            fileByteArr = FileUtils.readFileToByteArray(originalPdfFile);

            // 下载文件的名字强制为“编号 标准名.pdf”修改文件名的编码格式
            String downloadFileName = description;
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

    //..
    public JSONObject queryDataById(String id) {
        JSONObject jsonObject = schematicDiagramDao.queryDataById(id);
        return jsonObject;
    }

    //..
    public void deleteBusiness(JSONObject result, String id) {
        try {
            List<String> businessIds = new ArrayList<>();
            businessIds.add(id);
            JSONObject param = new JSONObject();
            param.put("businessIds", businessIds);
            List<JSONObject> files = getFileList(businessIds);
            schematicDiagramDao.deleteBusinessFile(param);
            schematicDiagramDao.deleteBusiness(id);
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringUploadPosition", "schematicDiagram").getValue();
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                        oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
            }
            for (String oneBusinessId : businessIds) {
                rdmZhglFileManager.deleteDirFromDisk(id, filePathBase);
            }
            deleteFileFromDisk(id);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    //..
    private void deleteFileFromDisk(String id) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "schematicDiagram").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        file.delete();
    }

    //..
    public void releaseBusiness(JSONObject result, String id) {
        try {
            //找物，类型，语相同的手册，将它们的状态都变为历史版本；
            //将本条状态变为可转出
            JSONObject jsonObject = schematicDiagramDao.queryDataById(id);
            JSONObject param = new JSONObject();
            param.put("materialCode", jsonObject.getString("materialCode"));
            param.put("diagramType", jsonObject.getString("diagramType"));
            param.put("manualLanguage", jsonObject.getString("manualLanguage"));
            List<JSONObject> jsonObjects = schematicDiagramDao.dataListQuery(param);
            for (JSONObject schematicDiagram : jsonObjects) {
                if (schematicDiagram.getString("manualStatus").equalsIgnoreCase(this.MANUAL_STATUS_READY)) {
                    schematicDiagram.put("manualStatus", this.MANUAL_STATUS_REVISION);
                    schematicDiagram.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    schematicDiagram.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    schematicDiagramDao.updateBusiness(schematicDiagram);
                }
            }
            jsonObject.put("manualStatus", this.MANUAL_STATUS_READY);
            jsonObject.put("keyUserId", ContextUtil.getCurrentUserId());
            jsonObject.put("keyUser", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("publishTime", DateFormatUtil.format(new Date(), "yyyy-MM-dd"));
            schematicDiagramDao.updateBusiness(jsonObject);
            result.put("message", "发布成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    //..
    public void saveBusiness(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            MultipartFile fileObj = multipartRequest.getFile("businessFile");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }

            Map<String, Object> objBody = new HashMap<>();
            constructBusinessParam(parameters, objBody);
            addOrUpdateBusiness(objBody, fileObj);
            result.put("message", "保存成功！");
            result.put("id", objBody.get("id"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in saveBusiness", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    //..
    private void constructBusinessParam(Map<String, String[]> parameters, Map<String, Object> objBody) {
        if (parameters.get("id") != null && parameters.get("id").length != 0
                && StringUtils.isNotBlank(parameters.get("id")[0])) {
            objBody.put("id", parameters.get("id")[0]);
        }
        if (parameters.get("materialCode") != null && parameters.get("materialCode").length != 0
                && StringUtils.isNotBlank(parameters.get("materialCode")[0])) {
            objBody.put("materialCode", parameters.get("materialCode")[0]);
        }
        if (parameters.get("diagramCode") != null && parameters.get("diagramCode").length != 0
                && StringUtils.isNotBlank(parameters.get("diagramCode")[0])) {
            objBody.put("diagramCode", parameters.get("diagramCode")[0]);
        }
        if (parameters.get("diagramType") != null && parameters.get("diagramType").length != 0
                && StringUtils.isNotBlank(parameters.get("diagramType")[0])) {
            objBody.put("diagramType", parameters.get("diagramType")[0]);
        }
        if (parameters.get("repUserId") != null && parameters.get("repUserId").length != 0
                && StringUtils.isNotBlank(parameters.get("repUserId")[0])) {
            objBody.put("repUserId", parameters.get("repUserId")[0]);
        }
        if (parameters.get("repUserName") != null && parameters.get("repUserName").length != 0
                && StringUtils.isNotBlank(parameters.get("repUserName")[0])) {
            objBody.put("repUserName", parameters.get("repUserName")[0]);
        }
        if (parameters.get("manualCode") != null && parameters.get("manualCode").length != 0
                && StringUtils.isNotBlank(parameters.get("manualCode")[0])) {
            objBody.put("manualCode", parameters.get("manualCode")[0]);
        }
        if (parameters.get("manualDescription") != null && parameters.get("manualDescription").length != 0
                && StringUtils.isNotBlank(parameters.get("manualDescription")[0])) {
            objBody.put("manualDescription", parameters.get("manualDescription")[0]);
        }
        if (parameters.get("manualLanguage") != null && parameters.get("manualLanguage").length != 0
                && StringUtils.isNotBlank(parameters.get("manualLanguage")[0])) {
            objBody.put("manualLanguage", parameters.get("manualLanguage")[0]);
        }
        if (parameters.get("manualVersion") != null && parameters.get("manualVersion").length != 0
                && StringUtils.isNotBlank(parameters.get("manualVersion")[0])) {
            objBody.put("manualVersion", parameters.get("manualVersion")[0]);
        }
        if (parameters.get("keyUserId") != null && parameters.get("keyUserId").length != 0
                && StringUtils.isNotBlank(parameters.get("keyUserId")[0])) {
            objBody.put("keyUserId", parameters.get("keyUserId")[0]);
        }
        if (parameters.get("keyUser") != null && parameters.get("keyUser").length != 0
                && StringUtils.isNotBlank(parameters.get("keyUser")[0])) {
            objBody.put("keyUser", parameters.get("keyUser")[0]);
        }
        if (parameters.get("publishTime") != null && parameters.get("publishTime").length != 0
                && StringUtils.isNotBlank(parameters.get("publishTime")[0])) {
            objBody.put("publishTime", parameters.get("publishTime")[0]);
        }
        if (parameters.get("manualStatus") != null && parameters.get("manualStatus").length != 0
                && StringUtils.isNotBlank(parameters.get("manualStatus")[0])) {
            objBody.put("manualStatus", parameters.get("manualStatus")[0]);
        }
        if (parameters.get("note") != null && parameters.get("note").length != 0
                && StringUtils.isNotBlank(parameters.get("note")[0])) {
            objBody.put("note", parameters.get("note")[0]);
        }
        if (parameters.get("itemChangeData") != null && parameters.get("itemChangeData").length != 0
                && StringUtils.isNotBlank(parameters.get("itemChangeData")[0])) {
            String itemChangeDataString = parameters.get("itemChangeData")[0];
            JSONArray jsonArray = JSONObject.parseArray(itemChangeDataString);
            JSONArray jsonArray2 = new JSONArray();
            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                if (!jsonObject.containsKey("_state") ||
                        jsonObject.getString("_state").equalsIgnoreCase("added") ||
                        jsonObject.getString("_state").equalsIgnoreCase("modified")) {
                    jsonObject.remove("_state");
                    jsonObject.remove("_id");
                    jsonObject.remove("_uid");
                    jsonArray2.add(jsonObject);
                }
            }
            objBody.put("remark", jsonArray2.toString());
        }
    }

    //..
    private void addOrUpdateBusiness(Map<String, Object> objBody, MultipartFile fileObj) throws IOException {
        String id = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (StringUtils.isBlank(id)) {
            //新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                updateFile2Disk(newId, fileObj);
                objBody.put("manualDescription", fileObj.getOriginalFilename());
            }
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            objBody.put("manualStatus", this.MANUAL_STATUS_EDITING);
            schematicDiagramDao.insertBusiness(objBody);
        } else {
            if (fileObj != null) {
                updateFile2Disk(id, fileObj);
                objBody.put("manualDescription", fileObj.getOriginalFilename());
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            schematicDiagramDao.updateBusiness(objBody);
        }
    }

    //..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no id or fileObj");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "schematicDiagram").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }

        // 处理下载目录的更新
        File pathFile = new File(filePathBase);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

    //..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = RequestUtil.getParameterValueMap(request, true);
        JsonPageResult result = dataListQuery(request, response);
        List<JSONObject> listData = result.getData();
        List<JSONObject> listDataTrue = new ArrayList<>();
        for (JSONObject jsonObject : listData) {
            if (jsonObject.containsKey("remark")) {
                JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("remark"));
                for (Object o : jsonArray) {
                    JSONObject jsonObjectTrue = (JSONObject) o;
                    jsonObjectTrue.put("materialCode", jsonObject.getString("materialCode"));
                    jsonObjectTrue.put("diagramCode", jsonObject.getString("diagramCode"));
                    jsonObjectTrue.put("diagramType", jsonObject.getString("diagramType"));
                    jsonObjectTrue.put("repUserName", jsonObject.getString("repUserName"));
                    jsonObjectTrue.put("manualCode", jsonObject.getString("manualCode"));
                    jsonObjectTrue.put("manualDescription", jsonObject.getString("manualDescription"));
                    jsonObjectTrue.put("manualLanguage", jsonObject.getString("manualLanguage"));
                    jsonObjectTrue.put("manualVersion", jsonObject.getString("manualVersion"));
                    jsonObjectTrue.put("publishTime", jsonObject.getString("publishTime"));
                    jsonObjectTrue.put("manualStatus", jsonObject.getString("manualStatus"));
                    listDataTrue.add(jsonObjectTrue);
                }
            } else {
                listDataTrue.add(jsonObject);
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "原理图列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"物料编码", "原理图号", "原理图类型", "原理图负责人", "手册编号", "文件名称", "语言",
                "版本", "归档时间", "文件状态", "销售型号", "设计型号", "物料编码", "产品主管"};

        String[] fieldCodes = {"materialCode", "diagramCode", "diagramType", "repUserName", "manualCode", "manualDescription", "manualLanguage",
                "manualVersion", "publishTime", "manualStatus", "salesModel_item", "designModel_item", "materialCode_item", "cpzgId_item_name"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listDataTrue, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
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
                "serviceEngineeringUploadPosition", "schematicDiagram").getValue();
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
            schematicDiagramDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = schematicDiagramDao.queryFileList(param);
        return fileList;
    }

    //..
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "schematicDiagram").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        schematicDiagramDao.deleteBusinessFile(param);
    }

    //..
    public JsonResult doCallBackExiu(String businessId) throws Exception {
        try {
            JsonResult result = new JsonResult(true, "回调成功！");
            JSONObject schematicDiagram = this.queryDataById(businessId);
            if (schematicDiagram == null) {
                result.setSuccess(false);
                result.setMessage("回调失败，没找到相关原理图记录！");
                return result;
            }
            if (!schematicDiagram.containsKey("manualDescription") ||
                    StringUtil.isEmpty(schematicDiagram.getString("manualDescription"))) {
                result.setSuccess(false);
                result.setMessage("回调失败，没找到相关原理图名称！");
                return result;
            }
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringUploadPosition", "schematicDiagram").getValue();//存储路径
            String suffix = CommonFuns.toGetFileSuffix(schematicDiagram.getString("manualDescription"));//扩展名
            String filePathFull = filePathBase + File.separator + businessId + "." + suffix;
            File file = new File(filePathFull);
            if (!file.exists()) {
                result.setSuccess(false);
                result.setMessage("回调失败，没找到相关原理图物理文件！");
                return result;
            }
//            //拼接文件二进制-分段编码
//            ByteArrayOutputStream OutputStream = new ByteArrayOutputStream();
//            InputStream fileStream = new FileInputStream(filePathFull);
//            byte[] byteBuf = new byte[3 * 1024 * 1024];
//            byte[] base64ByteBuf;
//            int count; //每次从文件中读取到的有效字节数
//            while ((count = fileStream.read(byteBuf)) != -1) {
//                if (count != byteBuf.length) {//如果有效字节数不为3*1024*1024，则说明文件已经读到尾了，不够填充满byteBuf了
//                    byte[] copy = Arrays.copyOf(byteBuf, count); //从byteBuf中截取包含有效字节数的字节段
//                    base64ByteBuf = Base64.getEncoder().encode(copy); //对有效字节段进行编码
//                } else {
//                    base64ByteBuf = Base64.getEncoder().encode(byteBuf);
//                }
//                OutputStream.write(base64ByteBuf, 0, base64ByteBuf.length);
//                OutputStream.flush();
//            }
//            fileStream.close();
            //拼接文件二进制-整体编码，也可以，只要java不报错，据说超过71Mjava会报错
            byte[] bytes = new byte[0];
            bytes = Files.readAllBytes(Paths.get(filePathFull));
            String base64 = Base64.getEncoder().encodeToString(bytes);
            //拼接单个属性
            JSONObject postJson = new JSONObject();
            postJson.put("fileBase64", base64);
            postJson.put("manualVersion", schematicDiagram.getString("manualVersion"));
            postJson.put("note", schematicDiagram.getString("note"));
            postJson.put("diagramType", schematicDiagram.getString("diagramType"));
            postJson.put("materialCode", schematicDiagram.getString("materialCode"));
            postJson.put("manualDescription", schematicDiagram.getString("manualDescription"));
            postJson.put("timeStamp", System.currentTimeMillis() / 1000);
            //拼接remark数组属性
            JSONArray remark = JSONArray.parseArray(schematicDiagram.getString("remark"));
            JSONArray remarkFinal = new JSONArray();
            for (Object o : remark) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("materialCode_item", ((JSONObject) o).getString("materialCode_item"));
                remarkFinal.add(jsonObject);
            }
            postJson.put("remark", remarkFinal);
            //调用
            String string = postJson.toJSONString();
            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            String Url = sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringCallbackUrls",
                    "getschematicdesign").getValue();
            String rtnContent = HttpClientUtil.postJson(Url, postJson.toJSONString(), reqHeaders);
            //处理调用结果
            logger.info("response from crm,return message:" + rtnContent);
            if (StringUtils.isBlank(rtnContent)) {
                result.setSuccess(false);
                result.setMessage("回调失败，CRM系统返回值为空！");
                return result;
            }
            JSONObject returnObj = JSONObject.parseObject(rtnContent);
            if ("0".equalsIgnoreCase(returnObj.getJSONObject("Data").getString("isok"))) {
                result.setSuccess(false);
                result.setMessage("回调失败，原因：" + returnObj.getJSONObject("Data").getString("errormsg"));
                return result;
            }
            //一路坎坷走下来了，也就是回调成功了，更新回调状态
            schematicDiagram.put("callBackResult", "是");
            schematicDiagramDao.updateBusiness(schematicDiagram);
            return result;
        } catch (Exception e) {
            logger.error("回调失败：" + e.getMessage(), e);
            throw e;
        }
    }
}
