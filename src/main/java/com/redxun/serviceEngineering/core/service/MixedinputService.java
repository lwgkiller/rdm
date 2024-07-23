package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MixedinputDao;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
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
import java.util.stream.Collectors;

@Service
public class MixedinputService {
    private static Logger logger = LoggerFactory.getLogger(MixedinputService.class);
    private static int EXCEL_ROW_OFFSET = 1;
    @Autowired
    private MixedinputDao mixedinputDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    public JsonPageResult<?> masterdataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParamsMasterdata(params, request);
        List<JSONObject> businessList = mixedinputDao.masterdataListQuery(params);
        for (JSONObject business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate(business.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        int businessListCount = mixedinputDao.countMasterdataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private void getListParamsMasterdata(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "signYear,signMonth");
            params.put("sortOrder", "asc");
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
        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JsonPageResult<?> filingDataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<Map<String, Object>> businessList = mixedinputDao.filingDataListQuery(params);
        //查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = mixedinputDao.countFilingDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonPageResult<?> discardDataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<Map<String, Object>> businessList = mixedinputDao.discardDataListQuery(params);
        //查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = mixedinputDao.countDiscardDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "applyTime");
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

        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JsonResult deleteBusinessFiling(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        mixedinputDao.deleteBusinessFiling(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JsonResult deleteBusinessDiscard(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        mixedinputDao.deleteBusinessDiscard(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JsonResult deleteBusinessMasterdata(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        mixedinputDao.deleteBusinessMasterdata(param);
        return result;
    }

    //..
    public JsonResult discardBusinessMasterdata(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        mixedinputDao.discardBusinessMasterdata(param);
        return result;
    }

    //..
    public JSONObject getFilingDetail(String businessId) {
        JSONObject jsonObject = mixedinputDao.queryFilingDetailById(businessId);
        if (jsonObject == null) {
            JSONObject newjson = new JSONObject();
            newjson.put("applyUserId", ContextUtil.getCurrentUserId());
            newjson.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            newjson.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            newjson.put("recordItems", "[]");
            return newjson;
        }
        return jsonObject;
    }

    //..
    public JSONObject getDiscardDetail(String businessId) {
        JSONObject jsonObject = mixedinputDao.queryDiscardDetailById(businessId);
        if (jsonObject == null) {
            JSONObject newjson = new JSONObject();
            newjson.put("applyUserId", ContextUtil.getCurrentUserId());
            newjson.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            newjson.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            newjson.put("recordItems", "[]");
            return newjson;
        }
        return jsonObject;
    }

    //..
    public JSONObject getMasterdataDetail(String businessId) {
        JSONObject jsonObject = mixedinputDao.queryMasterdataDetailById(businessId);
        return jsonObject;
    }

    //..
    public void createBusinessFiling(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            formData.put("businessStatus", "A");
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            this.processJsonItem(formData);
            mixedinputDao.insertBusinessFiling(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void updateBusinessFiling(JSONObject formData) {
        try {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            this.processJsonItem(formData);
            mixedinputDao.updateBusinessFiling(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void createBusinessDiscard(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            formData.put("businessStatus", "A");
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            this.processJsonItem(formData);
            mixedinputDao.insertBusinessDiscard(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void updateBusinessDiscard(JSONObject formData) {
        try {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            this.processJsonItem(formData);
            mixedinputDao.updateBusinessDiscard(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..@lwgkiller:无表化设计参考，简单情况
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
    public void doCreatMasterdataFromWorkflow(JSONObject formDataJson) {
        try {
            JSONArray recordItems = formDataJson.getJSONArray("recordItems");
            for (Object recordItem : recordItems) {
                JSONObject recordItemsJson = (JSONObject) recordItem;
                JSONObject repRelMasterData = new JSONObject();
                repRelMasterData.put("id", IdUtil.getId());
                repRelMasterData.put("businessStatus", "有效");
                repRelMasterData.put("orderNo", recordItemsJson.getString("orderNo_item"));
                repRelMasterData.put("batchNo", recordItemsJson.getString("batchNo_item"));
                repRelMasterData.put("orderInputCount", recordItemsJson.getString("orderInputCount_item"));
                repRelMasterData.put("materialCodeOfMachine", recordItemsJson.getString("materialCodeOfMachine_item"));
                repRelMasterData.put("materialDescriptionOfMachine", recordItemsJson.getString("materialDescriptionOfMachine_item"));
                repRelMasterData.put("materialCode", recordItemsJson.getString("materialCode_item"));
                repRelMasterData.put("materialDescription", recordItemsJson.getString("materialDescription_item"));
                repRelMasterData.put("materialCount", recordItemsJson.getString("materialCount_item"));
                repRelMasterData.put("isMixedInput", recordItemsJson.getString("isMixedInput_item"));
                repRelMasterData.put("remarks1", recordItemsJson.getString("remarks1_item"));
                repRelMasterData.put("remarks2", recordItemsJson.getString("remarks2_item"));
                repRelMasterData.put("repUserName", recordItemsJson.getString("repUserName_item"));
                repRelMasterData.put("signYear", recordItemsJson.getString("signYear_item"));
                repRelMasterData.put("signMonth", recordItemsJson.getString("signMonth_item"));
                repRelMasterData.put("remarks", recordItemsJson.getString("remarks_item"));
                repRelMasterData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                repRelMasterData.put("CREATE_TIME_", new Date());
                mixedinputDao.insertBusinessMasterData(repRelMasterData);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void doDiscardMasterdataFromWorkflow(JSONObject formDataJson) {
        try {
            JSONArray recordItems = formDataJson.getJSONArray("recordItems");
            String[] businessIds = recordItems.stream().
                    map(recordItem -> ((JSONObject) recordItem).getString("REF_ID_item")).
                    toArray(String[]::new);
            //toArray(i -> new String[i]);
            //以上两种都行，都是给个IntFunction<String[]>策略，通过一个整数入参返回一个字符串数组[整数入参大小]
            //lambda的魅力在于将我魂牵梦绕的“策略模式”以非常优雅的方式体现
            this.discardBusinessMasterdata(businessIds);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void exportListMasterdata(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        getListParamsMasterdata(params, request);
        params.remove("startIndex");
        params.remove("pageSize");
        List<JSONObject> listData = mixedinputDao.masterdataListQuery(params);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "混投台账导出";
        String excelName = nowDate + title;
        String[] fieldNames = {"订单信息", "批次号", "订单数量", "机型物料号", "机型物料描述",
                "物料号", "物料描述", "数量", "是否混投", "分配车号或异常说明",
                "混投依据或实际订单数量", "责任人", "年份", "月份", "备注", "业务状态"};
        String[] fieldCodes = {"orderNo", "batchNo", "orderInputCount", "materialCodeOfMachine", "materialDescriptionOfMachine",
                "materialCode", "materialDescription", "materialCount", "isMixedInput", "remarks1",
                "remarks2", "repUserName", "signYear", "signMonth", "remarks", "businessStatus"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
    public ResponseEntity<byte[]> importMasterdataTemplateDownload() {
        try {
            String fileName = "混投台账导入模板.xlsx";
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
            logger.error("Exception in importMasterdataTemplateDownload", e);
            return null;
        }
    }

    //..
    public void importMasterdata(JSONObject result, HttpServletRequest request) {
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
//            if (rowNum < 2) {
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
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

//            if (rowNum < 3) {
            if (rowNum < EXCEL_ROW_OFFSET + 1) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
//            for (int i = 2; i < rowNum; i++) {
            for (int i = EXCEL_ROW_OFFSET; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = this.generateMasterDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> map = itemList.get(i);
                JSONObject jsonObject = new JSONObject(map);
                jsonObject.put("businessStatus", "有效");
                mixedinputDao.insertBusinessMasterData(jsonObject);
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..
    public void importFilingItemdata(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("success", false);
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
                result.put("success", false);
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
//            if (rowNum < 2) {
            if (rowNum < EXCEL_ROW_OFFSET) {
                result.put("success", false);
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
//            Row titleRow = sheet.getRow(1);
            Row titleRow = sheet.getRow(EXCEL_ROW_OFFSET - 1);
            if (titleRow == null) {
                result.put("success", false);
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

//            if (rowNum < 3) {
            if (rowNum < EXCEL_ROW_OFFSET + 1) {
                result.put("success", false);
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
//            for (int i = 2; i < rowNum; i++) {
            for (int i = EXCEL_ROW_OFFSET; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = this.generateFilingDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("success", false);
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            result.put("success", true);
            result.put("data", itemList);
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("success", false);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..
    private JSONObject generateMasterDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
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
                case "订单信息":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "订单信息为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("orderNo", cellValue);
                    break;
                case "批次号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "批次号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("batchNo", cellValue);
                    break;
                case "订单数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "订单数量为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("orderInputCount", cellValue);
                    break;
                case "机型物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "机型物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCodeOfMachine", cellValue);
                    break;
                case "机型物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "机型物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialDescriptionOfMachine", cellValue);
                    break;
                case "物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialDescription", cellValue);
                    break;
                case "数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "数量为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCount", cellValue);
                    break;
                case "是否混投":
                    if (StringUtils.isBlank(cellValue) ||
                            (!cellValue.equalsIgnoreCase("是") &&
                                    !cellValue.equalsIgnoreCase("否"))) {
                        oneRowCheck.put("message", "是否混投不能为空，且只能为“是”和“否”");
                        return oneRowCheck;
                    }
                    oneRowMap.put("isMixedInput", cellValue);
                    break;
                case "分配车号或异常说明":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "分配车号或异常说明为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("remarks1", cellValue);
                    break;
                case "混投依据或实际订单数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "混投依据或实际订单数量为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("remarks2", cellValue);
                    break;
                case "责任人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "责任人为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("repUserName", cellValue);
                    break;
                case "年份"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "年份为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "signYear");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "年份必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("signYear", cellValue);
                    break;
                case "月份"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "月份为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "signMonth");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "月份必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("signMonth", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remarks", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    private JSONObject generateFilingDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
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
                case "订单信息":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "订单信息为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("orderNo_item", cellValue);
                    break;
                case "批次号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "批次号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("batchNo_item", cellValue);
                    break;
                case "订单数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "订单数量为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("orderInputCount_item", cellValue);
                    break;
                case "机型物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "机型物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCodeOfMachine_item", cellValue);
                    break;
                case "机型物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "机型物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialDescriptionOfMachine_item", cellValue);
                    break;
                case "物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode_item", cellValue);
                    break;
                case "物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialDescription_item", cellValue);
                    break;
                case "数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "数量为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCount_item", cellValue);
                    break;
                case "是否混投":
                    if (StringUtils.isBlank(cellValue) ||
                            (!cellValue.equalsIgnoreCase("是") &&
                                    !cellValue.equalsIgnoreCase("否"))) {
                        oneRowCheck.put("message", "是否混投不能为空，且只能为“是”和“否”");
                        return oneRowCheck;
                    }
                    oneRowMap.put("isMixedInput_item", cellValue);
                    break;
                case "分配车号或异常说明":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "分配车号或异常说明为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("remarks1_item", cellValue);
                    break;
                case "混投依据或实际订单数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "混投依据或实际订单数量为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("remarks2_item", cellValue);
                    break;
                case "责任人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "责任人为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("repUserName_item", cellValue);
                    break;
                case "年份"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "年份为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "signYear");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "年份必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("signYear_item", cellValue);
                    break;
                case "月份"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "月份为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "signMonth");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "月份必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("signMonth_item", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remarks_item", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..检查字典值
    private String sysDicCheck(String cellValue, String treeKey) {
        List<SysDic> sysDicList = sysDicManager.getByTreeKey(treeKey);
        boolean isok = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (SysDic sysDic : sysDicList) {
            if (cellValue.equalsIgnoreCase(sysDic.getName())) {
                isok = true;
            }
            stringBuilder.append(sysDic.getName()).append(",");
        }
        if (isok == false) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        } else {
            return "ok";
        }
    }
}
