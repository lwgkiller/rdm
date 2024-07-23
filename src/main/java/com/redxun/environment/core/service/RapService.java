package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.environment.core.dao.RapDao;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
@Slf4j
public class RapService {
    private Logger logger = LogManager.getLogger(RapService.class);
    @Autowired
    private RapDao rapDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> queryRap(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "rapDate");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("createtime1".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("createtime2".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        addQbgzRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        // 增加分页条件
        List<JSONObject> rapList = rapDao.queryRap(params);
        for (JSONObject rap : rapList) {
            if (rap.getDate("rapDate") != null) {
                rap.put("rapDate", DateUtil.formatDate(rap.getDate("rapDate"), "yyyy-MM-dd"));
            }

        }
        result.setData(rapList);
        int countQbgzDataList = rapDao.countRapList(params);
        result.setTotal(countQbgzDataList);
        return result;
    }
    private void addQbgzRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }

        boolean isFGLR = rdmZhglUtil.judgeIsPointRoleUser(userId, "法规录入专员");
        if (isFGLR) {
            params.put("roleName", "FGLR");
            return;
        }
        boolean isFGSH = rdmZhglUtil.judgeIsPointRoleUser(userId, "法规审核专员");
        if (isFGSH) {
            params.put("roleName", "FGSH");
            return;
        }
        params.put("roleName", "ptyg");
    }
    public void insertRap(JSONObject formData) {
        formData.put("rapId", IdUtil.getId());
        formData.put("auditStatus", "待提交");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        rapDao.insertRap(formData);
    }

    public void updateRap(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        rapDao.updateRap(formData);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    public void saveRapUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("rapFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find rapFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("rapId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
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
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("belongId", belongId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            rapDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getRapDetail(String standardId) {
        JSONObject detailObj = rapDao.queryRapById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("rapDate") != null) {
            detailObj.put("rapDate", DateUtil.formatDate(detailObj.getDate("rapDate"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public List<JSONObject> getRapFileList(List<String> rapIdList) {
        List<JSONObject> rapFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("rapIds", rapIdList);
        rapFileList = rapDao.queryRapFileList(param);
        return rapFileList;
    }

    public void deleteOneRapFile(String fileId, String fileName, String rapId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, rapId, WebAppUtil.getProperty("rapFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        param.put("belongId", rapId);
        rapDao.deleteRapFile(param);
    }

    public JsonResult deleteRap(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> rapIds = Arrays.asList(ids);
        List<JSONObject> files = getRapFileList(rapIds);
        String rapFilePathBase = WebAppUtil.getProperty("rapFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("rapId"), rapFilePathBase);
        }
        for (String onerap : ids) {
            rdmZhglFileManager.deleteDirFromDisk(onerap, rapFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("rapIds", rapIds);
        rapDao.deleteRapFile(param);
        rapDao.deleteRap(param);
        return result;
    }
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "环保法规政策导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    RapService.class.getClassLoader().getResource("templates/environment/" + fileName).toURI());
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

    public void importRap(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile) fileObj).getFileItem().getName();
            ((CommonsMultipartFile) fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if(fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)){
                wb = new HSSFWorkbook(fileObj.getInputStream());
            }else if(fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)){
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("模板");
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
            List<JSONObject> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row,itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            //保存主表数据
            for (int i = 0; i < itemList.size(); i++) {
                rapDao.insertRap(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<JSONObject> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        JSONObject oneRowMap = new JSONObject();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ","");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "环保法规/政策名称":
                    oneRowMap.put("rapName", cellValue);
                    break;
                case "所属阶段":
                    oneRowMap.put("rapStatus", cellValue);
                    break;
                case "所属国家/省份":
                    oneRowMap.put("rapArea", cellValue);
                    break;
                case "实施日期":
                    oneRowMap.put("rapDate", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("rapId",IdUtil.getId());
        oneRowMap.put("auditStatus","待提交");
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }
}
