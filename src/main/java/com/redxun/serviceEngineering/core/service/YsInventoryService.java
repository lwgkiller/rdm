package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.YsInventoryDao;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class YsInventoryService {
    private Logger logger = LogManager.getLogger(YsInventoryService.class);
    @Autowired
    private YsInventoryDao ysInventoryDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryYsInventory(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "ysInventory_baseinfo.CREATE_TIME_");
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
        // addYsInventoryRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> ysInventoryList = ysInventoryDao.queryYsInventory(params);
        for (JSONObject ysInventory : ysInventoryList) {
            if (ysInventory.get("CREATE_TIME_") != null) {
                ysInventory.put("CREATE_TIME_", DateUtil.formatDate((Date) ysInventory.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (ysInventory.get("UPDATE_TIME_") != null) {
                ysInventory.put("UPDATE_TIME_", DateUtil.formatDate((Date) ysInventory.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(ysInventoryList, ContextUtil.getCurrentUserId());
        result.setData(ysInventoryList);
        int ysInventoryListSize = ysInventoryDao.countYsInventory(params);
        result.setTotal(ysInventoryListSize);
        return result;
    }

    public void createYsInventory(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        ysInventoryDao.createYsInventory(formData);
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
                    ysInventoryDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateYsInventory(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        ysInventoryDao.updateYsInventory(formData);
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
                    ysInventoryDao.createDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    ysInventoryDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    ysInventoryDao.deleteDetail(param);
                }
            }
        }
    }

    public List<JSONObject> queryDetail(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> detailList = ysInventoryDao.queryDetail(param);
        return detailList;
    }

    public JSONObject getYsInventoryDetail(String id) {
        JSONObject detailObj = ysInventoryDao.queryYsInventoryById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate((Date) detailObj.get("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }


    public JsonResult deleteYsInventory(String[] ids, String instIdStr) {
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
        ysInventoryDao.deleteYsInventory(param);
        ysInventoryDao.deleteAllDetail(param);
        //删除流程实例
        if (StringUtils.isNotBlank(instIdStr)) {
            String[] instIds = instIdStr.split(",");
            for (int index = 0; index < instIds.length; index++) {
                bpmInstManager.deleteCascade(instIds[index], "");
            }
        }
        return result;
    }

    public void exportYsInventoryList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        List<JSONObject> detailList = queryDetail(belongId);
        int index = 1;
        for (JSONObject oneDetail : detailList) {
            oneDetail.put("index", index);
            index++;
        }
        String title = "易损件清单明细";
        String excelName = title;
        String[] fieldNames = {"编号", "总成部件", "物料", "总成物料编码", "总成物料描述", "物料编码", "物料描述",
                "数量/台", "备注"};
        String[] fieldCodes = {"index", "zcPart", "materiel", "zcMaterielCode", "zcMaterielContent", "materielCode", "materielContent",
                "num", "note"};
        XSSFWorkbook wbObj = ExcelUtils.exportXlsxExcelWBWithoutTitle(detailList,fieldNames, fieldCodes);
        ExcelUtils.writeXlsxWorkBook2Stream(excelName, wbObj, response);
    }


    public void importYsInventory(JSONObject result, HttpServletRequest request) {
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
                JSONObject rowParse = generateDataFromRowYsInventory(row, itemList, titleList,id);
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
            ysInventoryDao.deleteAllDetail(param);
            // 分批写入数据库
            List<JSONObject> tempInsert = new ArrayList<>();
            for (int i = 0; i < itemList.size(); i++) {
                tempInsert.add(itemList.get(i));
                if (tempInsert.size() % 20 == 0) {
                    ysInventoryDao.batchInsertDetail(tempInsert);
                    tempInsert.clear();
                }
            }
            if (!tempInsert.isEmpty()) {
                ysInventoryDao.batchInsertDetail(tempInsert);
                tempInsert.clear();
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRowYsInventory(Row row, List<JSONObject> itemList, List<String> titleList,String belongId) {
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
                case "总成部件":
                    oneRowMap.put("zcPart", cellValue);
                    break;
                case "物料":
                    oneRowMap.put("materiel", cellValue);
                    break;
                case "总成物料编码":
                    oneRowMap.put("zcMaterielCode", cellValue);
                    break;
                case "总成物料描述":
                    oneRowMap.put("zcMaterielContent", cellValue);
                    break;
                case "物料编码":
                    oneRowMap.put("materielCode", cellValue);
                    break;
                case "物料描述":
                    oneRowMap.put("materielContent", cellValue);
                    break;
                case "数量/台":
                    oneRowMap.put("num", cellValue);
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

    public ResponseEntity<byte[]> importTemplateDownloadYsInventory() {
        try {
            String fileName = "易损件清单结构化文档编制模版.xlsx";
            // 创建文件实例
            File file = new File(
                    YsInventoryService.class.getClassLoader().getResource("templates/inventory/" + fileName).toURI());
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
