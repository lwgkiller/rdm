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
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.materielextend.core.util.MaterielConstant;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.ByInventoryDao;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Service
@Slf4j
public class ByInventoryService {
    private Logger logger = LogManager.getLogger(ByInventoryService.class);
    @Autowired
    private ByInventoryDao byInventoryDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryByInventory(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "byInventory_baseinfo.CREATE_TIME_");
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
        // addByInventoryRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> byInventoryList = byInventoryDao.queryByInventory(params);
        for (JSONObject byInventory : byInventoryList) {
            if (byInventory.get("CREATE_TIME_") != null) {
                byInventory.put("CREATE_TIME_", DateUtil.formatDate((Date) byInventory.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (byInventory.get("UPDATE_TIME_") != null) {
                byInventory.put("UPDATE_TIME_", DateUtil.formatDate((Date) byInventory.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(byInventoryList, ContextUtil.getCurrentUserId());
        result.setData(byInventoryList);
        int byInventoryListSize = byInventoryDao.countByInventory(params);
        result.setTotal(byInventoryListSize);
        return result;
    }

    public void createByInventory(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        byInventoryDao.createByInventory(formData);
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
                    byInventoryDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateByInventory(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        byInventoryDao.updateByInventory(formData);
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
                    byInventoryDao.createDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    byInventoryDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    byInventoryDao.deleteDetail(param);
                }
            }
        }
    }

    public List<JSONObject> queryDetail(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> detailList = byInventoryDao.queryDetail(param);
        return detailList;
    }

    public JSONObject getByInventoryDetail(String id) {
        JSONObject detailObj = byInventoryDao.queryByInventoryById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate((Date) detailObj.get("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }


    public JsonResult deleteByInventory(String[] ids, String instIdStr) {
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
        byInventoryDao.deleteByInventory(param);
        byInventoryDao.deleteAllDetail(param);
        //删除流程实例
        if (StringUtils.isNotBlank(instIdStr)) {
            String[] instIds = instIdStr.split(",");
            for (int index = 0; index < instIds.length; index++) {
                bpmInstManager.deleteCascade(instIds[index], "");
            }
        }
        return result;
    }

    public void exportByInventoryList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        List<JSONObject> detailList = queryDetail(belongId);
        int index = 1;
        for (JSONObject oneDetail : detailList) {
            oneDetail.put("index", index);
            index++;
        }
        // 通过模板导出
//        String templateFileName = "保养件清单结构化文档编制模版.xlsx";
//        File file = new File(ByInventoryService.class.getClassLoader()
//                .getResource("templates/inventory/" + templateFileName).toURI());
//        Workbook wb = com.redxun.sys.util.ExcelUtil.getWorkBook(templateFileName, new FileInputStream(file));
//
//        if (wb == null) {
//            return;
//        }
        String title = "保养件清单明细";
        String excelName = title;
        String[] firstFieldNames = {"编号", "需要检查的项目", "需要检查的项目", "需要检查的项目", "对应保养操作的TOPIC号", "维护物料编码", "数量/容量",
                "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "备注"};
        String[] secondFieldNames = {"编号", "部件名称", "关联物料编码", "详细项目", "对应保养操作的TOPIC号", "维护物料编码", "数量/容量",
                "8/每日", "50", "100", "250", "500", "1000", "2000", "4000", "4500", "5000", "备注"};
        String[] fieldCodes = {"index", "partName", "linkMaterialCode", "detailContent", "topicNum", "whMaterialCode", "capacity",
                "cycle8", "cycle50", "cycle100", "cycle250", "cycle500", "cycle1000", "cycle2000", "cycle4000", "cycle4500", "cycle5000",
                "note"};
        XSSFWorkbook wbObj = ExcelUtils.exportExcelByInventory(detailList, firstFieldNames, secondFieldNames, fieldCodes);
        // 输出
//        generateWbByApplyNo(detailList, wb);
        ExcelUtils.writeXlsxWorkBook2Stream(excelName, wbObj, response);
    }


    public void generateWbByApplyNo(List<JSONObject> detailList, Workbook wb) {
        Map<String, Object> param = new HashMap<>();
        Sheet sheet = wb.getSheetAt(0);
        // 取标题行数据
        Row titleRow = sheet.getRow(1);
        if (titleRow == null) {
            logger.error("找不到标题行");
            return;
        }
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
        }
        // 写入数据
        String[] fieldCodes = {"index", "partName", "linkMaterialCode", "detailContent", "topicNum", "whMaterialCode", "capacity",
                "cycle8", "cycle50", "cycle100", "cycle250", "cycle500", "cycle1000", "cycle2000", "cycle4000", "cycle4500", "cycle5000",
                "note"};
        for (int rowIndex = 0; rowIndex < detailList.size(); rowIndex++) {
            JSONObject oneDetail = detailList.get(rowIndex);
            Row row = sheet.createRow(rowIndex + 2);
            for (int colIndex = 0; colIndex < titleList.size(); colIndex++) {
                String value = "";
                if (StringUtils.isNotBlank(oneDetail.getString(fieldCodes[colIndex]))) {
                    value = oneDetail.getString(fieldCodes[colIndex]);
                }
                Cell cell = row.createCell(colIndex);
                cell.setCellValue(value);
            }
        }
    }


    public void importByInventory(JSONObject result, HttpServletRequest request) {
        try {
            String id = RequestUtil.getString(request, "id");
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
            List<JSONObject> itemList = new ArrayList<>();
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRowByInventory(row, itemList, titleList,id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            //删除原来的编制内容,直接重新导入
            String[] ids = id.split(",");
            List<String> idsList = Arrays.asList(ids);
            Map<String, Object> param = new HashMap<>();
            param.put("ids", idsList);
            byInventoryDao.deleteAllDetail(param);
            // 分批写入数据库
            List<JSONObject> tempInsert = new ArrayList<>();
            for (int i = 0; i < itemList.size(); i++) {
                tempInsert.add(itemList.get(i));
                if (tempInsert.size() % 20 == 0) {
                    byInventoryDao.batchInsertDetail(tempInsert);
                    tempInsert.clear();
                }
            }
            if (!tempInsert.isEmpty()) {
                byInventoryDao.batchInsertDetail(tempInsert);
                tempInsert.clear();
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRowByInventory(Row row, List<JSONObject> itemList, List<String> titleList,String belongId) {
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
                case "部件名称":
                    oneRowMap.put("partName", cellValue);
                    break;
                case "关联物料编码":
                    oneRowMap.put("linkMaterialCode", cellValue);
                    break;
                case "详细项目":
                    oneRowMap.put("detailContent", cellValue);
                    break;
                case "对应保养操作的TOPIC号":
                    oneRowMap.put("topicNum", cellValue);
                    break;
                case "维护物料编码":
                    oneRowMap.put("whMaterialCode", cellValue);
                    break;
                case "数量/容量":
                    oneRowMap.put("capacity", cellValue);
                    break;
                case "8/每日":
                    oneRowMap.put("cycle8", cellValue);
                    break;
                case "50":
                    oneRowMap.put("cycle50", cellValue);
                    break;
                case "100":
                    oneRowMap.put("cycle100", cellValue);
                    break;
                case "250":
                    oneRowMap.put("cycle250", cellValue);
                    break;
                case "500":
                    oneRowMap.put("cycle500", cellValue);
                    break;
                case "1000":
                    oneRowMap.put("cycle1000", cellValue);
                    break;
                case "2000":
                    oneRowMap.put("cycle2000", cellValue);
                    break;
                case "4000":
                    oneRowMap.put("cycle4000", cellValue);
                    break;
                case "4500":
                    oneRowMap.put("cycle4500", cellValue);
                    break;
                case "5000":
                    oneRowMap.put("cycle5000", cellValue);
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
        oneRowMap.put("belongId",belongId);
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public ResponseEntity<byte[]> importTemplateDownloadByInventory() {
        try {
            String fileName = "保养件清单结构化文档编制模版.xlsx";
            // 创建文件实例
            File file = new File(
                    ByInventoryService.class.getClassLoader().getResource("templates/inventory/" + fileName).toURI());
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
}
