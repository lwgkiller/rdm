package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.BjInventoryDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
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
public class BjInventoryService {
    private Logger logger = LogManager.getLogger(BjInventoryService.class);
    @Autowired
    private BjInventoryDao bjInventoryDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private SysDicManager sysDicManager;

    public JsonPageResult<?> queryBjInventory(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "bjInventory_baseinfo.CREATE_TIME_");
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
        rdmZhglUtil.addPage(request, params);
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        // addBjInventoryRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> bjInventoryList = bjInventoryDao.queryBjInventory(params);
        for (JSONObject bjInventory : bjInventoryList) {
            if (bjInventory.get("CREATE_TIME_") != null) {
                bjInventory.put("CREATE_TIME_", DateUtil.formatDate((Date) bjInventory.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (bjInventory.get("UPDATE_TIME_") != null) {
                bjInventory.put("UPDATE_TIME_", DateUtil.formatDate((Date) bjInventory.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        rdmZhglUtil.setTaskInfo2Data(bjInventoryList, ContextUtil.getCurrentUserId());
        result.setData(bjInventoryList);
        int bjInventoryListSize = bjInventoryDao.countBjInventory(params);
        result.setTotal(bjInventoryListSize);
        return result;
    }

    public void createBjInventory(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        bjInventoryDao.createBjInventory(formData);
        if (StringUtils.isNotBlank(formData.getString("inventory"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("inventory"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    bjInventoryDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateBjInventory(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        bjInventoryDao.updateBjInventory(formData);
        if (StringUtils.isNotBlank(formData.getString("inventory"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("inventory"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    bjInventoryDao.createDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    bjInventoryDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    bjInventoryDao.deleteDetail(param);
                }
            }
        }
    }

    public List<JSONObject> queryDetail(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> detailList = bjInventoryDao.queryDetail(param);
        return detailList;
    }

    public JSONObject getBjInventoryDetail(String id) {
        JSONObject detailObj = bjInventoryDao.queryBjInventoryById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate((Date) detailObj.get("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }


    public JsonResult deleteBjInventory(String[] ids, String instIdStr) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idsList = Arrays.asList(ids);
        List<String> detailIdList = new ArrayList<>();
        for (String mainId : idsList) {
            List<JSONObject> detailList = queryDetail(mainId);
            for (JSONObject detail : detailList) {
                detailIdList.add(detail.getString("id"));
            }
        }
        Map<String, Object> param = new HashMap<>();
        param.clear();
        param.put("ids", idsList);
        bjInventoryDao.deleteBjInventory(param);
        bjInventoryDao.deleteAllDetail(param);
        //删除流程实例
        if (StringUtils.isNotBlank(instIdStr)) {
            String[] instIds = instIdStr.split(",");
            for (int index = 0; index < instIds.length; index++) {
                bpmInstManager.deleteCascade(instIds[index], "");
            }
        }
        return result;
    }

    public void exportBjInventoryList(HttpServletRequest request, HttpServletResponse response) {
        String exportType = RequestUtil.getString(request, "exportType");
        if("inProcess".equalsIgnoreCase(exportType)){
            exportBjInventoryListInProcess(request,response);
        }else if("endProcess".equalsIgnoreCase(exportType)){
            exportBjInventoryListEndProcess(request,response);
        }
    }



    public void exportBjInventoryListInProcess(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        JSONObject detailObj = bjInventoryDao.queryBjInventoryById(belongId);
        List<JSONObject> detailList = queryDetail(belongId);
        int index = 1;
        for (JSONObject oneDetail : detailList) {
            oneDetail.put("index", index);
            index++;
        }
        String title = "备件推荐清单";
        String excelName = detailObj.getString("designModelName") +title;
        String[] fieldNames = {"编号", "物料属性","位置","物料编码(总成)", "物料名称(总成)", "物料编码", "物料名称",
                "装配数量/台", "推荐数量","保养时长", "备注"};
        String[] fieldCodes = {"index", "materielType","materielPlace","zcMaterielCode", "zcMaterielName", "materielCode", "materielName",
                "num", "suggestNum","byTime", "note"};
        XSSFWorkbook wbObj = ExcelUtils.exportXlsxExcelWBWithoutTitle(detailList, fieldNames, fieldCodes);
        ExcelUtils.writeXlsxWorkBook2Stream(excelName, wbObj, response);
    }

    public void exportBjInventoryListEndProcess(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        JSONObject detailObj = bjInventoryDao.queryBjInventoryById(belongId);
        List<JSONObject> detailList = queryDetail(belongId);
        String title = "备件推荐清单";
        String excelName = detailObj.getString("designModelName") +title;
        String[] fieldNames = {"物料属性","位置","物料编码(总成)", "物料名称(总成)", "物料编码", "物料名称",
                "装配数量/台", "推荐数量","保养时长", "备注"};
        String[] fieldCodes = {"materielType","materielPlace","zcMaterielCode", "zcMaterielName", "materielCode", "materielName",
                "num", "suggestNum","byTime", "note"};
        XSSFWorkbook wbObj = exportExcelBjInventory(detailList, fieldNames, fieldCodes,title,detailObj);
        ExcelUtils.writeXlsxWorkBook2Stream(excelName, wbObj, response);
    }

    public static <T> XSSFWorkbook exportExcelBjInventory(List<T> dataList, String[] fieldNames
            , String[] fieldCodes,String title,JSONObject detailObj ) {
        XSSFWorkbook wbObj = new XSSFWorkbook();

        XSSFSheet sheet = wbObj.createSheet();

        XSSFCell cell = null;
        XSSFCellStyle firstRowStyle = createFirstRowStyleXlsx(wbObj);
        XSSFCellStyle dataRowStyle = createDataRowStyleXlsx(wbObj);
        //基础信息
        //第一行
        XSSFRow row = sheet.createRow(0);
        //申请编号
        cell = row.createCell(0);
        cell.setCellValue("申请编号");
        cell.setCellStyle(firstRowStyle);
        cell = row.createCell(1);
        cell.setCellValue(detailObj.getString("processNum"));
        cell.setCellStyle(firstRowStyle);
        //第二行
        row = sheet.createRow(1);
        //销售型号
        cell = row.createCell(0);
        cell.setCellValue("销售型号");
        cell.setCellStyle(firstRowStyle);
        cell = row.createCell(1);
        cell.setCellValue(detailObj.getString("saleModel"));
        cell.setCellStyle(firstRowStyle);
        //整机PIN码
        cell = row.createCell(2);
        cell.setCellValue("整机PIN码");
        cell.setCellStyle(firstRowStyle);
        cell = row.createCell(3);
        cell.setCellValue(detailObj.getString("machineNum"));
        cell.setCellStyle(firstRowStyle);
        //第三行
        row = sheet.createRow(2);
        //整机物料编码
        cell = row.createCell(0);
        cell.setCellValue("整机物料编码");
        cell.setCellStyle(firstRowStyle);
        cell = row.createCell(1);
        cell.setCellValue(detailObj.getString("materielCode"));
        cell.setCellStyle(firstRowStyle);
        //设计型号
        cell = row.createCell(2);
        cell.setCellValue("设计型号");
        cell.setCellStyle(firstRowStyle);
        cell = row.createCell(3);
        cell.setCellValue(detailObj.getString("designModelName"));
        cell.setCellStyle(firstRowStyle);
        //第四行
        row = sheet.createRow(3);
        // 标题设置样式
        XSSFCellStyle titleStyle = createTitleStyle(wbObj);
        cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(titleStyle);
        // 合并标题 单元格
        CellRangeAddress region = new CellRangeAddress(3, 3, 0, fieldNames.length - 3);
        sheet.addMergedRegionUnsafe(region);
        //备件推荐时长
        cell = row.createCell(fieldNames.length - 2);
        cell.setCellValue("备件推荐时长");
        cell.setCellStyle(firstRowStyle);
        cell = row.createCell(fieldNames.length - 1);
        cell.setCellValue(detailObj.getString("suggestTime"));
        cell.setCellStyle(firstRowStyle);
        //第五行
        // 表头1
        row = sheet.createRow(4);
        // 表头赋值
        for (int i = 0; i < fieldNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames[i]);
            cell.setCellStyle(firstRowStyle);
        }
        // 内容赋值
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                String json = JSON.toJSONString(dataList.get(i));
                JSONObject temp = JSON.parseObject(json);
                XSSFRow rows = sheet.createRow(i + 5);
                XSSFRow lastRow = null;
                if (i != 0) {
                    lastRow = sheet.getRow(i + 4);
                }
                for (int j = 0; j < fieldCodes.length; j++) {
                    cell = rows.createCell(j);
                    String value = "";
                    if (StringUtils.isNotBlank(temp.getString(fieldCodes[j]))) {
                        value = temp.getString(fieldCodes[j]);
                    }
                    cell.setCellValue(value);
                    if(j<=1){
                        cell.setCellStyle(firstRowStyle);
                    }else {
                        cell.setCellStyle(dataRowStyle);
                    }
                }
            }
        }

        // 设置每一列的自适应宽度
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 8000);
        for (int i = 4; i < fieldCodes.length; i++) {
            sheet.setColumnWidth(i, fieldNames[i].getBytes().length * 300);
        }
        //冻结前五行
        sheet.createFreezePane(0, 5);
        return wbObj;
    }

    private static XSSFCellStyle createTitleStyle(XSSFWorkbook wbObj) {
        // 设置字体
        XSSFFont font = wbObj.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short)14);
        // 字体加粗
        font.setBold(true);
        // 设置字体名字
        font.setFontName("仿宋");
        // 设置样式
        XSSFCellStyle topstyle = wbObj.createCellStyle();
        // 设置下边框
        topstyle.setBorderTop(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderLeft(BorderStyle.THIN);
        // 设置下边框
        topstyle.setBorderBottom(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderRight(BorderStyle.THIN);
        topstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        // 在样式中应用设置的字体
        topstyle.setFont(font);
        // 设置自动换行
        topstyle.setWrapText(false);
        // 设置水平对齐的样式为居中对齐；
        topstyle.setAlignment(HorizontalAlignment.CENTER);
        topstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return topstyle;
    }


    private static XSSFCellStyle createFirstRowStyleXlsx(XSSFWorkbook wbObj) {
        // 设置字体
        XSSFFont font = wbObj.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short)12);
        // 字体加粗
        font.setBold(true);
        // 设置字体名字
        font.setFontName("仿宋");
        // 设置样式
        XSSFCellStyle topstyle = wbObj.createCellStyle();
        // 设置下边框
        topstyle.setBorderTop(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderLeft(BorderStyle.THIN);
        // 设置下边框
        topstyle.setBorderBottom(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderRight(BorderStyle.THIN);
        topstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        // 在样式中应用设置的字体
        topstyle.setFont(font);
        // 设置自动换行
        topstyle.setWrapText(false);
        // 设置水平对齐的样式为居中对齐；
        topstyle.setAlignment(HorizontalAlignment.CENTER);
        topstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return topstyle;
    }

    private static XSSFCellStyle createDataRowStyleXlsx(XSSFWorkbook wbObj) {
        // 设置字体
        XSSFFont font = wbObj.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short)12);
        // 字体加粗
        font.setBold(false);
        // 设置字体名字
        font.setFontName("仿宋");
        // 设置样式
        XSSFCellStyle topstyle = wbObj.createCellStyle();
        // 设置下边框
        topstyle.setBorderTop(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderLeft(BorderStyle.THIN);
        // 设置下边框
        topstyle.setBorderBottom(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderRight(BorderStyle.THIN);
        topstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        // 在样式中应用设置的字体
        topstyle.setFont(font);
        // 设置自动换行
        topstyle.setWrapText(false);
        // 设置水平对齐的样式为居中对齐；
        topstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return topstyle;
    }

    public void importBjInventory(JSONObject result, HttpServletRequest request) {
        try {
            String id = RequestUtil.getString(request, "id");
            String needLanguage = RequestUtil.getString(request, "needLanguage");
            String stageName = RequestUtil.getString(request, "stageName");
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
            Sheet sheet = wb.getSheetAt(0);
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
                result.put("message", "数据导入失败，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<JSONObject> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRowBjInventory(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            //不是导入保养件/易损件/维修件清单的步骤就删除原来的编制内容,直接重新导入
            if (!"second".equalsIgnoreCase(stageName) && !"third".equalsIgnoreCase(stageName)) {
                String[] ids = id.split(",");
                List<String> idsList = Arrays.asList(ids);
                Map<String, Object> param = new HashMap<>();
                param.put("ids", idsList);
                bjInventoryDao.deleteAllDetail(param);
            }
            // 分批写入数据库
            List<JSONObject> tempInsert = new ArrayList<>();
            for (int i = 0; i < itemList.size(); i++) {
                tempInsert.add(itemList.get(i));
                if (tempInsert.size() % 20 == 0) {
                    bjInventoryDao.batchInsertDetail(tempInsert);
                    tempInsert.clear();
                }
            }
            if (!tempInsert.isEmpty()) {
                bjInventoryDao.batchInsertDetail(tempInsert);
                tempInsert.clear();
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRowBjInventory(Row row, List<JSONObject> itemList, List<String> titleList, String belongId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        JSONObject oneRowMap = new JSONObject();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cell.setCellType(CellType.STRING);
                cellValue = StringUtils.trim(cell.getStringCellValue());
            }
            if (StringUtils.isBlank(cellValue)) {
                cellValue = "";
            }
            switch (title) {
                case "编号":
                    break;
                case "物料属性":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料属性为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materielType", cellValue);
                    break;
                case "位置":
                    oneRowMap.put("materielPlace", cellValue);
                    break;
                case "物料编码(总成)":
                    oneRowMap.put("zcMaterielCode", cellValue);
                    break;
                case "物料名称(总成)":
                    oneRowMap.put("zcMaterielName", cellValue);
                    break;
                case "物料编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materielCode", cellValue);
                    break;
                case "物料名称":
                    oneRowMap.put("materielName", cellValue);
                    break;
                case "装配数量/台":
                    oneRowMap.put("num", cellValue);
                    break;
                case "推荐数量":
                    oneRowMap.put("suggestNum", cellValue);
                    break;
                case "保养时长":
                    oneRowMap.put("byTime", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("note", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("belongId", belongId);
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public ResponseEntity<byte[]> importTemplateDownloadBjInventory() {
        try {
            String fileName = "备件推荐清单结构化文档编制模版.xlsx";
            // 创建文件实例
            File file = new File(
                    BjInventoryService.class.getClassLoader().getResource("templates/inventory/" + fileName).toURI());
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

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public void saveBjInventoryUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = sysDicManager
                .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "bjInventoryFile").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find bjInventoryFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("id"));
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
            fileInfo.put("belongId", belongId);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            bjInventoryDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public List<JSONObject> getBjInventoryFileList(String belongId) {
        List<JSONObject> bjInventoryFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("id", belongId);
        bjInventoryFileList = bjInventoryDao.queryBjInventoryFileList(param);
        return bjInventoryFileList;
    }

    public void deleteOneBjInventoryFile(String fileId, String fileName, String id) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, id, sysDicManager
                .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "bjInventoryFile").getValue());
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        bjInventoryDao.deleteBjInventoryFile(param);
    }
}
