package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.CustomsDeclarationSupplementDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class CustomsDeclarationSupplementService {
    private static Logger logger = LoggerFactory.getLogger(CustomsDeclarationSupplementService.class);
    private static int EXCEL_ROW_OFFSET = 2;
    @Autowired
    private CustomsDeclarationSupplementDao customsDeclarationSupplementDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<Map<String, Object>> businessList = customsDeclarationSupplementDao.dataListQuery(params);
        //查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = customsDeclarationSupplementDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        customsDeclarationSupplementDao.deleteBusiness(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = customsDeclarationSupplementDao.queryDetailById(businessId);
        if (jsonObject == null) {
            JSONObject newjson = new JSONObject();
            newjson.put("recordItems", "[]");
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
            customsDeclarationSupplementDao.insertBusiness(formData);
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
            customsDeclarationSupplementDao.updateBusiness(formData);
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
    public void exportItemList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, false);
        JSONObject supplement = customsDeclarationSupplementDao.queryDetailById(params.getString("businessId"));
        JSONArray recordItems = supplement.getJSONArray("recordItems");
        List<JSONObject> listData = new ArrayList<>();
        for (Object recordItem : recordItems) {
            listData.add((JSONObject) recordItem);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "报关数据待补充明细导出";
        String excelName = nowDate + title;
        String[] fieldNames = {"适用机型", "物料号", "物料描述", "HS编码", "报关要素",
                "报关要素填写", "补充信息", "净重", "备注", "关联ID"};
        String[] fieldCodes = {"machineModel", "materialCode", "materialDescription", "hsCode", "elements",
                "elementsFill", "additionalInfo", "netWeight", "remarks", "rawDataItemId"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
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
            Sheet sheet = wb.getSheet("Sheet0");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            //if (rowNum < 2) {
            if (rowNum < EXCEL_ROW_OFFSET) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
//            Row titleRow = sheet.getRow(1);
            Row titleRow = sheet.getRow(EXCEL_ROW_OFFSET - 1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            boolean isOk = false;
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                if (StringUtils.trim(titleRow.getCell(i).getStringCellValue()).equalsIgnoreCase("关联ID")) {
                    isOk = true;
                }
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }
            if (!isOk) {
                result.put("message", "数据导入失败，导入列缺少 关联ID！");
                return;
            }
//            if (rowNum < 3) {
            if (rowNum < EXCEL_ROW_OFFSET + 1) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
//            for (int i = 2; i < rowNum; i++) {
            for (int i = EXCEL_ROW_OFFSET; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList);
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
                case "适用机型":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "适用机型为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("machineModel", cellValue);
                    break;
                case "物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue.split("\\.")[0]);
                    break;
                case "物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialDescription", cellValue);
                    break;
                case "HS编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "HS编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("hsCode", cellValue);
                    break;
                case "报关要素":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "报关要素为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("elements", cellValue);
                    break;
                case "报关要素填写":
                    oneRowMap.put("elementsFill", cellValue);
                case "补充信息":
                    oneRowMap.put("additionalInfo", cellValue);
                    break;
                case "净重":
                    if (StringUtils.isNotBlank(cellValue)) {
                        Boolean strResult = cellValue.matches("^[0-9]+(\\.[0-9]+)?$");
                        if (strResult == false) {
                            oneRowCheck.put("message", "净重必须为正值数字");
                            return oneRowCheck;
                        }
                        double _cellValue = Math.round(Double.parseDouble(cellValue) * 100.0) / 100.0;
                        oneRowMap.put("netWeight", Double.toString(_cellValue));
                    }
                    break;
                case "备注":
                    oneRowMap.put("remarks", cellValue);
                    break;
                case "关联ID":
                    oneRowMap.put("rawDataItemId", cellValue);
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
        JSONObject supplement = customsDeclarationSupplementDao.queryDetailById(businessId);
        JSONArray recordItems = new JSONArray();
        for (Map<String, Object> map : itemList) {
            JSONObject recordItem = new JSONObject(map);
            recordItems.add(recordItem);
        }
        supplement.put("recordItems", recordItems.toJSONString());
        customsDeclarationSupplementDao.updateBusiness(supplement);
        result.put("message", "数据导入成功！");
    }
}
