package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.*;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.CustomsDeclarationRawdataDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.collections.map.HashedMap;
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
import java.math.BigDecimal;
import java.util.*;

@Service
public class CustomsDeclarationRawdataService {
    private static Logger logger = LoggerFactory.getLogger(CustomsDeclarationRawdataService.class);
    private static int EXCEL_ROW_OFFSET = 1;
    private static String BUSINESS_STATUS_BIANJIZHONG = "编辑中";
    private static String BUSINESS_STATUS_YITIJIAO = "已提交";
    @Autowired
    private CustomsDeclarationRawdataDao customsDeclarationRawdataDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private BpmInstManager bpmInstManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = customsDeclarationRawdataDao.dataListQuery(params);
        int businessListCount = customsDeclarationRawdataDao.countDataListQuery(params);
        //progress
        for (JSONObject business : businessList) {
            params.clear();
            params.put("mainId", business.getString("id"));
            List<JSONObject> itemList = customsDeclarationRawdataDao.getItemList(params);
            Double totalCount = 0d;
            Double closeCount = 0d;
            for (JSONObject item : itemList) {
                totalCount++;
                if (StringUtil.isNotEmpty(item.getString("processTime"))) {
                    closeCount++;
                }
            }
            BigDecimal bigDecimal = new BigDecimal(totalCount == 0d ? 0d : closeCount / totalCount).
                    setScale(4, BigDecimal.ROUND_HALF_UP);
            business.put("progress", bigDecimal.toString());
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JSONObject getDataById(String id) throws Exception {
        return customsDeclarationRawdataDao.getDataById(id);
    }

    //..
    public JsonResult deleteBusiness(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("ids", idList);
        customsDeclarationRawdataDao.deleteBusiness(params);//删主档
        params.clear();
        params.put("mainIds", idList);
        customsDeclarationRawdataDao.deleteItems(params);//删明细
        return result;
    }

    //..
    public JsonResult saveBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            jsonObject.put("id", IdUtil.getId());
            jsonObject.put("businessStatus", this.BUSINESS_STATUS_BIANJIZHONG);
            jsonObject.put("submitterId", ContextUtil.getCurrentUserId());
            jsonObject.put("submitter", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("CREATE_TIME_", new Date());
            this.processItems(jsonObject);
            customsDeclarationRawdataDao.insertBusiness(jsonObject);
        } else {
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            this.processItems(jsonObject);
            customsDeclarationRawdataDao.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        return result;
    }

    //..
    public JsonResult doCommitBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        jsonObject.put("submitTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            jsonObject.put("id", IdUtil.getId());
            jsonObject.put("businessStatus", this.BUSINESS_STATUS_YITIJIAO);
            jsonObject.put("submitterId", ContextUtil.getCurrentUserId());
            jsonObject.put("submitter", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("CREATE_TIME_", new Date());
            this.processItems(jsonObject);
            customsDeclarationRawdataDao.insertBusiness(jsonObject);
        } else {
            jsonObject.put("businessStatus", this.BUSINESS_STATUS_YITIJIAO);
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            this.processItems(jsonObject);
            customsDeclarationRawdataDao.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        //给李辉发消息
        String customsDeclarationAdmin =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "customsDeclarationAdmin").getValue();
        String[] customsDeclarationAdminNos = customsDeclarationAdmin.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        if (customsDeclarationAdminNos.length > 0) {
            for (String customsDeclarationAdminNo : customsDeclarationAdminNos) {
                stringBuilder.append(osUserManager.getByUserName(customsDeclarationAdminNo).getUserId()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        sendDingDing(jsonObject, stringBuilder.toString());
        return result;
    }

    //..
    private void processItems(JSONObject jsonObject) throws Exception {
        if (jsonObject.containsKey("items")
                && StringUtil.isNotEmpty(jsonObject.getString("items"))) {
            JSONArray jsonArray = new JSONArray();
            JSONArray jsonArrayItemData = jsonObject.getJSONArray("items");
            List<String> itemIds = new ArrayList<>();
            for (Object item : jsonArrayItemData) {
                JSONObject itemJson = (JSONObject) item;
                if (itemJson.containsKey("_state")) {//处理新增和修改
                    if (itemJson.getString("_state").equalsIgnoreCase("added")) {
                        itemJson.put("id", IdUtil.getId());
                        itemJson.put("mainId", jsonObject.getString("id"));
                        itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        itemJson.put("CREATE_TIME_", new Date());
                        customsDeclarationRawdataDao.insertItem(itemJson);
                    } else if (itemJson.getString("_state").equalsIgnoreCase("modified")) {
                        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        jsonObject.put("UPDATE_TIME_", new Date());
                        customsDeclarationRawdataDao.updateItem(itemJson);
                    } else if (itemJson.getString("_state").equalsIgnoreCase("removed")) {
                        itemIds.add(itemJson.getString("id"));
                    }
                }
            }
            JSONObject params = new JSONObject();
            params.put("ids", itemIds);
            customsDeclarationRawdataDao.deleteItems(params);//统一处理删除
        }
    }

    //..钉钉发送
    private void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【物料报关要素归档】:批次号为‘");
        stringBuilder.append(jsonObject.getString("batchNo"))
                .append("’单据号为‘")
                .append(jsonObject.getString("businessNo"))
                .append("’的报关要素归档信息已提交，请尽快处理");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }

    //..
    public List<JSONObject> getItemList(JSONObject params) throws Exception {
        return customsDeclarationRawdataDao.getItemList(params);
    }

    //..
    public JsonPageResult<?> itemListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = customsDeclarationRawdataDao.itemListQuery(params);
        int businessListCount = customsDeclarationRawdataDao.countItemListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public ResponseEntity<byte[]> importItemTDownload() {
        try {
            String fileName = "物料报关要素明细导入模板.xlsx";
            // 创建文件实例
            File file = new File(CustomsDeclarationRawdataService.class.getClassLoader().
                    getResource("templates/serviceEngineering/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importItemTDownload", e);
            return null;
        }
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
            Sheet sheet = wb.getSheet("主表");
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
                case "物料":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料为空");
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
        //先将待导入记录的物料号提出来
        List<String> materialCodes = new ArrayList<>();
        for (Map<String, Object> map : itemList) {
            materialCodes.add(map.get("materialCode").toString());
        }
        //找到已经存在这些物料号的记录
        JSONObject params = new JSONObject();
        params.put("materialCodes", materialCodes);
        List<JSONObject> alreadyExist = customsDeclarationRawdataDao.itemListQuery(params);
        //将这些记录以物料号为key放入map
        Map<String, JSONObject> materialCode2Item = new HashedMap();
        for (JSONObject item : alreadyExist) {
            materialCode2Item.put(item.getString("materialCode"), item);
        }
        //轮询待导入的记录
        for (Map<String, Object> map : itemList) {
            JSONObject itemJson = new JSONObject(map);
            String materialCode = itemJson.getString("materialCode");
            if (materialCode2Item.containsKey(materialCode)) {
                //如果已经存在同物料号的记录，则把一些标识信息反写入待导入记录，然后用待导入的记录进行更新
                itemJson.put("id", materialCode2Item.get(materialCode).getString("id"));
                itemJson.put("mainId", materialCode2Item.get(materialCode).getString("mainId"));
                itemJson.put("processUser", materialCode2Item.get(materialCode).getString("processUser"));
                itemJson.put("processTime", materialCode2Item.get(materialCode).getString("processTime"));
                customsDeclarationRawdataDao.updateItem(itemJson);
            } else {
                //如果不存在同物料号的记录，则用待导入的记录进行新增
                itemJson.put("id", IdUtil.getId());
                itemJson.put("mainId", businessId);
                itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                itemJson.put("CREATE_TIME_", new Date());
                customsDeclarationRawdataDao.insertItem(itemJson);
            }
        }
        result.put("message", "数据导入成功！");
    }

    //..
    public void exportItemList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, false);
        List<JSONObject> listData = customsDeclarationRawdataDao.itemListQuery(params);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "报关数据导出";
        String excelName = nowDate + title;
        String[] fieldNames = {"适用机型", "物料号", "物料描述", "HS编码", "报关要素",
                "报关要素填写", "补充信息", "净重", "备注", "处理人",
                "处理完成时间", "批次号", "单据号"};
        String[] fieldCodes = {"machineModel", "materialCode", "materialDescription", "hsCode", "elements",
                "elementsFill", "additionalInfo", "netWeight", "remarks", "processUser",
                "processTime", "batchNo", "businessNo"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
    public JsonResult createSupplement(String processUserId, JSONArray jsonArray) throws Exception {
        JsonResult result = new JsonResult(true, "创建成功！");
        //查找相关solution
        String solId = this.synGetCustomsDeclarationSupplementSolId(result);
        if (StringUtil.isEmpty(solId)) {
            return result;
        }
        //拼接报关信息补充表单数据,同时反写报关信息明细数据
        JSONObject customsDeclarationSupplement = this.synTransToCustomsDeclarationSupplement(processUserId, jsonArray);
        //启动流程
        result = this.startProcess(solId, customsDeclarationSupplement);
        if (result.getSuccess()) {
            //反写的需求明细表数据更新
            for (Object o : jsonArray) {
                JSONObject rawdataItem = (JSONObject) o;
                rawdataItem.put("processUser", customsDeclarationSupplement.getString("supplementUser"));
                customsDeclarationRawdataDao.updateItem(rawdataItem);
            }
            result.setMessage("创建成功！");
        } else {
            return result;
        }
        return result;
    }

    //..查找相关solution
    private String synGetCustomsDeclarationSupplementSolId(JsonResult result) {
        BpmSolution bpmSol = bpmSolutionManager.getByKey("customsDeclarationSupplement", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        } else {
            result.setSuccess(false);
            result.setMessage("创建失败,报关信息补充流程方案获取出现异常！");
        }
        return solId;
    }

    //..拼接报关信息补充表单数据,同时反写报关信息明细数据
    private JSONObject synTransToCustomsDeclarationSupplement(String processUserId, JSONArray jsonArray) {
        JSONObject customsDeclarationSupplement = new JSONObject();
        customsDeclarationSupplement.put("rawDataId", jsonArray.getJSONObject(0).getString("mainId"));//取一个就够了
        customsDeclarationSupplement.put("supplementUserId", processUserId);
        customsDeclarationSupplement.put("supplementUser", osUserManager.get(processUserId).getFullname());
        JSONArray recordItems = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject recordItem = new JSONObject().fluentPut("rawDataItemId", jsonObject.getString("id"))
                    .fluentPut("machineModel", jsonObject.getString("machineModel"))
                    .fluentPut("materialCode", jsonObject.getString("materialCode"))
                    .fluentPut("materialDescription", jsonObject.getString("materialDescription"))
                    .fluentPut("hsCode", jsonObject.getString("hsCode"))
                    .fluentPut("elements", jsonObject.getString("elements"))
                    .fluentPut("additionalInfo", jsonObject.getString("additionalInfo"))
                    .fluentPut("netWeight", jsonObject.getString("netWeight"))
                    .fluentPut("remarks", jsonObject.getString("remarks"));
            recordItems.add(recordItem);
        }
        customsDeclarationSupplement.put("recordItems", recordItems.toJSONString());
        return customsDeclarationSupplement;
    }

    //..启动流程
    private JsonResult startProcess(String solId, JSONObject customsDeclarationSupplement) throws Exception {
        ProcessMessage handleMessage = new ProcessMessage();
        JsonResult result = new JsonResult();
        try {
            ContextUtil.setCurUser(ContextUtil.getCurrentUser());
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(customsDeclarationSupplement.toJSONString());
            //启动流程
            bpmInstManager.doStartProcess(startCmd);
            //创建草稿
            //bpmInstManager.doStartProcess(startCmd, false);
            result.setData(startCmd.getBusinessKey());
        } catch (Exception ex) {
            //把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (handleMessage.getErrorMsges().size() == 0) {
                handleMessage.addErrorMsg(ex.getMessage());
            }
        } finally {
            //在处理过程中，是否有错误的消息抛出
            if (handleMessage.getErrorMsges().size() > 0) {
                result.setSuccess(false);
                result.setMessage("启动流程失败!");
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
        }
        return result;
    }

    //..
    public JsonResult deleteItem(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("ids", idList);
        customsDeclarationRawdataDao.deleteItems(params);//删明细
        return result;
    }
}
