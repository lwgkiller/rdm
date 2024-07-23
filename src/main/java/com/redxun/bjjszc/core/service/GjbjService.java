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
import com.redxun.bjjszc.core.dao.GjbjDao;
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
public class GjbjService {
    private Logger logger = LogManager.getLogger(GjbjService.class);
    @Autowired
    private GjbjDao gjbjDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryGjbj(HttpServletRequest request, boolean doPage) {
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
        List<JSONObject> gjbjList = gjbjDao.queryGjbj(params);
        for (JSONObject gjbj : gjbjList) {
            if (gjbj.get("CREATE_TIME_") != null) {
                gjbj.put("CREATE_TIME_", DateUtil.formatDate((Date)gjbj.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(gjbjList, ContextUtil.getCurrentUserId());
        result.setData(gjbjList);
        int countGjbjDataList = gjbjDao.countGjbjList(params);
        result.setTotal(countGjbjDataList);
        return result;
    }

    public void createGjbj(JSONObject formData) {
        formData.put("gjbjId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        gjbjDao.createGjbj(formData);
        if (StringUtils.isNotBlank(formData.getString("gjbj"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("gjbj"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String timeId = oneObject.getString("detailId");
                if ("added".equals(state) || StringUtils.isBlank(timeId)) {
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("gjbjId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    gjbjDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateGjbj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        gjbjDao.updateGjbj(formData);
        if (StringUtils.isNotBlank(formData.getString("gjbj"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("gjbj"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String timeId = oneObject.getString("detailId");
                if ("added".equals(state) || StringUtils.isBlank(timeId)) {
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("gjbjId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    gjbjDao.createDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    gjbjDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("detailId", oneObject.getString("detailId"));
                    gjbjDao.deleteDetail(param);
                }
            }
        }
    }

    public JSONObject getGjbjDetail(String gjbjId) {
        JSONObject detailObj = gjbjDao.queryGjbjById(gjbjId);
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
        List<JSONObject> gjbjCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("gjbjId", belongId);
        gjbjCnList = gjbjDao.queryGjbjDetail(param);
        return gjbjCnList;
    }

    public JsonResult deleteGjbj(String[] gjbjIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String gjbjId : gjbjIdsArr) {
            // 删除基本信息
            param.put("gjbjId", gjbjId);
            gjbjDao.deleteGjbj(param);
            gjbjDao.deleteDetail(param);
        }
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    public JsonResult deleteDetail(String[] gjbjIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String detailId : gjbjIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("detailId", detailId);
            gjbjDao.deleteDetail(param);
        }
        return result;
    }

    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "关键备件导入模板.xlsx";
            // 创建文件实例
            File file = new File(OilService.class.getClassLoader().getResource("templates/gjbj/" + fileName).toURI());
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

    public void importGjbj(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("模板");
            if (sheet == null) {
                sheet = wb.getSheet("Sheet0");
            }
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
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            String belongId = RequestUtil.getString(request, "belongId", "");
            Map<String, Object> params = new HashMap<>();
            params.put("gjbjId", belongId);
            List<JSONObject> detailList = gjbjDao.queryGjbjDetail(params);
            // 保存主表数据
            for (int i = 0; i < itemList.size(); i++) {
                boolean isContains = false;
                String detailId = "";
                for(JSONObject detail:detailList){
                    // 如果物料编码相同则更新，没找到相同的则新建
                    if(detail.getString("materialCode").equals(itemList.get(i).getString("materialCode"))){
                        detailId = detail.getString("detailId");
                        isContains = true;
                        break;
                    }
                }
                if(!isContains){
                    itemList.get(i).put("belongId",belongId);
                    gjbjDao.createDetail(itemList.get(i));
                }else {
                    itemList.get(i).put("detailId",detailId);
                    gjbjDao.updateDetail(itemList.get(i));
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
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));
            }
            switch (title) {
                case "类别":
                    oneRowMap.put("partsType", cellValue);
                    break;
                case "设计机型":
                    oneRowMap.put("desginModel", cellValue);
                    break;
                case "机型物料编码":
                    oneRowMap.put("vinCode", cellValue);
                    break;
                case "产品所":
                    oneRowMap.put("productDepartName", cellValue);
                    break;
                case "总成物料编码":
                    oneRowMap.put("holeCode", cellValue);
                    break;
                case "总成物料描述":
                    oneRowMap.put("holeDesc", cellValue);
                    break;
                case "物料编码":
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "物料描述":
                    oneRowMap.put("materialDesc", cellValue);
                    break;
                case "装配数量":
                    oneRowMap.put("partsNum", cellValue);
                    break;
                case "供应商":
                    oneRowMap.put("supplier", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                case "提交人":
                    break;
                // 下列四个是后续填充
                case "对应开发件物料编码":
                     oneRowMap.put("devCode", cellValue);
                    break;
                case "对应开发件物料描述":
                     oneRowMap.put("devDesc", cellValue);
                    break;
                case "备件开发计划时间":
                     oneRowMap.put("planActTime", cellValue);
                    break;
                case "是否有试验验证需求":
                     oneRowMap.put("needExp", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("detailId", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public void exportGjbjDetail(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        List<JSONObject> gjbjOneList = new ArrayList<>();
        JSONObject baseObj = gjbjDao.queryGjbjById(belongId);
        String creatorName = baseObj.getString("creatorName");
        Map<String, Object> param = new HashMap<>();
        param.put("gjbjId", belongId);
        gjbjOneList = gjbjDao.queryGjbjDetail(param);
        for (JSONObject oneObj : gjbjOneList) {
            oneObj.put("creatorName", creatorName);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "关键备件明细表";
        String[] fieldNames = {"类别", "设计机型", "机型物料编码", "产品所", "总成物料编码", "总成物料描述", "物料编码", "物料描述", "供应商", "装配数量",
                "对应开发件物料编码", "对应开发件物料描述", "备件开发计划时间", "是否有试验验证需求", "提交人", "备注"};
        String[] fieldCodes = {"partsType", "desginModel", "vinCode", "productDepartName", "holeCode", "holeDesc", "materialCode",
                "materialDesc", "supplier", "partsNum", "devCode", "devDesc", "planActTime", "needExp",
                "creatorName", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelGy(gjbjOneList, fieldNames, fieldCodes);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public void exportGjbjList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryGjbj(request, false);
        List<Map<String, Object>> listData = result.getData();
        List<JSONObject> gjbjAllList = new ArrayList<>();
        for (Map<String, Object> data : listData) {
            String belongId = data.get("gjbjId").toString();
            String creatorName = data.get("creatorName").toString();
            List<JSONObject> gjbjOneList = new ArrayList<>();
            Map<String, Object> param = new HashMap<>();
            param.put("gjbjId", belongId);
            gjbjOneList = gjbjDao.queryGjbjDetail(param);
            for (JSONObject gjbjOne : gjbjOneList) {
                gjbjOne.put("creatorName", creatorName);
            }
            gjbjAllList.addAll(gjbjOneList);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "关键备件清单";
        String excelName = nowDate + title;
        String[] fieldNames = {"类别", "设计机型", "机型物料编码", "产品所", "总成物料编码", "总成物料描述", "物料编码", "物料描述", "供应商", "装配数量",
            "对应开发件物料编码", "对应开发件物料描述", "备件开发计划时间", "是否有试验验证需求", "提交人", "备注"};
        String[] fieldCodes = {"partsType", "desginModel", "vinCode", "productDepartName", "holeCode", "holeDesc", "materialCode",
             "materialDesc", "supplier", "partsNum", "devCode", "devDesc", "planActTime", "needExp",
            "creatorName", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(gjbjAllList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
