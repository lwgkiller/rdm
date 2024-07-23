package com.redxun.wwrz.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.util.CommonExcelUtils;
import com.redxun.wwrz.core.dao.WwrzFilesDao;
import com.redxun.wwrz.core.dao.WwrzPreviewApplyDao;
import com.redxun.wwrz.core.dao.WwrzTestApplyDao;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zhangzhen
 */
@Service
public class WwrzFilesService {
    private static final Logger logger = LoggerFactory.getLogger(WwrzFilesService.class);
    @Resource
    WwrzFilesDao wwrzFilesDao;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private LoginRecordManager loginRecordManager;
    @Resource
    private WwrzPreviewApplyDao wwrzPreviewApplyDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Resource
    private WwrzTestApplyDao wwrzTestApplyDao;

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("reportDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("reportValidity".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("closeDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                }
            }
        }
    }

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            String mainId = request.getParameter("detailId");
            String fileType = request.getParameter("fileType");
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("mainId", mainId);
            params.put("fileType", fileType);
            list = wwrzFilesDao.getFileList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("mainId", mainId);
            params.put("fileType", fileType);
            totalList = wwrzFilesDao.getFileList(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public JsonPageResult<?> getReportFileList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            String fileType = CommonFuns.nullToString(params.get("fileType"));
            if (StringUtil.isEmpty(fileType)) {
                fileType = request.getParameter("fileType");
                params.put("fileType", fileType);
            }
            list = wwrzFilesDao.getReportFileList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            fileType = CommonFuns.nullToString(params.get("fileType"));
            if (StringUtil.isEmpty(fileType)) {
                fileType = request.getParameter("fileType");
                params.put("fileType", fileType);
            }
            totalList = wwrzFilesDao.getReportFileList(params);
            convertDate(list);
            boolean glNet = StandardManagerUtil.judgeGLNetwork(request);
            boolean isJszx = loginRecordManager.judgeIsJSZX(ContextUtil.getCurrentUser().getMainGroupId(),
                ContextUtil.getCurrentUser().getMainGroupName()).getBooleanValue("isJSZX");
            JSONObject paramJson = new JSONObject();
            paramJson.put("userId", ContextUtil.getCurrentUserId());
            for (Map<String, Object> map : list) {
                map.put("glNet", glNet);
                map.put("isJszx", isJszx);
                paramJson.put("fileId", map.get("id"));
                JSONObject applyObj = wwrzPreviewApplyDao.getApplyObj(paramJson);
                if (applyObj == null) {
                    map.put("isApply", false);
                    map.put("isPass", false);
                } else {
                    if (applyObj.getDate("passDate") == null) {
                        map.put("isApply", true);
                        map.put("isPass", false);
                    } else {
                        long diff = System.currentTimeMillis() - applyObj.getDate("passDate").getTime();
                        long days = diff / (1000 * 60 * 60 * 24);
                        if (days <= 60) {
                            map.put("isApply", true);
                            map.put("isPass", true);
                        } else {
                            map.put("isApply", false);
                            map.put("isPass", false);
                        }
                    }
                }
            }
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public void exportReportListExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "报告证书明细";
        Map<String, Object> params = new HashMap<>(16);
        params = CommonFuns.getSearchParam(params, request, false);
        String fileType = "report";
        params.put("fileType", fileType);
        List<Map<String, Object>> list = wwrzFilesDao.getReportFileList(params);
        convertDate(list);
        String title = "报告证书明细";
        String[] fieldNames = {"销售型号","设计型号", "文件类型", "文件编号", "文件名称", "文件日期", "有效期", "产品主管","状态"};
        String[] fieldCodes = {"productModel", "designModel","reportType", "reportCode", "reportName", "reportDate", "reportValidity",
            "productManager","validText"};
        HSSFWorkbook wbObj = CommonExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        CommonExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public void saveUploadFiles(HttpServletRequest request) {
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
        String filePathBase = SysPropertiesUtil.getGlobalProperty("wwrzFileUrl");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find wwrzFileUrl");
            return;
        }
        try {
            JSONObject fileInfo = new JSONObject();
            String mainId = CommonFuns.toGetParamVal(parameters.get("detailId"));
            String id = IdUtil.getId();
            if(StringUtil.isEmpty(mainId)){
                mainId = id;
            }
            String fileName = CommonFuns.toGetParamVal(parameters.get("fileName"));
            String fileSize = CommonFuns.toGetParamVal(parameters.get("fileSize"));
            String fileDesc = CommonFuns.toGetParamVal(parameters.get("fileDesc"));
            String fileType = CommonFuns.toGetParamVal(parameters.get("fileType"));
            String indexSort = CommonFuns.toGetParamVal(parameters.get("indexSort"));
            if ("report".equals(fileType)) {
                // 如果上传得是报告证书，则查询产品主管和销售型号信息
                Map<String, Object> wwrzApplyMap = wwrzTestApplyDao.getObjectById(mainId);

                String reportType = CommonFuns.toGetParamVal(parameters.get("reportType"));
                String reportCode = CommonFuns.toGetParamVal(parameters.get("reportCode"));
                String reportValidity = CommonFuns.toGetParamVal(parameters.get("reportValidity"));
                String reportDate = CommonFuns.toGetParamVal(parameters.get("reportDate"));
                String reportName = CommonFuns.toGetParamVal(parameters.get("reportName"));
                if (StringUtil.isNotEmpty(reportDate)) {
                    reportDate = CommonFuns.convertDateToStr(new Date(reportDate), "yyyy-MM-dd");
                }
                if (StringUtil.isNotEmpty(reportValidity)) {
                    reportValidity = CommonFuns.convertDateToStr(new Date(reportValidity), "yyyy-MM-dd");
                }
                fileInfo.put("reportType", reportType);
                fileInfo.put("reportCode", reportCode);
                fileInfo.put("reportDate", reportDate);
                fileInfo.put("reportValidity", reportValidity);
                fileInfo.put("reportName", reportName);
                fileInfo.put("productModel", wwrzApplyMap.get("productModel"));
                fileInfo.put("designModel", wwrzApplyMap.get("designModel"));
                fileInfo.put("productManager", wwrzApplyMap.get("productLeaderName"));
                fileInfo.put("productManagerId", wwrzApplyMap.get("productLeader"));
            }

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + mainId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("mainId", mainId);
            fileInfo.put("fileType", fileType);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("indexSort", indexSort);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", new Date());
            wwrzFilesDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void deleteOneFile(String fileId, String fileName, String mainId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, mainId,
            SysPropertiesUtil.getGlobalProperty("wwrzFileUrl"));
        Map<String, Object> param = new HashMap<>(16);
        wwrzFilesDao.delFileById(fileId);
    }

    public JSONObject getFileList(JSONObject paramJson) {
        List<JSONObject> fileList = new ArrayList<>();
        try {
            fileList = wwrzFilesDao.getFileListByMainId(paramJson.getString("mainId"));
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
        return ResultUtil.result(true, "获取成功", fileList);
    }

    public JSONObject getFileListByParam(JSONObject paramJson) {
        List<JSONObject> fileList = new ArrayList<>();
        try {
            fileList = wwrzFilesDao.getFileListByParam(paramJson);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
        return ResultUtil.result(true, "获取成功", fileList);
    }

    public void fileToZip(String filePath, ZipOutputStream zipOutputStream, String fileName) throws IOException {
        // 需要压缩的文件
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(filePath);
        // 缓冲
        byte[] bufferArea = new byte[1024 * 10];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 1024 * 10);
        // 将当前文件作为一个zip实体写入压缩流，fileName代表压缩文件中的文件名称
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        int length = 0;
        while ((length = bufferedInputStream.read(bufferArea, 0, 1024 * 10)) != -1) {
            zipOutputStream.write(bufferArea, 0, length);
        }
        // 关闭流
        fileInputStream.close();
        bufferedInputStream.close();
    }
    public JSONObject addReportFile(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("reportDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                if (mapKey.equals("reportValidity")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            String id = IdUtil.getId();
            objBody.put("id", id);
            objBody.put("mainId", id);
            objBody.put("fileType", "report");
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", new Date());
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            wwrzFilesDao.addReportFileInfos(objBody);
        } catch (Exception e) {
            logger.error("Exception in 添加认证报告 ", e);
            return ResultUtil.result(false, "添加认证报告异常！", "");
        }
        return ResultUtil.result(true, "添加成功", resultJson);
    }
    public JSONObject updateReportFile(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("reportDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                if (mapKey.equals("reportValidity")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            wwrzFilesDao.updateReportFile(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新认证费用", e);
            return ResultUtil.result(false, "更新认证费用异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    public JSONObject removeReportFile(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            for (String fileId : idList) {
                JSONObject fileObj = wwrzFilesDao.getFileObj(fileId);
                deleteOneFile(fileObj.getString("id"), fileObj.getString("fileName"), fileObj.getString("mainId"));
            }
            // 删除附件信息
        } catch (Exception e) {
            logger.error("Exception in update 删除信息异常", e);
            return ResultUtil.result(false, "删除信息异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject batchInvalid(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            wwrzFilesDao.batchSetValid(params);
        } catch (Exception e) {
            logger.error("Exception in update 作废异常", e);
            return ResultUtil.result(false, "作废异常！", "");
        }
        return ResultUtil.result(true, "作废成功", resultJson);
    }

    /**
     * 模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "报告证书导入模板.xlsx";
            // 创建文件实例
            File file =
                new File(MaterielService.class.getClassLoader().getResource("templates/wwrz/" + fileName).toURI());
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

    public void importReportCert(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("documentImportFile");
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("历史数据台账");
            if (sheet == null) {
                logger.error("找不到历史数据台账明细导入页");
                result.put("message", "数据导入失败，找不到历史数据台账明细导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(0);
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
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<JSONObject> dataInsert = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, dataInsert, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }

            for (int i = 0; i < dataInsert.size(); i++) {
                JSONObject obj = wwrzFilesDao.getFileByReportCode(dataInsert.get(i));
                if (obj == null) {
                    wwrzFilesDao.addFileInfos(dataInsert.get(i));
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importDocument", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<JSONObject> dataInsert, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        JSONObject oneRowMap = new JSONObject();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "序号":
                    break;
                case "产品型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "产品型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("productModel", cellValue);
                    break;
                case "文件类型":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "文件类型为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("reportType", cellValue);
                    break;
                case "文件编号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "文件编号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("reportCode", cellValue);
                    break;
                case "文件名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "文件名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("reportName", cellValue);
                    break;
                case "文件日期":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "文件日期为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("reportDate", cellValue);
                    break;
                case "有效期":
                    if (StringUtil.isEmpty(cellValue)) {
                        cellValue = null;
                    }
                    oneRowMap.put("reportValidity", cellValue);
                    break;
                case "产品主管":
                    oneRowMap.put("productManager", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        List<JSONObject> productManagerIdList = commonInfoManager.getUserIdByUserName(oneRowMap.getString("productManager"));
        if(productManagerIdList != null&& !productManagerIdList.isEmpty()){
            oneRowMap.put("productManagerId",productManagerIdList.get(0).getString("USER_ID_"));
        }else {
            oneRowCheck.put("message", "在系统中找不到对应产品主管的账号");
        }
        oneRowMap.put("fileType", "report");
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        oneRowMap.put("id", IdUtil.getId());
        dataInsert.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public void dealReportFile(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("reportFile");
            String fileId = request.getParameter("fileId");
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.warn("没有找到上传的参数");
                return;
            }
            // 多附件上传需要用到的MultipartHttpServletRequest
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            if (fileMap == null || fileMap.isEmpty()) {
                logger.warn("没有找到上传的文件");
                return;
            }
            String filePathBase = SysPropertiesUtil.getGlobalProperty("wwrzFileUrl");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find wwrzFileUrl");
                return;
            }
            JSONObject fileInfo = new JSONObject();
            JSONObject fileJson = wwrzFilesDao.getFileObj(fileId);
            String mainId = fileJson.getString("mainId");
            if(StringUtil.isEmpty(mainId)){
                mainId = fileId;
            }
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + mainId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            fileInfo.put("id", fileId);
            fileInfo.put("mainId", fileId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", new Date());
            wwrzFilesDao.updateReportFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in importDocument", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    public JsonPageResult<?> getCommonFileList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            String fileType = request.getParameter("fileType");
            String fileName = request.getParameter("fileName");
            params.put("fileType", fileType);
            params.put("fileName", fileName);
            list = wwrzFilesDao.getReportFileList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("fileType", fileType);
            params.put("fileName", fileName);
            totalList = wwrzFilesDao.getReportFileList(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public JSONObject updateIndexSort(JSONObject postDate) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> objBody = new HashMap<>(16);
            objBody.put("id", postDate.getString("id"));
            objBody.put("indexSort", postDate.getString("dataValue"));
            wwrzFilesDao.updateIndexSort(objBody);
        } catch (Exception e) {
            logger.error("Exception in updateIndexSort 更新排序号", e);
            return ResultUtil.result(false, "更新排序号！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }
}
