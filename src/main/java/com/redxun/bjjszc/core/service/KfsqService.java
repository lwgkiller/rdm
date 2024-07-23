package com.redxun.bjjszc.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.dao.KfsqDao;
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
public class KfsqService {
    private Logger logger = LogManager.getLogger(KfsqService.class);
    @Autowired
    private KfsqDao kfsqDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryKfsq(HttpServletRequest request, boolean doPage) {
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
        //addKfsqRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> kfsqList = kfsqDao.queryKfsq(params);
        for (JSONObject kfsq : kfsqList) {
            if (kfsq.get("CREATE_TIME_") != null) {
                kfsq.put("CREATE_TIME_", DateUtil.formatDate((Date) kfsq.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(kfsqList, ContextUtil.getCurrentUserId());
        result.setData(kfsqList);
        int countKfsqDataList = kfsqDao.countKfsqList(params);
        result.setTotal(countKfsqDataList);
        return result;
    }

    public JsonPageResult<?> queryGcs() {
        JsonPageResult result = new JsonPageResult(true);
        List<JSONObject> kfsqList = kfsqDao.queryGcs();
        result.setData(kfsqList);
        return result;
    }



    public void createKfsq(JSONObject formData) {
        formData.put("kfsqId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        kfsqDao.createKfsq(formData);
        if(StringUtils.isNotBlank(formData.getString("kfsq"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("kfsq"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("detailId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("kfsqId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    kfsqDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateKfsq(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        kfsqDao.updateKfsq(formData);
        if(StringUtils.isNotBlank(formData.getString("kfsq"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("kfsq"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("detailId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("kfsqId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    kfsqDao.createDetail(oneObject);
                }else if("modified".equals(state)){
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    kfsqDao.updateDetail(oneObject);
                }else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("detailId", oneObject.getString("detailId"));
                    kfsqDao.deleteDetail(param);
                }
            }
        }
    }


    public JSONObject getKfsqDetail(String kfsqId) {
        JSONObject detailObj = kfsqDao.queryKfsqById(kfsqId);
        if (detailObj == null) {
            return new JSONObject();
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
        List<JSONObject> kfsqCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("kfsqId", belongId);
        kfsqCnList = kfsqDao.queryKfsqDetail(param);
        return kfsqCnList;
    }

    public JsonResult deleteKfsq(String[] kfsqIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String kfsqId : kfsqIdsArr) {
            // 删除基本信息
            param.put("kfsqId", kfsqId);
            kfsqDao.deleteKfsq(param);
            kfsqDao.deleteDetail(param);
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
            kfsqDao.deleteDetail(param);
        }
        return result;
    }
    public JSONObject getUserInfoByBj(String bjType) {
        Map<String, Object> params = new HashMap<>();
        params.put("bjType", bjType);
        JSONObject userMapInfo = kfsqDao.getUserInfoByBj(params);
        return userMapInfo;
    }

    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "备件开发申请导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    OilService.class.getClassLoader().getResource("templates/kfsq/" + fileName).toURI());
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

    public void importKfsq(JSONObject result, HttpServletRequest request) {
        try {
            String belongId = RequestUtil.getString(request, "belongId", "");
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
                itemList.get(i).put("belongId", belongId);
                kfsqDao.createDetail(itemList.get(i));
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
                case "需求类型":
                    oneRowMap.put("xqType", cellValue);
                    break;
                case "设计机型":
                    oneRowMap.put("sjjx", cellValue);
                    break;
                case "名称":
                    oneRowMap.put("bjName", cellValue);
                    break;
                case "开发原因":
                    oneRowMap.put("kfReason", cellValue);
                    break;
                case "原厂件物料编码":
                    oneRowMap.put("wlNumber", cellValue);
                    break;
                case "原厂件物料描述":
                    oneRowMap.put("wlms", cellValue);
                    break;
                case "计划供应商":
                    oneRowMap.put("supplier", cellValue);
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

    public void exportKfsqList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryKfsq(request, false);
        List<Map<String, Object>> listData = result.getData();
        List<JSONObject> kfsqAllList = new ArrayList<>();
        for(Map<String, Object> data:listData){
            String belongId = data.get("kfsqId").toString();
            String bjType = data.get("bjType").toString();
            String applyName = data.get("applyName").toString();
            String fwName = data.get("fwName").toString();
            List<JSONObject> kfsqOneList = new ArrayList<>();
            Map<String, Object> param = new HashMap<>();
            param.put("kfsqId", belongId);
            kfsqOneList = kfsqDao.queryKfsqDetail(param);
            for(JSONObject kfsqOne:kfsqOneList){
                kfsqOne.put("bjType",bjType);
                kfsqOne.put("applyName",applyName);
                kfsqOne.put("fwName",fwName);
            }
            kfsqAllList.addAll(kfsqOneList);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "备件开发申请";
        String excelName = nowDate + title;
        String[] fieldNames = {"备件类型", "需求类型", "设计机型", "名称", "开发原因","原厂件物料编码", "原厂件物料描述",
                "计划供应商", "提交人", "服务工程工程师","产品所","产品所备件技术支持对接人","备注"};
        String[] fieldCodes = {"bjType", "xqType", "sjjx", "bjName", "kfReason","wlNumber",
                "wlms", "supplier", "applyName","fwName", "cpsName", "resName", "note"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(kfsqAllList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }


    public void saveGcs(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            String state=businessObj.getString("_state");
            if (StringUtils.isBlank(businessObj.getString("gcsId"))&&"added".equals(state)) {
                businessObj.put("gcsId", IdUtil.getId());
                kfsqDao.createGcs(businessObj);
            } else if("modified".equals(state)) {
                kfsqDao.updateGcs(businessObj);
            }else if ("removed".equals(state)){
                Map<String, Object> param = new HashMap<>();
                param.put("gcsId", businessObj.getString("gcsId"));
                kfsqDao.deleteGcs(param);
            }
        }
    }
}
