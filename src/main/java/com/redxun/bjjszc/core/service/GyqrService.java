package com.redxun.bjjszc.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.dao.GyqrDao;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.service.OilService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class GyqrService {
    private Logger logger = LogManager.getLogger(GyqrService.class);
    @Autowired
    private GyqrDao gyqrDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryGyqr(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
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
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        //addGyqrRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> gyqrList = gyqrDao.queryGyqr(params);
        for (JSONObject gyqr : gyqrList) {
            if (gyqr.get("CREATE_TIME_") != null) {
                gyqr.put("CREATE_TIME_", DateUtil.formatDate((Date) gyqr.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(gyqrList, ContextUtil.getCurrentUserId());
        result.setData(gyqrList);
        int countGyqrDataList = gyqrDao.countGyqrList(params);
        result.setTotal(countGyqrDataList);
        return result;
    }


    public void createGyqr(JSONObject formData) {
        formData.put("gyqrId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        gyqrDao.createGyqr(formData);
        if(StringUtils.isNotBlank(formData.getString("gyqr"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("gyqr"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("detailId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("gyqrId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    gyqrDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateGyqr(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        gyqrDao.updateGyqr(formData);
        if(StringUtils.isNotBlank(formData.getString("gyqr"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("gyqr"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("detailId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("gyqrId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    gyqrDao.createDetail(oneObject);
                }else if("modified".equals(state)){
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    gyqrDao.updateDetail(oneObject);
                }else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("detailId", oneObject.getString("detailId"));
                    gyqrDao.deleteDetail(param);
                }
            }
        }
    }


    public JSONObject getGyqrDetail(String gyqrId) {
        JSONObject detailObj = gyqrDao.queryGyqrById(gyqrId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("ckMonth") != null) {
            detailObj.put("ckMonth", DateUtil.formatDate(detailObj.getDate("ckMonth"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }



    public List<JSONObject> getDetailList(String belongId) {
        List<JSONObject> gyqrCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("gyqrId", belongId);
        gyqrCnList = gyqrDao.queryGyqrDetail(param);
        return gyqrCnList;
    }

    public JsonResult deleteGyqr(String[] gyqrIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String gyqrId : gyqrIdsArr) {
            // 删除基本信息
            param.put("gyqrId", gyqrId);
            gyqrDao.deleteGyqr(param);
            gyqrDao.deleteDetail(param);
        }
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }


    public JsonResult deleteDetail(String[] YdjxIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String detailId : YdjxIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("detailId", detailId);
            gyqrDao.deleteDetail(param);
        }
        return result;
    }

    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "备件供应形式确认清单导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    OilService.class.getClassLoader().getResource("templates/gyqr/" + fileName).toURI());
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

    public void importGyqr(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("Sheet0");
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
            String belongId = RequestUtil.getString(request, "belongId", "");
            Map<String, Object> params = new HashMap<>();
            params.put("gyqrId",belongId);
            List<JSONObject> detailList = gyqrDao.queryGyqrDetail(params);
            //保存主表数据
            for (int i = 0; i < itemList.size(); i++) {
                boolean isContains = false;
                String detailId = "";
                for(JSONObject detail:detailList){
                    if(detail.getString("wlNumber").equals(itemList.get(i).getString("wlNumber"))){
                        detailId = detail.getString("detailId");
                        isContains = true;
                        break;
                    }
                }
                if(!isContains){
                    itemList.get(i).put("belongId",belongId);
                    gyqrDao.createDetail(itemList.get(i));
                }else {
                    itemList.get(i).put("detailId",detailId);
                    gyqrDao.updateDetail(itemList.get(i));
                }
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
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ","");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));
            }
            switch (title) {
                case "总成物料编码":
                    oneRowMap.put("zcNumber", cellValue);
                    break;
                case "总成物料描述":
                    oneRowMap.put("zcms", cellValue);
                    break;
                case "物料编码":
                    oneRowMap.put("wlNumber", cellValue);
                    break;
                case "物料描述":
                    oneRowMap.put("wlms", cellValue);
                    break;
                case "中文名称":
                    oneRowMap.put("zwName", cellValue);
                    break;
                case "英文名称":
                    oneRowMap.put("ywName", cellValue);
                    break;
                case "供应商":
                    oneRowMap.put("supplier", cellValue);
                    break;
                case "是否可购买":
                    oneRowMap.put("isBuy", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("note", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("detailId",IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public void   exportGyqrDetail(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        List<JSONObject> gyqrOneList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("gyqrId", belongId);
        gyqrOneList = gyqrDao.queryGyqrDetail(param);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "备件供应形式确认明细表";
        String[] fieldNames = {"总成物料编码", "总成物料描述","物料编码", "物料描述","中文名称","英文名称",
                "供应商","是否可购买","备注"};
        String[] fieldCodes = {"zcNumber", "zcms", "wlNumber", "wlms", "zwName","ywName","supplier", "isBuy","note"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelGy(gyqrOneList, fieldNames, fieldCodes);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
    public void   exportGyqrList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryGyqr(request, false);
        List<Map<String, Object>> listData = result.getData();
        List<JSONObject> gyqrAllList = new ArrayList<>();
        for(Map<String, Object> data:listData){
            String belongId = data.get("gyqrId").toString();
            String whName = data.get("whName").toString();
            String applyName = data.get("applyName").toString();
            List<JSONObject> gyqrOneList = new ArrayList<>();
            Map<String, Object> param = new HashMap<>();
            param.put("gyqrId", belongId);
            gyqrOneList = gyqrDao.queryGyqrDetail(param);
            for(JSONObject gyqrOne:gyqrOneList){
                gyqrOne.put("whName",whName);
                gyqrOne.put("applyName",applyName);
            }
            gyqrAllList.addAll(gyqrOneList);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "备件供应形式确认清单";
        String excelName = nowDate + title;
        String[] fieldNames = {"总成物料编码", "总成物料描述","物料编码", "物料描述","中文名称","英文名称",
                "供应商","提交人","是否可购买","维护人","备注"};
        String[] fieldCodes = {"zcNumber", "zcms", "wlNumber", "wlms", "zwName","ywName","supplier","applyName", "isBuy","whName","note"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(gyqrAllList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
