package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.*;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.OverseasProductRectificationDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
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
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OverseasProductRectificationService {
    private static Logger logger = LoggerFactory.getLogger(OverseasProductRectificationService.class);
    private static int EXCEL_ROW_OFFSET = 1;
    @Autowired
    private OverseasProductRectificationDao overseasProductRectificationDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = overseasProductRectificationDao.dataListQuery(params);
        //查询当前处理人
        xcmgProjectManager.setTaskCurrentUserJSON(businessList);
        int businessListCount = overseasProductRectificationDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = overseasProductRectificationDao.queryFileList(param);
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
                "serviceEngineeringUploadPosition", "overseasProductRectification").getValue();
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
            overseasProductRectificationDao.addFileInfos(fileInfo);
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
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "overseasProductRectification").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        overseasProductRectificationDao.deleteBusinessFile(param);
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        List<JSONObject> files = overseasProductRectificationDao.queryFileList(param);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "overseasProductRectification").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        overseasProductRectificationDao.deleteBusinessFile(param);
        overseasProductRectificationDao.deleteBusiness(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = overseasProductRectificationDao.queryDetailById(businessId);
        if (jsonObject == null) {
            JSONObject newjson = new JSONObject();
            newjson.put("applyUserId", ContextUtil.getCurrentUserId());
            newjson.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            newjson.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            newjson.put("recordItems", "[]");
            newjson.put("recordItems2", "[]");
            return newjson;
        }
        return jsonObject;
    }

    //..
    public void createBusiness(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            formData.put("businessStatus", "A");
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            this.processJsonItem(formData);
            this.processJsonItem2(formData);
            overseasProductRectificationDao.insertBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void updateBusiness(JSONObject formData) {
        try {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            this.processJsonItem(formData);
            this.processJsonItem2(formData);
            overseasProductRectificationDao.updateBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
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
    private void processJsonItem2(JSONObject formData) {
        JSONArray jsonArray = new JSONArray();
        if (formData.containsKey("recordItems2") && StringUtil.isNotEmpty(formData.getString("recordItems2"))) {
            String recordItems2 = formData.getString("recordItems2");
            JSONArray jsonArrayItemData = JSONObject.parseArray(recordItems2);
            for (Object itemDataObject : jsonArrayItemData) {
                JSONObject itemDataJson = (JSONObject) itemDataObject;
                if (itemDataJson.containsKey("_state")) {//处理新增和修改
                    if (itemDataJson.getString("_state").equalsIgnoreCase("added")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        itemDataJson.put("id", IdUtil.getId());
                        itemDataJson.put("isSynToGss", "否");
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
            formData.put("recordItems2", jsonArray.toString());
        } else {
            formData.put("recordItems2", "[]");
        }
    }

    //..
    public ResponseEntity<byte[]> importItemTDownload() {
        try {
            String fileName = "海外市场整改物料明细导入模板.xlsx";
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
            logger.error("Exception in importItemTDownload", e);
            return null;
        }
    }

    //..
    public void importItem(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile) fileObj).getFileItem().getName();
            ((CommonsMultipartFile) fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("主表");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < EXCEL_ROW_OFFSET) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(EXCEL_ROW_OFFSET - 1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < EXCEL_ROW_OFFSET + 1) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = EXCEL_ROW_OFFSET; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = this.generateDataFromRow(row, itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            String businessId = RequestUtil.getString(request, "businessId");
            this.importItemProcess(result, itemList, businessId);
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！:" + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
            System.out.println("");
        }
    }

    //..
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "新物料编码":
                    if (StringUtil.isNotEmpty(cellValue)) {
                        oneRowMap.put("materialCodeNew", cellValue.split("\\.")[0]);
                    }
                    break;
                case "新物料名称":
                    oneRowMap.put("materialNameNew", cellValue);
                    break;
                case "新物料数量":
                    oneRowMap.put("materialCountNew", cellValue);
                    break;
                case "旧物料编码":
                    if (StringUtil.isNotEmpty(cellValue)) {
                        oneRowMap.put("materialCodeOld", cellValue.split("\\.")[0]);
                    }
                    break;
                case "旧物料名称":
                    oneRowMap.put("materialNameOld", cellValue);
                    break;
                case "旧物料数量":
                    oneRowMap.put("materialCountOld", cellValue);
                    break;
                case "备注信息":
                    oneRowMap.put("remarks", cellValue);
                    break;
                default:
                    break;
            }
        }
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    private void importItemProcess(JSONObject result, List<Map<String, Object>> itemList,
                                   String businessId) {
        JSONObject supplement = overseasProductRectificationDao.queryDetailById(businessId);
        JSONArray recordItems = supplement.getJSONArray("recordItems2");
        for (Map<String, Object> map : itemList) {
            JSONObject recordItem = new JSONObject(map);
            recordItems.add(recordItem);
        }
        supplement.put("recordItems", recordItems.toJSONString());
        overseasProductRectificationDao.updateBusiness(supplement);
        result.put("message", "数据导入成功！");
    }

    //..
    public ResponseEntity<byte[]> importItemTDownload2() {
        try {
            String fileName = "海外市场整改受影响车辆导入模板.xlsx";
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
            logger.error("Exception in importItemTDownload2", e);
            return null;
        }
    }

    //..
    public void importItem2(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile) fileObj).getFileItem().getName();
            ((CommonsMultipartFile) fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("整机编号");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < EXCEL_ROW_OFFSET) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(EXCEL_ROW_OFFSET - 1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < EXCEL_ROW_OFFSET + 1) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = EXCEL_ROW_OFFSET; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = this.generateDataFromRow2(row, itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            String businessId = RequestUtil.getString(request, "businessId");
            this.importItemProcess2(result, itemList, businessId);
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！:" + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
            System.out.println("");
        }
    }

    //..
    private JSONObject generateDataFromRow2(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "整机编号":
                    oneRowMap.put("pin", cellValue);
                    break;
                default:
                    break;
            }
        }
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    private void importItemProcess2(JSONObject result, List<Map<String, Object>> itemList,
                                    String businessId) {
        JSONObject supplement = overseasProductRectificationDao.queryDetailById(businessId);
        JSONArray recordItems = supplement.getJSONArray("recordItems2");
        for (Map<String, Object> map : itemList) {
            JSONObject recordItem = new JSONObject(map);
            recordItem.put("isSynToGss", "否");
            recordItems.add(recordItem);
        }
        supplement.put("recordItems2", recordItems.toJSONString());
        overseasProductRectificationDao.updateBusiness(supplement);
        result.put("message", "数据导入成功！");
    }

    //..
    private void transJsonItemToFile(JSONObject formData) throws Exception {
        Map<String, JSONObject> pinToFile = new HashedMap();
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "overseasProductRectification").getValue();
        JSONArray recordItems = formData.getJSONArray("recordItems");
        List<JSONObject> recordItemList = new ArrayList<>();
        for (Object recordItem : recordItems) {
            recordItemList.add((JSONObject) recordItem);
        }
        String title = "整改物料明细";
        String[] fieldNames = {"新物料编码", "新物料名称", "数量", "旧物料编码", "旧物料名称",
                "数量", "备注信息"};
        String[] fieldCodes = {"materialCodeNew", "materialNameNew", "materialCountNew", "materialCodeOld", "materialNameOld",
                "materialCountOld", "remarks"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(recordItemList, fieldNames, fieldCodes, title);
        //1.根据formData的id找到所有fileType=autoCreate的文件列表，生成一个map<REF_ID_,file>
        List<String> businessIds = new ArrayList<>();
        businessIds.add(formData.getString("id"));
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        param.put("fileType", "autoCreate");
        List<JSONObject> files = overseasProductRectificationDao.queryFileList(param);
        for (JSONObject file : files) {
            pinToFile.put(file.getString("REF_ID_"), file);
        }
        //2.取formData的recordItems2,遍历每个recordItem2
        JSONArray recordItems2 = formData.getJSONArray("recordItems2");
        for (Object o : recordItems2) {
            JSONObject recordItem2 = (JSONObject) o;
            String pin = recordItem2.getString("pin");
            //2.1如果当前recordItems2.pin包含在map<REF_ID_,file>中，删除现有的文件信息和文件
            if (pinToFile.containsKey(pin)) {
                rdmZhglFileManager.deleteOneFileFromDisk(pinToFile.get(pin).getString("id"),
                        pinToFile.get(pin).getString("fileName"), pinToFile.get(pin).getString("mainId"), filePathBase);
                param.clear();
                param.put("id", pinToFile.get(pin).getString("id"));
                overseasProductRectificationDao.deleteBusinessFile(param);
            }
            //2.2取formData的recordItems,遍历每个recordItems重新生成一个文件（初始化好了），根据当前pin重新生成文件信息和文件
            String filePath = filePathBase + File.separator + formData.getString("id");
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String id = IdUtil.getId();
            String fileFullPath = filePath + File.separator + id + ".xls";
            FileOutputStream outputStream = new FileOutputStream(fileFullPath);
            wbObj.write(outputStream);
            outputStream.close();
            //写入数据库
            String excelName = pin + "_new_" +
                    (recordItemList.get(0).containsKey("materialCodeNew") ? recordItemList.get(0).getString("materialCodeNew") : "") +
                    "_old_" +
                    (recordItemList.get(0).containsKey("materialCodeOld") ? recordItemList.get(0).getString("materialCodeOld") : "") +
                    "_维修申请单.xls";
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", excelName);
            fileInfo.put("mainId", formData.getString("id"));
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("fileType", "autoCreate");
            fileInfo.put("REF_ID_", pin);
            overseasProductRectificationDao.addFileInfos(fileInfo);
        }
    }

    //..
    public JsonResult doTransFile(JSONObject jsonObject) throws Exception {
        try {
            JsonResult result = new JsonResult(true, "转换成功！");
            this.transJsonItemToFile(jsonObject);
            return result;
        } catch (Exception e) {
            logger.error("转换失败！错误信息：" + e.getMessage(), e);
            throw new Exception(e);
        }
    }

    //..
    public JsonResult doSynFormToGss(JSONObject jsonObject) throws Exception {
        try {
            if (!jsonObject.getString("businessStatus").equalsIgnoreCase("Z")) {
                throw new RuntimeException("只有已结束的流程能够传输至Gss！");
            }
            if (jsonObject.getString("isSynToGss").equalsIgnoreCase("是")) {
                throw new RuntimeException("只有传输不成功的能够传输至Gss！");
            }
            JsonResult result = new JsonResult(true, "传输成功！");
            JSONObject postJson = new JSONObject();
            JSONArray dataOfPostJson = new JSONArray();
            //..
            String orderNumber = jsonObject.getString("id") + "-" + jsonObject.getString("rectificationType") + "-RDM";
            postJson.put("orderNumber", orderNumber);
            JSONArray recordItems = jsonObject.getJSONArray("recordItems");
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");
            String formatDateTime = dateTime.format(formatter);
            for (int i = 0; i < recordItems.size(); i++) {
                JSONObject recordItem = recordItems.getJSONObject(i);
                JSONObject OneOfDataOfPostJson = new JSONObject();
                OneOfDataOfPostJson.put("time", formatDateTime);
                OneOfDataOfPostJson.put("file", null);
                if (recordItem.containsKey("materialCodeNew") && StringUtil.isNotEmpty(recordItem.getString("materialCodeNew")) &&
                        recordItem.containsKey("materialCodeOld") && StringUtil.isNotEmpty(recordItem.getString("materialCodeOld"))) {
                    //新旧物料都有-修改
                    OneOfDataOfPostJson.put("flag", "修改");
                    OneOfDataOfPostJson.put("amount", recordItem.getString("materialCountNew"));
                    OneOfDataOfPostJson.put("newNumber", i == 0 ? recordItem.getString("materialCodeNew") + "(RDM)" :
                            recordItem.getString("materialCodeNew"));
                    OneOfDataOfPostJson.put("newName", recordItem.getString("materialNameNew"));
                    OneOfDataOfPostJson.put("newCode", "");
                    OneOfDataOfPostJson.put("newSupplier", null);
                    OneOfDataOfPostJson.put("oldNumber", recordItem.getString("materialCodeOld"));
                    OneOfDataOfPostJson.put("oldName", recordItem.getString("materialNameOld"));
                    OneOfDataOfPostJson.put("oldCode", "");
                    OneOfDataOfPostJson.put("oldSupplier", null);
                    OneOfDataOfPostJson.put("parentCode", null);
                    dataOfPostJson.add(OneOfDataOfPostJson);
                } else if (recordItem.containsKey("materialCodeNew") && StringUtil.isNotEmpty(recordItem.getString("materialCodeNew")) &&
                        (!recordItem.containsKey("materialCodeOld") || StringUtil.isEmpty(recordItem.getString("materialCodeOld")))) {
                    //只有新物料-新增
                    OneOfDataOfPostJson.put("flag", "新增");
                    OneOfDataOfPostJson.put("amount", recordItem.getString("materialCountNew"));
                    OneOfDataOfPostJson.put("newNumber", recordItem.getString("materialCodeNew"));
                    OneOfDataOfPostJson.put("newName", recordItem.getString("materialNameNew"));
                    OneOfDataOfPostJson.put("newCode", "");
                    OneOfDataOfPostJson.put("newSupplier", null);
                    OneOfDataOfPostJson.put("oldNumber", null);
                    OneOfDataOfPostJson.put("oldName", null);
                    OneOfDataOfPostJson.put("oldCode", "");
                    OneOfDataOfPostJson.put("oldSupplier", null);
                    OneOfDataOfPostJson.put("parentCode", null);
                    dataOfPostJson.add(OneOfDataOfPostJson);
                } else if (recordItem.containsKey("materialCodeOld") && StringUtil.isNotEmpty(recordItem.getString("materialCodeOld")) &&
                        (!recordItem.containsKey("materialCodeNew") || StringUtil.isEmpty(recordItem.getString("materialCodeNew")))) {
                    //只有旧物料-删除
                    OneOfDataOfPostJson.put("flag", "删除");
                    OneOfDataOfPostJson.put("amount", null);
                    OneOfDataOfPostJson.put("newNumber", null);
                    OneOfDataOfPostJson.put("newName", null);
                    OneOfDataOfPostJson.put("newCode", "");
                    OneOfDataOfPostJson.put("newSupplier", null);
                    OneOfDataOfPostJson.put("oldNumber", recordItem.getString("materialCodeOld"));
                    OneOfDataOfPostJson.put("oldName", recordItem.getString("materialNameOld"));
                    OneOfDataOfPostJson.put("oldCode", "");
                    OneOfDataOfPostJson.put("oldSupplier", null);
                    OneOfDataOfPostJson.put("parentCode", null);
                    dataOfPostJson.add(OneOfDataOfPostJson);
                } else {
                    //不符合规范啥也不做
                    continue;
                }
            }
            postJson.put("data", dataOfPostJson.toJSONString(dataOfPostJson, SerializerFeature.WRITE_MAP_NULL_FEATURES));
            //..
            String Authorization = sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringCallbackUrls",
                    "sendMaterialsChangeListAtuh").getValue();
            String url = sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringCallbackUrls",
                    "sendMaterialsChangeList").getValue();
            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            reqHeaders.put("Authorization", Authorization);
            JSONArray recordItems2 = jsonObject.getJSONArray("recordItems2");
            for (Object o : recordItems2) {
                JSONObject recordItem2 = (JSONObject) o;
                if(!"是".equalsIgnoreCase(recordItem2.getString("isSynToGss"))){
                    postJson.put("pin", recordItem2.getString("pin"));
                    String rtnContent = HttpClientUtil.postJson(url, postJson.toJSONString(), reqHeaders);
                    //处理调用结果
                    logger.info("response from gss,return message:" + rtnContent);
                    if (StringUtils.isBlank(rtnContent)) {
                        result.setSuccess(false);
                        result.setMessage("传输失败，GSS系统返回值为空！");
                        return result;
                    }
                    JSONObject returnObj = JSONObject.parseObject(rtnContent);
                    if ("E".equalsIgnoreCase(returnObj.getString("result"))) {
                        result.setSuccess(false);
                        result.setMessage("传输失败，原因：" + returnObj.getString("message"));
                        return result;
                    }
                    recordItem2.put("isSynToGss", "是");//如果某次调用没问题，则把某次的车号明细更新为调用成功
                    jsonObject.put("recordItems2", recordItems2.toJSONString());
                    this.updateBusiness(jsonObject);
                }
            }
            //全部调用成功，则把主单据更新为调用成功
            jsonObject.put("isSynToGss", "是");
            this.updateBusiness(jsonObject);
            return result;
        } catch (Exception e) {
            logger.error("传输失败：" + e.getMessage(), e);
            throw e;
        }
    }
}
