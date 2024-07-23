package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmZhgl.core.dao.AwardTalentsDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class AwardTalentsService {
    private static Logger logger = LoggerFactory.getLogger(AwardTalentsService.class);
    @Autowired
    private AwardTalentsDao awardTalentsDao;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    /*
     * 查询列表
     * */
    public JsonPageResult<?> getAtsList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "prizeTime", "desc");
        String filterParams = request.getParameter("filter");

        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("rdTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("rdTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        List<Map<String, Object>> atsList = awardTalentsDao.queryAtsList(params);

        result.setData(atsList);
        int countAtsfyList = awardTalentsDao.countAtsfyList(params);
        result.setTotal(countAtsfyList);
        return result;
    }
    public Map<String, Object> queryAtsById(String awardId) {

        Map<String, Object> param = new HashMap<>();
        param.put("awardId", awardId);
        Map<String, Object> oneInfo = awardTalentsDao.queryAtsById(param);
        return oneInfo;
    }
    // 保存（包括新增保存、编辑保存）
    public void saveAts(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, String[]> parameters = request.getParameterMap();
            MultipartFile fileObj = multipartRequest.getFile("standardFile");
            String awardId = RequestUtil.getString(request, "awardId");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }

            Map<String, Object> objBody = new HashMap<>();

            addOrUpdateAts(parameters, objBody, fileObj, awardId);

            result.put("message", "保存成功！");
            // result.put("id", objBody.get("id"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in saveLbj", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }
    private void addOrUpdateAts(Map<String, String[]> parameters, Map<String, Object> objBody, MultipartFile fileObj,
                                String awardId) throws IOException {

        String fjId = awardTalentsDao.selectFjId(parameters.get("id")[0]);
        if (StringUtils.isBlank(awardId)) {
            // 新增文件
            String newStandardId = IdUtil.getId();
            String newId = IdUtil.getId();
            if (fileObj != null) {
                updatePublicStandardFile2Disk(newStandardId, fileObj, parameters, newId);
            }
            // 文件添加
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", newStandardId);
            fileInfo.put("fileName", parameters.get("fileName")[0]);
            // fileInfo.put("fileSize",parameters.get("fileSize")[0]);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            awardTalentsDao.addFileInfos(fileInfo);

            // 获奖信息添加
            JSONObject userInfo = new JSONObject();
            userInfo.put("id", newId);
            userInfo.put("awardType", parameters.get("awardType")[0]);
            userInfo.put("projectName", parameters.get("projectName")[0]);
            userInfo.put("prizewinner", parameters.get("prizewinner")[0]);
            userInfo.put("portrayalPointPersonId", parameters.get("portrayalPointPersonId")[0]);
            userInfo.put("portrayalPointPersonName", parameters.get("portrayalPointPersonName")[0]);
            userInfo.put("commendUnit", parameters.get("commendUnit")[0]);
            userInfo.put("remark", parameters.get("remark")[0]);
            String prizeTime = parameters.get("prizeTime")[0];
            try {
                String dateStr = prizeTime.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                userInfo.put("prizeTime", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }

            userInfo.put("fjId", newStandardId);
            userInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            userInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            awardTalentsDao.saveAtsList(userInfo);

        } else {
            // 附件为空添加附件
            if (StringUtils.isBlank(fjId)) {
                fjId = IdUtil.getId();
                JSONObject fileInfo = new JSONObject();
                fileInfo.put("id", fjId);
                fileInfo.put("fileName", parameters.get("fileName")[0]);
                // fileInfo.put("fileSize",parameters.get("fileSize")[0]);
                fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                fileInfo.put("CREATE_TIME_", new Date());
                awardTalentsDao.addFileInfos(fileInfo);
            } else {
                if (StringUtils.isNotBlank(parameters.get("fileName")[0])) {
                    // 文件修改
                    JSONObject info = new JSONObject();
                    info.put("id", fjId);
                    info.put("fileName", parameters.get("fileName")[0]);
                    // info.put("fileSize",parameters.get("fileSize")[0]);
                    info.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    info.put("UPDATE_TIME_", new Date());
                    awardTalentsDao.updateFileInfos(info);
                }
                if(StringUtils.isBlank(parameters.get("fileName")[0])){
                    Map<String, Object> param = new HashMap<>();
                    List<String> fileIds = Arrays.asList(fjId.split(","));
                    param.put("fileIds",fileIds);
                    awardTalentsDao.deleteAtsFileIds(param);
                }
            }

            if (fileObj != null) {
                // 更新文件
                updatePublicStandardFile2Disk(fjId, fileObj, parameters, awardId);
            } else {
                // fileObj为空，有可能是真的没有附件，也有可能是编辑场景（有fileName）
                // 如无fileName则用户前台希望删除该标准的文件，否则说明用户没处理
                String fileName = parameters.get("fileName")[0] == null ? "" : parameters.get("fileName")[0].toString();
                if (StringUtils.isBlank(fileName)) {
                    deletePublicStandardFileFromDisk(fjId, parameters,awardId);
                }
            }
            /*objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));*/

            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", parameters.get("id")[0]);
            fileInfo.put("awardType", parameters.get("awardType")[0]);
            fileInfo.put("projectName", parameters.get("projectName")[0]);
            fileInfo.put("prizewinner", parameters.get("prizewinner")[0]);
            fileInfo.put("portrayalPointPersonId", parameters.get("portrayalPointPersonId")[0]);
            fileInfo.put("portrayalPointPersonName", parameters.get("portrayalPointPersonName")[0]);
            fileInfo.put("commendUnit", parameters.get("commendUnit")[0]);
            fileInfo.put("remark", parameters.get("remark")[0]);
            String prizeTime = parameters.get("prizeTime")[0];
            try {
                String dateStr = prizeTime.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                fileInfo.put("prizeTime", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }

            if (StringUtils.isNotBlank(parameters.get("fileName")[0])) {
                fileInfo.put("fjId", fjId);
            } else {
                fileInfo.put("fjId", null);
            }
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));

            awardTalentsDao.updateAtsList(fileInfo);
        }
    }
    private void updatePublicStandardFile2Disk(String standardId, MultipartFile fileObj,
                                               Map<String, String[]> parameters, String awardId) throws IOException {
        if (StringUtils.isBlank(standardId) || fileObj == null) {
            logger.warn("no standardId or fileObj");
            return;
        }
        String standardFilePathBase = WebAppUtil.getProperty("atsFilePathBase");
        if (StringUtils.isBlank(standardFilePathBase)) {
            logger.error("can't find atsFilePathBase");
            return;
        }

        // 向下载目录中写入文件
        String filePath = standardFilePathBase + File.separator + awardId;
        File pathFile = new File(filePath);
        delFiles(pathFile);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String suffix = CommonFuns.toGetFileSuffix(parameters.get("fileName")[0]);
        String fileFullPath = filePath + File.separator + standardId + "." + suffix;
        File file = new File(fileFullPath);

        // 预览删除
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath =
                filePath + File.separator + File.separator + convertPdfDir + File.separator + standardId + ".pdf";
        File pdffile = new File(convertPdfPath);
        pdffile.delete();

        FileCopyUtils.copy(fileObj.getBytes(), file);

    }
    public static boolean delFiles(File file) {
        boolean result = false;
        // 目录
        if (file.isDirectory()) {
            File[] childrenFiles = file.listFiles();
            for (File childFile : childrenFiles) {
                result = delFiles(childFile);
                if (!result) {
                    return result;
                }
            }
        }
        // 删除 文件、空目录
        result = file.delete();
        return result;
    }
    public void importMaterial(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("人才奖");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
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
            List<Map<String, Object>> itemList = new ArrayList<>();
            // 异常信息
            StringBuilder stringBuilder = new StringBuilder();
            boolean flag = false;
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);

                JSONObject rowParse = generateDataFromRow(row, itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    stringBuilder.append("第" + (i + 1) + "行数据错误：" + rowParse.getString("message") + "<br>");
                    flag = true;
                }
            }

            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> message = itemList.get(i);
                String id = IdUtil.getId();
                // 添加
                message.put("id", id);
                awardTalentsDao.addAtsList(message);
            }

            if (flag) {
                result.put("message", stringBuilder.toString() + " (异常数据已跳过导入，请及时调整)");
            } else {
                result.put("message", "数据导入成功！");
            }

        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        StringBuilder sb = new StringBuilder();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            if (cell == null) {
                continue;
            }
            String cellValue  = StringUtils.trim(dataFormatter.formatCellValue(cell));
            switch (title) {
                case "获奖类别":
                    if ("国家级".equals(cellValue)) {
                        oneRowMap.put("awardType", "gjj");
                    } else if ("省部级、市级".equals(cellValue)) {
                        oneRowMap.put("awardType", "sbj");
                    } else {
                        oneRowMap.put("awardType", "other");
                    }
                    break;

                case "序号":

                    break;
                case "项目名称":
                    oneRowMap.put("projectName", cellValue);
                    break;
                case "表彰单位或部门":
                    oneRowMap.put("commendUnit", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                case "获奖人":
                    // 根据人员姓名，如果人员在系统中存在重名，则返回失败；否则返回去掉领导之后的姓名和id列表
                    JSONObject userProcess = rdmZhglUtil.toGetUserInfosByNameStr(cellValue, true);
                    if (!userProcess.getBooleanValue("result")) {
                        sb.append(userProcess.getString("message") + "<br>");
                    } else {
                        oneRowMap.put("prizewinner", userProcess.getString("userNameOriginal"));
                        oneRowMap.put("portrayalPointPersonId", userProcess.getString("userIdFilter"));
                        oneRowMap.put("portrayalPointPersonName", userProcess.getString("userNameFilter"));
                    }
                    break;
                case "获奖时间":
                    if (cellValue != null && !"".equals(cellValue)) {
//                        boolean legalDate = isLegalDate(cellValue.length(), cellValue, "yyyy-MM-dd");
//                        if (legalDate) {
//                            oneRowMap.put("prizeTime", cellValue);
//                        } else {
//                            sb.append("列“" + title + "”时间格式异常<br>");
//                        }
                        oneRowMap.put("prizeTime", cellValue);
                    } else {
                        oneRowCheck.put("message", "获奖时间不能为空");
                        return oneRowCheck;
                    }

                    break;
                default:
                    sb.append("列“" + title + "”不存在<br>");
            }
        }
        oneRowCheck.put("message", sb.toString());
        // 无异常返回true
        if (StringUtils.isBlank(sb.toString())) {
            oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            itemList.add(oneRowMap);
            oneRowCheck.put("result", true);
        }

        return oneRowCheck;
    }
    public static boolean isLegalDate(int length, String sDate, String format) {
        int legalLen = length;
        if ((sDate == null) || (sDate.length() != legalLen)) {
            return false;
        }
        DateFormat formatter = new SimpleDateFormat(format);
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "人才奖导入模板.xlsx";
            // 创建文件实例
            File file =
                    new File(MaterielService.class.getClassLoader().getResource("templates/zhgl/" + fileName).toURI());
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
    private void deletePublicStandardFileFromDisk(String standardId, Map<String, String[]> parameters,String awardId) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return;
        }
        String standardFilePathBase = WebAppUtil.getProperty("atsFilePathBase");
        // 处理下载目录的删除
        String filePath=standardFilePathBase+ File.separator +awardId;
        File pathFile = new File(filePath);
        delFiles(pathFile);
        String suffix = CommonFuns.toGetFileSuffix(parameters.get("fileName")[0]);
        String fileFullPath = standardFilePathBase + File.separator +standardId + '.' + suffix;
        File file = new File(fileFullPath);
        file.delete();
    }
    public void exportAtsList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getAtsList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> oneMap : listData) {
            oneMap.put("typeName", convertStatusName(oneMap.get("awardType")));
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "人才奖列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"获奖类别", "项目名称", "表彰单位或部门", "获奖人", "获奖时间", "备注", "创建时间"};
        String[] fieldCodes = {"typeName", "projectName","commendUnit", "prizewinner", "prizeTime", "remark", "CREATE_TIME_"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
    private String convertStatusName(Object statusKey) {
        if (statusKey == null) {
            return "";
        }
        String statusKeyStr = statusKey.toString();
        switch (statusKeyStr) {
            case "gjj":
                return "国家级";
            case "sbj":
                return "省部级、市级";
            case "other":
                return "其他(不计入画像)";
        }
        return "";
    }
}
