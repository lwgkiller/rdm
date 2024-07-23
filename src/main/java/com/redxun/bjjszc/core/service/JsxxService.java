package com.redxun.bjjszc.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.dao.JsxxDao;
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

@Service
@Slf4j
public class JsxxService {
    private Logger logger = LogManager.getLogger(JsxxService.class);
    @Autowired
    private JsxxDao jsxxDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryJsxx(HttpServletRequest request, boolean doPage) {
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
        List<JSONObject> jsxxList = jsxxDao.queryJsxx(params);
        for (JSONObject jsxx : jsxxList) {
            if (jsxx.get("CREATE_TIME_") != null) {
                jsxx.put("CREATE_TIME_", DateUtil.formatDate((Date) jsxx.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(jsxxList, ContextUtil.getCurrentUserId());
        result.setData(jsxxList);
        int countJsxxDataList = jsxxDao.countJsxxList(params);
        result.setTotal(countJsxxDataList);
        return result;
    }


    public void createJsxx(JSONObject formData) {
        formData.put("jsxxId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jsxxDao.createJsxx(formData);
        if(StringUtils.isNotBlank(formData.getString("jsxx"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("jsxx"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("detailId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("jsxxId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    jsxxDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateJsxx(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jsxxDao.updateJsxx(formData);
        if(StringUtils.isNotBlank(formData.getString("jsxx"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("jsxx"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("detailId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("jsxxId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    jsxxDao.createDetail(oneObject);
                }else if("modified".equals(state)){
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    jsxxDao.updateDetail(oneObject);
                }else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("detailId", oneObject.getString("detailId"));
                    jsxxDao.deleteDetail(param);
                }
            }
        }
    }


    public JSONObject getJsxxDetail(String jsxxId) {
        JSONObject detailObj = jsxxDao.queryJsxxById(jsxxId);
        if (detailObj == null) {
            return new JSONObject();
        }
//        if (detailObj.getDate("ckMonth") != null) {
//            detailObj.put("ckMonth", DateUtil.formatDate(detailObj.getDate("ckMonth"), "yyyy-MM-dd"));
//        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }



    public List<JSONObject> getDetailList(String belongId) {
        List<JSONObject> jsxxCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jsxxId", belongId);
        jsxxCnList = jsxxDao.queryJsxxDetail(param);
        return jsxxCnList;
    }

    public JsonResult deleteJsxx(String[] jsxxIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String jsxxId : jsxxIdsArr) {
            // 删除基本信息
            param.put("jsxxId", jsxxId);
            jsxxDao.deleteJsxx(param);
            jsxxDao.deleteDetail(param);
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
            jsxxDao.deleteDetail(param);
        }
        return result;
    }

    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "备件开发件技术信息清单导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    OilService.class.getClassLoader().getResource("templates/jsxx/" + fileName).toURI());
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

    public void importJsxx(JSONObject result, HttpServletRequest request) {
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
            String belongId = RequestUtil.getString(request, "belongId", "");
            Map<String, Object> params = new HashMap<>();
            params.put("jsxxId",belongId);
            List<JSONObject> detailList = jsxxDao.queryJsxxDetail(params);
            //保存主表数据
            for (int i = 0; i < itemList.size(); i++) {
                itemList.get(i).put("belongId", belongId);
                jsxxDao.createDetail(itemList.get(i));
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
                case "备件品类":
                    oneRowMap.put("partsType", cellValue);
                    break;
                case "设计机型":
                    oneRowMap.put("desginModel", cellValue);
                    break;
                case "整机物料编码":
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "原车件物料编码":
                    oneRowMap.put("orgCode", cellValue);
                    break;
                case "原车件物料描述":
                    oneRowMap.put("orgDesc", cellValue);
                    break;
                case "备件开发件物料编码":
                    oneRowMap.put("partsDevCode", cellValue);
                    break;
                case "备件开发件物料描述":
                    oneRowMap.put("partsDevDesc", cellValue);
                    break;
                case "供应商":
                    oneRowMap.put("supplier", cellValue);
                    break;
                case "主要参数":
                    oneRowMap.put("mainParam", cellValue);
                    break;
                case "性能参数":
                    oneRowMap.put("performParam", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
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

    public void   exportJsxxDetail(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        List<JSONObject> jsxxOneList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jsxxId", belongId);
        jsxxOneList = jsxxDao.queryJsxxDetail(param);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "备件开发技术信息明细表";
        String[] fieldNames = {"备件品类", "设计机型","整机物料编码", "原车件物料编码","原车件物料描述","备件开发件物料编码",
                "备件开发件物料描述","供应商","主要参数","性能参数","备注"};
        String[] fieldCodes = {"partsType", "desginModel", "materialCode", "orgCode", "orgDesc","partsDevCode","partsDevDesc", "supplier", "mainParam", "performParam","remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelGy(jsxxOneList, fieldNames, fieldCodes);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
    public void   exportJsxxList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryJsxx(request, false);
        List<Map<String, Object>> listData = result.getData();
        List<JSONObject> jsxxAllList = new ArrayList<>();
        for(Map<String, Object> data:listData){
            String belongId = data.get("jsxxId").toString();
            String creatorName = data.get("creatorName").toString();
            List<JSONObject> jsxxOneList = new ArrayList<>();
            Map<String, Object> param = new HashMap<>();
            param.put("jsxxId", belongId);
            jsxxOneList = jsxxDao.queryJsxxDetail(param);
            for(JSONObject jsxxOne:jsxxOneList){
                jsxxOne.put("creatorName",creatorName);
            }
            jsxxAllList.addAll(jsxxOneList);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "备件开发技术信息清单";
        String excelName = nowDate + title;
        String[] fieldNames = {"备件品类", "设计机型","整机物料编码", "原车件物料编码","原车件物料描述","备件开发件物料编码",
                "备件开发件物料描述","供应商","主要参数","性能参数","提交人","备注"};
        String[] fieldCodes = {"partsType", "desginModel", "materialCode", "orgCode", "orgDesc","partsDevCode","partsDevDesc", "supplier", "mainParam", "performParam","creatorName" ,"remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(jsxxAllList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
