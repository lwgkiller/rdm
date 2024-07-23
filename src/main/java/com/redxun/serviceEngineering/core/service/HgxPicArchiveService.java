package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.HgxPicArchiveDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

@Service
public class HgxPicArchiveService {
    private static Logger logger = LoggerFactory.getLogger(HgxPicArchiveService.class);
    @Autowired
    private HgxPicArchiveDao hgxPicArchiveDao;
    @Autowired
    private SysDicManager sysDicManager;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        int businessListCount;

        businessList = hgxPicArchiveDao.dataListQuery(params);
        // 加文件存在状态及时间转换
        if (businessList.size() > 0) {
            for (JSONObject jsonObject : businessList) {
                boolean existFile = false;
                existFile = checkFileExist(jsonObject.getString("id"), jsonObject.getString("fileName"));
                jsonObject.put("existFile", existFile);
                if (jsonObject.get("CREATE_TIME_") != null) {
                    jsonObject.put("CREATE_TIME_",
                        DateUtil.formatDate((Date)jsonObject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
                if (jsonObject.get("UPDATE_TIME_") != null) {
                    jsonObject.put("UPDATE_TIME_",
                        DateUtil.formatDate((Date)jsonObject.get("UPDATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }
        businessListCount = hgxPicArchiveDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // ..
    private boolean checkFileExist(String id, String fileName) {
        File file = null;
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        file = findFile(id, suffix);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    // ..
    private File findFile(String id, String suffix) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return null;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxPicArchive").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        // 加了个目录/id/id.doc
        String fileFullPath = filePathBase + File.separator + id + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    // ..
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
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    // if ("communicateStartTime".equalsIgnoreCase(name)) {
                    // value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    // }
                    // if ("communicateEndTime".equalsIgnoreCase(name)) {
                    // value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    // }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        // params.put("startIndex", 0);
        // params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    // ..
    public void Preview(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        File file = findFile(id, suffix);
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





    // ..
    public ResponseEntity<byte[]> Download(HttpServletRequest request, String id, String description) {
        try {
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
            String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxPicArchive").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return null;
            }
            String fileName = id + "." + CommonFuns.toGetFileSuffix(description);
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

    // ..
    public JSONObject queryDataById(String id) {
        JSONObject jsonObject = hgxPicArchiveDao.queryDataById(id);
        return jsonObject;
    }

    // ..
    public void deleteBusiness(JSONObject result, String id, String fileName) {
        try {
            deleteFileFromDisk(id, fileName);
            hgxPicArchiveDao.deleteBusiness(id);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    // ..
    private void deleteFileFromDisk(String id, String fileName) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxPicArchive").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + id + File.separator + id + "." + CommonFuns.toGetFileSuffix(fileName);
        File file = new File(fileFullPath);
        file.delete();
    }

    // ..
    public void saveBusiness(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
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
            logger.error("Exception in savePublicStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    // ..
    private void constructBusinessParam(Map<String, String[]> parameters, Map<String, Object> objBody) {
        if (parameters.get("id") != null && parameters.get("id").length != 0
            && StringUtils.isNotBlank(parameters.get("id")[0])) {
            objBody.put("id", parameters.get("id")[0]);
        }
        if (parameters.get("remark") != null && parameters.get("remark").length != 0
            && StringUtils.isNotBlank(parameters.get("remark")[0])) {
            objBody.put("remark", parameters.get("remark")[0]);
        }
        if (parameters.get("creatorName") != null && parameters.get("creatorName").length != 0
            && StringUtils.isNotBlank(parameters.get("creatorName")[0])) {
            objBody.put("creatorName", parameters.get("creatorName")[0]);
        }
        if (parameters.get("fileName") != null && parameters.get("fileName").length != 0
            && StringUtils.isNotBlank(parameters.get("fileName")[0])) {
            objBody.put("fileName", parameters.get("fileName")[0]);
        }

        if (parameters.get("picCode") != null && parameters.get("picCode").length != 0
                && StringUtils.isNotBlank(parameters.get("picCode")[0])) {
            objBody.put("picCode", parameters.get("picCode")[0]);
        }if (parameters.get("picType") != null && parameters.get("picType").length != 0
                && StringUtils.isNotBlank(parameters.get("picType")[0])) {
            objBody.put("picType", parameters.get("picType")[0]);
        }
        if (parameters.get("region") != null && parameters.get("region").length != 0
                && StringUtils.isNotBlank(parameters.get("region")[0])) {
            objBody.put("region", parameters.get("region")[0]);
        }
        if (parameters.get("dataSrc") != null && parameters.get("dataSrc").length != 0
                && StringUtils.isNotBlank(parameters.get("dataSrc")[0])) {
            objBody.put("dataSrc", parameters.get("dataSrc")[0]);
        }
        if (parameters.get("applyInst") != null && parameters.get("applyInst").length != 0
                && StringUtils.isNotBlank(parameters.get("applyInst")[0])) {
            objBody.put("applyInst", parameters.get("applyInst")[0]);
        }if (parameters.get("versionStatus") != null && parameters.get("versionStatus").length != 0
                && StringUtils.isNotBlank(parameters.get("versionStatus")[0])) {
            objBody.put("versionStatus", parameters.get("versionStatus")[0]);
        }

    }

    // ..
    private void addOrUpdateBusiness(Map<String, Object> objBody, MultipartFile fileObj) throws IOException {
        String id = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (StringUtils.isBlank(id)) {
            // 新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                updateFile2Disk(newId, fileObj);
                objBody.put("fileName", fileObj.getOriginalFilename());
            }
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            hgxPicArchiveDao.insertBusiness(objBody);
        } else {
            if (fileObj != null) {
                updateFile2Disk(id, fileObj);
                objBody.put("fileName", fileObj.getOriginalFilename());
            } else {
                // 前台清除文件后，删除磁盘中的文件
                JSONObject jsonObject = hgxPicArchiveDao.queryDataById(id);
                // todo 删pdf
                deleteFileFromDisk(id, jsonObject.getString("fileName"));
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            hgxPicArchiveDao.updateBusiness(objBody);
        }
    }

    // ..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no id or fileObj");
            return;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxPicArchive").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }

        // 处理下载目录的更新
        String filePath = filePathBase + File.separator + id;
        File pathFile = new File(filePath);
//        File pathFile = new File(filePathBase + File.separator + id);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileName = fileObj.getOriginalFilename();
        String suffix = CommonFuns.toGetFileSuffix(fileName);

        String fileFullPath = filePath  + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        // 已存在不同类型的文件则删除
        JSONObject jsonObject = hgxPicArchiveDao.queryDataById(id);
        if (jsonObject != null) {
            String orgFileName = jsonObject.getString("fileName");
            if (CommonFuns.toGetFileSuffix(orgFileName) != suffix) {
                deleteFileFromDisk(id, jsonObject.getString("fileName"));
            }

        }

        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页


        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        businessList = hgxPicArchiveDao.dataListQuery(params);

        // 加文件存在状态及时间转换
        if (businessList.size() > 0) {
            for (JSONObject jsonObject : businessList) {
                boolean existFile = checkFileExist(jsonObject.getString("id"), jsonObject.getString("fileName"));
                if (existFile) {
                    jsonObject.put("existFile", "是");
                }
                else{
                    jsonObject.put("existFile", "否");
                }

                if (jsonObject.get("CREATE_TIME_") != null) {
                    jsonObject.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)jsonObject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
                if (jsonObject.get("UPDATE_TIME_") != null) {
                    jsonObject.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)jsonObject.get("UPDATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "操保手册标准图片库";
        String excelName = nowDate + title;
        String[] fieldNames = {"编号", "文件名称","图片类别", "适用范围", "数据来源",
                "应用载体", "归档人", "归档时间", "版本状态",
                };
        String[] fieldCodes = {"picCode", "fileName", "picType", "region", "dataSrc",
                "applyInst", "creatorName", "CREATE_TIME_", "versionStatus"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }


    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "标准图片库导入模板.xlsx";
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
            logger.error("Exception in importTemplateDownload", e);
            return null;
        }
    }

    //..
    public void importExcel(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("模板");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 2) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 3) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            //根据PIN码判断每个导入信息，有的更新，没的新增
            for (int i = 0; i < itemList.size(); i++) {
                if (hgxPicArchiveDao.getListByCode(itemList.get(i).get("picCode").toString()).size() > 0) {
                    //清一下id 根据picCode更新
                    itemList.get(i).put("id", "");
                    hgxPicArchiveDao.updateBusiness(itemList.get(i));
                } else {
                    hgxPicArchiveDao.insertBusiness(itemList.get(i));
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
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
                case "编号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("picCode", cellValue);
                    break;

                case "名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("fileName", cellValue);
                    break;
                case "类别":
//                    if (StringUtils.isBlank(cellValue)) {
//                        oneRowCheck.put("message", "图片类别为空");
//                        return oneRowCheck;
//                    }
                    oneRowMap.put("picType", cellValue);
                    break;

                case "适用范围":
//                    if (StringUtils.isBlank(cellValue)) {
//                        oneRowCheck.put("message", "适用范围为空");
//                        return oneRowCheck;
//                    }
                    oneRowMap.put("region", cellValue);
                    break;
                case "数据来源":
//                    if (StringUtils.isBlank(cellValue)) {
//                        oneRowCheck.put("message", "数据来源为空");
//                        return oneRowCheck;
//                    }
                    oneRowMap.put("dataSrc", cellValue);
                    break;

                case "应用载体":
//                    if (StringUtils.isBlank(cellValue)) {
//                        oneRowCheck.put("message", "应用载体为空");
//                        return oneRowCheck;
//                    }
                    oneRowMap.put("applyInst", cellValue);
                    break;
                case "备注":
//                    if (StringUtils.isBlank(cellValue)) {
//                        oneRowCheck.put("message", "备注为空");
//                        return oneRowCheck;
//                    }
                    oneRowMap.put("remark", cellValue);
                    break;
                case "版本":
                    oneRowMap.put("version", cellValue);
                    break;

                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("creatorName", ContextUtil.getCurrentUser().getFullname());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    // 附件上传
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            // 这里要找和文件名对应的id，然后按id去存文件

            String fileName = toGetParamVal(parameters.get("fileName"));
            String id = hgxPicArchiveDao.queryIdByFileName(fileName);
            if (StringUtil.isEmpty(id)) {
                logger.warn("没有找到文件名称对应的基础数据");
                return;
            }

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase =
                    sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxPicArchive").getValue();

            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + id;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 写入数据库


        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }



}
