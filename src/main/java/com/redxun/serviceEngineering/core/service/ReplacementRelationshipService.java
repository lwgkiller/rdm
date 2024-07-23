package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSON;
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
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.ReplacementRelationshipDao;
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

@Service
public class ReplacementRelationshipService {
    private static Logger logger = LoggerFactory.getLogger(ReplacementRelationshipService.class);
    @Autowired
    private ReplacementRelationshipDao replacementRelationshipDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<Map<String, Object>> businessList = replacementRelationshipDao.dataListQuery(params);
        //查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = replacementRelationshipDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonPageResult<?> masterdataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParamsMasterdata(params, request);
        List<JSONObject> businessList = replacementRelationshipDao.masterdataListQuery(params);
        for (JSONObject business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate(business.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        int businessListCount = replacementRelationshipDao.countMasterdataListQuery(params);
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
            params.put("sortField", "businessStatus");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
//                    if ("communicateStartTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
//                    }
//                    if ("communicateEndTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
//                    }
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
    private void getListParamsMasterdata(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "busunessNo,busunessNoItem");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("materialCodeOri".equalsIgnoreCase(name)) {
                        JSONObject param = new JSONObject();
                        param.put("materialCodeOri", value);
                        List<String> oriGroups = replacementRelationshipDao.getDistinctGroupList(param);
                        if (oriGroups.isEmpty()) {
                            oriGroups.add("nothing");
                        }
                        params.put("oriGroups", oriGroups);
                    } else if ("materialCodeRep".equalsIgnoreCase(name)) {
                        JSONObject param = new JSONObject();
                        param.put("materialCodeRep", value);
                        List<String> repGroups = replacementRelationshipDao.getDistinctGroupList(param);
                        if (repGroups.isEmpty()) {
                            repGroups.add("nothing");
                        }
                        params.put("repGroups", repGroups);
                    } else {
                        params.put(name, value);
                    }

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
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        replacementRelationshipDao.deleteBusiness(param);
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
        replacementRelationshipDao.deleteBusinessMasterdata(param);
        return result;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = replacementRelationshipDao.queryDetailById(businessId);
        if (jsonObject == null) {
            JSONObject newjson = new JSONObject();
            newjson.put("applicableModels", "-");
            newjson.put("applyUserId", ContextUtil.getCurrentUserId());
            newjson.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            newjson.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            newjson.put("recordItems", "[]");
            return newjson;
        }
        return jsonObject;
    }

    //..
    public void createBusiness(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            //@lwgkiller:此处是因为(草稿状态和空状态)无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
            formData.put("businessStatus", "A");
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            this.processJsonItem(formData);
            replacementRelationshipDao.insertBusiness(formData);
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
            replacementRelationshipDao.updateBusiness(formData);
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
            String busunessNo = sysSeqIdManager.genSequenceNo("replacementRelationshipGroup", ContextUtil.getCurrentTenantId());
            JSONArray recordItems = formDataJson.getJSONArray("recordItems");
            for (Object recordItem : recordItems) {
                JSONObject recordItemsJson = (JSONObject) recordItem;
                JSONObject repRelMasterData = new JSONObject();
                repRelMasterData.put("id", IdUtil.getId());
                repRelMasterData.put("busunessNo", busunessNo);
                repRelMasterData.put("busunessNoItem", recordItemsJson.getString("busunessNoItem_item"));
                repRelMasterData.put("materialCodeOri", recordItemsJson.getString("materialCodeOri_item"));
                repRelMasterData.put("materialNameOri", recordItemsJson.getString("materialNameOri_item"));
                repRelMasterData.put("materialCountOri", recordItemsJson.getString("materialCountOri_item"));
                repRelMasterData.put("materialCodeRep", recordItemsJson.getString("materialCodeRep_item"));
                repRelMasterData.put("materialNameRep", recordItemsJson.getString("materialNameRep_item"));
                repRelMasterData.put("materialCountRep", recordItemsJson.getString("materialCountRep_item"));
                repRelMasterData.put("replaceTypeOri", formDataJson.getString("replaceTypeOri"));
                repRelMasterData.put("replaceType", formDataJson.getString("replaceType"));
                repRelMasterData.put("applicableModels", formDataJson.getString("applicableModels"));
                repRelMasterData.put("WIPHandlingComments", formDataJson.getString("WIPHandlingComments"));
                repRelMasterData.put("informationSources", formDataJson.getString("informationSources"));
                repRelMasterData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                repRelMasterData.put("CREATE_TIME_", new Date());
                replacementRelationshipDao.insertBusinessMasterData(repRelMasterData);
            }
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
        List<JSONObject> listData = replacementRelationshipDao.masterdataListQuery(params);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "物料替换关系导出";
        String excelName = nowDate + title;
        String[] fieldNames = {"替换分组", "原车件物料", "原车件名称", "原车件数量", "替换件物料", "替换件名称",
                "替换件数量", "替换类型", "信息源替换类型", "适用机型", "制品处理意见", "信息来源",
                "维护人", "维护时间", "备注"};
        String[] fieldCodes = {"busunessNo", "materialCodeOri", "materialNameOri", "materialCountOri", "materialCodeRep", "materialNameRep",
                "materialCountRep", "replaceType", "replaceTypeOri", "applicableModels", "WIPHandlingComments", "informationSources",
                "createUser", "createTime", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
    public ResponseEntity<byte[]> importMasterdataTemplateDownload() {
        try {
            String fileName = "物料替换关系导入模板.xlsx";
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
    public ResponseEntity<byte[]> importItemdataTemplateDownload() {
        try {
            String fileName = "物料替换关系明细导入模板.xlsx";
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
            logger.error("Exception in importItemdataTemplateDownload", e);
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
            if (rowNum < 2) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 3) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = this.generateModelMadeDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            String currentGroupIndex = "";//记录当前某条导入数据的真实busunessNo字段值
            String currentGroupIndexTrue = "";//记录当前使用的转换过的busunessNo字段值
            Integer busunessNoItem = 0;//记录当前行项目号
            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> map = itemList.get(i);
                JSONObject jsonObject = new JSONObject(map);
                if (!jsonObject.getString("busunessNo").equalsIgnoreCase(currentGroupIndex)) {//如果当前记录的真实busunessNo有所改变
                    //将当前记录的真实busunessNo值记录在currentGroupIndex里面
                    currentGroupIndex = jsonObject.getString("busunessNo");
                    //生成一个新的转换过的busunessNo，并记录在currentGroupIndexTrue里面
                    currentGroupIndexTrue = sysSeqIdManager.genSequenceNo("replacementRelationshipGroup", ContextUtil.getCurrentTenantId());
                    busunessNoItem = 0;//当前行项目号归零
                }
                jsonObject.put("busunessNo", currentGroupIndexTrue);//当前使用的转换过的busunessNo字段值写回当前记录
                jsonObject.put("busunessNoItem", busunessNoItem);//当前行项目号写回当前记录
                busunessNoItem++;//当前行项目号自增
                replacementRelationshipDao.insertBusinessMasterData(jsonObject);
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..
    private JSONObject generateModelMadeDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
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
                case "替换分组":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "替换分组为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("busunessNo", cellValue);
                    break;
                case "原车件物料":
//                    if (!StringUtils.isBlank(cellValue) && cellValue.length() != 9) {
//                        oneRowCheck.put("message", "原车件物料号必须是9位");
//                        return oneRowCheck;
//                    }
                    oneRowMap.put("materialCodeOri", cellValue);
                    break;
                case "原车件名称":
                    oneRowMap.put("materialNameOri", cellValue);
                    break;
                case "原车件数量":
                    if (cellValue.split("\\.").length > 1) {
                        oneRowMap.put("materialCountOri", cellValue.split("\\.")[0]);
                    } else {
                        oneRowMap.put("materialCountOri", cellValue);
                    }
                    break;
                case "替换件物料":
//                    if (!StringUtils.isBlank(cellValue) && cellValue.length() != 9) {
//                        oneRowCheck.put("message", "替换件物料号必须是9位");
//                        return oneRowCheck;
//                    }
                    oneRowMap.put("materialCodeRep", cellValue);
                    break;
                case "替换件名称":
                    oneRowMap.put("materialNameRep", cellValue);
                    break;
                case "替换件数量":
                    if (cellValue.split("\\.").length > 1) {
                        oneRowMap.put("materialCountRep", cellValue.split("\\.")[0]);
                    } else {
                        oneRowMap.put("materialCountRep", cellValue);
                    }
                    break;
                case "替换类型"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "替换类型为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "serviceEngineeringReplacementRelationshipReplaceType");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "替换类型必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("replaceType", cellValue);
                    break;
                case "信息源替换类型"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "信息源替换类型为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "serviceEngineeringReplacementRelationshipReplaceTypeOri");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "信息源替换类型必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("replaceTypeOri", cellValue);
                    break;
                case "适用机型":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("applicableModels", "-");
                    } else {
                        oneRowMap.put("applicableModels", cellValue);
                    }
                    break;
                case "制品处理意见":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "制品处理意见为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("WIPHandlingComments", cellValue);
                    break;
                case "信息来源":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "信息来源为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("informationSources", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
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
    public void importItemdata(JSONObject result, HttpServletRequest request) {
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
            if (rowNum < 2) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 3) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = this.generateItemMadeDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            String businessId = request.getParameter("businessId");
            JSONObject detail = this.getDetail(businessId);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < itemList.size(); i++) {
                JSONObject jsonObject = new JSONObject(itemList.get(i));
                jsonObject.put("busunessNoItem_item", i);
                jsonArray.add(jsonObject);
            }
            detail.put("recordItems", jsonArray.toJSONString());
            this.updateBusiness(detail);
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..
    private JSONObject generateItemMadeDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
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
                case "原车件物料":
                    oneRowMap.put("materialCodeOri_item", cellValue);
                    break;
                case "原车件名称":
                    oneRowMap.put("materialNameOri_item", cellValue);
                    break;
                case "原车件数量":
                    if (cellValue.split("\\.").length > 1) {
                        oneRowMap.put("materialCountOri_item", cellValue.split("\\.")[0]);
                    } else {
                        oneRowMap.put("materialCountOri_item", cellValue);
                    }
                    break;
                case "替换件物料":
                    oneRowMap.put("materialCodeRep_item", cellValue);
                    break;
                case "替换件名称":
                    oneRowMap.put("materialNameRep_item", cellValue);
                    break;
                case "替换件数量":
                    if (cellValue.split("\\.").length > 1) {
                        oneRowMap.put("materialCountRep_item", cellValue.split("\\.")[0]);
                    } else {
                        oneRowMap.put("materialCountRep_item", cellValue);
                    }
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
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
