package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.PartsAtlasDao;
import com.redxun.serviceEngineering.core.dao.SeGeneralKanbanCompletenessDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.OsUserManager;
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
public class SeGeneralKanbanCompletenessService {
    private static Logger logger = LoggerFactory.getLogger(SeGeneralKanbanCompletenessService.class);
    @Autowired
    private SeGeneralKanbanCompletenessDao seGeneralKanbanCompletenessDao;
    @Autowired
    private PartsAtlasDao partsAtlasDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OsUserManager osUserManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = new ArrayList<>();
        int businessListCount = 0;

        if (params.containsKey("signYear") && StringUtils.isNotEmpty(params.get("signYear").toString())) {
            businessList = seGeneralKanbanCompletenessDao.dataListQuery(params);
            businessListCount = seGeneralKanbanCompletenessDao.countDataListQuery(params);
        } else {
            businessList = seGeneralKanbanCompletenessDao.dataListQuery2(params);
            businessListCount = seGeneralKanbanCompletenessDao.countDataListQuery2(params);
            for (JSONObject business : businessList) {
                List<JSONObject> businessListSameMaterial =
                        seGeneralKanbanCompletenessDao.getListByMaterialCode(business.getString("materialCode"));
                int output = 0;
                for (JSONObject businessSameMaterial : businessListSameMaterial) {
                    output += businessSameMaterial.getInteger("output");
                }
                business.put("output", output);
                business.put("id", businessListSameMaterial.get(0).getString("id"));
                business.put("signYear", businessListSameMaterial.get(0).getString("signYear"));
                business.put("materialCode", businessListSameMaterial.get(0).getString("materialCode"));
                business.put("salesModel", businessListSameMaterial.get(0).getString("salesModel"));
                business.put("designModel", businessListSameMaterial.get(0).getString("designModel"));
                business.put("interpret", businessListSameMaterial.get(0).getString("interpret"));
                business.put("completenessEvaluation", businessListSameMaterial.get(0).getString("completenessEvaluation"));
                business.put("partsAtlas", businessListSameMaterial.get(0).getString("partsAtlas"));
                business.put("maintenancePartsList", businessListSameMaterial.get(0).getString("maintenancePartsList"));
                business.put("wearingPartsList", businessListSameMaterial.get(0).getString("wearingPartsList"));
                business.put("regularEdition", businessListSameMaterial.get(0).getString("regularEdition"));
                business.put("CEEdition", businessListSameMaterial.get(0).getString("CEEdition"));
                business.put("packingList", businessListSameMaterial.get(0).getString("packingList"));
                business.put("decorationManual", businessListSameMaterial.get(0).getString("decorationManual"));
                business.put("disassAndAssManual", businessListSameMaterial.get(0).getString("disassAndAssManual"));
                business.put("structurefunctionAndPrincipleManual", businessListSameMaterial.get(0).getString("structurefunctionAndPrincipleManual"));
                business.put("testAndAdjustmentManual", businessListSameMaterial.get(0).getString("testAndAdjustmentManual"));
                business.put("troubleshootingManual", businessListSameMaterial.get(0).getString("troubleshootingManual"));
                business.put("torqueAndToolStandardValueTable", businessListSameMaterial.get(0).getString("torqueAndToolStandardValueTable"));
                business.put("maintenanceStandardValueTable", businessListSameMaterial.get(0).getString("maintenanceStandardValueTable"));
                business.put("engineManual", businessListSameMaterial.get(0).getString("engineManual"));
                business.put("lifeCycleCostList", businessListSameMaterial.get(0).getString("lifeCycleCostList"));
                business.put("airconditioningUseAndMaintenanceManual", businessListSameMaterial.get(0).getString("airconditioningUseAndMaintenanceManual"));
                business.put("remark", businessListSameMaterial.get(0).getString("remark"));
            }
        }
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
            params.put("sortField", "designModel");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if (name.equalsIgnoreCase("others")) {
                        String[] values = value.split(",");
                        for (String field : values) {
                            params.put(field, "√");
                        }
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
    public JsonResult deleteData(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        seGeneralKanbanCompletenessDao.deleteData(param);
        return result;
    }

    //..
    public void saveBusiness(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            Map<String, Object> param = new HashMap<>();
            param.put("materialCode", businessObj.get("materialCode").toString());
            param.put("signYear", businessObj.get("signYear").toString());
            if (StringUtils.isBlank(businessObj.getString("id"))) {
                businessObj.put("id", IdUtil.getId());
                //产量按物料编码和年份提取零件图册表中的入库数量,没有就保留原值
                businessObj.put("output", partsAtlasDao.getStorageCountByMaterialCodeAndStorageTime(param) == 0 ?
                        businessObj.getString("output") : partsAtlasDao.getStorageCountByMaterialCodeAndStorageTime(param));
                param.remove("signYear");
                //找到已存在的不同年份的同类物料记录，按照任一条更新自己的状态信息
                this.processByThisDao(businessObj, param);
                businessObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("CREATE_TIME_", new Date());
                seGeneralKanbanCompletenessDao.insertData(businessObj);
                //发送通知到相关人员
                this.processSendDingDing(businessObj);
            } else {
                //自动修正完备性标记
                this.completenessEvaluationCorrect(businessObj);
                //产量按物料编码和年份提取零件图册表中的入库数量,没有就保留原值
                businessObj.put("output", partsAtlasDao.getStorageCountByMaterialCodeAndStorageTime(param) == 0 ?
                        businessObj.getString("output") : partsAtlasDao.getStorageCountByMaterialCodeAndStorageTime(param));
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                seGeneralKanbanCompletenessDao.updateData(businessObj);
                //补充更新其他年份的相同物料的看板状态信息
                seGeneralKanbanCompletenessDao.updateDataByMaterialCode(businessObj);
            }
        }
    }

    //..
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "完备性看板导入模板.xlsx";
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
            logger.error("Exception in importTemplateDownload", e);
            return null;
        }
    }

    //..
    public void importExcel(JSONObject result, HttpServletRequest request) {
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
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            //根据物料号，年份判断每个导入信息，有的更新，没的新增
            JSONObject params = new JSONObject();
            for (int i = 0; i < itemList.size(); i++) {
                params.put("materialCode", itemList.get(i).get("materialCode").toString());
                params.put("signYear", itemList.get(i).get("signYear").toString());
                if (seGeneralKanbanCompletenessDao.dataListQuery(params).size() > 0) {
                    seGeneralKanbanCompletenessDao.updateDataByMaterialCodeAndSingYear(itemList.get(i));
                } else {
                    seGeneralKanbanCompletenessDao.insertData(itemList.get(i));
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
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
                case "年份":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "专业类别为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("signYear", cellValue);
                    break;
                case "物料编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "销售型号":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("salesModel", cellValue);
                    }
                    break;
                case "设计型号":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("designModel", cellValue);
                    }
                    break;
                case "产量":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("output", cellValue);
                    }
                    break;
                case "说明":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("interpret", cellValue);
                    }
                    break;
                case "完备性评价":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("completenessEvaluation", cellValue);
                    }
                    break;
                case "零件图册":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("partsAtlas", cellValue);
                    }
                    break;
                case "保养件清单":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("maintenancePartsList", cellValue);
                    }
                    break;
                case "易损件清单":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("wearingPartsList", cellValue);
                    }
                    break;
                case "常规版":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("regularEdition", cellValue);
                    }
                    break;
                case "CE版":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("CEEdition", cellValue);
                    }
                    break;
                case "装箱单":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("packingList", cellValue);
                    }
                    break;
                case "装修手册":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("decorationManual", cellValue);
                    }
                    break;
                case "分解与组装手册":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("disassAndAssManual", cellValue);
                    }
                    break;
                case "结构功能与原理手册":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("structurefunctionAndPrincipleManual", cellValue);
                    }
                    break;
                case "测试与调整手册":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("testAndAdjustmentManual", cellValue);
                    }
                    break;
                case "故障诊断手册":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("troubleshootingManual", cellValue);
                    }
                    break;
                case "力矩及工具标准值表":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("torqueAndToolStandardValueTable", cellValue);
                    }
                    break;
                case "检修标准值表":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("maintenanceStandardValueTable", cellValue);
                    }
                    break;
                case "发动机手册":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("engineManual", cellValue);
                    }
                    break;
                case "全生命周期成本清单":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("lifeCycleCostList", cellValue);
                    }
                    break;
                case "空调使用与维护保养手册":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("airconditioningUseAndMaintenanceManual", cellValue);
                    }
                    break;
                case "备注":
                    if (!StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("remark", cellValue);
                    }
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..自动修正完备性标记
    public void completenessEvaluationCorrect(JSONObject jsonObject) {
        String denominator = "/16";
        int molecule = 0;
        molecule = completenessEvaluationCorrect2(molecule, "partsAtlas", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "maintenancePartsList", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "wearingPartsList", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "regularEdition", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "CEEdition", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "packingList", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "decorationManual", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "disassAndAssManual", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "structurefunctionAndPrincipleManual", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "testAndAdjustmentManual", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "troubleshootingManual", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "torqueAndToolStandardValueTable", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "maintenanceStandardValueTable", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "engineManual", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "lifeCycleCostList", jsonObject);
        molecule = completenessEvaluationCorrect2(molecule, "airconditioningUseAndMaintenanceManual", jsonObject);
        jsonObject.put("completenessEvaluation", String.valueOf(molecule) + denominator);
    }

    //..自动修正完备性标记2
    public int completenessEvaluationCorrect2(int molecule, String key, JSONObject jsonObject) {
        if (StringUtils.isNotEmpty(jsonObject.getString(key))) {
            molecule++;
        }
        return molecule;
    }

    //..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = RequestUtil.getParameterValueMap(request, true);
        String filterParams = request.getParameter("filter");
        JsonPageResult result = dataListQuery(request, response);
        String[] fieldNames1 = {"年份", "物料编码", "销售型号", "设计型号", "产量", "说明", "完备性评价",
                "零件图册", "保养件清单", "易损件清单", "常规版", "CE版", "装箱单", "装修手册",
                "分解与组装手册", "结构功能与原理手册", "测试与调整手册",
                "故障诊断手册", "力矩及工具标准值表", "检修标准值表",
                "发动机手册", "全生命周期成本清单", "空调使用与维护保养手册", "备注"};

        String[] fieldCodes1 = {"signYear", "materialCode", "salesModel", "designModel", "output", "interpret", "completenessEvaluation",
                "partsAtlas", "maintenancePartsList", "wearingPartsList", "regularEdition", "CEEdition", "packingList", "decorationManual",
                "disassAndAssManual", "structurefunctionAndPrincipleManual", "testAndAdjustmentManual",
                "troubleshootingManual", "torqueAndToolStandardValueTable", "maintenanceStandardValueTable",
                "engineManual", "lifeCycleCostList", "airconditioningUseAndMaintenanceManual", "remark"};

        String[] fieldNames2 = {"物料编码", "销售型号", "设计型号", "产量", "说明", "完备性评价",
                "零件图册", "保养件清单", "易损件清单", "常规版", "CE版", "装箱单", "装修手册",
                "分解与组装手册", "结构功能与原理手册", "测试与调整手册",
                "故障诊断手册", "力矩及工具标准值表", "检修标准值表",
                "发动机手册", "全生命周期成本清单", "空调使用与维护保养手册", "备注"};

        String[] fieldCodes2 = {"materialCode", "salesModel", "designModel", "output", "interpret", "completenessEvaluation",
                "partsAtlas", "maintenancePartsList", "wearingPartsList", "regularEdition", "CEEdition", "packingList", "decorationManual",
                "disassAndAssManual", "structurefunctionAndPrincipleManual", "testAndAdjustmentManual",
                "troubleshootingManual", "torqueAndToolStandardValueTable", "maintenanceStandardValueTable",
                "engineManual", "lifeCycleCostList", "airconditioningUseAndMaintenanceManual", "remark"};
        String[] fieldNames = null;
        String[] fieldCodes = null;
        List<Map<String, Object>> listData = result.getData();
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "完备性数据";
        String excelName = nowDate + title;
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (name.equalsIgnoreCase("signYear") && StringUtils.isNotEmpty(value)) {
                    fieldNames = fieldNames1.clone();
                    fieldCodes = fieldCodes1.clone();
                    break;
                } else if (name.equalsIgnoreCase("signYear") && StringUtils.isEmpty(value)) {
                    fieldNames = fieldNames2.clone();
                    fieldCodes = fieldCodes2.clone();
                    break;
                }
            }
        }
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..发送通知到相关人员
    private void processSendDingDing(JSONObject businessObj) {
        String receiverNoString = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringGroups", "completenessKaban").getValue();
        String[] receiverNos = receiverNoString.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        if (receiverNos.length > 0) {
            for (String userNo : receiverNos) {
                stringBuilder.append(osUserManager.getByUserName(userNo).getUserId());
                stringBuilder.append(",");
            }
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        sendDingDing(businessObj, stringBuilder.toString());
    }

    //..p
    public void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("完备性看板有新机型加入:销售型号[");
        stringBuilder.append(jsonObject.getString("salesModel"));
        stringBuilder.append("],物料编码[");
        stringBuilder.append(jsonObject.getString("materialCode"));
        stringBuilder.append("],设计型号[");
        stringBuilder.append(jsonObject.getString("designModel"));
        stringBuilder.append("],产量[");
        stringBuilder.append(jsonObject.getString("output")).append("]");
        stringBuilder.append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }

    //..找到已存在的不同年份的同类物料记录，按照任一条更新自己的状态信息,partsAtlas,maintenancePartsList,wearingPartsList除外
    private void processByThisDao(JSONObject businessObj, Map<String, Object> params) {
        List<JSONObject> already = seGeneralKanbanCompletenessDao.dataListQuery(params);
        if (already.size() > 0) {
            businessObj.put("completenessEvaluation", already.get(0).getString("completenessEvaluation"));
            businessObj.put("partsAtlas", already.get(0).getString("partsAtlas"));
            businessObj.put("maintenancePartsList", already.get(0).getString("maintenancePartsList"));
            businessObj.put("wearingPartsList", already.get(0).getString("wearingPartsList"));
            businessObj.put("regularEdition", already.get(0).getString("regularEdition"));
            businessObj.put("CEEdition", already.get(0).getString("CEEdition"));
            businessObj.put("packingList", already.get(0).getString("packingList"));
            businessObj.put("decorationManual", already.get(0).getString("decorationManual"));
            businessObj.put("disassAndAssManual", already.get(0).getString("disassAndAssManual"));
            businessObj.put("structurefunctionAndPrincipleManual", already.get(0).getString("structurefunctionAndPrincipleManual"));
            businessObj.put("testAndAdjustmentManual", already.get(0).getString("testAndAdjustmentManual"));
            businessObj.put("troubleshootingManual", already.get(0).getString("troubleshootingManual"));
            businessObj.put("torqueAndToolStandardValueTable", already.get(0).getString("torqueAndToolStandardValueTable"));
            businessObj.put("maintenanceStandardValueTable", already.get(0).getString("maintenanceStandardValueTable"));
            businessObj.put("engineManual", already.get(0).getString("engineManual"));
            businessObj.put("lifeCycleCostList", already.get(0).getString("lifeCycleCostList"));
            businessObj.put("airconditioningUseAndMaintenanceManual", already.get(0).getString("airconditioningUseAndMaintenanceManual"));
        }
    }
}
